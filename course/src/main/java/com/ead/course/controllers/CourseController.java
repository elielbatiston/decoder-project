package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService service;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid final CourseDto dto) {
        var model = new CourseModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(model));
    }

    @DeleteMapping("{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value="courseId") final UUID courseId) {
        final Optional<CourseModel> modelOptional = service.findById(courseId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        service.delete(modelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("{courseId}")
    public ResponseEntity<Object> updateCourse(
        @PathVariable(value="courseId") final UUID courseId,
        @RequestBody @Valid final CourseDto dto
    ) {
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
        return ResponseEntity.status(HttpStatus.OK).body(service.save(model));
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(
        final SpecificationTemplate.CourseSpec spec,
        @PageableDefault(sort = "courseId", direction = Sort.Direction.ASC)
        final Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(spec, pageable));
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

