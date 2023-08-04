package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository repository;

    @Override
    public LessonModel save(final LessonModel model) {
        return repository.save(model);
    }

    @Override
    public Optional<LessonModel> findLessonIntoModule(final UUID moduleId, final UUID lessonId) {
        return repository.findLessonIntoModule(moduleId, lessonId);
    }

    @Override
    public void delete(final LessonModel lessonModel) {
        repository.delete(lessonModel);
    }

    @Override
    public Page<LessonModel> findAllByModule(final Specification<LessonModel> spec, final Pageable pageable) {
        return repository.findAll(spec, pageable);
    }
}
