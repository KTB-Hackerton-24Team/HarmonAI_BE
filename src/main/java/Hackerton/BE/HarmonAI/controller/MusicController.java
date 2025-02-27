package Hackerton.BE.HarmonAI.controller;

import Hackerton.BE.HarmonAI.domain.MusicVideo;
import Hackerton.BE.HarmonAI.dto.ErrorResponseDTO;
import Hackerton.BE.HarmonAI.dto.MusicRecommendationDTO;
import Hackerton.BE.HarmonAI.dto.MusicRecommendationsDTO;
import Hackerton.BE.HarmonAI.dto.YoutubeSearchResultDTO;
import Hackerton.BE.HarmonAI.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final YoutubeService youtubeService;

    /**
     * AI 모델에서 받은 추천 음악을 처리하는 API
     * POST 요청으로 JSON 형식의 노래 정보(제목, 가수)를 받아 유튜브 검색 결과 반환
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> processRecommendation(@RequestBody MusicRecommendationsDTO recommendationDTO) {
        log.info("Received recommendation: {}", recommendationDTO);

        // 입력 검증
        if (recommendationDTO.getRecommendations() == null || recommendationDTO.getRecommendations().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponseDTO.builder()
                            .message("음악 추천 정보가 필요합니다")
                            .status("error")
                            .timestamp(LocalDateTime.now())
                            .build());
        }

        // 각 추천 항목 검증
        for (MusicRecommendationDTO rec : recommendationDTO.getRecommendations()) {
            if (rec.getTitle() == null || rec.getArtist() == null ||
                    rec.getTitle().isEmpty() || rec.getArtist().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponseDTO.builder()
                                .message("모든 추천 항목에 제목과 가수 정보가 필요합니다")
                                .status("error")
                                .timestamp(LocalDateTime.now())
                                .build());
            }
        }

        // 유튜브 검색
        List<MusicVideo> musicVideos = youtubeService.searchVideos(recommendationDTO.getRecommendations());

        if (musicVideos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 결과 변환
        List<YoutubeSearchResultDTO> results = musicVideos.stream()
                .map(YoutubeSearchResultDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    /**
     * 직접 노래 검색하는 API
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchMusic(
            @RequestParam String title,
            @RequestParam String artist) {

        log.info("Search request - title: {}, artist: {}", title, artist);

        // 입력 검증
        if (title.isEmpty() || artist.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponseDTO.of("제목과 가수 정보가 필요합니다"));
        }

        // 유튜브 검색
        Optional<MusicVideo> musicVideoOpt = youtubeService.searchVideo(title, artist);

        return musicVideoOpt
                .map(musicVideo -> ResponseEntity.ok(YoutubeSearchResultDTO.fromEntity(musicVideo)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 서비스 상태 확인 API
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("HarmonAI Music Service is Running");
    }
}