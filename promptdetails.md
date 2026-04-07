# account-master — Prompt History & Development Log

This service manages account products, account opening, account lifecycle, and balance management for the BankSoft CBS platform.

- **Port:** 8082
- **Base URL:** `http://localhost:8082`
- **Database:** PostgreSQL
- **Framework:** Spring Boot 3 / Java 21

---

## Prompt 1 — Initial Service Build

**Prompt:**
> "Build the account-master microservice with full account product and account management APIs."

### Steps Taken
1. Created Spring Boot project with dependencies: Spring Web, Spring Data JPA, PostgreSQL driver, Lombok, Validation, Actuator, SpringDoc OpenAPI
2. Defined domain entities:
   - `AccountProduct` — product catalogue (CURRENT, SAVINGS, LOAN, DEPOSIT, CALL, OVERDRAFT)
   - `ProductParameter` — configurable product parameters
   - `ProductInterestTier` — tiered interest rate bands
   - `ProductCharge` — account fees and charges
   - `AccountMaster` — individual account records
   - `AccountBalance` — real-time balance tracking (ledger, available, cleared)
3. Created DTOs: `ProductRequest`, `ProductResponse`, `AccountOpenRequest`, `AccountOpenResponse`, `AccountBalanceResponse` and sub-DTOs
4. Created repositories, services, and controllers
5. Added `RestClientConfig.java` for inter-service HTTP calls to customer-entity
6. Configured `application.yml` with datasource, JPA settings, server port 8082
7. Added Postman collection `account-master.postman_collection.json`

### Key API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | `/v1/products` | Create account product |
| GET | `/v1/products` | List products (paginated, filterable by type/status) |
| GET | `/v1/products/{id}` | Get product detail |
| POST | `/v1/products/{id}/parameters` | Add product parameter |
| POST | `/v1/products/{id}/tiers` | Add interest tier |
| POST | `/v1/products/{id}/charges` | Add product charge |
| POST | `/v1/accounts` | Open new account |
| GET | `/v1/accounts` | List accounts (paginated) |
| GET | `/v1/accounts/{id}` | Get account detail |
| GET | `/v1/accounts/{id}/balance` | Get account balance |
| PATCH | `/v1/accounts/{id}/status` | Update account status |

---

## Prompt 2 — README Documentation

**Prompt:**
> "Prep readme.md files in git for user-admin and bank ops in respective repos."

### Steps Taken
1. Created `README.md` with service overview, prerequisites, setup instructions, endpoint summary
2. Committed and pushed to GitHub (`banksoft2026/account-master`)

---

## Prompt 3 — UI Integration Fix: Correct Payload Structure

**Prompt:**
> "Enhance bank ops UI to make sure each module is using underlying APIs."

### Root Cause Identified
The `OpenAccountPage` frontend was sending a flat payload to `POST /v1/accounts`. The API expected nested structure with master details under a `master` key.

**Wrong payload (flat):**
```json
{
  "productId": "...",
  "accountName": "...",
  "customerId": "...",
  "branchCode": "..."
}
```

**Correct payload (nested):**
```json
{
  "productId": "...",
  "master": {
    "accountName": "...",
    "customerId": "...",
    "branchCode": "...",
    "riskCategory": "LOW",
    "ownershipType": "SOLE",
    "createdBy": "OPS-PORTAL"
  }
}
```

### Steps Taken
1. Identified the `AccountOpenRequest` DTO structure from the service code
2. Updated `OpenAccountPage.tsx` in `banking-ops-ui` to send the correct nested payload
3. Added new optional fields: `shortName`, `riskCategory`, `ownershipType`, `relationshipManagerId`, `entityId`, `purposeCode`

### Issues Resolved
| Issue | Fix |
|-------|-----|
| Account open returning 400 Bad Request | Fixed payload nesting: `{ productId, master: { ... } }` |
| `currencyCode` field missing on account open | Removed — currency comes from the selected product |

---

## Prompt 4 — Fix CORS Errors

**Prompt:**
> "Errors on UI — CORS policy: No 'Access-Control-Allow-Origin' header on http://localhost:8082"

### Root Cause
No CORS configuration existed. Browser preflight (`OPTIONS`) requests from `http://localhost:5173` were blocked.

### Steps Taken
1. Created `src/main/java/com/banking/cbs/account/common/config/WebConfig.java`
2. Implemented `WebMvcConfigurer.addCorsMappings()` allowing all origins, methods, headers
3. Committed and pushed to GitHub

### File Added
```
src/main/java/com/banking/cbs/account/common/config/WebConfig.java
```

### Verification
```bash
curl -I -X OPTIONS http://localhost:8082/v1/accounts \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET"
# Returns: Access-Control-Allow-Origin: http://localhost:5173
```

### Issues Resolved
| Issue | Fix |
|-------|-----|
| CORS blocked for all endpoints | Added `WebConfig.java` implementing `WebMvcConfigurer` |

---

## Prompt 5 — Service Restart

**Prompt:**
> "Restart all services after CORS fix."

### Steps Taken
1. Existing process on port 8082 killed
2. `git pull` — already up to date
3. Started with Maven + Java 21 — **success**
4. Service confirmed UP on port 8082

### Issues Resolved
| Issue | Fix |
|-------|-----|
| No `mvnw` file | Used full path to Maven |
| Java 25 incompatible with compiler plugin | Used Java 21 |

---

## Key DTOs

### AccountOpenRequest
| Field | Required | Notes |
|-------|----------|-------|
| productId | ✓ | UUID of the AccountProduct |
| master.accountName | ✓ | Display name for the account |
| master.customerId | ✓ | UUID of the owning customer |
| master.branchCode | ✓ | Opening branch code |
| master.riskCategory | ✓ | LOW / MEDIUM / HIGH |
| master.ownershipType | ✓ | SOLE / JOINT / CORPORATE / TRUST |
| master.createdBy | ✓ | Operator ID |
| master.shortName | | Short label |
| master.entityId | | UUID if account linked to entity |
| master.purposeCode | | Account purpose code |
| master.relationshipManagerId | | Assigned RM |

### ProductRequest
| Field | Required | Notes |
|-------|----------|-------|
| productCode | ✓ | Unique code e.g. SAV-001 |
| productName | ✓ | |
| accountType | ✓ | CURRENT/SAVINGS/LOAN/DEPOSIT/CALL/OVERDRAFT |
| targetSegment | ✓ | RETAIL/SME/CORPORATE/PRIVATE/ALL |
| currencyCode | ✓ | ISO 4217 |
| effectiveFrom | ✓ | Date |
| createdBy | ✓ | |
| description | | |
| allowJoint | | boolean |
| allowCorporate | | boolean |
| allowIndividual | | boolean |
| minOpeningBalance | | decimal |
| maxBalance | | decimal |
| maxAccountPerCustomer | | integer |
| effectiveTo | | Date |
