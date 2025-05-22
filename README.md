# Order Service

A Spring Boot microservice for managing orders with MongoDB persistence and Kafka messaging integration.

## Tech Stack

- **Java 17**
- **Spring Boot 3.4.5**
- **MongoDB**
- **Apache Kafka**
- **Actuator**
- **SpringDoc OpenAPI**
- **JUnit 5 & Mockito**
- **Maven**

## API Endpoints

### Orders API
- `GET /api/v1/orders/test` - Test connection
- `POST /api/v1/orders/create` - Create new order
- `GET /api/v1/orders/{orderId}` - Get order by ID
- `GET /api/v1/orders?page=0&size=20` - Get all orders with pagination

## Key Implementations

### Event-Driven Architecture
- Kafka Producer for order creation events
- Kafka Consumer for order processing
- Avoids infinite loops in event processing
- Proper error handling for Kafka operations

### Exception Handling
- Global exception handling with `@ControllerAdvice`
- Custom exceptions:
  - `ResourceNotFoundException`
  - `InvalidInputException`
  - `KafkaPublishException`

### API Documentation
- Swagger UI available at `/api/v1/swagger-ui/index.html`
- OpenAPI documentation at `/api/v1/api-docs`

### Testing
- Unit tests for controllers and services
- Mocked dependencies using Mockito
- Test data utilities
- Integration tests for Kafka components

## Running the Application

### Prerequisites
- Java 17
- MongoDB
- Apache Kafka

### Configuration
Key configurations in `application.yaml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/
      database: assignment
  kafka:
    bootstrap-servers: localhost:9092
```

### Build and Run
```
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Running Tests
``` 
# Run test
mvn test
```

## Monitoring and Management
- Spring Boot Actuator endpoints enabled
- Access at `/api/v1/actuator`
- Includes health checks, metrics, etc.

## Error Handling
- Standardized error responses
- HTTP status codes:
  - 200: Success
  - 201: Created
  - 400: Bad Request
  - 404: Not Found
  - 409: Conflict
  - 500: Internal Server Error

