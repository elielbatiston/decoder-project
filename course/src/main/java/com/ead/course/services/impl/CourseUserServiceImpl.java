package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    @Autowired
    private CourseUserRepository repository;

    @Autowired
    private AuthUserClient authUserClient;

    @Override
    public boolean existsByCourseAndUserId(final CourseModel courseModel, final UUID userId) {
        return repository.existsByCourseAndUserId(courseModel, userId);
    }

    @Override
    public CourseUserModel save(final CourseUserModel courseUserModel) {
        return repository.save(courseUserModel);
    }

    @Transactional
    @Override
    public CourseUserModel saveAndSendSubscriptionUserInCourse(final CourseUserModel courseUserModel) {
        final CourseUserModel model = repository.save(courseUserModel);
        authUserClient.postSubscriptionUserInCourse(
            courseUserModel.getCourse().getCourseId(),
            courseUserModel.getUserId()
        );
        return model;
    }

    @Override
    public boolean existsByUserId(final UUID userId) {
        return repository.existsByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteCourseUserByUser(final UUID userId) {
        repository.deleteAllByUserId(userId);
    }
}
