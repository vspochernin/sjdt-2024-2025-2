package ru.vspochernin.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.vspochernin.otp.model.NotificationType;

@Data
public class OtpRequest {
    @NotBlank
    private String operationId;
    
    private NotificationType notificationType;
} 