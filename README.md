E-commerce API (Product module)

Overview
- Small, modular Spring Boot application focused on Product domain for portfolio.

Architecture
- Package-by-domain: com.miguelpedraza.ecommerce.product with domain, application, infrastructure, presentation layers.

How to run
- Configure Postgres and run with Gradle: gradlew bootRun
- Flyway migrations located in src/main/resources/db/migration
- Docker: build and run with docker-compose (example):
  - docker compose up --build
  - Access API at http://localhost:8080

Next steps
- Add users, cart, orders, payments modules incrementally.
