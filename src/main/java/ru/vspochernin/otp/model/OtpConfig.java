package ru.vspochernin.otp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_config")
@Data
@NoArgsConstructor
public class OtpConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer codeLength;

    @Column(nullable = false)
    private Integer expirationTimeSeconds;
} 