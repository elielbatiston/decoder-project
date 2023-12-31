package com.ead.authuser.controller;

import com.ead.authuser.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.configs.security.UserDetailsImpl;
import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationCurrentUserService authenticationCurrentUserService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
        SpecificationTemplate.UserSpec spec,
        @PageableDefault(sort = "userId", direction = Sort.Direction.ASC)
        final Pageable pageable,
        final Authentication authentication
    ) {
        final UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Authentication {}", userDetails.getUsername());
        final Page<UserModel> model = service.findAll(spec, pageable);
        if (!model.isEmpty()) {
            for (final UserModel user : model.toList()) {
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(model);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("{userId}")
    public ResponseEntity<Object> getOneUser(
        @PathVariable(value = "userId")
        final UUID userId
    ) {
        final UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        if (currentUserId.equals(userId)) {
            Optional<UserModel> model = service.findById(userId);
            if (model.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(model.get());
            }
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(
        @PathVariable(value = "userId")
        final UUID userId
    ) {
        log.debug("DELETE deleteUser userId received {} ", userId);
        Optional<UserModel> model = service.findById(userId);
        if (model.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else {
            service.deleteUser(model.get());
            log.debug("DELETE deleteUser userId saved {} ", userId);
            log.info("User deleted successfully userId {} ", userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        }
    }

    @PutMapping("{userId}")
    public ResponseEntity<Object> updateUser(
        @PathVariable(value = "userId")
        final UUID userId,
        @RequestBody
        @Validated(UserDto.UserView.UserPut.class)
        @JsonView(UserDto.UserView.UserPut.class)
        final UserDto dto
    ) {
        log.debug("PUT updateUser userDto received {} ", dto.toString());
        Optional<UserModel> model = service.findById(userId);
        if (model.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else {
            var userModel = model.get();
            userModel.setFullName(dto.getFullName());
            userModel.setPhoneNumber(dto.getPhoneNumber());
            userModel.setCpf(dto.getCpf());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            service.updateUser(userModel);
            log.debug("PUT updateUser userModel userId {} ", userModel.getUserId());
            log.info("User updated successfully userId {} ", userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("{userId}/password")
    public ResponseEntity<Object> updatePassword(
        @PathVariable(value = "userId")
        final UUID userId,
        @RequestBody
        @Validated(UserDto.UserView.PasswordPut.class)
        @JsonView(UserDto.UserView.PasswordPut.class)
        final UserDto dto
    ) {
        Optional<UserModel> model = service.findById(userId);
        if (model.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var user = model.get();
        if (!user.getPassword().equals(dto.getOldPassword())) {
            log.warn("Mismatched old password userId {}", user.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        } else {
            user.setPassword(dto.getPassword());
            user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            service.updatePassword(user);
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        }
    }

    @PutMapping("{userId}/image")
    public ResponseEntity<Object> updateImage(
        @PathVariable(value = "userId")
        final UUID userId,
        @RequestBody
        @Validated(UserDto.UserView.ImagePut.class)
        @JsonView(UserDto.UserView.ImagePut.class)
        final UserDto dto
    ) {
        Optional<UserModel> model = service.findById(userId);
        if (model.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else {
            var user = model.get();
            user.setImageUrl(dto.getImageUrl());
            user.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            service.updateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
    }
}
