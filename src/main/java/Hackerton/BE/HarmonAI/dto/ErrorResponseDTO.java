package Hackerton.BE.HarmonAI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private String status;
    private LocalDateTime timestamp;

    public static ErrorResponseDTO of(String message) {
        return ErrorResponseDTO.builder()
                .message(message)
                .status("error")
                .timestamp(LocalDateTime.now()).build();
    }
}
