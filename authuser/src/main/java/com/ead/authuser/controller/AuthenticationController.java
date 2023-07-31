package com.ead.authuser.controller;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private UserService service;

    @PostMapping("signup")
    public ResponseEntity<Object> registerUser(
        @RequestBody
        @Validated(UserDto.UserView.RegistrationPost.class)
        @JsonView(UserDto.UserView.RegistrationPost.class)
        UserDto dto
    ) {
        if (service.existsByUsername(dto.getUserName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken!");
        }
        if (service.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: E-mail is already taken!");
        }
        var model = new UserModel();
        BeanUtils.copyProperties(dto, model);
        model.setUserStatus(UserStatus.ACTIVE);
        model.setUserType(UserType.STUDENT);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        service.save(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
}