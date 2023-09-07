package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(
        @PathVariable(value="courseId")
        final UUID courseId,
        @RequestBody @Valid
        final ModuleDto dto
    ) {
        final Optional<CourseModel> courseModel = courseService.findById(courseId);
        if (courseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        var model = new ModuleModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setCourse(courseModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(model));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(
        @PathVariable(value="courseId") final UUID courseId,
        @PathVariable(value="moduleId") final UUID moduleId
    ) {
        final Optional<ModuleModel> modelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        moduleService.delete(modelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(
        @PathVariable(value="courseId") final UUID courseId,
        @PathVariable(value="moduleId") final UUID moduleId,
        @RequestBody @Valid ModuleDto dto
    ) {
        final Optional<ModuleModel> modelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        var model = modelOptional.get();
        model.setTitle(dto.getTitle());
        model.setDescription(dto.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(model));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(
        @PathVariable(value="courseId")
        final UUID courseId,
        final SpecificationTemplate.ModuleSpec spec,
        @PageableDefault(sort = "moduleId", direction = Sort.Direction.ASC)
        final Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
            moduleService.findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable)
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(
        @PathVariable(value="courseId") final UUID courseId,
        @PathVariable(value="moduleId") final UUID moduleId
    ) {
        final Optional<ModuleModel> modelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (modelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelOptional.get());
    }
}

