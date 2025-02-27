package Hackerton.BE.HarmonAI.service;

import Hackerton.BE.HarmonAI.domain.MusicVideo;
import Hackerton.BE.HarmonAI.dto.InfoRequestDTO;
import Hackerton.BE.HarmonAI.dto.InfoResponseDTO;
import Hackerton.BE.HarmonAI.dto.InfoResponseDTO.MusicResponseItem;
import Hackerton.BE.HarmonAI.dto.MusicRecommendationDTO;
import Hackerton.BE.HarmonAI.dto.MusicRecommendationsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final YoutubeService youtubeService;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${app.test-mode:true}")
    private boolean testMode;

    @Override
    public InfoResponseDTO processQuestion(InfoRequestDTO requestDTO) {
        log.info("위도: {}, 경도:{}, 입력:{}, 선호도:{}",
                requestDTO.getLatitude(),
                requestDTO.getLongitude(),
                requestDTO.getQuestion(),
                requestDTO.getPop());

        // AI 서비스 호출 또는 테스트 더미 데이터 생성
        List<MusicRecommendationDTO> recommendations;
        if (testMode) {
            recommendations = generateDummyRecommendations(requestDTO.getQuestion());
            log.info("테스트 모드: 더미 추천 생성 - {} 개의 추천", recommendations.size());
        } else {
            // AI 서비스 호출
            recommendations = callAiService(
                    requestDTO.getQuestion(),
                    requestDTO.getLatitude(),
                    requestDTO.getLongitude(),
                    requestDTO.getPop()
            );
        }

        // 추천 결과가 없는 경우 빈 응답 반환
        if (recommendations.isEmpty()) {
            return InfoResponseDTO.builder()
                    .recommendations(new ArrayList<>())
                    .build();
        }

        // YouTube API로 각 추천 항목 검색
        List<MusicVideo> musicVideos = youtubeService.searchVideos(recommendations);

        // 검색 결과를 응답 항목으로 변환
        List<MusicResponseItem> responseItems = new ArrayList<>();

        // 검색된 비디오와 원래 추천 항목을 매핑
        for (int i = 0; i < recommendations.size(); i++) {
            MusicRecommendationDTO rec = recommendations.get(i);

            // 해당 추천에 대한 검색 결과가 있는지 확인
            Optional<MusicVideo> matchingVideo = musicVideos.stream()
                    .filter(mv -> mv.getArtist().equals(rec.getArtist()))
                    .findFirst();

            if (matchingVideo.isPresent()) {
                // 검색 결과가 있으면 사용
                MusicVideo mv = matchingVideo.get();
                responseItems.add(MusicResponseItem.builder()
                        .title(mv.getTitle())
                        .artist(mv.getArtist())
                        .embedUrl(mv.getEmbedUrl())
                        .build());
            } else {
                // 검색 결과가 없으면 원래 추천 정보만 사용하고 embedUrl은 빈값
                responseItems.add(MusicResponseItem.builder()
                        .title(rec.getTitle())
                        .artist(rec.getArtist())
                        .embedUrl("")
                        .build());
            }
        }

        // 최종 응답 생성
        return InfoResponseDTO.builder()
                .recommendations(responseItems)
                .build();
    }

    private List<MusicRecommendationDTO> callAiService(String question, double latitude, double longitude, int pop) {
        // HTTP 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // 요청 본문 데이터 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question", question);
            requestBody.put("latitude", latitude);
            requestBody.put("longitude", longitude);
            requestBody.put("pop", pop);

            // HTTP 엔티티 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("AI 서비스 호출: {}", aiServerUrl);


            // AI로 POST 요청 전송
            ResponseEntity<MusicRecommendationsDTO> response = restTemplate.exchange(
                    aiServerUrl + "/api/music/recommend",
                    HttpMethod.POST,
                    requestEntity,
                    MusicRecommendationsDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<MusicRecommendationDTO> recommendations = response.getBody().getRecommendations();
                log.info("AI 서비스로부터 {} 개의 음악 추천을 받았습니다", recommendations.size());
                return recommendations;
            }

            log.error("AI 서비스 응답 처리 실패: {}", response.getStatusCode());
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("AI 서비스 호출 중 오류 발생: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 테스트용 더미 음악 추천 데이터 생성
     */
    private List<MusicRecommendationDTO> generateDummyRecommendations(String question) {
        List<MusicRecommendationDTO> recommendations = new ArrayList<>();

        // 질문에 따라 다른 추천 결과 생성
        if (question.contains("신나는") || question.contains("행복")) {
            recommendations.add(createRecommendation("Dynamite", "BTS"));
            recommendations.add(createRecommendation("Butter", "BTS"));
            recommendations.add(createRecommendation("Uptown Funk", "Mark Ronson"));
        } else if (question.contains("슬픈") || question.contains("우울")) {
            recommendations.add(createRecommendation("Someone Like You", "Adele"));
            recommendations.add(createRecommendation("Fix You", "Coldplay"));
            recommendations.add(createRecommendation("All of Me", "John Legend"));
        } else if (question.contains("팝") || question.contains("pop")) {
            recommendations.add(createRecommendation("Blinding Lights", "The Weeknd"));
            recommendations.add(createRecommendation("Levitating", "Dua Lipa"));
            recommendations.add(createRecommendation("Stay", "The Kid LAROI"));
        } else {
            // 기본 추천
            recommendations.add(createRecommendation("Dynamite", "BTS"));
            recommendations.add(createRecommendation("Blinding Lights", "The Weeknd"));
        }

        return recommendations;
    }

    /**
     * 음악 추천 DTO 생성 헬퍼 메서드
     */
    private MusicRecommendationDTO createRecommendation(String title, String artist) {
        MusicRecommendationDTO dto = new MusicRecommendationDTO();
        dto.setTitle(title);
        dto.setArtist(artist);
        return dto;
    }
}