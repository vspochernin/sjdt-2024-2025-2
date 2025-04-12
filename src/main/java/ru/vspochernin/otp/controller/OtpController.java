package ru.vspochernin.otp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.vspochernin.otp.dto.OtpRequest;
import ru.vspochernin.otp.dto.OtpValidationRequest;
import ru.vspochernin.otp.model.NotificationType;
import ru.vspochernin.otp.model.OtpCode;
import ru.vspochernin.otp.security.UserDetailsImpl;
import ru.vspochernin.otp.service.OtpService;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Slf4j
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpCode> generateOtpCode(
            @Valid @RequestBody OtpRequest request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Запрос на генерацию OTP-кода для пользователя {} через {}", userDetails.getUser().getId(), request.getNotificationType());
        OtpCode otpCode = otpService.generateOtpCode(
                userDetails.getUser().getId().toString(), 
                request.getOperationId(),
                request.getNotificationType());
        log.info("OTP-код успешно сгенерирован: {}", otpCode);
        return ResponseEntity.ok(otpCode);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateOtpCode(
            @Valid @RequestBody OtpValidationRequest request) {
        log.info("Запрос на валидацию OTP-кода {} для пользователя {}", request.getCode(), request.getOperationId());
        boolean isValid = otpService.validateOtpCode(request.getCode(), request.getOperationId());
        log.info("Результат валидации OTP-кода: {}", isValid);
        return ResponseEntity.ok(isValid);
    }
} 