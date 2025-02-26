package __Hackerton_BE.HarmonAI.dto;

import __Hackerton_BE.HarmonAI.domain.MusicVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeSearchResultDTO {
    private String videoId;
    private String title;
    private String embedUrl;

    // 도메인 객체로 변환하는 메서드
    public MusicVideo toEntity() {
        return MusicVideo.builder()
                .videoId(this.videoId)
                .title(this.title)
                .embedUrl(this.embedUrl).build();
    }

    // 도메인 객체에서 DTO로 변환하는 정적 메서드
    public static YoutubeSearchResultDTO fromEntity(MusicVideo musicVideo) {
        return YoutubeSearchResultDTO.builder()
                .videoId(musicVideo.getVideoId())
                .title(musicVideo.getTitle())
                .embedUrl(musicVideo.getEmbedUrl())
                .build();
    }
}
