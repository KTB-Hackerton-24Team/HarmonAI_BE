package __Hackerton_BE.HarmonAI.service;

import __Hackerton_BE.HarmonAI.dto.InfoRequestDTO;
import __Hackerton_BE.HarmonAI.dto.InfoResponseDTO;

public interface InfoService {
    InfoResponseDTO processQuestion(InfoRequestDTO requestDTO);
}
