package com.ead.authuser.services.impl;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.services.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class UserCourseServiceImpl implements UserCourseService {

    @Autowired
    private UserCourseRepository repository;

    @Override
    public boolean existsByUserAndCourseId(final UserModel userModel, final UUID courseId) {
        return repository.existsByUserAndCourseId(userModel, courseId);
    }

    @Override
    public UserCourseModel save(final UserCourseModel userCourseModel) {
        return repository.save(userCourseModel);
    }

    @Override
    public boolean existsByCourseId(final UUID courseId) {
        return repository.existsByCourseId(courseId);
    }

    @Transactional
    @Override
    public void deleteUserCourseByCourse(final UUID courseId) {
        repository.deleteAllByCourseId(courseId);
    }
}
