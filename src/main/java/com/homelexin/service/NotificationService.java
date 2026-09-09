package com.homelexin.service;

import com.homelexin.dto.NotificationDTO;
import com.homelexin.entity.Notification;
import com.homelexin.entity.User;
import com.homelexin.repository.NotificationRepository;
import com.homelexin.exception.ResourceNotFoundException;
import com.homelexin.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationDTO createNotification(Long userId, String title, String message, 
                                              Notification.NotificationType type) {
        User user = userService.findById(userId);

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getId()) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to view these notifications");
        }

        User user = userService.findById(userId);
        return notificationRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getId()) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to view these notifications");
        }

        User user = userService.findById(userId);
        return notificationRepository.findByUserAndRead(user, false).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public int getUnreadCount(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getId()) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to access this information");
        }

        User user = userService.findById(userId);
        return notificationRepository.findByUserAndRead(user, false).size();
    }

    public NotificationDTO markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to mark this notification as read");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    public void markAllAsRead(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getId()) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to perform this action");
        }

        User user = userService.findById(userId);
        List<Notification> unreadNotifications = notificationRepository.findByUserAndRead(user, false);
        
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    public void deleteAllNotifications(Long userId, User currentUser) {
        if (!userId.equals(currentUser.getId()) && !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to perform this action");
        }

        User user = userService.findById(userId);
        List<Notification> notifications = notificationRepository.findByUser(user);
        notificationRepository.deleteAll(notifications);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
