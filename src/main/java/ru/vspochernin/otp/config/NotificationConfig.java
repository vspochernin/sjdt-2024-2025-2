package ru.vspochernin.otp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:email.properties")
@PropertySource("classpath:sms.properties")
@Getter
public class NotificationConfig {

    // Конфигурация Email.
    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private String smtpStarttls;

    // Конфигурация SMS.
    @Value("${smpp.host}")
    private String smppHost;

    @Value("${smpp.port}")
    private String smppPort;

    @Value("${smpp.system_id}")
    private String smppSystemId;

    @Value("${smpp.password}")
    private String smppPassword;

    @Value("${smpp.system_type}")
    private String smppSystemType;

    @Value("${smpp.source_addr}")
    private String smppSourceAddress;
} 