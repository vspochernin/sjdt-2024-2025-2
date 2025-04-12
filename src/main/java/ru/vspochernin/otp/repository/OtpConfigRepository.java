package ru.vspochernin.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vspochernin.otp.model.OtpConfig;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {
    OtpConfig findFirstByOrderByIdAsc();
} 