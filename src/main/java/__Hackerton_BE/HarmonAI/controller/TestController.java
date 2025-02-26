package __Hackerton_BE.HarmonAI.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/api/v1/test")
    public ResponseEntity<Object> testapi() {
        String result = "Hello World!";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
