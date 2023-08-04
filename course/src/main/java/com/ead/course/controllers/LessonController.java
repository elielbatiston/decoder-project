package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
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
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService lessonService;

    @PostMapping("modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(
        @PathVariable(value="moduleId")
        final UUID moduleId,
        @RequestBody @Valid
        final LessonDto dto
    ) {
        final Optional<ModuleModel> moduleModel = moduleService.findById(moduleId);
        if (moduleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }
        var model = new LessonModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setModule(moduleModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(model));
    }

    @DeleteMapping("modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(
        @PathVariable(value="moduleId") final UUID moduleId,
        @PathVariable(value="lessonId") final UUID lessonId
    ) {
        final Optional<LessonModel> modelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        lessonService.delete(modelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PutMapping("modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(
        @PathVariable(value="moduleId") final UUID moduleId,
        @PathVariable(value="lessonId") final UUID lessonId,
        @RequestBody @Valid final LessonDto dto
    ) {
        final Optional<LessonModel> modelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        var model = modelOptional.get();
        model.setTitle(dto.getTitle());
        model.setDescription(dto.getDescription());
        model.setVideoUrl(dto.getVideoUrl());
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(model));
    }

    @GetMapping("modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(
        @PathVariable(value="moduleId")
        final UUID moduleId,
        final SpecificationTemplate.LessonSpec spec,
        @PageableDefault(sort = "lessonId", direction = Sort.Direction.ASC)
        final Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
            lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable)
        );
    }

    @GetMapping("modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(
        @PathVariable(value="moduleId") final UUID moduleId,
        @PathVariable(value="lessonId") final UUID lessonId
    ) {
        final Optional<LessonModel> modelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelOptional.get());
    }
}
