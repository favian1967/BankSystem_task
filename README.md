# Bank Card Management System

REST API система управления банковскими картами на базе Spring Boot.

## Содержание

- [Описание](#описание)
- [Технологии](#технологии)
- [Функциональность](#функциональность)
- [Архитектура](#архитектура)
- [Запуск приложения](#запуск-приложения)
- [API Документация](#api-документация)
- [Безопасность](#безопасность)
- [Тестирование](#тестирование)

## Описание

Система предоставляет функционал для управления банковскими картами:
- Создание и управление картами
- Просмотр карт с фильтрацией и пагинацией
- Переводы между картами
- Администрирование пользователей и карт

## Технологии

- Java 21
- Spring Boot 3.3.5
  - Spring Security
  - Spring Data JPA
  - Spring Web
  - Spring Validation
- PostgreSQL (основная БД)
- H2 (БД для тестов)
- Liquibase (миграции БД)
- JWT (jjwt 0.12.3)
- Swagger/OpenAPI 3
- JUnit 5 + Mockito
- Docker & Docker Compose
- Maven
- Lombok

## Функциональность

### Атрибуты карты
- Номер карты - зашифрован AES-256, отображается в маскированном виде: `**** **** **** 1234`
- Владелец - связь с пользователем
- Срок действия - дата окончания срока карты
- Статус: `ACTIVE`, `BLOCKED`, `EXPIRED`
- Баланс - текущий баланс карты

### Роли и права доступа

#### ADMIN
- Создание карт для пользователей
- Блокировка/активация карт
- Удаление карт
- Просмотр всех карт в системе
- Управление пользователями (создание, просмотр, обновление, удаление)
- Просмотр всех пользователей с пагинацией

#### USER
- Просмотр своих карт с фильтрацией и пагинацией
- Поиск карт по номеру
- Фильтрация карт по статусу
- Запрос блокировки своей карты
- Переводы между своими картами
- Просмотр баланса
- Просмотр истории транзакций

## Архитектура

Проект следует многослойной архитектуре:

```
├── controller/           # REST контроллеры
│   ├── AuthController
│   ├── CardController
│   ├── AdminCardController
│   └── AdminUserController
├── service/             # Бизнес-логика
│   ├── AuthService
│   ├── CardService
│   ├── TransactionService
│   ├── UserService
│   ├── JwtService
│   ├── EncryptionService
│   └── RefreshTokenService
├── repository/          # Доступ к данным
│   ├── UserRepository
│   ├── CardRepository
│   ├── TransactionRepository
│   ├── RoleRepository
│   └── RefreshTokenRepository
├── entity/              # JPA сущности
│   ├── User
│   ├── Card
│   ├── Transaction
│   ├── Role
│   └── RefreshToken
├── dto/                 # Data Transfer Objects
├── security/            # Конфигурация безопасности
│   ├── SecurityConfig
│   ├── JwtAuthenticationFilter
│   └── CustomUserDetailsService
├── config/              # Конфигурация приложения
│   └── OpenApiConfig
├── exception/           # Обработка ошибок
│   ├── GlobalExceptionHandler
│   └── exceptions/
└── util/                # Утилиты
```

## Запуск приложения

### Предварительные требования

- Java 21+
- Docker и Docker Compose (для запуска в контейнерах)
- Maven 3.9+ (опционально, можно использовать встроенный wrapper)

### Вариант 1: Запуск с Docker Compose (рекомендуется)

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd Bank_test_case
```

2. Создайте файл `.env` (опционально):
```bash
cp .env.example .env
```

3. Запустите приложение:
```bash
docker-compose up -d
```

Приложение будет доступно по адресу: `http://localhost:8080`

4. Остановка:
```bash
docker-compose down
```

### Вариант 2: Локальный запуск

1. Запустите PostgreSQL:
```bash
docker run -d \
  --name postgres \
  -e POSTGRES_DB=bank_test \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

2. Настройте переменные окружения:
```bash
cp .env.example .env
```

3. Запустите приложение:
```bash
./mvnw spring-boot:run
```

Или соберите и запустите jar:
```bash
./mvnw clean package
java -jar target/Bank_test_case-0.0.1-SNAPSHOT.jar
```

### Вариант 3: Только база данных в Docker

```bash
docker-compose up -d postgres
./mvnw spring-boot:run
```

## API Документация

После запуска приложения документация API доступна по адресам:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: Статическая документация в `docs/openapi.yml`

### Основные эндпоинты

#### Аутентификация
```
POST   /api/auth/register        # Регистрация
POST   /api/auth/login           # Вход
POST   /api/auth/refresh         # Обновление токена
```

#### Карты (USER)
```
GET    /api/cards                # Список карт с пагинацией
GET    /api/cards/search         # Поиск карт
GET    /api/cards/status/{status} # Фильтр по статусу
GET    /api/cards/{id}           # Получить карту
GET    /api/cards/{id}/balance   # Баланс карты
POST   /api/cards/{id}/block     # Заблокировать карту
POST   /api/cards/transfer       # Перевод между картами
GET    /api/cards/{id}/transactions # История транзакций
GET    /api/cards/transactions   # Все транзакции
```

#### Администрирование карт (ADMIN)
```
POST   /api/admin/cards          # Создать карту
GET    /api/admin/cards          # Все карты
POST   /api/admin/cards/{id}/block     # Заблокировать
POST   /api/admin/cards/{id}/activate  # Активировать
DELETE /api/admin/cards/{id}     # Удалить
```

#### Администрирование пользователей (ADMIN)
```
POST   /api/admin/users          # Создать пользователя
GET    /api/admin/users          # Все пользователи
GET    /api/admin/users/{id}     # Получить пользователя
PUT    /api/admin/users/{id}     # Обновить
DELETE /api/admin/users/{id}     # Удалить
```

### Примеры запросов

#### Регистрация
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890"
  }'
```

#### Вход
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Ответ:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "d4f5e6g7-h8i9-j0k1-l2m3-n4o5p6q7r8s9"
}
```

#### Получение карт (требуется токен)
```bash
curl -X GET http://localhost:8080/api/cards \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Перевод между картами
```bash
curl -X POST http://localhost:8080/api/cards/transfer \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fromCardId": 1,
    "toCardId": 2,
    "amount": 100.50,
    "description": "Перевод на накопительную карту"
  }'
```

#### Создание карты (ADMIN)
```bash
curl -X POST http://localhost:8080/api/admin/cards \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1
  }'
```

## Безопасность

### Аутентификация и авторизация
- JWT токены для stateless аутентификации
- Access Token - короткоживущий (1 час по умолчанию)
- Refresh Token - долгоживущий (24 часа по умолчанию), хранится в БД
- BCrypt для хеширования паролей
- Ролевой доступ через Spring Security (`@PreAuthorize`)

### Шифрование данных
- Номера карт шифруются с помощью AES-256
- Ключ шифрования настраивается через переменную окружения `CARD_ENCRYPTION_KEY`
- Маскирование номеров карт при отображении: `**** **** **** 1234`

### Валидация
- Валидация входных данных с помощью Bean Validation (`@Valid`, `@NotBlank`, `@Email`)
- Кастомные исключения для различных ошибок
- Глобальный обработчик исключений `GlobalExceptionHandler`

### Защита API
- CSRF отключен (stateless API)
- CORS можно настроить при необходимости
- Rate limiting можно добавить через Spring Cloud Gateway или Bucket4j

## База данных

### Миграции
Миграции управляются через Liquibase. Файлы миграций находятся в `src/main/resources/db/migration/`:

```
db/migration/
├── db.changelog-master.yml      # Главный файл
├── 001-init-users.yml           # Пользователи и роли
├── 002-create-cards.yml         # Таблица карт
├── 003-create-transactions.yml  # Транзакции
├── 004-create-refresh-tokens.yml # Refresh токены
├── 005-seed-roles.yml           # Начальные роли
└── 006-seed-users.yml           # Тестовые пользователи
```

### Начальные данные

После первого запуска создаются:

Роли:
- `ADMIN`
- `USER`

Тестовые пользователи (созданные через seed миграцию):
- Admin: `admin@test.com` / `admin123`
- User: `user@test.com` / `user123`

### Схема БД

```
users
├── id (PK)
├── email (unique)
├── password_hash
├── first_name
├── last_name
├── phone (unique)
├── created_at
└── updated_at

roles
├── id (PK)
└── name (unique)

users_roles (many-to-many)
├── user_id (FK)
└── role_id (FK)

cards
├── id (PK)
├── user_id (FK)
├── encrypted_number
├── masked_number
├── expiry_date
├── status (ACTIVE/BLOCKED/EXPIRED)
└── balance

transactions
├── id (PK)
├── from_card_id (FK, nullable)
├── to_card_id (FK, nullable)
├── type (TRANSFER/DEPOSIT/WITHDRAWAL)
├── amount
├── description
├── status (PENDING/COMPLETED/FAILED)
├── created_at
└── completed_at

refresh_tokens
├── id (PK)
├── token (unique)
├── user_id (FK)
├── expiry_date
└── created_at
```

## Тестирование

### Запуск тестов

```bash
# Все тесты
./mvnw test

# Только unit-тесты
./mvnw test -Dtest="*Test"

# Только integration-тесты
./mvnw test -Dtest="*IT"
```

### Покрытие тестами

Проект включает следующие типы тестов:

#### Unit-тесты
- `AuthServiceTest` - тестирование аутентификации
- `CardServiceTest` - тестирование логики карт
- `TransactionServiceTest` - тестирование переводов
- `UserServiceTest` - тестирование пользователей

#### Integration-тесты контроллеров
- `AuthControllerTest` - API аутентификации
- `CardControllerTest` - API карт для пользователей
- `AdminCardControllerTest` - API администрирования карт
- `AdminUserControllerTest` - API администрирования пользователей

#### Особенности тестов
- Используется H2 in-memory database для изоляции
- `@SpringBootTest` для интеграционных тестов
- `MockMvc` для тестирования REST API
- `Mockito` для мокирования зависимостей
- `@DataJpaTest` для тестов репозиториев

### Отчеты о тестах
После выполнения тестов отчеты доступны в:
- `target/surefire-reports/` - текстовые отчеты
- `target/site/jacoco/` - покрытие кода (после `mvnw jacoco:report`)

## Сборка

### Сборка JAR
```bash
./mvnw clean package
```

Результат: `target/Bank_test_case-0.0.1-SNAPSHOT.jar`

### Сборка без тестов
```bash
./mvnw clean package -DskipTests
```

### Сборка Docker образа
```bash
docker build -t bank-app .
```

## Конфигурация

Основные параметры конфигурации задаются через переменные окружения в файле `.env`:

| Переменная | Описание | Значение по умолчанию |
|-----------|----------|----------------------|
| `DB_URL` | URL подключения к БД | `jdbc:postgresql://localhost:5432/bank_test` |
| `DB_USERNAME` | Имя пользователя БД | `postgres` |
| `DB_PASSWORD` | Пароль БД | `postgres` |
| `JWT_SECRET` | Секретный ключ для JWT (base64) | См. `.env.example` |
| `JWT_ACCESS_EXPIRATION` | Время жизни access token (мс) | `3600000` (1 час) |
| `JWT_REFRESH_EXPIRATION` | Время жизни refresh token (мс) | `86400000` (24 часа) |
| `CARD_ENCRYPTION_KEY` | Ключ шифрования карт (32 символа) | См. `.env.example` |

## Структура проекта

```
Bank_test_case/
├── .github/              # GitHub workflows
├── .mvn/                 # Maven wrapper
├── docs/                 # Документация
│   └── openapi.yml       # OpenAPI спецификация
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/favian/bank_test_case/
│   │   │       ├── config/          # Конфигурация
│   │   │       ├── controller/      # REST контроллеры
│   │   │       ├── dto/             # DTO
│   │   │       ├── entity/          # JPA сущности
│   │   │       ├── exception/       # Обработка ошибок
│   │   │       ├── repository/      # Репозитории
│   │   │       ├── security/        # Безопасность
│   │   │       ├── service/         # Бизнес-логика
│   │   │       └── util/            # Утилиты
│   │   └── resources/
│   │       ├── db/migration/        # Liquibase миграции
│   │       └── application.yml      # Конфигурация Spring
│   └── test/
│       ├── java/                    # Тесты
│       └── resources/
│           ├── application-test.yml
│           └── data.sql             # Тестовые данные
├── target/                          # Сборка (генерируется)
├── .dockerignore
├── .env.example                     # Пример переменных окружения
├── .gitignore
├── docker-compose.yml               # Docker Compose конфигурация
├── Dockerfile                       # Docker образ приложения
├── HELP.md
├── mvnw                            # Maven wrapper (Unix)
├── mvnw.cmd                        # Maven wrapper (Windows)
├── pom.xml                         # Maven конфигурация
└── README.md                       # Этот файл
```

## Обработка ошибок

API возвращает структурированные сообщения об ошибках:

```json
{
  "timestamp": "2026-02-14T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Card not found with id: 123",
  "path": "/api/cards/123"
}
```

### Коды ошибок

| Код | Описание |
|-----|----------|
| 400 | Bad Request - Невалидные данные |
| 401 | Unauthorized - Не авторизован |
| 403 | Forbidden - Нет прав доступа |
| 404 | Not Found - Ресурс не найден |
| 409 | Conflict - Конфликт (например, email занят) |
| 500 | Internal Server Error - Внутренняя ошибка |

## Лицензия

Этот проект создан в учебных целях.

## Контакты

Для вопросов и предложений:
- Email: support@bank.com
- GitHub Issues: создайте issue в репозитории

---

Разработано с использованием Spring Boot 3.3.5 и Java 21
