package ru.vspochernin.otp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.vspochernin.otp.dto.OtpRequest;
import ru.vspochernin.otp.dto.OtpValidationRequest;
import ru.vspochernin.otp.model.OtpCode;
import ru.vspochernin.otp.security.UserDetailsImpl;
import ru.vspochernin.otp.service.OtpService;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpCode> generateOtpCode(
            @Valid @RequestBody OtpRequest request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        OtpCode otpCode = otpService.generateOtpCode(userDetails.getUser(), request.getOperationId());
        return ResponseEntity.ok(otpCode);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateOtpCode(
            @Valid @RequestBody OtpValidationRequest request) {
        boolean isValid = otpService.validateOtpCode(request.getCode(), request.getOperationId());
        return ResponseEntity.ok(isValid);
    }
} 