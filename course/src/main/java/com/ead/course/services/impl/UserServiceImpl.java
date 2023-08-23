package com.ead.course.services.impl;

import com.ead.course.models.UserModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Page<UserModel> findAll(final Specification<UserModel> spec, final Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public UserModel save(final UserModel model) {
        return repository.save(model);
    }

    @Transactional
    @Override
    public void delete(final UUID userId) {
        courseRepository.deleteCourseUserByUser(userId);
        repository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(final UUID userInstructor) {
        return repository.findById(userInstructor);
    }
}
