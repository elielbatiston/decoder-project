package com.ead.authuser.services.impl;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dtos.UserEventDto;
import com.ead.authuser.enums.ActionType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.publishers.UserEventPublisher;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private UserEventPublisher userEventPublisher;

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
        repository.delete(userModel);
    }

    @Override
    public UserModel save(final UserModel model) {
        return repository.save(model);
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

    @Transactional
    @Override
    public UserModel saveUser(final UserModel userModel) {
        final UserModel userModelSaved = save(userModel);
        final UserEventDto dto = userModelSaved.convertToUserEventDto();
        userEventPublisher.publishUserEvent(dto, ActionType.CREATE);
        return userModelSaved;
    }

    @Transactional
    @Override
    public void deleteUser(final UserModel userModel) {
        delete(userModel);
        final UserEventDto dto = userModel.convertToUserEventDto();
        userEventPublisher.publishUserEvent(dto, ActionType.DELETE);
    }

    @Transactional
    @Override
    public UserModel updateUser(final UserModel userModel) {
        final UserModel userModelSaved = save(userModel);
        final UserEventDto dto = userModelSaved.convertToUserEventDto();
        userEventPublisher.publishUserEvent(dto, ActionType.UPDATE);
        return userModelSaved;
    }

    @Override
    public UserModel updatePassword(final UserModel userModel) {
        return save(userModel);
    }
}
