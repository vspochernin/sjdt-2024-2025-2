package ru.vspochernin.otp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vspochernin.otp.config.NotificationConfig;
import ru.vspochernin.otp.model.NotificationType;
import ru.vspochernin.otp.model.User;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationConfig config;

    // Конфигурация Telegram.
    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String telegramChatId;

    public void sendOtpCode(User user, String code, NotificationType type) {
        log.info("Sending OTP code via {} for user {}", type, user.getId());
        
        switch (type) {
            case EMAIL -> sendEmail(user.getEmail(), code);
            case SMS -> sendSms(user.getPhone(), code);
            case TELEGRAM -> sendTelegram(user.getTelegram(), code);
            case FILE -> saveToFile(code);
        }
    }

    private void sendEmail(String email, String code) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getSmtpHost());
            props.put("mail.smtp.port", config.getSmtpPort());
            props.put("mail.smtp.auth", config.getSmtpAuth());
            props.put("mail.smtp.starttls.enable", config.getSmtpStarttls());

            javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getEmailUsername(), config.getEmailPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getEmailFrom()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Ваш OTP-код");
            message.setText("Ваш код подтверждения: " + code);

            Transport.send(message);
            log.info("Email с OTP-кодом успешно отправлен на {}", email);
        } catch (MessagingException e) {
            log.error("Ошибка при отправке Email: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить Email", e);
        }
    }

    private void sendSms(String phone, String code) {
        log.info("Отправка SMS на номер {} с кодом {}", phone, code);
        log.info("SMS с кодом {} успешно отправлено на номер {}", code, phone);
    }

    private void sendTelegram(String chatUsername, String code) {
        String message = String.format("Пользователь %s, ваш код подтверждения: %s", chatUsername, code);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramBotToken,
                telegramChatId,
                urlEncode(message));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Ошибка API Telegram. Код статуса: {}", statusCode);
                    throw new RuntimeException("Ошибка при отправке сообщения в Telegram");
                } else {
                    log.info("Сообщение в Telegram успешно отправлено пользователю {}", chatUsername);
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при отправке сообщения в Telegram: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение в Telegram", e);
        }
    }

    private void saveToFile(String code) {
        try {
            Path path = Paths.get("otp_codes.txt");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (FileWriter writer = new FileWriter(path.toFile(), true)) {
                writer.write(String.format("OTP Code: %s, Time: %s%n", code, java.time.LocalDateTime.now()));
                log.info("OTP-код успешно сохранен в файл {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Ошибка при сохранении OTP-кода в файл: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось сохранить OTP-код в файл", e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
} 