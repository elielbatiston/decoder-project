package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URL_AUTHUSER;

    public Page<UserDto> getAllUsersByCourse(final UUID courseId, final Pageable pageable) {
        List<UserDto> searchResult;
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        final String url = REQUEST_URL_AUTHUSER + utilsService.createUrlGetAllUsersByCourse(courseId, pageable);

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType = new ParameterizedTypeReference<>() {};
            result = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType
            );
            searchResult = result.getBody().getContent();

            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request /users {} ", e);
        }
        log.info("Ending request /users userId {} ", courseId);
        return result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(final UUID userId) {
        final String url = REQUEST_URL_AUTHUSER + "/users/" + userId;
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

    public void postSubscriptionUserInCourse(final UUID courseId, final UUID userId) {
        final String url = REQUEST_URL_AUTHUSER + "/users/" + userId + "/courses/subscription";
        final var courseUserDto = new CourseUserDto();
        courseUserDto.setUserId(userId);
        courseUserDto.setCourseId(courseId);
        restTemplate.postForObject(url, courseUserDto, String.class);
    }
}