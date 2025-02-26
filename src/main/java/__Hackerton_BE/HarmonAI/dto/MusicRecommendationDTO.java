package __Hackerton_BE.HarmonAI.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicRecommendationDTO {
    private String title;
    private String artist;
}
