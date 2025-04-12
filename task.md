# Защита OTP-кодом - задание

## О проекте

Планируется разработать простое и удобное backend-приложение, которое поможет защитить операции с помощью временных кодов. Основная цель сервиса — обеспечить безопасность при выполнении различных действий, требующих подтверждения.

Сервис будет:

- Создавать операции на защиту — пользователи смогут инициировать защиту своих операций, чтобы добавить дополнительный уровень безопасности.
- Генерировать защитный код — для каждой операции будет автоматически создаваться уникальный код, который будет использоваться для подтверждения.
- Отправлять код — пользователи смогут получать защитные коды через разные каналы:
  - SMS - необходимо использовать эмулятор для отправки SMS, чтобы протестировать функционал.
  - Email - коды можно будет отправлять как на эмулятор почты.
  - Telegram - с помощью Telegram API необходимо будет создать бота, который будет отправлять коды пользователям.
- Проверять код — при выполнении операции пользователи смогут вводить полученный код, который будет проверяться на правильность.
- Настраивать время жизни и длину кода — каждый код будет иметь ограниченное время действия. Администратор сможет настраивать, как долго код будет действовать и сколько цифр он будет содержать.

Вдохновение для проекта можно взять из концепции TOTP (Time-based One-Time Password), что поможет сделать защиту более надежной.

Таким образом будет создан удобный сервис, который поможет пользователям безопасно выполнять операции, используя временные коды для подтверждения.

## Требования к базе данных и работе с ней

База данных должна быть реализована с помощью PostgreSQL 17, взаимодействие с базой данных должен быть реализован через JDBC.

Должно быть реализовано минимум 3 таблицы:

- Пользователи (хранит логин пользователя, его пароль в зашифрованном виде, а также его роль).
- Конфигурация OTP-кода (количество записей в ней никогда не должно превышать 1).
- Таблица OTP-кодов (может содержать идентификатор операции в привязке к OTP-коду, но также допускается вынести логику работы с операциями в отдельную таблицу).

OTP-код должен иметь минимум 3 статуса:

1. ACTIVE (код активен);
2. EXPIRED (код просрочен);
3. USED (код прошел валидацию и был использован).

## Требования к API

Для регистрации и аутентификации пользователей необходимо реализовать соответствующее API, которое должно минимально выполнять следующие операции:

- Регистрация нового пользователя. У пользователей может быть две роли: либо администратор, либо простой пользователь. Если администратор уже существует, то регистрация второго администратора должна быть невозможной.
- Логин зарегистрированного пользователя. Данная операция должна возвращать токен с ограниченным сроком действия для осуществления аутентификации и авторизации пользователя (предполагается использование JSON Web Token).

У администратора должна быть свое отдельное API, которое позволяет как минимум:

- Менять конфигурацию OTP-кодов (время жизни и количество знаков в коде).
- Получать список всех пользователей кроме администраторов.
- Удалять пользователей и привязанные к ним OTP-коды.

API пользователя минимально должно реализовывать следующие функции:

- Генерация OTP-кода привязанного к операции либо к ее идентификатору и рассылка его тремя способами либо сохранение сгенерированного кода в файл в корне проекта.
- Валидация OTP-кода, который был выслан пользователю по одному из каналов.

Пользователи, не являющиеся администраторами, не должны иметь доступа к API администратора.

## Требования к каналам рассылки сгенерированных кодов

Пользователи смогут получать защитные коды через различные каналы, что обеспечит гибкость и удобство в использовании сервиса. Для реализации этой функциональности необходимо учесть следующие требования:

- Отправка кода по SMS - предстоит использовать эмулятор для отправки SMS, чтобы протестировать функционал. Это позволит имитировать процесс получения кодов без необходимости использования реальных SMS.
- Отправка кода по Email - коды можно будет отправлять как на эмулятор почты.
- Отправка кода через Telegram - с помощью Telegram API нужно будет создать бота, который будет отправлять коды пользователям. Это позволит мгновенно доставлять коды через популярное приложение для обмена сообщениями.
- Сохранение кода в файл - здесь нужно будет реализовать возможность сохранения сгенерированных кодов в файл.

## Требование к структуре приложения

Приложение должно иметь три основных слоя.

