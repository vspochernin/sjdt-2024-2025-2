# sjdt-2024-2025-2
Итоговый проект «Защита OTP-кодом» по дисциплине "Специализированные инструменты разработки на языке Java". 2-й семестр 1-го курса МИФИ ИИКС РПО (2024-2025 уч. г).

## Описание проекта
Сервис для защиты операций с помощью временных OTP-кодов. Подробное описание задания находится в файле [task.md](task.md).

## Предварительные требования
1. Установленный Docker и Docker Compose.
2. Java 17 или выше.
3. Maven.

## Запуск проекта
1. Поднять базу данных PostgreSQL (находясь в директории `src/main/resources/db`):
```bash
docker-compose up -d
```

2. Настроить конфигурационные файлы:
- `src/main/resources/sms.properties` - настройки SMPP.
- `src/main/resources/email.properties` - настройки почтового сервера.
- `src/main/resources/application.yml` - настройки Telegram бота (токен бота).

3. Собрать и запустить приложение:
```bash
mvn clean install
mvn spring-boot:run
```

## Использование сервиса

### Регистрация и аутентификация
1. Регистрация нового пользователя:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "user","password": "password","email": "test@gmail.com","telegram": "telegram","phone": "phone","role": "USER"}'
```

2. Аутентификация:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

### Генерация и валидация OTP-кодов
1. Генерация OTP-кода (на примере отправки кода в файл):
```bash
curl -X POST http://localhost:8080/api/otp/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer USER_JWT_TOKEN" \
  -d '{"operationId":"123","notificationType":"FILE"}'
```

2. Валидация OTP-кода:
```bash
curl -X POST http://localhost:8080/api/otp/validate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer USER_JWT_TOKEN" \
  -d '{"code":"OPT_CODE","operationId":"123"}'
```

### Функции администратора
1. Регистрация администратора:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin","password": "password","email": "test@gmail.com","telegram": "telegram","phone": "phone","role": "ADMIN"}'
```

2. Обновление конфигурации OTP:
```bash
curl -X PUT http://localhost:8080/api/admin/config \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -d '{"codeLength":6,"expirationTimeSeconds":5}'
```

3. Получение списка пользователей:
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

4. Удаление пользователя:
```bash
curl -X DELETE http://localhost:8080/api/admin/users/USER_ID \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Тестирование

Для выполнения тестирования достаточно запустить проект и вызывать ручки сервиса. Удобнее всего это делать через Postman.

## Выполнение критериев

1. Структура приложения соответствует требованиям (5 баллов):
   - Приложение разделено на слои: API (контроллеры), сервисы, DAO (репозитории).
   - Используется Spring MVC для API.
   - Соблюдены принципы SOLID.

2. Используется система сборки Maven (5 баллов):
   - Проект использует Maven для управления зависимостями.

3. Реализован минимальный функционал основных операций (17 баллов).
   - Генерация OTP-кодов.
   - Валидация OTP-кодов.
   - Отправка кодов через разные каналы.

4. Запросы к приложению имеют разграничение по ролям (5 баллов):
   - Реализованы отдельные эндпоинты для админа.
   - Используется @PreAuthorize для проверки ролей.
   - Обычные пользователи не имеют доступа к админским функциям.

5. Для разработки API был использован Spring MVC (6 баллов):
   - Используются аннотации @RestController, @RequestMapping.
   - Реализованы RESTful эндпоинты.
   - Настроена валидация входных данных.

6. Минимальное покрытие логами каждого запроса (3 балла):
   - Логируются все входящие запросы.
   - Логируются результаты операций.
   - Используется SLF4J (аннотация @Slf4j).

7. Реализован механизм рассылки OTP-кодов по почте (5 баллов):
   - Используется JavaMail.
   - Настроена конфигурация через properties.
   - Реализована отправка писем (эмуляция).

8. Реализован механизм рассылки OTP-кодов через эмулятор SMPP (5 баллов):
   - Используется opensmpp.
   - Реализовано подключение к SMPP серверу (эмуляция).
   - Настроена отправка SMS.

9. Реализован механизм рассылки OTP-кодов через Telegram (5 баллов):
   - Используется Telegram Bot API.
   - Реализована отправка сообщений.
   - Настроен парсинг JSON ответов (для поиска chatId пользователя).

10. Реализован механизм сохранения OTP-кодов в файл (5 баллов):
    - Коды сохраняются в файл.

11. Реализован механизм токенной аутентификации и авторизации (5 баллов):
    - Используется JWT.
    - Реализована регистрация и аутентификация.
    - Настроена проверка токенов (а также их протухание).

12. Подробное покрытие всех запросов к API логами (3 балла):
    - Логируются все этапы обработки запросов.
    - Логируются ошибки и исключения.
    - Используется аннотация @Slf4j.