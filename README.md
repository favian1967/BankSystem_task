<a id="readme-top"></a>

# Bank Card Management System

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Оглавление</summary>
  <ol>
    <li>
      <a href="#about-the-project">О проекте</a>
      <ul>
        <li><a href="#built-with">Стек</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Быстрый старт</a>
      <ul>
        <li><a href="#prerequisites">Требования</a></li>
        <li><a href="#installation">Установка и запуск</a></li>
      </ul>
    </li>
    <li><a href="#usage">Использование</a></li>
    <li><a href="#configuration">Конфигурация</a></li>
    <li><a href="#architecture">Архитектура</a></li>
    <li><a href="#api-overview">API Overview</a></li>
    <li><a href="#testing">Тестирование</a></li>
    <li><a href="#database-schema">Database Schema</a></li>
    <li><a href="#contact">Контакты</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
<a id="about-the-project"></a>
## О проекте

**Bank Card Management System** — REST API для управления банковскими картами с JWT-аутентификацией, ролевым доступом и интеграцией с PostgreSQL.

**Основные возможности:**
- Создание и управление банковскими картами
- Переводы между картами пользователя
- Администрирование пользователей и карт
- Шифрование номеров карт (AES-256)
- Фильтрация и пагинация данных

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<a id="built-with"></a>
### Стек

* [![Java][java-shield]][java-url]
* [![Spring Boot][spring-boot-shield]][spring-boot-url]
* [![PostgreSQL][postgres-shield]][postgres-url]
* [![JWT][jwt-shield]][jwt-url]
* [![Docker][docker-shield]][docker-url]
* ![Liquibase](https://img.shields.io/badge/Liquibase-DB%20Migrations-2962FF?style=for-the-badge&logo=liquibase)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
<a id="getting-started"></a>
## Быстрый старт

<a id="prerequisites"></a>
### Требования

- **Java 21**
- **Docker** и **Docker Compose** (для контейнерного запуска)
- **Maven** (опционально, используется встроенный wrapper)

<a id="installation"></a>
### Установка и запуск

1. Скопируйте пример окружения:
   ```bash
   cp .env.example .env
   ```

2. Заполните переменные в `.env`

3. Выберите сценарий запуска:

#### Через Docker Compose (рекомендуется)

```bash
docker compose up --build
```

#### Локально (без Docker)

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

2. Запустите приложение:
   ```bash
   ./mvnw spring-boot:run
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
<a id="usage"></a>
## Использование

- Приложение доступно на: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- PostgreSQL пробрасывается на `localhost:5432`

**Тестовые пользователи:**
- Admin: `d@g` / `25442544`
- User: `m@g` / `25442544`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONFIGURATION -->
<a id="configuration"></a>
## Конфигурация

Параметры конфигурации задаются через файл `.env`:

| Переменная | Описание |
| --- | --- |
| `DB_URL` | JDBC URL до PostgreSQL |
| `DB_USERNAME` / `DB_PASSWORD` | Логин/пароль БД |
| `JWT_SECRET` | Секрет для подписи JWT токенов (base64) |
| `JWT_ACCESS_EXPIRATION` | Время жизни access token (мс) - по умолчанию `3600000` (1 час) |
| `JWT_REFRESH_EXPIRATION` | Время жизни refresh token (мс) - по умолчанию `86400000` (24 часа) |
| `CARD_ENCRYPTION_KEY` | Ключ шифрования карт (32 символа) |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<a id="architecture"></a>
## Архитектура

Проект следует многослойной архитектуре (Layered Architecture):

```
├── controller/           # REST контроллеры (presentation layer)
├── service/             # Бизнес-логика (business layer)
├── repository/          # Доступ к данным (data access layer)
├── entity/              # JPA сущности
├── dto/                 # Data Transfer Objects
├── security/            # JWT + Spring Security
├── config/              # Конфигурация
└── exception/           # Обработка ошибок
```

**Ключевые компоненты:**
- **JWT Authentication** - stateless аутентификация с access/refresh токенами
- **Role-based Access Control** - роли ADMIN и USER
- **Card Encryption** - AES-256 шифрование номеров карт
- **Liquibase Migrations** - версионирование схемы БД
- **Global Exception Handler** - централизованная обработка ошибок

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<a id="api-overview"></a>
## API Overview

### Authentication
```
POST /api/auth/register   # Регистрация
POST /api/auth/login      # Вход
POST /api/auth/refresh    # Обновление токена
```

### Cards (USER)
```
GET  /api/cards                 # Список карт с пагинацией
GET  /api/cards/search          # Поиск карт
GET  /api/cards/status/{status} # Фильтр по статусу
GET  /api/cards/{id}            # Получить карту
GET  /api/cards/{id}/balance    # Баланс карты
POST /api/cards/{id}/block      # Заблокировать карту
POST /api/cards/transfer        # Перевод между картами
GET  /api/cards/transactions    # История транзакций
```

### Admin - Cards
```
POST   /api/admin/cards/{id}/activate  # Активировать карту
POST   /api/admin/cards/{id}/block     # Заблокировать
DELETE /api/admin/cards/{id}           # Удалить
```

### Admin - Users
```
GET    /api/admin/users       # Все пользователи
GET    /api/admin/users/{id}  # Получить пользователя
PUT    /api/admin/users/{id}  # Обновить
DELETE /api/admin/users/{id}  # Удалить
```

**Документация:**  
Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<a id="database-schema"></a>
## Database Schema

```
users                    cards                     transactions
├── id (PK)             ├── id (PK)               ├── id (PK)
├── email (unique)      ├── user_id (FK)          ├── from_card_id (FK)
├── password_hash       ├── encrypted_number      ├── to_card_id (FK)
├── first_name          ├── masked_number         ├── type
├── last_name           ├── expiry_date           ├── amount
├── phone (unique)      ├── status                ├── description
├── created_at          └── balance               ├── status
└── updated_at                                    ├── created_at
                                                  └── completed_at
roles                   refresh_tokens
├── id (PK)             ├── id (PK)
└── name (unique)       ├── token (unique)
                        ├── user_id (FK)
users_roles             ├── expiry_date
├── user_id (FK)        └── created_at
└── role_id (FK)
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- TESTING -->
<a id="testing"></a>
## Тестирование

```bash
./mvnw test
```

**Покрытие тестами:**
- Unit-тесты сервисов (`AuthServiceTest`, `CardServiceTest`, `TransactionServiceTest`, `UserServiceTest`)
- Integration-тесты контроллеров (`AuthControllerTest`, `CardControllerTest`, `AdminCardControllerTest`, `AdminUserControllerTest`)
- Используется H2 in-memory database для изоляции тестов

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
<a id="contact"></a>
## Контакты

Email: dpastuhovj@gmail.com  
Telegram: @Rafink  
GitHub: создайте issue в репозитории

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[java-shield]: https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://openjdk.org/
[spring-boot-shield]: https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[spring-boot-url]: https://spring.io/projects/spring-boot
[postgres-shield]: https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[jwt-shield]: https://img.shields.io/badge/JWT-0.12.x-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white
[jwt-url]: https://github.com/jwtk/jjwt
[docker-shield]: https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://docs.docker.com/compose/
