package ru.alfastrah.library.log.rest.starter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = {LogServerRequest.class, TestRestController.class})
class LogServerRequestTest {
    private static final Logger log = (Logger) LoggerFactory.getLogger(LogServerRequest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        log.detachAppender("listAppender");
    }


    @DisplayName("Тест логирования параметров простого GET запроса")
    @Test
    void healthTest() throws Exception {
        //given
        String url = "/health";
        String response = "ok";

        //when
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(response));

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[GET]", "[]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + url + "]", "[200]", "[" + response + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }

    @DisplayName("Тест логирования параметров простого POST запроса с параметром и RequestBody")
    @Test
    void stringTest() throws Exception {
        //given
        String param = "param";
        String body = "body";
        String response = param + " " + body;
        String url = "/string";

        //when
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .param("param", param)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(response));

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[POST]", "[" + body + "]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + url + "]", "[200]", "[" + response + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }

    @DisplayName("Тест логирования параметров POST запроса с объектом без метода toString() в RequestBody")
    @Test
    void holderTest() throws Exception {
        //given
        String param = "param";
        TestHolder body = new TestHolder("body");
        TestHolder response = new TestHolder(param + " " + body.getValue());
        String url = "/holder";

        //when
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .param("param", param)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)));

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[POST]", "[" + mapper.writeValueAsString(body) + "]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + url + "]", "[200]", "[" + mapper.writeValueAsString(response) + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }
}
