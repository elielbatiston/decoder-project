package com.ead.authuser.controller;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.UserCourseDto;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserCourseService;
import com.ead.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserCourseController {

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseDto>> getAllCoursesByUser(
        @PageableDefault(sort = "courseId", direction = Sort.Direction.ASC) final Pageable pageable,
        @PathVariable(value = "userId") final UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(courseClient.getAllCoursesByUser(userId, pageable));
    }

    @PostMapping("/users/{userId}/courses/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
        @PathVariable(value = "userId") final UUID userId,
        @Valid @RequestBody UserCourseDto userCourseDto
    ) {
        Optional<UserModel> model = userService.findById(userId);
        if (model.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (userCourseService.existsByUserAndCourseId(model.get(), userCourseDto.getCourseId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
        }
        final UserCourseModel userCourseModel = userCourseService.save(
            model.get().convertToUserCourseModel(userCourseDto.getCourseId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userCourseModel);
    }

    @DeleteMapping("/users/courses/{courseId}")
    public ResponseEntity<Object> deleteUserCouseByCourse(
        @PathVariable(value = "courseId") UUID courseId
    ) {
        if (!userCourseService.existsByCourseId(courseId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserCourse not found");
        }
        userCourseService.deleteUserCourseByCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body("UserCourse deleted successfully");
    }
}
