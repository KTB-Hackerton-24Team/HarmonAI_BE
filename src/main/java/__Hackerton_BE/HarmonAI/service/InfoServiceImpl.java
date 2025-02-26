package __Hackerton_BE.HarmonAI.service;


import __Hackerton_BE.HarmonAI.dto.InfoRequestDTO;
import __Hackerton_BE.HarmonAI.dto.InfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    @Override
    public InfoResponseDTO processQuestion(InfoRequestDTO requestDTO) {

        log.info("위도: {}, 경도:{}, 입력:{}",
                requestDTO.getLatitude(),
                requestDTO.getLongitude(),
                requestDTO.getQuestion());

        InfoResponseDTO responseDTO = new InfoResponseDTO();
        responseDTO.setLatitude(requestDTO.getLatitude());
        responseDTO.setLongitude(requestDTO.getLongitude());
        responseDTO.setQuestion(requestDTO.getQuestion());

        return responseDTO;
    }

    private String callInfoService(String question, double latitude, double longitude) {
        // 여기에 실제 Info API 호출 로직을 구현
        // 위도, 경도, 질문을 AI 모델에 전달하고 응답을 받는 로직

        return "위도 : " + latitude + "경도 : " + longitude + "질문 : " + question;
    }
}
