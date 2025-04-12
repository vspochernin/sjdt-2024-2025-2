package ru.vspochernin.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vspochernin.otp.model.OtpCode;
import ru.vspochernin.otp.model.OtpStatus;
import ru.vspochernin.otp.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByCodeAndStatus(String code, OtpStatus status);
    List<OtpCode> findByStatusAndCreatedAtBefore(OtpStatus status, LocalDateTime dateTime);
    List<OtpCode> findByUser(User user);
    Optional<OtpCode> findByOperationId(String operationId);
} 