package Hackerton.BE.HarmonAI.service;

import Hackerton.BE.HarmonAI.dto.InfoRequestDTO;
import Hackerton.BE.HarmonAI.dto.InfoResponseDTO;

public interface InfoService {
    InfoResponseDTO processQuestion(InfoRequestDTO requestDTO);
}
