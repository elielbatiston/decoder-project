package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${ead.api.url.course}")
    private String REQUEST_URI_COURSE = "http://localhost:8082";

//    @Retry(name = "retryInstance", fallbackMethod = "retryFallback")
//    @CircuitBreaker(name = "circuitbreakInstance", fallbackMethod = "circuitbreakerfallback")
    @CircuitBreaker(name = "circuitbreakInstance")
    public Page<CourseDto> getAllCoursesByUser(
        final UUID userId,
        final Pageable pageable,
        final String token
    ) {
        List<CourseDto> searchResult;
        ResponseEntity<ResponsePageDto<CourseDto>> result = null;
        final String url = REQUEST_URI_COURSE + utilsService.createUrl(userId, pageable);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        final HttpEntity<String> requestEntity = new HttpEntity<>("parameters", headers);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        System.out.println("------ Start Request ao Course MicroService -------- ");
        ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType =
                new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
        result = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            responseType
        );
        searchResult = result.getBody().getContent();
        log.debug("Response Number of Elements: {} ", searchResult.size());
        log.info("Ending request /courses userId {} ", userId);
        return result.getBody();
    }

    public Page<CourseDto> circuitbreakerfallback(final UUID userId, final Pageable pageable, Throwable t) {
        log.error("Inside circuit breaker fallback, cause - {}", t.toString());
        final List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

    public Page<CourseDto> retryFallback(final UUID userId, final Pageable pageable, Throwable t) {
        log.error("Inside retry retryFallback, cause - {}", t.toString());
        final List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }
}
