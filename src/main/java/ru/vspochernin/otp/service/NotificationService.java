package ru.vspochernin.otp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationConfig config;

    // Конфигурация Telegram.
    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    public void sendOtpCode(User user, String code, NotificationType type) {
        log.info("Отправка OTP кода через {} для пользователя {}", type, user.getId());

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
        Connection connection;
        Session session;

        try {
            String host = config.getSmppHost();
            int port = Integer.parseInt(config.getSmppPort());
            String systemId = config.getSmppSystemId();
            String password = config.getSmppPassword();
            String systemType = config.getSmppSystemType();
            String sourceAddr = config.getSmppSourceAddress();

            // 1. Установка соединения.
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);

            // 2. Подготовка Bind Request.
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4.
            bindRequest.setAddressRange(sourceAddr);

            // 3. Выполнение привязки.
            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Ошибка привязки: " + bindResponse.getCommandStatus());
            }

            // 4. Отправка сообщения
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddr);
            submitSM.setDestAddr(phone);
            submitSM.setShortMessage("Ваш код: " + code);

            session.submit(submitSM);
            log.info("SMS с кодом {} успешно отправлено на номер {}", code, phone);
        } catch (Exception e) {
            log.error("Ошибка при отправке SMS: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить SMS", e);
        }
    }

    private void sendTelegram(String telegramUsername, String code) {
        // Получаем chatId для пользователя.
        String chatId = getChatId(telegramUsername);
        if (chatId == null) {
            throw new RuntimeException("Не удалось найти chatId для пользователя " + telegramUsername);
        }

        String message = String.format("Пользователь %s, ваш код подтверждения: %s", telegramUsername, code);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramBotToken,
                chatId,
                urlEncode(message));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Ошибка API Telegram. Код статуса: {}", statusCode);
                    throw new RuntimeException("Ошибка при отправке сообщения в Telegram");
                } else {
                    log.info("Сообщение в Telegram успешно отправлено пользователю {}", telegramUsername);
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при отправке сообщения в Telegram: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение в Telegram", e);
        }
    }

    private String getChatId(String telegramUsername) {
        String url = String.format("https://api.telegram.org/bot%s/getUpdates", telegramBotToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());

                    // Парсим JSON ответ.
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(responseBody);

                    if (root.has("result")) {
                        JsonNode result = root.get("result");
                        for (JsonNode update : result) {
                            if (update.has("message")) {
                                JsonNode message = update.get("message");
                                if (message.has("from") && message.has("chat")) {
                                    JsonNode from = message.get("from");
                                    if (from.has("username") &&
                                        from.get("username").asText().equals(telegramUsername)) {
                                        return message.get("chat").get("id").asText();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при получении chatId: {}", e.getMessage(), e);
        }
        return null;
    }

    private void saveToFile(String code) {
        try {
            Path path = Paths.get("otp_codes.txt");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (FileWriter writer = new FileWriter(path.toFile(), true)) {
                writer.write(String.format("OTP код: %s, Время: %s%n", code, java.time.LocalDateTime.now()));
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