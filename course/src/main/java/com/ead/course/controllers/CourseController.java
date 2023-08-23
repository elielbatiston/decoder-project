package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validation.CourseValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService service;

    @Autowired
    private CourseValidator validator;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody final CourseDto dto, final Errors errors) {
        log.debug("POST saveCourse courseDto received {}", dto.toString());
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
        }
        var model = new CourseModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        service.save(model);
        log.debug("POST saveCourse courseModel saved {}", model.toString());
        log.info("Course saved successfully {}", model.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @DeleteMapping("{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value="courseId") final UUID courseId) {
        log.debug("DELETE deleteCourse courseId received {}", courseId);
        final Optional<CourseModel> modelOptional = service.findById(courseId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        service.delete(modelOptional.get());
        log.debug("DELETE deleteCourse courseId deleted {}", courseId);
        log.info("Course deleted successfully courseId {}", courseId);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("{courseId}")
    public ResponseEntity<Object> updateCourse(
        @PathVariable(value="courseId") final UUID courseId,
        @RequestBody @Valid final CourseDto dto
    ) {
        log.debug("PUT updateCourse courseDto received {} ", dto.toString());
        final Optional<CourseModel> modelOptional = service.findById(courseId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        var model = modelOptional.get();
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setImageUrl(dto.getImageUrl());
        model.setCourseStatus(dto.getCourseStatus());
        model.setCourseLevel(dto.getCourseLevel());
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        log.debug("PUT updateCourse courseId saved {} ", model.getCourseId());
        log.info("Course updated successfully courseId {} ", model.getCourseId());
        return ResponseEntity.status(HttpStatus.OK).body(service.save(model));
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(
        final SpecificationTemplate.CourseSpec spec,
        @PageableDefault(sort = "courseId", direction = Sort.Direction.ASC)
        final Pageable pageable,
        @RequestParam(required = false) UUID userId
    ) {
        final Page<CourseModel> model;
        if (userId != null) {
            model = service.findAll(
                SpecificationTemplate.courseUserId(userId).and(spec),
                pageable
            );
        } else {
            model = service.findAll(spec, pageable);
        }
        return ResponseEntity.status(HttpStatus.OK).body(model);
    }

    @GetMapping("{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value="courseId") final UUID courseId) {
        final Optional<CourseModel> modelOptional = service.findById(courseId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelOptional.get());
    }
}

