# Todo API - Spring Boot

API REST para gerenciamento de tarefas desenvolvida com Java Spring Boot.

## Tecnologias

- Java 21
- Spring Boot 3.3.0
- Spring Security (JWT)
- H2 Database
- Maven
- Swagger/OpenAPI

## Pré-requisitos

- Java 21+
- Maven 3.6+

## Como Executar

### 1. Clone o projeto
```bash
git clone https://github.com/ggzuka/viceri-desafio-java-spring-boot.git
```

### 2. Execute a aplicação
```bash
mvn clean spring-boot:run
```

**OU**

```bash
mvn clean package
java -jar target/Todo-0.0.1-SNAPSHOT.jar
```

### 3. Acesse a aplicação
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:tododb`
  - User: `sa` | Password: `password`

## Build

### Gerar JAR
```bash
mvn clean package
```

### Executar testes
```bash
mvn test
```

## Deploy

### Executar JAR
```bash
java -jar target/Todo-0.0.1-SNAPSHOT.jar
```

*Documentação completa disponível no Swagger UI*