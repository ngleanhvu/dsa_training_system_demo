package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/users")
    public ResponseEntity<?> getNotificationsUser(@RequestHeader("Authorization") String token,
                                                  @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                  @RequestParam(required = false, defaultValue = "1") int page,
                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                  @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(Math.max(page-1, 0));
        pagingSearch.setSize(size);
        pagingSearch.setDirection(sortDir);
        var usersId = jwtUtil.getUserIdFromToken(token);
        var response = notificationService.getNotifications(pagingSearch,
                usersId);
        var apiResponse = ApiResponse.builder()
                .message("Get notifications")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{notificationId}/users")
    public ResponseEntity<?> deleteNotification(@PathVariable("notificationId") Integer notificationId,
                                                @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        notificationService.deleteNotificationByUser(userId, notificationId);
        var apiResponse = ApiResponse.builder()
                .message("Delete notification")
                .status(HttpStatus.NO_CONTENT.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/users")
    public ResponseEntity<?> markReadNotification(@PathVariable("notificationId") Integer notificationId,
                                                  @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        notificationService.markRead(userId, notificationId);
        var apiResponse = ApiResponse.builder()
                .message("Mark notification")
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/users/is-not-read")
    public ResponseEntity<?> getQuantityNotificationUserIsNotRead(@RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        var response = notificationService.countNotificationUserIsNotRead(userId);
        var apiResponse = ApiResponse.builder()
                .message("Count notification user is read")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
