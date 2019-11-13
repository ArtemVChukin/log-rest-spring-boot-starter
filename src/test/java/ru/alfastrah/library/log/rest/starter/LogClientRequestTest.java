package ru.alfastrah.library.log.rest.starter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static ru.alfastrah.library.log.rest.starter.TestRestClient.HOST;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LogRestTemplateCustomizer.class, LogClientRequest.class, TestRestClient.class})
@AutoConfigureWebClient(registerRestTemplate = true)
class LogClientRequestTest {
    private static final Logger log = (Logger) LoggerFactory.getLogger(LogClientRequest.class);

    @Autowired
    TestRestClient restClient;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    private MockRestServiceServer mockServer;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
        mockServer = MockRestServiceServer.bindTo(restTemplate).bufferContent().build();
    }

    @AfterEach
    void tearDown() {
        log.detachAppender("listAppender");
    }

    @DisplayName("Тест логирования параметров простого GET запроса")
    @Test
    void health() {
        //given
        String response = "ok";
        String url = "/health";
        String fullUrl = HOST + url;
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(fullUrl))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        //then
        String restClientResponse = restClient.health();

        //when
        assertEquals(response, restClientResponse);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[GET]", "[]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + fullUrl + "]", "[200]", "[" + response + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }

    @DisplayName("Тест логирования параметров простого POST запроса с параметром и RequestBody")
    @Test
    void string() {
        //given
        String param = "param";
        String body = "body";
        String response = param + " " + body;
        String url = "/string";
        String fullUrl = HOST + url + "?param=" + param;
        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(fullUrl))
                .andExpect(queryParam("param", param))
                .andExpect(content().string(body))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        //when
        String restClientResponse = restClient.string(param, body);

        //then
        assertEquals(response, restClientResponse);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[POST]", "[" + body + "]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + fullUrl + "]", "[200]", "[" + response + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }

    @DisplayName("Тест логирования параметров POST запроса с объектом без метода toString() в RequestBody")
    @Test
    void holder() throws JsonProcessingException {
        //given
        String param = "param";
        TestHolder body = new TestHolder("body");
        TestHolder response = new TestHolder(param + " " + body);
        String url = "/holder";
        String fullUrl = HOST + url + "?param=" + param;
        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(fullUrl))
                .andExpect(queryParam("param", param))
                .andExpect(content().string(mapper.writeValueAsString(body)))
                .andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        //when
        TestHolder restClientResponse = restClient.holder(param, body);

        //then
        assertEquals(response.getValue(), restClientResponse.getValue());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertTrue(Stream.of("url", "start", "method", "request_body", "[" + url + "]", "[POST]", "[" + mapper.writeValueAsString(body) + "]")
                .allMatch(word -> logsList.get(0).getMessage().contains(word)));
        assertTrue(Stream.of("url", "duration", "status", "response_body", "[" + fullUrl + "]", "[200]", "[" + mapper.writeValueAsString(response) + "]")
                .allMatch(word -> logsList.get(1).getMessage().contains(word)));

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }
}
