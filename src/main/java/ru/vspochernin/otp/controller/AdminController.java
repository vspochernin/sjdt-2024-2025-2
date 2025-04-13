package ru.vspochernin.otp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vspochernin.otp.model.OtpConfig;
import ru.vspochernin.otp.model.User;
import ru.vspochernin.otp.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public OtpConfig updateOtpConfig(@RequestBody OtpConfig config) {
        log.info("Admin запросил обновление конфигурации OTP: {}", config);
        config.setId(1L);
        OtpConfig updatedConfig = adminService.updateOtpConfig(config);
        log.info("Конфигурация OTP успешно обновлена: {}", updatedConfig);
        return updatedConfig;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        log.info("Admin запросил список всех пользователей");
        List<User> users = adminService.getAllUsers();
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String userId) {
        log.info("Admin запросил удаление пользователя с ID: {}", userId);
        adminService.deleteUser(userId);
        log.info("Пользователь с ID {} успешно удален", userId);
    }
} 