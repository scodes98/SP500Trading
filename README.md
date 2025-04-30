# SP500Trading – Microservices Trading Platform

A modern, cloud-native **trading system** built using:

- Java (Spring Boot)
- gRPC + Protocol Buffers
- PostgreSQL (via Cloud SQL)
- Docker + GCP Cloud Run
- Artifact Registry
- Kreya for gRPC testing

---

## Architecture Overview

Microservices communicate over gRPC:

order-service → matching-engine → trade-executor-service → ledger-service → portfolio-service


Each service is independent, containerized, and deployable to GCP.

---

## 📦 Microservices Breakdown

| Service                 | Description                                                                 |
|-------------------------|-----------------------------------------------------------------------------|
| **order-service**       | Accepts user orders via gRPC and persists them                             |
| **matching-engine**     | Matches incoming orders against existing opposite-side orders (BUY/SELL)   |
| **trade-executor**      | Executes trades, persists them, and sends details to ledger                |
| **ledger-service**      | Records both BUY and SELL entries per trade                                |
| **portfolio-service**   | Aggregates ledger data to return user's holdings                           |

---

## gRPC + Protobuf Integration

- Microservices use gRPC to communicate efficiently.
- `.proto` files defined in a shared `protos` module.
- Java classes and gRPC stubs are auto-generated.
- `@GrpcClient` is used to inject stubs.
- Strong typing + fast binary protocol.
- Protobuf supports schema evolution for long-term compatibility.

---

## Database

- All services persist data in **Cloud SQL** (PostgreSQL).
- Docker-compose uses local Postgres for development.
- Spring Data JPA manages entity persistence.

---

## Deployment Pipeline

1. **Docker Build & Push**
   ```bash
   docker build --platform=linux/amd64 -t order-service -f Dockerfile.order-service .
   docker tag order-service us-central1-docker.pkg.dev/sp500-trading/sp500-trading-repo/order-service
   docker push us-central1-docker.pkg.dev/sp500-trading/sp500-trading-repo/order-service

    Repeat above steps for each microservice.


2. **Deploy to Cloud Run**

    - Deploy each service from Artifact Registry images.
    - Set container port, allow unauthenticated access (for now), and connect to Cloud SQL.

3. **Cloud SQL Setup**

    - PostgreSQL instance created in GCP.
    - Public IP authorized + JDBC URL configured in each service.