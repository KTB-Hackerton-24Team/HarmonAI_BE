package Hackerton.BE.HarmonAI.service;

import Hackerton.BE.HarmonAI.domain.MusicVideo;
import Hackerton.BE.HarmonAI.dto.MusicRecommendationDTO;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YouTube youtubeClient;

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final long MAX_SEARCH_RESULTS = 1;

    /**
     * 여러 노래 정보를 받아 각각에 대한 YouTube 동영상 정보를 검색하여 반환
     *
     * @param recommendations 노래 제목과 가수 정보가 담긴 DTO 리스트
     * @return 검색된 YouTube 동영상 정보 리스트
     */
    public List<MusicVideo> searchVideos(List<MusicRecommendationDTO> recommendations) {
        List<MusicVideo> videos = new ArrayList<>();

        for (MusicRecommendationDTO dto : recommendations) {
            Optional<MusicVideo> videoResult = searchVideo(dto);
            if (videoResult.isPresent()) {
                videos.add(videoResult.get());
            }
        }

        return videos;
    }

    /**
     * DTO로부터 제목과 가수 정보를 추출하여 YouTube 검색 수행
     *
     * @param dto 노래 제목과 가수 정보가 담긴 DTO
     * @return 검색된 YouTube 동영상 정보 (Optional 래핑)
     */
    public Optional<MusicVideo> searchVideo(MusicRecommendationDTO dto) {
        String title = dto.getTitle();
        String artist = dto.getArtist();

        return searchVideo(title, artist);
    }

    /**
     * 제목과 가수 정보를 사용하여 YouTube에서 동영상 검색
     *
     * @param title 노래 제목
     * @param artist 가수 이름
     * @return 검색된 YouTube 동영상 정보 (Optional 래핑)
     */
    public Optional<MusicVideo> searchVideo(String title, String artist) {
        try {
            // 제목 + 가수로 검색
            String query = title + " " + artist + " official";
            log.info("Searching Youtube for: {}", query);

            // Youtube 검색 요청 설정
            YouTube.Search.List search = youtubeClient.search().list(List.of("id", "snippet"));
            search.setKey(apiKey);
            search.setQ(query);
            search.setType(List.of("video"));
            search.setVideoCategoryId("10"); // 음악 카테고리
            search.setMaxResults(MAX_SEARCH_RESULTS);

            // 검색 결과 반환
            SearchListResponse SearchResponse = search.execute();
            List<SearchResult> searchResults = SearchResponse.getItems();

            if (searchResults != null && !searchResults.isEmpty()) {
                SearchResult video = searchResults.get(0);
                String videoId = video.getId().getVideoId();
//                String videoTitle = video.getSnippet().getTitle();
                String embedUrl = "https://www.youtube.com/embed/" + videoId;

                MusicVideo musicVideo = MusicVideo.builder()
                        .artist(artist)
                        .title(title)
                        .embedUrl(embedUrl)
                        .build();

                // 콘솔에 출력
                System.out.println("Video ID: " + videoId);
                System.out.println("Title: " + title);
                System.out.println("Embed URL: " + embedUrl);


                log.info("Found Youtube video: {}", musicVideo);
                return Optional.of(musicVideo);
            } else {
                log.warn("No videos found for query: {}", query);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error searching Youtube: ", e);
            return Optional.empty();
        }
    }

    /**
     * 테스트용 더미 MusicVideo 목록 생성
     */
    private List<MusicVideo> generateDummyMusicVideos(List<MusicRecommendationDTO> recommendations) {
        List<MusicVideo> dummyVideos = new ArrayList<>();

        // 추천 목록을 순회하며 더미 비디오 생성
        for (MusicRecommendationDTO rec : recommendations) {
            dummyVideos.add(createDummyMusicVideo(rec.getTitle(), rec.getArtist()));
        }

        // 추천 목록이 비어있을 경우 기본 데이터 제공
        if (dummyVideos.isEmpty()) {
            dummyVideos.add(createDummyMusicVideo("Dynamite", "BTS"));
            dummyVideos.add(createDummyMusicVideo("Blinding Lights", "The Weeknd"));
        }

        return dummyVideos;
    }

    /**
     * 테스트용 더미 MusicVideo 객체 생성
     */
    private MusicVideo createDummyMusicVideo(String title, String artist) {
        // 고정된 영상 ID 맵 (특정 노래에 대해 실제 영상 ID 매핑)
        Map<String, String> videoIdMap = new HashMap<>();
        videoIdMap.put("Dynamite-BTS", "gdZLi9oWNZg");
        videoIdMap.put("Butter-BTS", "WMweEpGlu_U");
        videoIdMap.put("Blinding Lights-The Weeknd", "4NRXx6U8ABQ");
        videoIdMap.put("Levitating-Dua Lipa", "TUVcZfQe-Kw");

        // 맵에서 비디오 ID 찾기, 없으면 기본값 사용
        String key = title + "-" + artist;
        String videoId = videoIdMap.getOrDefault(key, "dQw4w9WgXcQ"); // 기본값: Rick Astley - Never Gonna Give You Up

        // 임베드 URL 생성
        String embedUrl = "https://www.youtube.com/embed/" + videoId;

        return MusicVideo.builder()
                .artist(artist)
                .title(title + " (Official Music Video)")
                .embedUrl(embedUrl)
                .build();
    }
}