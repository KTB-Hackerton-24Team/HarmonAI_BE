package __Hackerton_BE.HarmonAI.service;


import __Hackerton_BE.HarmonAI.domain.MusicVideo;
import __Hackerton_BE.HarmonAI.dto.MusicRecommendationDTO;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YouTube youtubeClient;

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final long MAX_SEARCH_RESULTS = 1;

    /**
     * 제목과 가수 정보로 유튜브에서 노래 검색
     * @param 노래 제목과 가수 정보를 담은 dto (recommendationDTO)
     * @return 검색된 유튜브 동영상 정보를 Optinal로 래핑
     */

    public Optional<MusicVideo> searchVideo(MusicRecommendationDTO recommendationDTO) {
        String title = recommendationDTO.getTitle();
        String artist = recommendationDTO.getArtist();

        return searchVideo(title, artist);
    }

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
//            search.setVideoCategoryId("10"); // 음악 카테고리
            search.setMaxResults(MAX_SEARCH_RESULTS);

            // 검색 결과 반환
            SearchListResponse SearchResponse = search.execute();
            List<SearchResult> searchResults = SearchResponse.getItems();

            if (searchResults != null && searchResults.isEmpty()) {
                SearchResult video = searchResults.get(0);
                String videoId = video.getId().getVideoId();
                String videoTitle = video.getSnippet().getTitle();
                String embedUrl = "https://www.youtube.com/embed/" + videoId;

                MusicVideo musicVideo = MusicVideo.builder()
                        .videoId(videoId)
                        .title(videoTitle)
                        .embedUrl(embedUrl)
                        .build();

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
}
