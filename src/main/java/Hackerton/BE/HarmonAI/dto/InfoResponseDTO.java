package Hackerton.BE.HarmonAI.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoResponseDTO {
    private List<MusicResponseItem> recommendations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MusicResponseItem {
        private String title;
        private String artist;
        private String embedUrl;
    }
}