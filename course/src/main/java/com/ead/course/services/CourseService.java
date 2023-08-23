package com.ead.course.services;

import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    void delete(final CourseModel courseModel);
    CourseModel save(final CourseModel model);
    Optional<CourseModel> findById(final UUID courseId);
    Page<CourseModel> findAll(final Specification<CourseModel> spec, final Pageable pageable);
    boolean existsByCourseAndUser(final UUID courseId, final UUID userId);
    void saveSubscriptionUserInCourse(final UUID courseId, final UUID userId);
}
