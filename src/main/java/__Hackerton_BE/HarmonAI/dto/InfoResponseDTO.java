package __Hackerton_BE.HarmonAI.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoResponseDTO {
    private double latitude;
    private double longitude;
    private String question;
}
