package com.ead.authuser.services.impl;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private CourseClient courseClient;

    @Override
    public List<UserModel> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<UserModel> findById(final UUID userId) {
        return repository.findById(userId);
    }

    @Override
    public void delete(final UserModel userModel) {
        boolean deleteUserCourseInCourse = false;
        final List<UserCourseModel> userCourseModels = userCourseRepository.findAllUserCourseIntoUser(userModel.getUserId());
        if (!userCourseModels.isEmpty()) {
            userCourseRepository.deleteAll(userCourseModels);
            deleteUserCourseInCourse = true;
        }
        repository.delete(userModel);
        if (deleteUserCourseInCourse) {
            courseClient.deleteUserInCourse(userModel.getUserId());
        }
    }

    @Override
    public void save(final UserModel model) {
        repository.save(model);
    }

    @Override
    public boolean existsByUsername(final String userName) {
        return repository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Page<UserModel> findAll(final Specification<UserModel> spec, final Pageable pageable) {
        return repository.findAll(spec, pageable);
    }
}
