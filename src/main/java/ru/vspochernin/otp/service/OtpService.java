package ru.vspochernin.otp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vspochernin.otp.model.NotificationType;
import ru.vspochernin.otp.model.OtpCode;
import ru.vspochernin.otp.model.OtpConfig;
import ru.vspochernin.otp.model.OtpStatus;
import ru.vspochernin.otp.model.User;
import ru.vspochernin.otp.repository.OtpCodeRepository;
import ru.vspochernin.otp.repository.OtpConfigRepository;
import ru.vspochernin.otp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;
    private final NotificationService notificationService;
    private final Random random = new Random();

    public OtpCode generateOtpCode(String userId, String operationId, NotificationType notificationType) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = generateRandomCode();
        OtpCode otpCode = new OtpCode();
        otpCode.setCode(code);
        otpCode.setStatus(OtpStatus.ACTIVE);
        otpCode.setUser(user);
        otpCode.setOperationId(operationId);
        otpCode.setCreatedAt(LocalDateTime.now());

        otpCodeRepository.save(otpCode);
        notificationService.sendOtpCode(user, code, notificationType);

        return otpCode;
    }

    @Transactional
    public boolean validateOtpCode(String code, String operationId) {
        OtpCode otpCode = otpCodeRepository.findByCodeAndStatus(code, OtpStatus.ACTIVE)
                .orElse(null);

        if (otpCode == null || !otpCode.getOperationId().equals(operationId)) {
            return false;
        }

        OtpConfig config = otpConfigRepository.findFirstByOrderByIdAsc();
        LocalDateTime expirationTime = otpCode.getCreatedAt().plusSeconds(config.getExpirationTimeSeconds());

        if (LocalDateTime.now().isAfter(expirationTime)) {
            otpCode.setStatus(OtpStatus.EXPIRED);
            otpCodeRepository.save(otpCode);
            return false;
        }

        otpCode.setStatus(OtpStatus.USED);
        otpCodeRepository.save(otpCode);
        return true;
    }

    @Scheduled(fixedRate = 60000) // Каждую минуту.
    @Transactional
    public void expireOldOtpCodes() {
        OtpConfig config = otpConfigRepository.findFirstByOrderByIdAsc();
        LocalDateTime expirationTime = LocalDateTime.now().minusSeconds(config.getExpirationTimeSeconds());
        
        List<OtpCode> expiredCodes = otpCodeRepository.findByStatusAndCreatedAtBefore(
                OtpStatus.ACTIVE, expirationTime);
        
        expiredCodes.forEach(code -> code.setStatus(OtpStatus.EXPIRED));
        otpCodeRepository.saveAll(expiredCodes);
    }

    private String generateRandomCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
} 