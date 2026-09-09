package com.homelexin.controller;

import com.homelexin.dto.NotificationDTO;
import com.homelexin.entity.User;
import com.homelexin.service.NotificationService;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
            @RequestAttribute("currentUser") User currentUser) {
        try {
            List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser.getId(), currentUser);
            return ResponseEntity.ok(notifications);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @RequestAttribute("currentUser") User currentUser) {
        try {
            List<NotificationDTO> unreadNotifications = notificationService.getUnreadNotifications(currentUser.getId(), currentUser);
            return ResponseEntity.ok(unreadNotifications);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            NotificationDTO readNotification = notificationService.markAsRead(id, currentUser);
            return ResponseEntity.ok(readNotification);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @RequestAttribute("currentUser") User currentUser) {
        try {
            notificationService.markAllAsRead(currentUser.getId(), currentUser);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        try {
            notificationService.deleteNotification(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
