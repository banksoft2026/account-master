# Account Master — Core Banking System

An **Account Product Master and Account Master** microservice built with Spring Boot 3 and PostgreSQL. It manages the full lifecycle of account products and individual customer accounts, integrating with the CBS Common Maintenance and Customer & Entity services.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Service Architecture](#service-architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Reference](#api-reference)
  - [1. Account Product Master](#1-account-product-master)
  - [2. Account Master](#2-account-master)
- [Business Rules](#business-rules)
- [Cross-Service Integration](#cross-service-integration)
- [Swagger UI](#swagger-ui)
- [Postman Collection](#postman-collection)
- [Response Format](#response-format)
- [Error Handling](#error-handling)
- [Configuration](#configuration)

---

## Overview

This service implements two core modules:

| Module | Description |
|---|---|
| **Account Product Master** | Define and manage account product templates with parameters, interest tiers, and charge schedules |
| **Account Master** | Open and manage individual accounts against a product, with parameter overrides, balance management, and earmarks |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.2.4 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 18 |
| Migrations | Flyway 10.10.0 |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok 1.18.36 |
| API Docs | SpringDoc OpenAPI 2.4.0 (Swagger UI) |
| Build | Apache Maven 3.9.x |

---

## Service Architecture

This service is the third microservice in the CBS suite and depends on the other two:

| Service | Port | Database | Repo |
|---|---|---|---|
| CBS Common Maintenance | 8080 | `cbs_maintenance` | [banksoft2026/common-maintenance](https://github.com/banksoft2026/common-maintenance) |
| Customer & Entity | 8081 | `customer_entity` | [banksoft2026/customer-entity](https://github.com/banksoft2026/customer-entity) |
| **Account Master** | **8082** | **`account_master_db`** | [banksoft2026/account-master](https://github.com/banksoft2026/account-master) |

---

## Prerequisites

- **Java 21** — [Download Temurin 21](https://adoptium.net/)
- **Maven 3.9+** — [Download Maven](https://maven.apache.org/download.cgi)
- **PostgreSQL 18** — running locally on port `5432`
- **CBS Common Maintenance** running on port `8080` (for numbering, branch, currency validation)
- **Customer & Entity** running on port `8081` (for entity eligibility checks on account opening)

---

## Getting Started

### 1. Create the database

```sql
CREATE DATABASE account_master_db;
```

### 2. Configure credentials

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/account_master_db
    username: postgres
    password: your_password
```

### 3. Run the application

```bash
export JAVA_HOME=/path/to/jdk-21
mvn spring-boot:run
```

Flyway will automatically apply all 10 migrations on first run.

### 4. Verify

```bash
curl http://localhost:8082/v1/products
```

Expected:
```json
{ "success": true, "message": "Products retrieved.", "data": [] }
```

---

## Project Structure

```
account-master/
├── pom.xml
└── src/
    └── main/
        ├── java/com/banking/cbs/account/
        │   ├── AccountMasterApplication.java
        │   ├── common/
        │   │   ├── client/
        │   │   │   ├── CbsMaintenanceClient.java     # Calls port 8080
        │   │   │   └── CustomerEntityClient.java     # Calls port 8081
        │   │   ├── config/
        │   │   │   ├── OpenApiConfig.java
        │   │   │   └── RestClientConfig.java         # RestTemplate with timeouts
        │   │   ├── exception/
        │   │   │   ├── CbsException.java
        │   │   │   └── GlobalExceptionHandler.java
        │   │   └── response/ApiResponse.java
        │   ├── product/                              # Account Product Master
        │   │   ├── controller/ProductController.java
        │   │   ├── dto/                              # 7 DTOs
        │   │   ├── entity/                           # AccountProduct, ProductParameter,
        │   │   │                                     # ProductInterestTier, ProductCharge
        │   │   ├── repository/                       # 4 repositories
        │   │   └── service/ProductService.java
        │   └── account/                              # Account Master
        │       ├── controller/AccountController.java
        │       ├── dto/                              # 13 DTOs
        │       ├── entity/                           # AccountMaster, AccountBalance,
        │       │                                     # AccountParameters, AccountParameterOverride,
        │       │                                     # AccountEarmark, AccountLedger
        │       ├── repository/                       # 6 repositories
        │       └── service/AccountService.java
        └── resources/
            ├── application.yml
            └── db/migration/
                ├── V1__account_product.sql
                ├── V2__product_parameter.sql
                ├── V3__product_interest_tier.sql
                ├── V4__product_charge.sql
                ├── V5__account_master.sql
                ├── V6__account_parameters.sql
                ├── V7__account_parameter_override.sql
                ├── V8__account_balance.sql
                ├── V9__account_earmark.sql
                └── V10__account_ledger.sql
```

---

## Database Schema

10 tables across 10 Flyway migrations:

| Table | Migration | Description |
|---|---|---|
| `account_product` | V1 | Product template with lifecycle state machine |
| `product_parameter` | V2 | Configurable parameters per product (rates, limits, flags) |
| `product_interest_tier` | V3 | Tiered interest rates by balance range |
| `product_charge` | V4 | Fee and charge schedules (maintenance, transaction, penalty, etc.) |
| `account_master` | V5 | Individual account records with denormalised balance cache |
| `account_parameters` | V6 | Resolved effective parameters per account |
| `account_parameter_override` | V7 | Audit log of account-level parameter overrides |
| `account_balance` | V8 | Authoritative balance store (optimistic locking) |
| `account_earmark` | V9 | Funds earmarked (reserved) against available balance |
| `account_ledger` | V10 | Ledger entries for statement and history queries |

All tables use:
- **UUID primary keys** (`@UuidGenerator`)
- **Optimistic locking** (`@Version`)
- **Audit columns** (`created_by`, `created_at`, `updated_by`, `updated_at`)

---

## API Reference

All endpoints return a standard `ApiResponse<T>` wrapper.

Base URL: `http://localhost:8082`

---

### 1. Account Product Master

#### Product CRUD

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/v1/products` | Create product with parameters, tiers, and charges |
| `GET` | `/v1/products` | List all products |
| `GET` | `/v1/products/{productId}` | Get product with full details |
| `PATCH` | `/v1/products/{productId}` | Update product mutable fields |
| `PUT` | `/v1/products/{productId}/status` | Transition lifecycle status |

**Product lifecycle state machine:**
```
DRAFT → ACTIVE → SUSPENDED → ACTIVE (re-activate)
                           ↓
                        RETIRED  ← terminal, cannot re-enter
```

- `DRAFT` → `ACTIVE`: requires at least one parameter
- `ACTIVE` → `SUSPENDED`: suspends new account openings
- `SUSPENDED` → `ACTIVE`: re-activates
- `* → RETIRED`: terminal — no further transitions allowed

**Create product request example:**
```json
{
  "productCode": "CORP-CURRENT-GBP-001",
  "productName": "Corporate Current Account GBP",
  "productType": "CURRENT",
  "currency": "GBP",
  "description": "Corporate current account in GBP",
  "effectiveFrom": "2026-01-01",
  "createdBy": "admin",
  "parameters": [
    {
      "paramKey": "MIN_OPENING_BALANCE",
      "valueType": "DECIMAL",
      "defaultValue": "1000.00",
      "minValue": "500.00",
      "maxValue": "50000.00",
      "overridableAtAccount": true,
      "mandatory": true
    },
    {
      "paramKey": "OVERDRAFT_LIMIT",
      "valueType": "DECIMAL",
      "defaultValue": "0.00",
      "minValue": "0.00",
      "maxValue": "500000.00",
      "overridableAtAccount": true,
      "mandatory": false
    }
  ],
  "interestTiers": [
    {
      "tierSequence": 1,
      "minBalance": "0.00",
      "maxBalance": "99999.99",
      "annualRate": "0.10",
      "effectiveFrom": "2026-01-01"
    }
  ],
  "charges": [
    {
      "chargeType": "MAINTENANCE",
      "chargeName": "Monthly Maintenance Fee",
      "chargeAmount": "25.00",
      "currency": "GBP",
      "frequency": "MONTHLY",
      "waivable": true,
      "minBalanceForWaiver": "10000.00"
    }
  ]
}
```

#### Product Parameters

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/v1/products/{productId}/parameters` | Add parameter to product |
| `DELETE` | `/v1/products/{productId}/parameters/{paramKey}` | Remove parameter (only if no active accounts) |

**Value types:** `DECIMAL` | `RATE` | `INTEGER` | `BOOLEAN` | `ENUM:val1,val2,...`

#### Product Interest Tiers

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/v1/products/{productId}/tiers` | Add interest tier |
| `GET` | `/v1/products/{productId}/tiers` | List tiers |
| `DELETE` | `/v1/products/{productId}/tiers/{tierId}` | Remove tier |

#### Product Charges

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/v1/products/{productId}/charges` | Add charge |
| `GET` | `/v1/products/{productId}/charges` | List charges |
| `DELETE` | `/v1/products/{productId}/charges/{chargeId}` | Remove charge |

**Charge types:** `MAINTENANCE` | `TRANSACTION` | `PENALTY` | `DORMANCY` | `PAPER_STATEMENT` | `CLOSURE`

---

### 2. Account Master

#### Account Lifecycle

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/v1/accounts` | Open new account |
| `GET` | `/v1/accounts` | List accounts (filter by entityId, productId, status) |
| `GET` | `/v1/accounts/{accountId}` | Get full account details |
| `PATCH` | `/v1/accounts/{accountId}` | Update account mutable fields |
| `PUT` | `/v1/accounts/{accountId}/status` | Transition account status |

**Account status state machine:**
```
PENDING_ACTIVATION → ACTIVE → DORMANT ↔ ACTIVE
                            → FROZEN ↔ ACTIVE
                            → SUSPENDED ↔ ACTIVE
                            → CLOSED  ← terminal
                            → UNCLAIMED
```

**Open account request example:**
```json
{
  "entityId": "uuid-of-entity",
  "productId": "uuid-of-product",
  "branchCode": "HQ-001",
  "currencyCode": "GBP",
  "accountName": "Acme Corp Operating Account",
  "openingBalance": "5000.00",
  "createdBy": "admin",
  "overrides": [
    {
      "paramKey": "OVERDRAFT_LIMIT",
      "overriddenValue": "25000.00",
      "reason": "Corporate credit facility approved",
      "changedBy": "admin"
    }
  ]
}
```

**Entity eligibility checks on account opening (6 checks):**

| Check | Required | Error if fails |
|---|---|---|
| Entity status | `ACTIVE` | `ENTITY_NOT_ACTIVE` |
| KYB status | `VERIFIED` | `KYB_NOT_VERIFIED` |
| Sanctions screening | not `FLAGGED` | `ENTITY_SANCTIONS_FLAGGED` |
| Authorised signatory | at least 1 active | `NO_AUTHORISED_SIGNATORY` |
| Mandatory documents | all present & unexpired | `MANDATORY_DOCS_MISSING` |
| Compliance review date | not expired | `COMPLIANCE_REVIEW_EXPIRED` |

If any checks fail, a structured error is returned:
```json
{
  "error": "ENTITY_NOT_ELIGIBLE_FOR_ACCOUNT_OPENING",
  "failed_checks": [
    { "check": "KYB_STATUS", "current": "EXPIRED", "required": "VERIFIED" },
    { "check": "MANDATORY_DOCS", "missing": ["REGISTER_OF_SHAREHOLDERS"] }
  ]
}
```

#### Account Parameters

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/v1/accounts/{accountId}/parameters` | Resolved parameters (product defaults + account overrides) |
| `POST` | `/v1/accounts/{accountId}/parameters/override` | Apply parameter override |
| `GET` | `/v1/accounts/{accountId}/parameters/overrides` | Override history |

**Parameter resolution:** For each product parameter, the service checks for an active account-level override. Response includes `source` field: `ACCOUNT_OVERRIDE` or `PRODUCT_DEFAULT`.

**Only one active override per parameter** — applying a new override automatically expires the previous one. Constraint enforced via partial unique index:
```sql
CREATE UNIQUE INDEX idx_override_active ON account_parameter_override(account_id, param_key) WHERE is_active = TRUE
```

#### Balance and Earmarks

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/v1/accounts/{accountId}/balance` | Get authoritative balance |
| `POST` | `/v1/accounts/{accountId}/earmarks` | Place earmark (reserve funds) |
| `DELETE` | `/v1/accounts/{accountId}/earmarks/{earmarkId}` | Release earmark |
| `GET` | `/v1/accounts/{accountId}/earmarks` | List active earmarks |

**Balance components:**
- `ledgerBalance` — total funds in the account
- `earmarkedAmount` — sum of all active earmarks
- `overdraftLimit` — maximum debit headroom below zero
- `availableBalance` = `ledgerBalance` − `earmarkedAmount` + `overdraftLimit`

Placing an earmark validates: `availableBalance >= earmarkAmount` before proceeding.

#### Statement / Ledger

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/v1/accounts/{accountId}/ledger` | Ledger entries (filter by date range, type) |

---

## Business Rules

### Product Rules

1. A product can only be activated if it has at least one parameter defined.
2. `RETIRED` is a terminal status — no transitions out of it.
3. A parameter cannot be deleted if there are active accounts using that product.
4. Interest tiers must not have overlapping balance ranges within the same effective period.
5. Only one charge per `chargeType` is recommended per product.

### Account Opening Rules

1. Product must be in `ACTIVE` lifecycle status.
2. Account `currencyCode` must match the product `currency`.
3. Branch must be active in CBS Maintenance (port 8080).
4. Currency must be active in CBS Maintenance (port 8080).
5. Entity must pass all 6 eligibility checks from Customer & Entity service (port 8081).
6. Account number is auto-generated via CBS Maintenance numbering scheme (`ACCOUNT` entity type).
7. Opening balance must be ≥ product `MIN_OPENING_BALANCE` parameter (if defined).
8. Parameter overrides provided at opening are validated against `min_value`/`max_value` bounds.

### Account Status Rules

1. `PENDING_ACTIVATION` → `ACTIVE`: Initial activation.
2. `ACTIVE` → `DORMANT`: No transactions for configured dormancy period.
3. `DORMANT` → `ACTIVE`: Re-activated by transaction or manual request.
4. `ACTIVE` / `DORMANT` → `FROZEN`: Regulatory or compliance freeze.
5. `FROZEN` → `ACTIVE`: Freeze lifted.
6. `ACTIVE` / `SUSPENDED` → `CLOSED`: Terminal — no further transitions.
7. `CLOSED` accounts cannot be re-opened.

---

## Cross-Service Integration

### CBS Maintenance (port 8080)

| Operation | Endpoint | Behaviour if unavailable |
|---|---|---|
| Generate account number | `POST /v1/config/numbering/ACCOUNT/next` | Fallback: `ACC-{timestamp}` |
| Validate branch active | `GET /v1/config/branches/{code}` | Fallback: assume active (log warning) |
| Validate currency active | `GET /v1/config/currencies/{code}` | Fallback: assume active (log warning) |

CBS Maintenance calls fail **gracefully** — the account opening proceeds with fallback behaviour.

### Customer & Entity (port 8081)

| Operation | Endpoint | Behaviour if unavailable |
|---|---|---|
| Get entity summary | `GET /v1/entities/{entityId}/summary` | Throws `503 SERVICE_UNAVAILABLE` |
| Get compliance info | `GET /v1/entities/{entityId}/compliance` | Throws `503 SERVICE_UNAVAILABLE` |
| Get entity links | `GET /v1/entities/{entityId}/customers` | Throws `503 SERVICE_UNAVAILABLE` |
| Get entity documents | `GET /v1/entities/{entityId}/documents` | Throws `503 SERVICE_UNAVAILABLE` |

Customer & Entity calls are **critical** — account opening fails if the service is unreachable.

**Timeout configuration:** 3 seconds connect / 5 seconds read.

---

## Swagger UI

Interactive API documentation:

```
http://localhost:8082/swagger-ui.html
```

Raw OpenAPI JSON spec:

```
http://localhost:8082/v3/api-docs
```

---

## Postman Collection

A ready-to-use Postman collection is included at the root:

```
account-master.postman_collection.json
```

**How to import:**
1. Open Postman → click **Import**
2. Select `account-master.postman_collection.json`
3. Run requests **top to bottom** — each POST auto-saves its ID for subsequent requests

**Auto-saved variables:** `productId`, `paramKey`, `tierId`, `chargeId`, `accountId`, `earmarkId`

**Folders:**
1. Product — Create & Manage Products
2. Product Parameters
3. Product Interest Tiers
4. Product Charges
5. Account — Open & Manage Accounts
6. Account Parameters & Overrides
7. Account Balance & Earmarks
8. Account Ledger

---

## Response Format

All endpoints return a consistent `ApiResponse<T>` envelope:

```json
{
  "success": true,
  "message": "Operation successful.",
  "data": {},
  "errorCode": null,
  "timestamp": "2026-04-05T10:00:00Z"
}
```

**Error response:**
```json
{
  "success": false,
  "message": "Product is RETIRED and cannot be reactivated.",
  "errorCode": "INVALID_STATUS_TRANSITION",
  "timestamp": "2026-04-05T10:00:00Z"
}
```

---

## Error Handling

| HTTP Status | Error Code | Cause |
|---|---|---|
| `400` | `INVALID_REQUEST` | Validation failure |
| `400` | `INVALID_STATUS_TRANSITION` | Illegal state machine transition |
| `400` | `OVERRIDE_OUT_OF_BOUNDS` | Override value outside min/max range |
| `400` | `INSUFFICIENT_BALANCE` | Earmark exceeds available balance |
| `400` | `CURRENCY_MISMATCH` | Account currency does not match product currency |
| `404` | `NOT_FOUND` | Resource does not exist |
| `422` | `ENTITY_NOT_ELIGIBLE` | Entity failed one or more eligibility checks |
| `422` | `PRODUCT_NOT_ACTIVE` | Product is not in ACTIVE lifecycle status |
| `503` | `ENTITY_SERVICE_UNAVAILABLE` | Customer & Entity service unreachable |
| `500` | `INTERNAL_ERROR` | Unexpected server error |

---

## Configuration

Full `application.yml` reference:

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/account_master_db
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

integration:
  cbs-maintenance:
    base-url: http://localhost:8080
  customer-entity:
    base-url: http://localhost:8081

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true

logging:
  level:
    com.banking.cbs: DEBUG
    org.flywaydb: INFO
```

---

## GitHub Repository

[https://github.com/banksoft2026/account-master](https://github.com/banksoft2026/account-master)
