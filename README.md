# Hangman Game - Multilingual Web Application

- A modern, multilingual Hangman game build with Spring Boot, following
Domain-Driven Design (DDD) principles and SOLID architecture patterns.
The game supports multiple languages, categories, difficulty levels, and
provides both REST API and web interface.

## Features
### Core Game Features
    - Multilingual Support: English, Ukrainian, German, French, Spanish
    - Category-based Words: Programming, Animals, Technology, General, etc.
    - Difficulty Levels: Easy (3-4 letters), Medium (5-7 letters), Hard (8+ letters)
    - Language-specific Validation: Proper alphabet validation for each language
    - Virtual Keyboard: Adaptive keyboard based on selected language
    - Real-time Game State: Session-based game persistence
### Technical Features
    - Domain-Driven Design (DDD) architecture
    - SOLID Principles implementation
    - Clean Architecture with separated layers
    - RESTful API with comprehensive endpoints
    - JPA/Hibernate with H2 (dev) and PostgreSQL (prod) support
    - Multilingual Word Management system
    - File-based Word Loading with validation
    - Comprehensive Error Handling
    - Logging and Monitoring
## 🏗️ Architecture
```courseignore
domain/
├── aggregate/
│   └── HangmanGame.java          # Main game aggregate
├── model/
│   └── Word.java                 # Word entity
├── valueobject/
│   ├── Language.java             # Language value object
│   ├── GamePreferences.java      # Game configuration
│   ├── Letter.java               # Letter validation
│   └── GameId.java               # Game identifier
├── event/
│   ├── GameStartedEvent.java     # Domain events
│   ├── LetterGuessedEvent.java
│   └── GameEndedEvent.java
├── exception/
│   ├── GameNotFoundException.java
│   ├── UnsupportedLanguageException.java
│   └── LetterAlreadyGuessedException.java
└── repository/
    ├── WordRepository.java       # Domain repository interfaces
    └── GameRepository.java
```
## Application Layer
```courseignore
application/
├── service/
│   ├── HangmanGameService.java   # Main game service
│   └── WordManagementService.java # Word administration
└── dto/
    ├── GameDto.java              # Data transfer objects
    ├── GuessDto.java
    └── LanguageInfoDto.java
```
## Infrastructure Layer
```courseignore
infra
├── entity/
│   └── WordEntity.java           # JPA entities
├── repository/
│   ├── jpa/
│   │   └── WordJpaRepository.java # JPA repositories
│   └── impl/
│       ├── JpaWordRepository.java # Repository implementations
│       └── InMemoryGameRepository.java
└── service/
    └── WordLoaderService.java    # File loading service
```
## Controller Layer
```courseignore
controller/
├── HangmanController.java        # Main game API
├── HomeController.java           # Web interface
├── WordManagementController.java # Admin API
└── LanguageManagementController.java
```

## 🚀 Quick Start
Prerequisites

- Java 17 or later
-  Maven 3.6+
- (Optional) PostgreSQL for production

### Installation
1. Clone repo
```courseignore
git@github.com:dramaticLemon/hangman-web.git
cd hangman-web
```
2. Build the project
```courseignore
filled .env 
    DB_PASSWORD=******
    DB_USER=********** 
mvn clean install
```
3. Run the application
```courseignore
mvn spring-boot:run
```
4. Access the application
   - Web Interface: http://localhost:8081/api/game

## 🌍 Language Support
| Language | Code | Alphabet | Sample Words |
| :--- | :---: | :---: | :--- |
| English | `en` | Latin | computer, algorithm, spring |
| Ukrainian | `uk` | Cyrillic | комп'ютер, алгоритм, програма |

