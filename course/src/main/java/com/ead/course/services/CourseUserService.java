package com.ead.course.services;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;

import java.util.UUID;

public interface CourseUserService {

    boolean existsByCourseAndUserId(final CourseModel courseModel, final UUID userId);
    CourseUserModel save(final CourseUserModel courseUserModel);
    CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel);
    boolean existsByUserId(UUID userId);
    void deleteCourseUserByUser(UUID userId);
}