1. Слой API, содержащий обработчики HTTP-запросов. Слой API (хэндлеров или контроллеров) должен быть выполнен с помощью Spring MVC.
2. Слой сервисов, содержащий в себе основную бизнес-логику приложения.
3. Слой DAO, содержащий в себе классы, осуществляющие выполнение запросов к БД.

## Остальные требования к функционалу

- Необходимо реализовать механизм, который будет отмечать просроченные OTP-коды раз в определенный интервал времени и присваивать им статус EXPIRED.
- Необходимо настроить логирование в приложении с помощью любой понравившейся библиотеки из модуля про логирование (JUL, Log4j, Logback, Slf4j).
- Приложение должно использовать систему сборки Maven.

## Как настроить интеграцию с почтовым сервисом в Java.

Ниже описывается процесс интеграции Java приложения с почтовым сервисом с использованием библиотеки JavaMail.

Для начала нужно добавить зависимость JavaMail в ваш проект. В вашем файле pom.xml добавьте следующий код:

```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```

Далее нужно создать файл email.properties в папке src/main/resources проекта. Этот файл будет содержать параметры конфигурации для подключения к почтовому сервису. Пример содержимого файла:

```properties
email.username=your_email@example.com
email.password=your_email_password
email.from=your_email@example.com
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
```

Для загрузки пропертей следует использовать следующий метод:

```java
private Properties loadConfig() {
   try {
       Properties props = new Properties();
       props.load(EmailNotificationService.class.getClassLoader()
               .getResourceAsStream("email.properties"));
       return props;
   } catch (Exception e) {
       throw new RuntimeException("Failed to load email configuration", e);
   }
}
```

Далее нужно создать конструктор почтового сервиса:

```java
public EmailNotificationService() {
   // Загрузка конфигурации
   Properties config = loadConfig();
   this.username = config.getProperty("email.username");
   this.password = config.getProperty("email.password");
   this.fromEmail = config.getProperty("email.from");
   this.session = Session.getInstance(config, new Authenticator() {
       protected PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(username, password);
       }
   });
}
```

Наконец, нужно реализовать метод отправки письма с кодом подтверждения на электронную почту:

```java
public void sendCode(String toEmail, String code) {
   try {
       Message message = new MimeMessage(session);
       message.setFrom(new InternetAddress(fromEmail));
       message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
       message.setSubject("Your OTP Code");
       message.setText("Your verification code is: " + code);

       Transport.send(message);
   } catch (MessagingException e) {
       throw new RuntimeException("Failed to send email", e);
   }
}
```

## Как настроить интеграцию с эмулятором SMPP сервера в Java

Для начала необходимо скачать эмулятор SMPP-протокола, распаковать его и запустить.

Далее необходимо подключить зависимость для работы с протоколом SMPP в Java:

```xml
<dependency>
   <groupId>org.opensmpp</groupId>
   <artifactId>opensmpp-core</artifactId>
   <version>3.0.0</version>
</dependency>
```

Далее следует создать файл sms.properties c следующим наполнением:

```properties
smpp.host=localhost
smpp.port=2775
smpp.system_id=smppclient1
smpp.password=password
smpp.system_type=OTP
smpp.source_addr=OTPService
```
Параметры smpp.system_id и smpp.password нужно взять из установленного эмулятора.

Далее по аналогии с рассылкой по email нужно загрузить проперти в наш сервис.

Теперь необходимо написать метод, который будет отправлять СМС в эмулятор по протоколу SMPP:

```java
public void sendCode(String destination, String code) {
   Connection connection;
   Session session;

   try {
       // 1. Установка соединения
       connection = new TCPIPConnection(host, port);
       session = new Session(connection);
       // 2. Подготовка Bind Request
       BindTransmitter bindRequest = new BindTransmitter();
       bindRequest.setSystemId(systemId);
       bindRequest.setPassword(password);
       bindRequest.setSystemType(systemType);
       bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
       bindRequest.setAddressRange(sourceAddress);
       // 3. Выполнение привязки
       BindResponse bindResponse = session.bind(bindRequest);
       if (bindResponse.getCommandStatus() != 0) {
           throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
       }
       // 4. Отправка сообщения
       SubmitSM submitSM = new SubmitSM();
       submitSM.setSourceAddr(sourceAddress);
       submitSM.setDestAddr(destination);
       submitSM.setShortMessage("Your code: " + code);

       session.submit(submitSM);
       logSuccess();
   } catch (Exception e) {
       handleError(e.getMessage(), e);
   }
}
```

