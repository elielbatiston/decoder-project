package com.ead.authuser.services.impl;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.services.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserCourseServiceImpl implements UserCourseService {

    @Autowired
    private UserCourseRepository repository;

    @Override
    public boolean existsByUserAndCourseId(UserModel userModel, UUID courseId) {
        return repository.existsByUserAndCourseId(userModel, courseId);
    }

    @Override
    public UserCourseModel save(final UserCourseModel userCourseModel) {
        return repository.save(userCourseModel);
    }
}
