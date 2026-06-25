# E-Commerce Microservices

A Spring Boot 3.2 / Java 17 microservices backend for a small e-commerce domain. The project uses Eureka for service discovery, Spring Cloud Gateway for routing, PostgreSQL for persistence, JWT-based authentication, and Kafka for event publishing.

## Architecture

```text
Clients
  |
  v
API Gateway (:8080)
  |- /api/products/**  -> product-service (:8081)
  |- /api/orders/**    -> order-service (:8082)
  |- /api/inventory/** -> inventory-service (:8083)
  |- /api/auth/**      -> user-service (:8084)
  `- /api/users/**     -> user-service (:8084)

Service Registry (Eureka) :8761
Kafka :9092
PostgreSQL :5432
```

## Current Status

- `product-service`, `order-service`, `inventory-service`, `user-service`, `api-gateway`, and `service-registry` are containerized and wired through Docker Compose.
- `order-service` performs a synchronous inventory availability check through OpenFeign before creating an order.
- `order-service` publishes an `order-created` Kafka event after a successful order save.
- Kafka consumption is not implemented yet in `inventory-service`, so the event-driven stock update flow is only partially implemented right now.

## Tech Stack

| Category | Technology |
| --- | --- |
| Runtime | Java 17, Spring Boot 3.2.5, Spring Cloud 2023.0.1 |
| Discovery | Netflix Eureka |
| Gateway | Spring Cloud Gateway |
| Security | Spring Security, JWT (`jjwt`), BCrypt |
| Sync communication | OpenFeign |
| Async communication | Apache Kafka (`confluentinc/cp-kafka:7.6.0`) |
| Database | PostgreSQL 16, Spring Data JPA, Hibernate |
| Build | Maven multi-module |
| Containers | Docker, Docker Compose |

## Services

### `service-registry`

Runs Eureka Server. Other services register themselves here and are discovered by logical service name.

### `api-gateway`

Single entry point for clients.

- Routes requests to downstream services using Eureka
- Validates JWT tokens
- Enforces route-level authorization rules
- Forwards authenticated user metadata downstream through headers

### `product-service`

Manages product catalog data.

Implemented endpoints:

- `POST /api/products`
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/category/{category}`

### `order-service`

Handles order creation and retrieval.

- Checks stock synchronously through `inventory-service`
- Saves the order in PostgreSQL
- Publishes `order-created` to Kafka

Implemented endpoints:

- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{orderNumber}`
- `GET /api/orders/customer/{email}`

### `inventory-service`

Manages stock records by SKU.

Implemented endpoints:

- `POST /api/inventory`
- `GET /api/inventory`
- `GET /api/inventory/{sku}?quantity=N`
- `GET /api/inventory/{sku}/details`

Note: it currently exposes inventory APIs but does not yet consume Kafka events.

### `user-service`

Handles registration, login, and user management.

- Issues JWT tokens
- Hashes passwords with BCrypt
- Supports `ROLE_USER` and `ROLE_ADMIN`

Implemented endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/users`
- `GET /api/users`
- `GET /api/users/me`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

## Database Per Service

| Service | Database | Tables |
| --- | --- | --- |
| product-service | `product_db` | `products` |
| order-service | `order_db` | `orders`, `order_items` |
| inventory-service | `inventory_db` | `inventory` |
| user-service | `user_db` | `users` |

Databases are created by [initdb/init-db.sql](/C:/Users/nouri/zeyad/micro/initdb/init-db.sql).

## Gateway Authorization Rules

The gateway currently applies these rules:

- `POST /api/auth/**` and `GET /api/products/**` are public
- `POST`, `PUT`, and `DELETE` on `/api/products/**` require `ADMIN`
- `/api/inventory/**` requires `ADMIN`
- `/api/orders/**` requires authentication
- `GET /api/users/**` requires `ADMIN`
- `/api/users/me` requires authentication
- other routes require authentication

If a request is rejected by the gateway, it returns JSON error bodies for `401` and `403`.

## Quick Start

### Prerequisites

- Docker
- Docker Compose
- Java 17+
- Maven (for local non-container runs)

### Run Everything with Docker

From the project root:

```bash
docker compose up -d --build
```

Useful checks:

```bash
docker compose ps
docker logs -f api-gateway
docker logs -f service-registry
```

If the gateway returns `503 Service Unavailable` immediately after startup, wait a few seconds and retry. Eureka registration can lag briefly while services are still booting.

### Local Development Scripts

The repo includes PowerShell helpers in [scripts](/C:/Users/nouri/zeyad/micro/scripts):

- `run-infra.ps1`
- `run-service-registry.ps1`
- `run-product-service.ps1`
- `run-order-service.ps1`
- `run-inventory-service.ps1`
- `run-user-service.ps1`
- `run-api-gateway.ps1`
- `run-all.ps1`

Typical local flow:

```powershell
.\scripts\run-infra.ps1
.\scripts\run-service-registry.ps1
.\scripts\run-product-service.ps1
.\scripts\run-order-service.ps1
.\scripts\run-inventory-service.ps1
.\scripts\run-user-service.ps1
.\scripts\run-api-gateway.ps1
```

### Build from Source

From the root module:

```bash
mvn clean package -DskipTests
```

## Example Requests

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Zeyad Nouri",
    "username": "zeyad",
    "email": "zeyad@example.com",
    "password": "secret123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zeyad",
    "password": "secret123"
  }'
```

### Public Product Read

```bash
curl http://localhost:8080/api/products
```

### Protected Product Create

This route requires an admin token at the gateway.

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <admin-jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 99.99,
    "sku": "SKU-001",
    "category": "Electronics"
  }'
```

### Create Order

This route requires a valid JWT.

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "zeyad@example.com",
    "items": [
      {
        "productSku": "SKU-001",
        "quantity": 1,
        "unitPrice": 99.99
      }
    ]
  }'
```

## Kafka

Current Kafka usage:

- Producer: `order-service`
- Topic: `order-created`
- Payload: `OrderCreatedEvent`

At the moment, Kafka is used for publishing order events only. Inventory-side consumption and stock reservation via Kafka are still a next step.

## Configuration

Important environment variables:

| Variable | Description |
| --- | --- |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL |
| `SPRING_DATASOURCE_URL` | Per-service PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Shared HMAC signing secret for `user-service` and `api-gateway` |
| `JWT_EXPIRATION` | JWT lifetime in milliseconds |

Security note:

- The default JWT secret in Compose is for development only.
- `api-gateway` and `user-service` must use the same `JWT_SECRET`.

## Project Structure

```text
.
|-- pom.xml
|-- docker-compose.yml
|-- README.md
|-- initdb/
|-- scripts/
|-- service-registry/
|-- api-gateway/
|-- product-service/
|-- order-service/
|-- inventory-service/
`-- user-service/
```
