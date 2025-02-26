package __Hackerton_BE.HarmonAI.controller;

import __Hackerton_BE.HarmonAI.domain.MusicVideo;
import __Hackerton_BE.HarmonAI.dto.ErrorResponseDTO;
import __Hackerton_BE.HarmonAI.dto.MusicRecommendationDTO;
import __Hackerton_BE.HarmonAI.dto.YoutubeSearchResultDTO;
import __Hackerton_BE.HarmonAI.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

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
    public ResponseEntity<?> processRecommendation(@RequestBody MusicRecommendationDTO recommendationDTO) {
        log.info("Received recommendation: {}", recommendationDTO);

        // 입력 검증
        if (recommendationDTO.getTitle() == null || recommendationDTO.getArtist() == null ||
            recommendationDTO.getTitle().isEmpty() || recommendationDTO.getArtist().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponseDTO.of("제목과 가수 정보가 필요함"));
        }

        // 유튜브 검색
        Optional<MusicVideo> musicVideoOpt = youtubeService.searchVideo(recommendationDTO);


        return musicVideoOpt
                .map(musicVideo -> ResponseEntity.ok(YoutubeSearchResultDTO.fromEntity(musicVideo)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
                    .body(ErrorResponseDTO.of("제목과 가수 정보가 필요함"));
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
