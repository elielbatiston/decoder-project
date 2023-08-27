package com.ead.notification.controller;

import com.ead.notification.dtos.NotificationDto;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserNotificationController {

    private final NotificationService service;

    public UserNotificationController(final NotificationService service) {
        this.service = service;
    }

    @GetMapping("/users/{userId}/notifications")
    private ResponseEntity<Page<NotificationModel>> getAllNotificationsByUser(
        @PathVariable(value = "userId") final UUID userId,
        @PageableDefault(sort = "notificationId", direction = Sort.Direction.ASC) final Pageable pageable
    ) {
        final var notification = service.findAllNotifiationsByUser(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(notification);
    }

    @PutMapping("/users/{userId}/notifications/{notificationId}")
    private ResponseEntity<Object> updateNotification(
            @PathVariable(value = "userId") final UUID userId,
            @PathVariable(value = "notificationId") final UUID notificationId,
            @RequestBody @Valid final NotificationDto dto
    ) {
        final Optional<NotificationModel> notificationModelOptional = service.findByNotificationIdAndUserId(
            notificationId,
            userId
        );
        if (notificationModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
        notificationModelOptional.get().setNotificationStatus(dto.getNotificationStatus());
        service.saveNotification(notificationModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(notificationModelOptional.get());
    }
}