## Как настроить интеграцию с Telegram в Java

Для начала необходимо создать Telegram-бота через @BotFather и получить его токен.

Затем необходимо подключить библиотеку org.apache.httpcomponents чтобы выполнять запросы к Telegram API:

```xml
<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.5.13</version>
</dependency>
```

Теперь необходимо начать диалог с ботом в Telegram, после чего следует выполнить запрос к TelegramAPI, чтобы получить значение chatId. Это будет id диалога с ботом.

https://api.telegram.org/botYOUR_BOT_TOKEN/getUpdates

В результате получится примерно следующее:

```json
{
    "ok": true,
    "result": [
        {
            "update_id": 123456789,
            "message": {
                "message_id": 1,
                "from": {
                    "id": 987654321,
                    "is_bot": false,
                    "first_name": "YourName",
                    "username": "YourUsername",
                    "language_code": "en"
                },
                "chat": {
                    "id": -123456789,  // Это и есть ваш чат ID
                    "first_name": "YourName",
                    "username": "YourUsername",
                    "type": "private"
                },
                "date": 1610000000,
                "text": "Hello, bot!"
            }
        }
    ]
}
```

После получения нужного chat.id и токена бота можно приступать к реализации отправки сообщения с кодом через него:

```java
public void sendCode(String destination, String code) {
   String message = String.format(destination + ", your confirmation code is: %s", code);
   String url = String.format("%s?chat_id=%s&text=%s",
           telegramApiUrl,
           chatId,
           urlEncode(message));

   sendTelegramRequest(url);
}
private void sendTelegramRequest(String url) {
   try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
       HttpGet request = new HttpGet(url);
       try (CloseableHttpResponse response = httpClient.execute(request)) {
           int statusCode = response.getStatusLine().getStatusCode();
           if (statusCode != 200) {
               logger.error("Telegram API error. Status code: {}", statusCode);
           } else {
               logger.info("Telegram message sent successfully");
           }
       }
   } catch (IOException e) {
       logger.error("Error sending Telegram message: {}", e.getMessage());
   }
}
private static String urlEncode(String value) {
   return URLEncoder.encode(value, StandardCharsets.UTF_8);
}
```

## Этапы сдачи проекта

### Размещение кода на GitHub

Необходимо разместить свой проект на GitHub, а также убедиться, что структура репозитория понятна: добавить файл README.md, в котором описать:

- Как пользоваться сервисом.
- Какие команды поддерживаются.
- Как протестировать код.

### Подготовка к отправке проекта на проверку

Необходимо проверить, что:

- Все функции проекта работают корректно и соответствуют ТЗ.
- Код оформлен и структурирован согласно стандартам.
- В репозитории есть описание проекта и документация по каждому модулю.
- Если используются внешние библиотеки, добавить инструкции по их установке.

После того как проект будет готов, отправить ссылку на репозиторий через форму сдачи проекта.

## Критерии оценивания (максимум - 69 баллов)

- Структура приложения соответствует требованиям — 5 баллов;
- Используется система сборки Maven — 5 баллов;
- Реализован минимальный функционал основных операций приложения без токенной аутентификации и авторизации — 17 баллов;
- Запросы к приложению имеют разграничение по ролям администратора и обычного пользователя — 5 баллов;
- Для разработки API был использован Spring MVC — 6 баллов;
- Было реализовано минимальное покрытие логами каждого запроса к API — 3 балла.
- Реализован механизм рассылки OTP-кодов по эмулятору почты — 5 баллов.
- Реализован механизм рассылки OTP-кодов через эмулятор SMPP — 5 баллов.
- Реализован механизм рассылки OTP-кодов через Telegram — 5 баллов.
- Реализован механизм сохранения OTP-кодов в файл — 5 баллов.
- Реализован механизм токенной аутентификации и авторизации — 5 баллов.
- Реализовано подробное покрытие всех запросов к API логами — 3 балла.

