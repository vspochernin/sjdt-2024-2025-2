package ru.vspochernin.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpValidationRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String operationId;
} 