package ru.alfastrah.library.log.rest.starter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestRestController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

    @PostMapping("/string")
    public String string(String param, @RequestBody String body) {
        return param + " " + body;
    }

    @PostMapping("/holder")
    public TestHolder holder(String param, @RequestBody TestHolder body) {
        return new TestHolder(param + " " + body.getValue());
    }
}
