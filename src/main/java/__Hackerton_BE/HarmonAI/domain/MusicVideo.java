package __Hackerton_BE.HarmonAI.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicVideo {
    private String videoId;
    private String title;
    private String embedUrl;
}
