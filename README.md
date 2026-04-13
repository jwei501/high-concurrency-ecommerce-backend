# High Availability Online Shopping System

Spring Boot backend system designed to handle high concurrency.

## Tech Stack
- Java, Spring Boot
- MySQL, Redis
- RocketMQ
- Sentinel

## Highlights
- Prevent overselling with distributed locks
- Cache optimization (Redis)
- Message queue for async processing
- Stress tested with JMeter
- Sentinel for rate limiting

## How to Run
mvn spring-boot:run
