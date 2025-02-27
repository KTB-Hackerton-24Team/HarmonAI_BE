package __Hackerton_BE.HarmonAI.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InfoRequestDTO {
    private double latitude;
    private double longitude;
    private int pop;
    private String question;
}