### Adding New Languages
1. Create ford files:
```courseignore
src/main/resources/words/[language]/
├── general-words.txt
├── programming-words.txt
└── animals-words.txt
```
2. Update Language enum: 
``` java
// Add to Language.java
private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
    "en", "ru", "de", "fr", "es", "it", "pt" // Add new language code
);
```
3. Add validation patterns:
```java
// Add to WordLoaderService.java
validationPatterns.put("it", Pattern.compile("^[a-zA-Zàèéìíîòóù]{3,50}$"));
```
## 📡 API Endpoints
Game Management
```courseignore
POST /api/hangman/start
POST /api/hangman/start-with-preferences?language=en&category=programming&difficulty=medium
POST /api/hangman/guess?letter=a
GET  /api/hangman/status
DELETE /api/hangman/end
```
Language Support
```courseignore
GET /api/hangman/languages
GET /api/hangman/languages/{languageCode}
```
Admin Endpoints
```courseignore
GET    /api/admin/words/stats
POST   /api/admin/words/upload
POST   /api/admin/words/add?word=example&category=general
DELETE /api/admin/words/{word}
POST   /api/admin/words/reload
```
### Example API Usage
Start a new game in English:
```bash
curl -X POST "http://localhost:8081/api/hangman/start?language=en"
```
Guess a letter:
```bash
curl -X POST "http://localhost:8081/api/hangman/guess?letter=а"
```
Response:
```json
{
  "currentState": "p_o___m",
  "currentStateWithSpaces": "p _ о _ _ _ m",
  "remainingTries": 5,
  "status": "IN_PROGRESS",
  "language": "en",
  "wasCorrect": true,
  "guessedLetter": "а",
  "allGuessedLetters": ["p", "g"],
  "message": "Correct!"
}
```
🗄️ Database Schema
Words Table
```sql
CREATE TABLE words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    value VARCHAR(50) NOT NULL,
    language VARCHAR(5) NOT NULL,
    length INTEGER NOT NULL,
    category VARCHAR(30),
    difficulty_level VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_word_language (value, language)
);
```
Indexes
- idx_word_language on language
- idx_word_language_category on (language, category)
- idx_word_active_language on (is_active, language)

📝 Configuration
Production (PostgreSQL):
```courseignore
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hangman_db
    username: ${DB_USERNAME:hangman_user}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```
🧪 Testing
Run Tests
```
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=HangmanGameServiceTest

# Run integration tests
mvn integration-test
```
Test Coverage
```
mvn jacoco:report
```

## 📊 Word File Format
```Basic Format
# Comments start with #
# Category: general
# Language: en

# Easy words
cat
dog
sun

# Medium words
computer
keyboard
internet

# Hard words
programming
architecture
development
```

## 🛠️ Development
Running in Development Mode
```
# With hot reload
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# With debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```
### Adding New Features

1. Domain First: Start with domain model
2. Test-Driven: Write tests before implementation
3. API Design: Design REST endpoints
4. Database Migration: Create Flyway scripts
5. Frontend Integration: Update JavaScript client

Code Style

* Follow Google Java Style Guide
* Use meaningful variable names
* Document public APIs
* Write comprehensive tests

### 🔧 Production Deployment
Docker Support
```courseignore
FROM openjdk:17-jdk-slim
COPY target/hangman-game-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
Build and Deploy
```courseignore
# Build JAR
mvn clean package -Pprod

# Build Docker image
docker build -t hangman-game .

# Run with PostgreSQL
docker-compose up -d

```
Environment Variables
```courseignore
DB_URL=jdbc:postgresql://db:5432/hangman
DB_USERNAME=hangman_user
DB_PASSWORD=secure_password
JAVA_OPTS=-Xmx512m
SPRING_PROFILES_ACTIVE=prod

```
📈 Monitoring and Logging
* /actuator/health - Application health
* /actuator/metrics - Application metrics
* /actuator/info - Application info

### Logging Configuration
```
logging:
level:
com.join.tab: DEBUG
org.springframework.web: INFO
org.hibernate.SQL: DEBUG
pattern:
console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

🤝 Contributing
1. Fork the repository
2. Create a feature branch (git checkout -b feature/new-language)
3. Commit your changes (git commit -am 'Add Italian language support')
4. Push to the branch (git push origin feature/new-language)
5. Create a Pull Request

Development Guidelines

Follow DDD principles
Maintain SOLID architecture
Write comprehensive tests
Update documentation
Add language-specific word files

📄 License
This project is licensed under the MIT License - see the [LICENSE.md]() file for details.


🙏 Acknowledgments

Spring Boot team for the excellent framework
Contributors to the multilingual word datasets
Community feedback and suggestions

📞 Support
For questions, issues, or contributions:

Create an Issue
Start a Discussion
Contact: chebandima27@gmail.com
