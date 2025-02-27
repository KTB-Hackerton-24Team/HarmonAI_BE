package Hackerton.BE.HarmonAI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicRecommendationsDTO {
    private List<MusicRecommendationDTO> recommendations;
}