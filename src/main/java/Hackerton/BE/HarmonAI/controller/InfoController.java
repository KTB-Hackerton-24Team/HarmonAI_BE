package Hackerton.BE.HarmonAI.controller;

import Hackerton.BE.HarmonAI.dto.InfoRequestDTO;
import Hackerton.BE.HarmonAI.dto.InfoResponseDTO;
import Hackerton.BE.HarmonAI.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @PostMapping("/current")
    public ResponseEntity<InfoResponseDTO> processInfoQuestion(@RequestBody InfoRequestDTO requestDto) {
        InfoResponseDTO response = infoService.processQuestion(requestDto);
        System.out.println("response = " + requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
