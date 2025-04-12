package ru.vspochernin.otp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vspochernin.otp.model.OtpConfig;
import ru.vspochernin.otp.model.User;
import ru.vspochernin.otp.repository.OtpConfigRepository;
import ru.vspochernin.otp.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final OtpConfigRepository otpConfigRepository;

    @Transactional
    public OtpConfig updateOtpConfig(OtpConfig config) {
        log.info("Обновление конфигурации OTP: {}", config);
        return otpConfigRepository.save(config);
    }

    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAllByRoleNot("ADMIN");
    }

    @Transactional
    public void deleteUser(String userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        userRepository.deleteById(UUID.fromString(userId));
    }
} 