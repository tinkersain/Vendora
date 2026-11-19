# VENDORA: Evaluation Preparation & Technical Defense Master Study Guide

> **Academic Project Evaluation & Defense Manual**  
> **Platform**: Vendora — Campus Microservice Ecosystem  
> **Target Date**: Project Evaluation Day  
> **Architecture**: Spring Boot 3.x, Spring Cloud (Eureka + Gateway), Spring Security, Stateless JWT, MySQL 8.0, JUnit 5, Mockito, JaCoCo, SonarQube  
> **Quality Metrics**: 100% Quality Gate Passed | 0 Bugs | 0 Vulnerabilities | 0 Code Smells | 343 Unit Tests | >89% Code Coverage  

---

## TABLE OF CONTENTS
1. [Project Introduction & Presentation Preparation](#1-project-introduction--presentation-preparation)
   - Professional Introduction Script
   - Problem Statement, Objectives, Target Users & Key Features
   - Slide-by-Slide PPT Walkthrough (Slides 1–16) with Diagram Analyses
   - Monolithic vs. Microservices Architecture Justification
2. [Complete Project Architecture & Microservice Deep Dive](#2-complete-project-architecture--microservice-deep-dive)
   - Core Concepts: Microservices, Spring Boot, Service Discovery, API Gateway
   - Inter-Service Communication & Shared DB Logical Isolation
   - Enterprise Layering Pattern (Controller, Service, Repository, Entity, DTO)
   - Detailed Specification for All 8 Components (Eureka, Gateway, Auth, User, Marketplace, Scrap, Helpdesk, Payment)
3. [Authentication & Authorization Deep Dive](#3-authentication--authorization-deep-dive)
   - Authn vs. Authz, JWT Structure (Header, Payload, Signature)
   - BCrypt Hashing, 256-bit Secret Keys & HMAC-SHA256
   - Complete Registration, Login, Token Issuance & Gateway Identity Injection Flow
   - Role-Based Access Control (RBAC), 401 vs. 403, Public vs. Protected Endpoints
4. [Annotation-by-Annotation Master Catalog](#4-annotation-by-annotation-master-catalog)
   - Complete dissection of 35+ annotations used across Vendora
   - Meaning, Vendora usage, internal JVM/Spring mechanics, mandatory status, alternatives & code snippets
5. [Controller & Code-Level Execution Walkthrough](#5-controller--code-level-execution-walkthrough)
   - Line-by-line controller execution, dependency injection (Constructor vs. Field)
   - DTO encapsulation, ResponseEntity, Optional handling, Stream API, AOP Logging
6. [Global Exception Handling Architecture](#6-global-exception-handling-architecture)
   - Checked vs. Unchecked exceptions, @RestControllerAdvice + @ExceptionHandler
   - Standardized JSON schema, line-by-line GlobalExceptionHandler breakdown
7. [Inter-Service Communication & Declarative REST](#7-inter-service-communication--declarative-rest)
   - Declarative REST with Spring Cloud OpenFeign vs. RestTemplate vs. WebClient
   - Discovery integration, circuit breakers, synchronous vs. asynchronous messaging
8. [Database Architecture & ER Diagram Walkthrough](#8-database-architecture--er-diagram-walkthrough)
   - MySQL 8.0 schema (`vendora_db`), table-by-table schema analysis
   - Logical domain isolation, reference keys without physical foreign key constraints
9. [Testing, Quality Assurance & SonarQube Metrics](#9-testing-quality-assurance--sonarqube-metrics)
   - JUnit 5 & Mockito testing framework, Unit vs. Integration testing
   - JaCoCo bytecode coverage, SonarQube Quality Gate benchmarks & resolved rules
10. [Configuration, Startup & Deployment Operations](#10-configuration-startup--deployment-operations)
    - application.properties breakdown, startup sequence, verification commands, Swagger UI
11. [Architectural Defense: "Why Did We Implement It This Way?"](#11-architectural-defense-why-did-we-implement-it-this-way)
    - 15 core architectural trade-offs, advantages, disadvantages & alternatives
12. [Evaluation Viva Voce: 80 Comprehensive Technical Questions & Answers](#12-evaluation-viva-voce-80-comprehensive-technical-questions--answers)
    - Core Spring, Microservices, Security, DB, Exception Handling, Testing & Tricky Follow-Ups
13. [Live Demonstration Step-by-Step Playbook](#13-live-demonstration-step-by-step-playbook)
    - Exact chronological demo script with endpoints, request payloads & expected responses

---

# 1. Project Introduction & Presentation Preparation

### Professional Introduction Script
> *"Good morning / afternoon, respected evaluators and professors.  
> My name is [Your Name], and along with my team members, I am proud to present **Vendora: Your Campus. Your Community. One Platform.**  
>
> Vendora is an enterprise-grade, distributed microservices platform built using **Java 17, Spring Boot 3, and Spring Cloud**. It addresses the chronic digital fragmentation found across modern university campuses by consolidating student-to-student peer commerce, recyclable scrap collection, free senior-to-junior hostel giveaways, and facility maintenance ticketing into a unified, secure, and reactive digital ecosystem.  
>
> Over the course of this presentation, we will walk you through our architectural design, explain our zero-trust authentication perimeter, demonstrate how our six specialized domain microservices collaborate via dynamic service discovery, showcase our test-driven development backed by **343 automated unit tests with over 89% code coverage**, and present our live, running system."*

---

### Problem Statement, Objectives, Target Users & Features

| Dimension | Description |
| :--- | :--- |
| **Problem Statement** | Campus life is plagued by fragmented, informal channels. Student commerce relies on unorganized WhatsApp/Telegram groups riddled with scams and spam; recyclable study materials and hostel waste are discarded into municipal landfills due to lack of collection logistics; hostel facility breakdowns (electrical, plumbing, Wi-Fi) depend on paper registers with zero transparency or SLA tracking; and campus transactions lack idempotency safeguards. |
| **Primary Objectives** | 1. Unify campus life into one reactive, single-sign-on platform.<br/>2. Guarantee zero-trust security where client identity is cryptographically validated at the API Gateway.<br/>3. Promote campus sustainability through doorstep scrap pickups and free item giveaways.<br/>4. Modernize facility maintenance with automated ticket state transitions and role-based staff assignments.<br/>5. Maintain enterprise code quality verified by continuous SonarQube inspection. |
| **Target Users** | • **Students & Faculty**: Buy/sell campus items, request recyclable scrap pickup, claim free giveaways, lodge maintenance tickets.<br/>• **Maintenance Technicians**: Update ticket progress, record resolution notes, close tickets.<br/>• **Scrap Collectors**: Manage doorstep pickup schedules, verify collected material quantities.<br/>• **Campus Administrators**: Govern accounts, elevate staff permissions, review audit trails. |
| **Core Features** | 1. **Central Gateway & Discovery**: Spring Cloud Gateway (:8080) + Netflix Eureka (:8761).<br/>2. **Stateless JWT Security**: HMAC-SHA256 tokens, BCrypt password hashing (cost factor 10).<br/>3. **Peer-to-Peer Marketplace**: Categorized listings, dynamic multi-attribute search, buyer offers.<br/>4. **Circular Economy Hub**: Doorstep recyclable pickup scheduling + free senior-to-junior giveaways.<br/>5. **Facilities Helpdesk**: Deterministic state machine (`OPEN` $\to$ `IN_PROGRESS` $\to$ `RESOLVED` $\to$ `CLOSED`).<br/>6. **Idempotent Payment Engine**: Header-driven `Idempotency-Key` validation preventing double-billing. |

---

### Slide-by-Slide PPT Walkthrough (Slides 1–16)

#### SLIDE 1: Title & Executive Overview
- **Visuals**: Modern banner, Vendora logo, architecture tech stack tags, SonarQube Quality Gate badge.
- **Speaking Script**:
  > *"Slide 1 introduces Vendora. We designed Vendora as a cloud-ready, production-grade campus microservice ecosystem. We did not build a monolithic demo; our system consists of 8 independent services adhering to high engineering standards: 100% Quality Gate passed, 0 SonarQube violations, and over 89% automated test coverage across all domain modules."*

#### SLIDE 2: Requirement Understanding & Campus Problem Statement
- **Visuals**: Comparison matrix showing existing fragmented channels vs. Vendora's unified solution.
- **Speaking Script**:
  > *"On Slide 2, we highlight the requirements driving our design. Currently, students rely on informal chat groups for second-hand books and cycles, leaving them vulnerable to fraud. Hostel maintenance requests are scribbled in physical registers where they get lost. Vendora solves this by providing dedicated domain microservices with role-based access control, real-time status tracking, and financial transaction safeguards."*

#### SLIDE 3: System Architecture & Infrastructure Design
- **Visuals**: Three-tier architecture diagram ([diag_arch.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_arch.png)) showing Client $\to$ Gateway (:8080) $\to$ Eureka (:8761) $\to$ 6 Microservices (:8081–:8086) $\to$ MySQL 8.0.
- **Diagram Analysis**:
  - **Edge Tier**: Spring Cloud Gateway acts as the single reverse proxy and security perimeter. Netflix Eureka maintains dynamic ephemeral service registrations.
  - **Domain Microservices Tier**: 6 decoupled services running independently on dedicated ports.
  - **Data Tier**: Single MySQL instance (`vendora_db`) enforcing logical schema isolation with zero cross-service foreign keys.
- **Speaking Script**:
  > *"Slide 3 illustrates our three-tier microservices architecture. Downstream microservices are completely isolated from direct public access. External clients communicate exclusively through our Spring Cloud API Gateway on port 8080. The Gateway discovers service locations dynamically via Netflix Eureka on port 8761, eliminating hardcoded hostnames and enabling horizontal scaling."*

#### SLIDE 4: Platform Use Case Diagram
- **Visuals**: UML Use Case diagram ([diag_usecase.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_usecase.png)) detailing actors (Student, Faculty, Maintenance Staff, Scrap Collector, Admin) and boundary interactions.
- **Diagram Analysis**:
  - Demonstrates strict boundary separation between student activities (trading, recycling, ticketing) and operational duties (technician resolution, scrap weighing, admin user governance).
- **Speaking Script**:
  > *"Slide 4 outlines our role-based functional boundaries. Students and faculty have access to peer trading, scrap requests, and ticket creation. Maintenance technicians and scrap collectors access specialized management workflows, while Administrators hold governance oversight. This separation is enforced at the Gateway and service layers."*

#### SLIDE 5: Authentication & Security Architecture
- **Visuals**: Authentication sequence diagram ([diag_auth_seq.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_auth_seq.png)) showing client login, token minting, Gateway validation, and downstream identity injection.
- **Diagram Analysis**:
  - 1. Client sends login credentials to `POST /api/auth/login`.
  - 2. Gateway routes request to `vendora-auth-service` (:8082).
  - 3. Auth Service calls `vendora-user-service` (:8081) via internal secured REST with `X-Internal-Secret` to verify BCrypt password hash.
  - 4. Auth Service signs an HMAC-SHA256 JWT containing `userId`, `email`, and `role`.
  - 5. On subsequent requests, Gateway intercepts `Authorization: Bearer <token>`, validates the cryptographic signature, strips spoofed client headers, and injects trusted `X-User-Id`, `X-User-Email`, and `X-User-Role` headers downstream.
- **Speaking Script**:
  > *"Slide 5 details our zero-trust security architecture. The Auth Service is stateless and owns no database tables; it verifies passwords through an internal secured channel with the User Service. Once minted, the JWT is validated centrally by the API Gateway. Downstream services never need to re-verify credentials—they consume pre-validated identity headers injected by the Gateway."*

#### SLIDE 6: User Management & Authentication Services
- **Visuals**: Cards for `vendora-user-service` (:8081) and `vendora-auth-service` (:8082) with metrics (51 tests / 89.7% coverage; 36 tests / 96.9% coverage).
- **Speaking Script**:
  > *"Slide 6 explains our identity layer. User Service manages user profiles, passwords, and representative applications with 51 unit tests and 89.7% coverage. Auth Service handles token issuance and token validation with 36 unit tests and 96.9% coverage. This separation ensures that high-frequency token operations never degrade user profile operations."*

#### SLIDE 7: Marketplace Service
- **Visuals**: Marketplace architecture card (:8083), domain model, metrics (69 tests / 92.1% coverage).
- **Speaking Script**:
  > *"Slide 7 covers our Marketplace Service. Students can list items with images and prices, perform dynamic filtered searches across categories, and submit purchase offers. The service enforces strict ownership checks: a user cannot edit or delete another student's listing. It is verified by 69 unit tests achieving 92.1% code coverage."*

#### SLIDE 8: Scrap Hub & Giveaways Service
- **Visuals**: Dual circular economy workflow diagram ([diag_scrap.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_scrap.png)), metrics (75 tests / 92.9% coverage).
- **Diagram Analysis**:
  - **Workflow 1 (Doorstep Scrap Pickup)**: Student logs pickup request $\to$ Collector assigned $\to$ Status progresses `REQUESTED` $\to$ `ASSIGNED` $\to$ `COLLECTED` (weight & payout recorded).
  - **Workflow 2 (Hostel Giveaways)**: Graduating student lists free items $\to$ Junior claims item $\to$ Status becomes `CLAIMED`.
- **Speaking Script**:
  > *"Slide 8 highlights our sustainability workflows. Workflow 1 schedules doorstep collection of recyclable materials like old books and mattresses with collector assignment and weight tracking. Workflow 2 allows seniors to give away useful items to juniors for free. Backed by 75 unit tests and 92.9% coverage, this promotes campus zero-waste."*

#### SLIDE 9: Facilities Helpdesk Service
- **Visuals**: Finite state machine diagram ([diag_ticket_state.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_ticket_state.png)), metrics (55 tests / 89.0% coverage).
- **Diagram Analysis**:
  - `OPEN` $\xrightarrow{\text{assign staff}}$ `IN_PROGRESS` $\xrightarrow{\text{technician resolves}}$ `RESOLVED` $\xrightarrow{\text{student/admin verifies}}$ `CLOSED`.
- **Speaking Script**:
  > *"Slide 9 presents our Facilities Helpdesk. It replaces slow physical logbooks with an automated ticket lifecycle. It handles 8 facility categories with 4 priority levels. State transitions are strictly validated—for example, a ticket cannot transition directly from OPEN to CLOSED without technician resolution notes. Backed by 55 unit tests, it maintains 89.0% coverage."*

#### SLIDE 10: Payment Service & Transaction Idempotency
- **Visuals**: Payment sequence diagram ([diag_payment_flow.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_payment_flow.png)), metrics (44 tests / 93.4% coverage).
- **Diagram Analysis**:
  - Demonstrates client submitting payment with `Idempotency-Key`. The service checks the database: if the key exists, it returns the cached transaction immediately; if new, it processes the transaction atomically.
- **Speaking Script**:
  > *"Slide 10 details our Payment Service. In distributed systems, network retries can cause duplicate charges. We implemented header-driven idempotency protection: each payment requires an Idempotency-Key. If a duplicate request is received, the service detects the key and returns the original transaction record without re-charging. It achieves 93.4% test coverage."*

#### SLIDE 11: End-to-End Campus Workflow Sequence Diagram
- **Visuals**: Comprehensive sequence diagram ([diag_e2e_seq.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_e2e_seq.png)) showing student purchasing a cycle, making an idempotent payment, and lodging a delivery locker maintenance ticket.
- **Speaking Script**:
  > *"Slide 11 brings everything together in an end-to-end campus scenario. A student searches for a bicycle on Marketplace, makes a secure purchase through Payment Service with an idempotency key, and subsequently raises a locker repair ticket with Helpdesk Service—all routed through the API Gateway seamlessly."*

#### SLIDE 12: Database Architecture & Logical Schema Isolation
- **Visuals**: Complete Entity-Relationship diagram ([diag_erd.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_erd.png)) covering all 11 tables.
- **Diagram Analysis**:
  - Tables: `users`, `app_representatives`, `listings`, `listing_images`, `purchase_requests`, `messages`, `scrap_requests`, `giveaway_items`, `tickets`, `ticket_comments`, `payment_transactions`.
  - Zero cross-service foreign keys. Inter-service relationships use logical IDs (`user_id`, `listing_id`, `ticket_id`).
- **Speaking Script**:
  > *"Slide 12 explains our data tier. We use a shared MySQL instance named vendora_db, but we enforce strict logical domain isolation. Each microservice manages its own tables. There are zero cross-domain SQL joins or physical foreign keys. If we move to 6 separate physical databases tomorrow, we only change JDBC connection strings without touching Java code."*

#### SLIDE 13: Core Class Diagram & Layered Design
- **Visuals**: Clean class diagram ([diag_class.png](file:///c:/Users/tankar/Downloads/Vendora/presentation_assets/diag_class.png)) showing `TicketController` $\to$ `TicketService` $\leftarrow$ `TicketServiceImpl` $\to$ `TicketRepository` $\to$ `Ticket`.
- **Diagram Analysis**:
  - Strict separation of concerns: Controller processes HTTP requests, Service interface defines business contracts, ServiceImpl executes transactional logic, Repository abstracts SQL queries via Spring Data JPA, and DTOs prevent domain entity leakage.
- **Speaking Script**:
  > *"Slide 13 demonstrates our code architecture. Every microservice adheres to the Controller-Service-Repository pattern. Service interfaces decouple business definitions from implementations, enabling dependency injection and effortless unit test mocking. DTOs ensure internal entity structures are never exposed to external clients."*

#### SLIDE 14: Exception Handling & Cross-Cutting Concerns
- **Visuals**: Architecture diagram showing Spring AOP Logging intercepting requests and `@RestControllerAdvice` mapping domain exceptions to standardized JSON errors.
- **Speaking Script**:
  > *"Slide 14 showcases our cross-cutting concerns. We use Spring AOP to transparently intercept and log method execution durations across all layers without cluttering business logic. Our centralized GlobalExceptionHandler catches domain errors and maps them to standard HTTP status codes, preventing raw stack traces from reaching clients."*

#### SLIDE 15: Quality Assurance, SonarQube & Testing Verification
- **Visuals**: SonarQube summary table showing 100% Quality Gate Passed across all 8 microservices, 343 unit tests, 0 bugs, 0 vulnerabilities, 0 code smells, Grade A.
- **Speaking Script**:
  > *"Slide 15 highlights our code quality metrics. Every single microservice passed SonarQube's strict Quality Gate. We fixed blocker rules like java:S2699 and security rules like java:S5122. Across the platform, 343 automated unit tests guarantee reliability, maintainability, and regression safety."*

#### SLIDE 16: Conclusion, Future Roadmap & Live Demonstration
- **Visuals**: Summary of architectural achievements and upcoming features (WebSocket chat, Redis caching, Kafka event streaming).
- **Speaking Script**:
  > *"In conclusion, Vendora delivers a resilient, cloud-native campus platform built with industry best practices. We are now ready to demonstrate the live running application, walk through Eureka and API Gateway, and execute real campus workflows."*

---

### Monolithic vs. Microservices Architecture Justification

| Architectural Dimension | Monolithic Architecture | Vendora Microservices Architecture | Evaluator Defense Rationale |
| :--- | :--- | :--- | :--- |
| **Fault Isolation** | A crash in ticket processing or payment crashes the entire campus portal. | Failures are strictly contained. If Helpdesk Service (:8084) is down, Marketplace (:8083) and Auth (:8082) continue operating uninterrupted. | Campus services have different risk profiles; high-traffic commerce must never be taken down by a maintenance bug. |
| **Independent Scalability** | The entire monolith must be scaled horizontally, wasting compute and database connections. | Services scale independently. During hostel move-out week, Scrap Service (:8085) can scale to 5 instances while Helpdesk remains at 1 instance. | Optimizes cloud resource allocation and cost efficiency. |
| **Deployment Independence** | Any minor bug fix requires re-testing, rebuilding, and redeploying the entire multi-gigabyte application. | Each service has its own Maven build lifecycle (`pom.xml`) and can be redeployed without restarting other services. | Facilitates continuous delivery and zero-downtime rolling updates. |
| **Team Autonomous Development** | Developers work on a single shared codebase, creating merge conflicts and tight coupling. | Clear domain boundaries (Bounded Contexts) allow separate teams to own individual services without stepping on each other's code. | Follows Conway's Law and domain-driven design principles. |
| **Technology Flexibility** | Locked into a single language and framework version across the entire system. | Polyglot potential: while built with Spring Boot 3, individual services can adopt different databases (e.g., MongoDB for chat) in the future. | Prevents technological lock-in as requirements evolve. |
| **Trade-offs Acknowledged** | Simpler initial setup, trivial in-memory ACID transactions. | Increased operational complexity (network latency, distributed security, service discovery, eventual consistency). | We mitigated these trade-offs using Spring Cloud Gateway, Eureka Server, standardized DTOs, and automated PowerShell orchestrators. |

---

# 2. Complete Project Architecture & Microservice Deep Dive

```
                             [ CLIENT APPLICATIONS / POSTMAN / SWAGGER UI ]
                                                   │
                                                   ▼
                                [ SPRING CLOUD API GATEWAY (:8080) ]
                                 • Zero-Trust Perimeter Filter
                                 • HMAC-SHA256 Token Validation
                                 • Strips Client Spoofed Headers
                                 • Injects X-User-Id, X-User-Role
                                         │               │
                  Dynamic Service Lookup │               │ Heartbeat & Registration
                                         ▼               ▼
                           [ NETFLIX EUREKA SERVER (:8761) ]
                                         │
        ┌──────────────┬─────────────────┼────────────────┬──────────────┬──────────────┐
        ▼              ▼                 ▼                ▼              ▼              ▼
  [User Service] [Auth Service]    [Marketplace]     [Scrap Service] [Helpdesk]    [Payment]
     (:8081)        (:8082)           (:8083)           (:8085)       (:8084)       (:8086)
        │              │                 │                 │             │             │
        │              │ Internal REST   │                 │             │             │
        │◄─────────────┘ (X-Internal-Sec)│                 │             │             │
        │                                │                 │             │             │
        └─────────────────┬──────────────┴─────────────────┴─────────────┴─────────────┘
                          │  Spring Data JPA / HikariCP Connection Pools
                          ▼
             [ MYSQL 8.0: vendora_db ]
             • Logical Schema Isolation (Domain-Owned Tables)
             • Zero Cross-Domain Foreign Keys or Joins
```

---

### Core Concepts Explained from Scratch

#### 1. What are Microservices?
A microservice architecture decomposes a large software application into a suite of small, autonomous, independently deployable services. Each service runs in its own process, implements a distinct business capability within a well-defined **Bounded Context** (Domain-Driven Design), and communicates with other services over lightweight network protocols (typically HTTP/REST or asynchronous messaging).

#### 2. Why Spring Boot 3 for Microservices?
- **Opinionated Configuration**: Spring Boot eliminates thousands of lines of boilerplate XML configuration through auto-configuration (`@EnableAutoConfiguration`).
- **Embedded Servlet Container**: Each microservice packages an embedded Tomcat or Netty server into an executable JAR, simplifying deployment.
- **Spring Cloud Ecosystem**: First-class integration with Netflix Eureka, Spring Cloud Gateway, and OpenFeign.
- **Production-Ready Features**: Built-in health checks, metrics, and application monitoring via Spring Boot Actuator.

#### 3. What is Service Discovery & Why Eureka Server?
In cloud and microservice environments, service instances have dynamic, ephemeral network locations (IP addresses and ports change during scaling, container restarts, or auto-healing).  
- **Problem**: Hardcoding IP addresses (e.g., `http://192.168.1.50:8081`) causes catastrophic failures when an instance restarts on a new IP.
- **Solution (Netflix Eureka)**:
  - **Eureka Server (:8761)** maintains an in-memory service registry mapping logical service names (e.g., `VENDORA-USER-SERVICE`) to live IP/port endpoints.
  - **Eureka Client**: When each microservice boots, it registers its name, hostname, and port with Eureka.
  - **Heartbeats**: Clients send periodic heartbeats (default every 30 seconds). If Eureka receives no heartbeat for 90 seconds, it evicts the instance.
  - **Client-Side Load Balancing**: The API Gateway queries Eureka for available instances and balances traffic without needing an expensive hardware load balancer.

#### 4. What is an API Gateway & Why Spring Cloud Gateway?
The API Gateway acts as the single, secure entry point for all external client traffic.
- **Why Spring Cloud Gateway over Netflix Zuul 1.x?**: Zuul 1.x used blocking I/O (one thread per request), which bottlenecks under high concurrency. Spring Cloud Gateway is built on **Spring WebFlux and Project Reactor running on Netty**, providing asynchronous, non-blocking I/O capable of handling thousands of concurrent connections with low memory overhead.
- **Key Responsibilities in Vendora**:
  1. **Reverse Proxy & Routing**: Maps public paths (e.g., `/api/marketplace/**`) to logical microservice names (`lb://vendora-marketplace-service`).
  2. **Security & Perimeter Defense**: Validates JWT cryptographic signatures before forwarding requests downstream.
  3. **Header Sanitization**: Strips client-supplied headers (`X-User-Id`, `X-User-Role`) to prevent identity spoofing, then injects verified claims from the validated JWT.
  4. **CORS Governance**: Centralizes Cross-Origin Resource Sharing policies for web frontends.

#### 5. How Do the Services Communicate?
- **External to Gateway**: Clients send standard HTTP REST requests to `http://localhost:8080`.
- **Gateway to Microservices**: Gateway resolves service locations via Eureka (`lb://<SERVICE-NAME>`) and forwards non-blocking HTTP requests.
- **Service-to-Service (Auth $\to$ User)**:
  - `vendora-auth-service` communicates with `vendora-user-service` to verify credentials.
  - To secure this internal channel, requests pass an internal shared secret header: `X-Internal-Secret`. User Service verifies this secret before exposing internal authentication data.

#### 6. Shared Database & Logical Schema Isolation
- **The Design Dilemma**: Pure microservice theory suggests "Database-per-Service" (each service having its own isolated MySQL container/server). However, in academic and resource-constrained environments, running 6 separate MySQL instances consumes excessive RAM and CPU.
- **Vendora's Solution (Logical Database Isolation)**:
  - All services connect to a single MySQL instance (`vendora_db`).
  - **Strict Logical Boundaries**: Each service owns its dedicated tables (e.g., User Service owns `users`; Helpdesk owns `tickets`).
  - **Zero Cross-Domain Joins**: No service ever executes a SQL join across another service's tables.
  - **Zero Physical Foreign Keys Across Services**: Relationships store logical IDs (e.g., `tickets.user_id = 5` stores the user's ID as a plain `BIGINT`, without a SQL `FOREIGN KEY` constraint to `users.id`).
  - **Production Independence**: Moving to 6 separate physical databases requires only updating the `spring.datasource.url` in each service's `application.properties`—**zero Java code changes required**.

---

### Enterprise Layering Pattern

Every business microservice strictly enforces a 5-tier architecture:

```
[HTTP Request] ──► [Controller] ──► [Service Interface] ──► [ServiceImpl] ──► [Repository] ──► [MySQL]
                         │                   ▲                     ▲
                         ▼                   │                     │
                    [DTO Layer] ─────────────┴─────────────────────┘
```

1. **Controller Layer (`@RestController`)**: Entry point for HTTP requests. Validates incoming payload syntax using `@Valid`, translates requests into DTOs, delegates to the Service layer, and returns standardized `ResponseEntity<T>` with appropriate HTTP status codes.
2. **DTO Layer (Data Transfer Objects)**: Separates external API contracts from internal database models. Prevents **Mass Assignment vulnerabilities** and prevents accidental leakage of sensitive database fields (like `passwordHash`).
3. **Service Interface (`<<interface>>`)**: Defines the business contract. Decouples controllers from business logic implementations, enabling dependency injection, test mocking, and adherence to the Dependency Inversion Principle.
4. **Service Implementation Layer (`@Service`, `@Transactional`)**: Implements business rules, orchestrates repository operations, enforces domain validations, and manages database transactions.
5. **Repository Layer (`@Repository`)**: Extends Spring Data JPA interfaces (`JpaRepository<Entity, ID>`). Provides out-of-the-box CRUD operations, pagination, and derived query methods without writing manual SQL.
6. **Entity Layer (`@Entity`, `@Table`)**: JPA ORM representations mapped directly to relational database tables.

---

### Detailed Specification for All 8 Components

#### 1. Netflix Eureka Server (`vendora-eureka-server`)
- **Port**: `8761` | **Dashboard**: `http://localhost:8761`
- **Purpose**: Centralized service registry. Maintains live instance endpoints and enables dynamic client-side load balancing.
- **Main Package**: `com.vendora.eureka`
- **Key Classes**: `VendoraEurekaServerApplication.java`
- **Key Annotations**: `@SpringBootApplication`, `@EnableEurekaServer`
- **Configuration Highlights** (`application.properties`):
  ```properties
  server.port=8761
  eureka.client.register-with-eureka=false
  eureka.client.fetch-registry=false
  ```
  *(Disabled registration with itself because it is the registry).*
- **Internal Mechanisms**:
  - `register-with-eureka=false`: Tells Eureka not to register itself as a service.
  - `fetch-registry=false`: Tells Eureka it doesn't need to fetch a registry from another node (standalone mode).
  - Self-Preservation Mode: If network issues cause heartbeats to drop below 85%, Eureka pauses instance eviction to prevent accidental mass deregistrations.
- **Alternatives**: HashiCorp Consul, Kubernetes CoreDNS, Apache Zookeeper.

#### 2. Spring Cloud API Gateway (`vendora-api-gateway`)
- **Port**: `8080` | **Type**: Reactive Non-Blocking Gateway (Netty)
- **Purpose**: Zero-trust security perimeter, centralized JWT signature verification, request routing, and client header sanitization.
- **Main Packages**: `com.vendora.gateway`, `com.vendora.gateway.config`, `com.vendora.gateway.filter`
- **Key Classes**:
  - `VendoraApiGatewayApplication.java`: Application entry point.
  - `JwtAuthenticationFilter.java`: Global reactive filter (`GlobalFilter`, `Ordered`).
  - `SecurityConfig.java`: Spring Security WebFlux configuration (`SecurityWebFilterChain`).
- **Route Mapping Table**:

| Public Path Pattern | Downstream Destination (Eureka ID) | Authentication Required |
| :--- | :--- | :---: |
| `/api/auth/**` | `lb://vendora-auth-service` | No (Public) |
| `/api/users/register`, `/api/users/login` | `lb://vendora-user-service` | No (Public) |
| `/api/users/**` | `lb://vendora-user-service` | Yes (Bearer JWT) |
| `/api/marketplace/**` | `lb://vendora-marketplace-service` | Yes (Bearer JWT) |
| `/api/scrap/**` | `lb://vendora-scrap-service` | Yes (Bearer JWT) |
| `/api/helpdesk/**` | `lb://vendora-helpdesk-service` | Yes (Bearer JWT) |
| `/api/payments/**` | `lb://vendora-payment-service` | Yes (Bearer JWT) |

- **Security & Filter Mechanics**:
  1. Intercepts incoming `ServerWebExchange`.
  2. Bypasses public endpoints (auth/register/login/swagger).
  3. Extracts `Authorization: Bearer <token>` header.
  4. Cryptographically verifies HMAC-SHA256 signature using the 256-bit secret key.
  5. Extracts claims (`userId`, `email`, `role`).
  6. **Sanitization**: Mutates the request to strip any client-supplied `X-User-Id`, `X-User-Email`, `X-User-Role` headers.
  7. **Injection**: Injects verified claims into downstream headers:
     ```java
     ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
         .header("X-User-Id", claims.getSubject())
         .header("X-User-Email", claims.get("email", String.class))
         .header("X-User-Role", claims.get("role", String.class))
         .build();
     ```
- **Alternatives**: Netflix Zuul (blocking), Nginx (requires custom Lua scripts for JWT), Traefik, Kong.

#### 3. Authentication Service (`vendora-auth-service`)
- **Port**: `8082` | **Testing**: 36 unit tests, **96.9% code coverage**
- **Purpose**: Stateless identity provider. Issues HMAC-SHA256 signed JWT tokens upon credential validation.
- **Main Packages**: `com.vendora.auth.controller`, `com.vendora.auth.service`, `com.vendora.auth.client`, `com.vendora.auth.dto`, `com.vendora.auth.util`
- **Key Classes**:
  - `AuthController.java`: REST controller exposing login and validation endpoints.
  - `AuthService.java` & `AuthServiceImpl.java`: Validates credentials against User Service and mints tokens.
  - `UserClient.java`: Internal client calling User Service.
  - `JwtUtil.java`: Token generation, signing, and parsing utility.
- **Endpoints**:
  - `POST /api/auth/login`: Authenticates credentials; returns `{ token, type: "Bearer", userId, email, role }`.
  - `POST /api/auth/validate`: Validates token signature and returns claims.
- **Database Tables**: **ZERO**. Auth Service is 100% stateless and owns no database tables.
- **Inter-Service Communication**: Calls `vendora-user-service` at `http://localhost:8081/api/users/internal/verify` with header `X-Internal-Secret: vendora-internal-secret-key-2026`.
- **Alternatives**: Keycloak, OAuth2/OIDC with Spring Authorization Server, Auth0.

#### 4. User Management Service (`vendora-user-service`)
- **Port**: `8081` | **Testing**: 51 unit tests, **89.7% code coverage**
- **Purpose**: Manages student and faculty profiles, password hashing via BCrypt, and representative approval workflows.
- **Main Packages**: `com.vendora.user.controller`, `com.vendora.user.service`, `com.vendora.user.repository`, `com.vendora.user.entity`, `com.vendora.user.dto`
- **Key Entities & Tables**:
  - `User` $\to$ `users`: `id`, `full_name`, `email` (unique), `password_hash`, `phone`, `role`, `account_status`, `created_at`.
  - `AppRepresentative` $\to$ `app_representatives`: `id`, `user_id`, `approval_status`, `approved_by`, `approved_at`.
- **Key Endpoints**:
  - `POST /api/users/register`: Public registration (hashes password with BCrypt cost 10).
  - `GET /api/users/{id}`: Profile retrieval (requires valid JWT).
  - `PUT /api/users/{id}`: Profile update (guarded by ownership check).
  - `POST /api/users/representatives/apply`: Apply for student representative role.
  - `PUT /api/users/representatives/{id}/status`: Admin-only approval/rejection.
  - `POST /api/users/internal/verify`: Internal-only credential verification (secured by `X-Internal-Secret`).
- **Business Validations**:
  - Email uniqueness check prior to insert.
  - Non-blank full name, phone format validation.
  - Role elevation restricted strictly to `CAMPUS_ADMIN`.

#### 5. Marketplace Service (`vendora-marketplace-service`)
- **Port**: `8083` | **Testing**: 69 unit tests, **92.1% code coverage**
- **Purpose**: Peer-to-peer campus item trading, image attachments, dynamic category search, and purchase negotiations.
- **Main Packages**: `com.vendora.marketplace.controller`, `com.vendora.marketplace.service`, `com.vendora.marketplace.repository`, `com.vendora.marketplace.entity`, `com.vendora.marketplace.dto`
- **Key Entities & Tables**:
  - `Listing` $\to$ `listings`: `id`, `seller_id`, `title`, `description`, `price`, `category`, `status` (`ACTIVE`, `RESERVED`, `SOLD`), `created_at`.
  - `ListingImage` $\to$ `listing_images`: `id`, `listing_id`, `image_url`.
  - `PurchaseRequest` $\to$ `purchase_requests`: `id`, `listing_id`, `buyer_id`, `offered_price`, `status` (`PENDING`, `ACCEPTED`, `REJECTED`).
  - `Message` $\to$ `messages`: `id`, `sender_id`, `receiver_id`, `listing_id`, `content`, `sent_at`.
- **Key Endpoints**:
  - `POST /api/marketplace/listings`: Create new listing.
  - `GET /api/marketplace/listings`: Search listings with filters (`category`, `minPrice`, `maxPrice`, `keyword`).
  - `GET /api/marketplace/listings/{id}`: Get listing details.
  - `PUT /api/marketplace/listings/{id}`: Update listing (seller ownership check).
  - `DELETE /api/marketplace/listings/{id}`: Delete listing (seller ownership check).
  - `POST /api/marketplace/offers`: Submit purchase offer.
  - `PUT /api/marketplace/offers/{id}/status`: Accept/reject offer (seller only).
- **Business Rules**:
  - Seller cannot submit a purchase offer on their own listing.
  - Offered price must be greater than zero.
  - When an offer is `ACCEPTED`, listing status transitions to `RESERVED`.

#### 6. Scrap & Giveaway Service (`vendora-scrap-service`)
- **Port**: `8085` | **Testing**: 75 unit tests, **92.9% code coverage**
- **Purpose**: Promotes campus circular economy through doorstep recyclable collection and free hostel item giveaways.
- **Main Packages**: `com.vendora.scrap.controller`, `com.vendora.scrap.service`, `com.vendora.scrap.repository`, `com.vendora.scrap.entity`, `com.vendora.scrap.dto`
- **Key Entities & Tables**:
  - `ScrapRequest` $\to$ `scrap_requests`: `id`, `student_id`, `scrap_type` (`PAPER`, `PLASTIC`, `METAL`, `E_WASTE`, `CARDBOARD`), `estimated_weight_kg`, `actual_weight_kg`, `hostel_name`, `room_number`, `status` (`REQUESTED`, `ASSIGNED`, `COLLECTED`, `CANCELLED`), `collector_id`, `payout_amount`.
  - `GiveawayItem` $\to$ `giveaway_items`: `id`, `donor_id`, `title`, `description`, `category`, `status` (`AVAILABLE`, `CLAIMED`), `claimed_by_id`, `created_at`.
- **Key Endpoints**:
  - `POST /api/scrap/requests`: Schedule doorstep scrap pickup.
  - `GET /api/scrap/requests/my`: View student's scrap requests.
  - `PUT /api/scrap/requests/{id}/assign`: Assign request to scrap collector (staff/admin).
  - `PUT /api/scrap/requests/{id}/collect`: Record final weight and payout (collector only).
  - `POST /api/scrap/giveaways`: List free hostel item.
  - `GET /api/scrap/giveaways`: Browse available free items.
  - `PUT /api/scrap/giveaways/{id}/claim`: Claim item (donor cannot claim own item).

#### 7. Facilities Helpdesk Service (`vendora-helpdesk-service`)
- **Port**: `8084` | **Testing**: 55 unit tests, **89.0% code coverage**
- **Purpose**: Hostel and campus facility complaint tracking with deterministic state transitions and staff assignment.
- **Main Packages**: `com.vendora.helpdesk.controller`, `com.vendora.helpdesk.service`, `com.vendora.helpdesk.repository`, `com.vendora.helpdesk.entity`, `com.vendora.helpdesk.dto`
- **Key Entities & Tables**:
  - `Ticket` $\to$ `tickets`: `id`, `ticket_number` (UUID), `user_id`, `category` (`ELECTRICAL`, `PLUMBING`, `FURNITURE`, `INTERNET_WIFI`, `LAB_EQUIPMENT`, `SANITATION`, `AC_COOLING`, `OTHER`), `priority` (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), `status` (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`), `assigned_staff_id`, `location`, `description`, `resolution_notes`, `created_at`.
  - `TicketComment` $\to$ `ticket_comments`: `id`, `ticket_id`, `author_id`, `content`, `created_at`.
- **Deterministic State Machine**:
  - `OPEN`: Initial state upon creation.
  - `IN_PROGRESS`: Triggered when staff is assigned.
  - `RESOLVED`: Set by technician after adding `resolutionNotes`.
  - `CLOSED`: Final verification by student or administrator.
  - *Illegal Transitions (e.g., OPEN $\to$ CLOSED) throw `BadRequestException`*.

#### 8. Payment Service (`vendora-payment-service`)
- **Port**: `8086` | **Testing**: 44 unit tests, **93.4% code coverage**
- **Purpose**: Mock financial checkout engine with strict header-driven idempotency protection.
- **Main Packages**: `com.vendora.payment.controller`, `com.vendora.payment.service`, `com.vendora.payment.repository`, `com.vendora.payment.entity`, `com.vendora.payment.dto`
- **Key Entities & Tables**:
  - `PaymentTransaction` $\to$ `payment_transactions`: `id`, `idempotency_key` (unique), `payer_id`, `amount`, `currency`, `status` (`PENDING`, `COMPLETED`, `FAILED`), `payment_method`, `created_at`.
- **Key Endpoints**:
  - `POST /api/payments/checkout`: Execute payment with header `Idempotency-Key: <UUID>`.
  - `GET /api/payments/{id}`: Retrieve transaction details.
- **Idempotency Protection Algorithm**:
  ```java
  Optional<PaymentTransaction> existing = paymentRepo.findByIdempotencyKey(idempotencyKey);
  if (existing.isPresent()) {
      return mapToResponse(existing.get()); // Return existing record without charging again
  }
  // Otherwise, execute charge atomically and persist
  PaymentTransaction tx = new PaymentTransaction(...);
  tx.setStatus(PaymentStatus.COMPLETED);
  paymentRepo.save(tx);
  ```

---

# 3. Complete Authentication & Authorization Deep Dive

### Authentication vs. Authorization

| Concept | Definition | Question It Answers | Handled By in Vendora |
| :--- | :--- | :--- | :--- |
| **Authentication (AuthN)** | The process of verifying the identity of a user or system based on credentials (e.g., email + password). | *"Who are you?"* | `vendora-auth-service` validates credentials and issues a signed JWT. |
| **Authorization (AuthZ)** | The process of granting or denying access to specific resources based on the user's role or permissions. | *"What are you allowed to do?"* | `vendora-api-gateway` and downstream `@PreAuthorize` rules check roles (`STUDENT`, `STAFF`, `ADMIN`). |

---

### Anatomy of a JSON Web Token (JWT)

A JWT is a compact, URL-safe, tamper-evident token composed of three Base64URL-encoded parts separated by dots (`.`):
$$\text{JWT} = \text{Base64Url}(\text{Header}) + \text{"."} + \text{Base64Url}(\text{Payload}) + \text{"."} + \text{Signature}$$

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTYiLCJlbWFpbCI6InN0dWRlbnRAdW5pdi5lZHUiLCJyb2xlIjoiU1RVREVOVCIsImlhdCI6MTc5NDgwMDAwMCwiZXhwIjoxNzk0ODg2NDAwfQ.sI3f8K...
└─────────────────┬─────────────────┘ └──────────────────────────────┬──────────────────────────────┘ └──────────────┬──────────────┘
              1. HEADER                                         2. PAYLOAD                                      3. SIGNATURE
```

1. **Header**: Contains metadata describing the token format and cryptographic algorithm:
   ```json
   { "alg": "HS256", "typ": "JWT" }
   ```
2. **Payload (Claims)**: Contains verified assertions about the user and token expiration:
   ```json
   {
     "sub": "5",
     "email": "rahul@campus.edu",
     "role": "STUDENT",
     "iat": 1794800000,
     "exp": 1794886400
   }
   ```
   - `sub` (Subject): The internal `userId`.
   - `email`: User's university email address.
   - `role`: Role for downstream authorization.
   - `iat` (Issued At): UNIX timestamp when token was created.
   - `exp` (Expiration Time): UNIX timestamp when token expires (24 hours in Vendora).
3. **Signature**: Cryptographic proof guaranteeing the token has not been modified in transit:
   $$\text{Signature} = \text{HMACSHA256}(\text{Base64Url}(\text{Header}) + \text{"."} + \text{Base64Url}(\text{Payload}), \text{SecretKey})$$
   - If an attacker tampers with the payload (e.g., changes `"role": "STUDENT"` to `"CAMPUS_ADMIN"`), the recalculated signature will not match, and the API Gateway rejects the token immediately.

---

### BCrypt Password Hashing & Secret Key Security

- **Why BCrypt over Plain SHA-256 or MD5?**:
  - SHA-256 and MD5 are general-purpose hash functions designed to be fast. Modern GPUs can calculate billions of SHA-256 hashes per second, making them susceptible to **Rainbow Table attacks** and brute-force cracking.
  - **BCrypt** is an adaptive cryptographic hash function based on the Blowfish cipher. It incorporates:
    1. **Automatic Salt Generation**: A 128-bit random salt is generated and embedded in the hash, ensuring identical passwords yield completely different hashes.
    2. **Configurable Work Factor (Cost)**: In Vendora, we use cost factor 10 ($2^{10} = 1024$ key expansion rounds), making brute-force attempts computationally prohibitive.
- **Why a 256-Bit Secret Key?**:
  - The HMAC-SHA256 algorithm requires a key length at least equal to its output hash size (256 bits = 32 bytes). Using a shorter key causes cryptographic vulnerabilities and triggers runtime exceptions in modern JJWT libraries.

---

### Step-by-Step Authentication Sequence

```
Client               API Gateway (:8080)     Auth Service (:8082)     User Service (:8081)
  │                          │                        │                        │
  │── 1. POST /api/auth/login ───────────────────────►│                        │
  │   { email, password }    │                        │── 2. Internal Verify ─►│
  │                          │                        │   (X-Internal-Secret)  │
  │                          │                        │                        │ (BCrypt check)
  │                          │                        │◄── 3. Return UserDTO ──│
  │                          │                        │   { id, email, role }  │
  │                          │                        │                        │
  │                          │                        │ (Mints HMAC-SHA256 JWT)│
  │◄── 4. Return JWT Token ───────────────────────────│                        │
  │   { token, Bearer }      │                        │                        │
  │                          │                        │                        │
  │── 5. GET /api/marketplace/listings ───────────────┼────────────────────────┼──► [Marketplace]
  │   (Authorization: Bearer <token>)                 │                        │
  │                          │                        │                        │
  │                  (Validates JWT signature)        │                        │
  │                  (Strips client X-User-* headers) │                        │
  │                  (Injects verified X-User-* claims)                        │
  │                          │─────────────────────────────────────────────────┼──► Injected Headers:
  │                          │                                                 │    X-User-Id: 5
  │                          │                                                 │    X-User-Role: STUDENT
```

---

# 4. Annotation-by-Annotation Master Catalog

### Core Spring & Spring Boot Annotations

#### `@SpringBootApplication`
- **Meaning**: Meta-annotation combining `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- **Why Used**: Marks the primary configuration class that bootstraps each of the 8 microservices.
- **Internal Mechanics**: Triggers Spring Boot's classpath scanning, initializes the `ApplicationContext`, and starts the embedded web container (Tomcat or Netty).
- **Mandatory?**: Yes, exactly once per Spring Boot application.
- **Example**:
  ```java
  @SpringBootApplication
  public class VendoraUserServiceApplication {
      public static void main(String[] args) {
          SpringApplication.run(VendoraUserServiceApplication.class, args);
      }
  }
  ```

#### `@RestController`
- **Meaning**: Combines `@Controller` and `@ResponseBody`.
- **Why Used**: Applied to all REST controllers in Vendora (`TicketController`, `MarketplaceController`, etc.).
- **Internal Mechanics**: Signals to Spring MVC that every handler method's return value should be automatically serialized into HTTP response JSON via Jackson `HttpMessageConverter`.
- **Alternatives**: `@Controller` with `@ResponseBody` on every individual method.

#### `@Service`
- **Meaning**: Specialization of `@Component` for the business logic layer.
- **Why Used**: Placed on service implementation classes (`TicketServiceImpl`, `AuthServiceImpl`).
- **Internal Mechanics**: Registers the class as a Spring-managed bean in the IoC container, making it eligible for transaction management and AOP interception.

#### `@Repository`
- **Meaning**: Specialization of `@Component` for the Data Access Object (DAO) layer.
- **Why Used**: Applied to interfaces extending `JpaRepository` (e.g., `TicketRepository`).
- **Internal Mechanics**: Enables automatic translation of database-specific SQL exceptions into Spring's unified `DataAccessException` hierarchy.

#### `@Configuration` and `@Bean`
- **Meaning**: `@Configuration` indicates a source of bean definitions; `@Bean` denotes a factory method instantiating a Spring bean.
- **Why Used**: Used in `SecurityConfig`, `OpenApiConfig`, and `WebConfig` to instantiate `BCryptPasswordEncoder`, `SecurityWebFilterChain`, and Swagger OpenAPI specs.
- **Internal Mechanics**: CGLIB proxies the configuration class to ensure singleton semantics (calling a `@Bean` method multiple times returns the same cached instance).
- **Example**:
  ```java
  @Configuration
  public class SecurityBeansConfig {
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder(10);
      }
  }
  ```

#### `@Value`
- **Meaning**: Injects externalized configuration property values into bean fields.
- **Why Used**: Injects JWT secret keys (`${jwt.secret}`), token expiration durations, and internal secrets (`${internal.secret}`).
- **Internal Mechanics**: Evaluated at bean initialization time via `PropertySourcesPlaceholderConfigurer`.

---

### Spring Data JPA Annotations

#### `@Entity` and `@Table`
- **Meaning**: `@Entity` declares a Java class as a JPA entity mapped to a relational database; `@Table` specifies the database table name.
- **Why Used**: Applied to `User`, `Ticket`, `Listing`, `PaymentTransaction`.
- **Internal Mechanics**: Hibernate reads these annotations to build the metamodel, manage persistence states, and generate SQL DDL/DML.

#### `@Id` and `@GeneratedValue`
- **Meaning**: `@Id` designates the primary key; `@GeneratedValue(strategy = GenerationType.IDENTITY)` configures auto-incrementing surrogate keys.
- **Why Used**: Used across all 11 database entities for unique record identification.
- **Internal Mechanics**: Delegates primary key generation to MySQL's native `AUTO_INCREMENT` column mechanism.

#### `@Enumerated(EnumType.STRING)`
- **Meaning**: Maps Java enums to database string columns (`VARCHAR`).
- **Why Used**: Applied to `TicketStatus`, `TicketPriority`, `Role`, `PaymentStatus`.
- **Why Crucial**: Without `EnumType.STRING`, JPA defaults to `EnumType.ORDINAL` (storing integers 0, 1, 2). If someone reorders or adds an enum value later, existing database records become corrupted.

#### `@Transactional`
- **Meaning**: Defines the scope of a single database transaction.
- **Why Used**: Applied to service methods that mutate database state (e.g., `TicketServiceImpl.updateStatus`, `PaymentServiceImpl.processPayment`).
- **Internal Mechanics**: Spring AOP intercepts the method execution: opens a transaction via `PlatformTransactionManager`, commits on successful completion, and triggers automatic rollback if a `RuntimeException` is thrown.

---

### Spring Validation Annotations

#### `@Valid`
- **Meaning**: Triggers recursive Jakarta Bean Validation on an argument.
- **Why Used**: Placed on `@RequestBody` parameters in controllers (e.g., `+createTicket(@Valid @RequestBody CreateTicketRequest request)`).
- **Internal Mechanics**: Before the controller method runs, Hibernate Validator inspects all constrained fields. If any constraint fails, Spring throws `MethodArgumentNotValidException`, which our `GlobalExceptionHandler` intercepts and converts into a 400 Bad Request.

#### Constraints: `@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Min`
- **`@NotBlank`**: Ensures a `String` is not null, not empty, and contains at least one non-whitespace character.
- **`@Email`**: Validates proper email syntax (`username@domain.tld`).
- **`@Size(min = 6, max = 32)`**: Enforces length boundaries on passwords and titles.
- **`@Min(0)` / `@DecimalMin("0.01")`**: Enforces positive financial amounts and prices.

---

### Spring Cloud Annotations

#### `@EnableEurekaServer`
- **Meaning**: Configures the Spring Boot application as a Netflix Eureka Service Registry.
- **Where Used**: `VendoraEurekaServerApplication.java` (:8761).
- **Internal Mechanics**: Activates `EurekaServerMarkerConfiguration`, instantiating the registry servlet, discovery dashboard, and peer-replication handlers.

#### `@EnableDiscoveryClient`
- **Meaning**: Enables a microservice to register itself with the discovery service (Eureka) and query other registered services.
- **Where Used**: In API Gateway and all domain microservices.

---

# 5. Controller & Code-Level Execution Walkthrough

### Detailed Walkthrough: `TicketController.java`

```java
@RestController
@RequestMapping("/api/helpdesk/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        
        TicketResponse response = ticketService.createTicket(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

#### Line-by-Line Execution Explanation:
1. **`@RestController`**: Registers `TicketController` as a RESTful web controller. All returned objects will be automatically serialized to JSON.
2. **`@RequestMapping("/api/helpdesk/tickets")`**: Configures the base URI route for all endpoints in this controller.
3. **`@RequiredArgsConstructor`**: Lombok annotation that generates a constructor for all `final` fields (`ticketService`), enabling **Constructor Dependency Injection**.
4. **`private final TicketService ticketService;`**: References the service interface rather than the concrete class (`TicketServiceImpl`), adhering to the **Dependency Inversion Principle**.
5. **`@PostMapping`**: Maps HTTP `POST` requests sent to `/api/helpdesk/tickets` to this method.
6. **`@Valid @RequestBody CreateTicketRequest request`**: Reads the incoming JSON payload from the HTTP body and triggers validation annotations (`@NotBlank`, `@NotNull`) defined on `CreateTicketRequest`.
7. **`@RequestHeader("X-User-Id") Long userId`**: Extracts the authenticated user's ID, which was injected into the request header by the API Gateway after validating the client's JWT.
8. **`ticketService.createTicket(request, userId)`**: Calls the business service layer to process the ticket creation within a database transaction.
9. **`return ResponseEntity.status(HttpStatus.CREATED).body(response)`**: Wraps the resulting `TicketResponse` DTO in a `ResponseEntity` with HTTP status code `201 Created`.

---

### Constructor Injection vs. Field Injection

| Metric | Field Injection (`@Autowired private TicketService svc;`) | Constructor Injection (Vendora Implementation) |
| :--- | :--- | :--- |
| **Immutability** | Impossible; fields cannot be marked `final`. | **Enforced**: Fields are marked `final` and initialized once at startup. |
| **Unit Testability** | Hard: requires reflection or Spring test context to inject mocks. | **Trivial**: Can pass mocked dependencies directly via `new TicketController(mockService)` without Spring. |
| **Circular Dependencies** | Hidden at startup; fails unpredictably during runtime. | **Detected Immediately**: Spring detects circular constructor dependencies at container startup. |
| **Clean Code Standard** | Considered an anti-pattern in modern Spring development. | **Recommended Industry Best Practice**. |

---

# 6. Global Exception Handling Architecture

### Checked vs. Unchecked Exceptions in Spring Boot
- **Checked Exceptions** (`Exception`): The Java compiler forces callers to declare `throws` or wrap in `try-catch`. They clutter business logic.
- **Unchecked Exceptions** (`RuntimeException`): Subclasses of `RuntimeException`. They represent unrecoverable programming errors, illegal arguments, or domain rule violations.  
*All custom exceptions in Vendora extend `RuntimeException`*, allowing transactions to automatically roll back when an exception is thrown.

---

### Why Not Use `try-catch` Everywhere?
- **Code Duplication**: Writing `try-catch` in every controller method leads to hundreds of lines of repetitive error-handling boilerplate.
- **Inconsistent Error Responses**: Different developers format error messages differently (some returning strings, some custom objects, some HTML error pages).
- **Security Leaks**: Poorly handled `catch (Exception e)` blocks often call `e.printStackTrace()`, leaking internal database table names and stack traces to potential attackers.

---

### Standardized Error JSON Schema
Every error across all 8 microservices returns an identical, structured JSON payload:
```json
{
  "timestamp": "2026-11-18T10:15:30.123",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 42",
  "path": "/api/helpdesk/tickets/42"
}
```

---

### Code Walkthrough: `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorResponse error = new ValidationErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input payload validation errors",
            request.getRequestURI(),
            fieldErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please contact system administrator.",
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

---

# 7. Inter-Service Communication & Declarative REST

### OpenFeign vs. RestTemplate vs. WebClient

| Feature | Spring Cloud OpenFeign | RestTemplate | Spring WebClient |
| :--- | :--- | :--- | :--- |
| **Style** | Declarative interface (`@FeignClient`) | Imperative, template-based | Functional, reactive fluent API |
| **Code Required** | Minimal: just interface + annotations | Verbose: manual URL construction & serialization | Moderate: chained reactive operators |
| **I/O Model** | Blocking (wraps standard HTTP clients) | Blocking (one thread per request) | **Non-Blocking** (Event-loop / Reactive) |
| **Discovery Integration**| Native integration with Eureka & LoadBalancer | Requires `@LoadBalanced` annotation | Requires reactive discovery filter |
| **Best Used For** | Microservice-to-microservice synchronous REST | Legacy Spring applications | High-concurrency reactive streaming |

---

### Feign Client Implementation Example
```java
@FeignClient(name = "vendora-user-service")
public interface UserClient {

    @PostMapping("/api/users/internal/verify")
    InternalUserDTO verifyCredentials(
        @RequestBody LoginRequest request,
        @RequestHeader("X-Internal-Secret") String secret
    );
}
```
- **How It Works**:
  1. Developer declares an interface annotated with `@FeignClient(name = "vendora-user-service")`.
  2. At startup, Spring Cloud creates a dynamic runtime proxy implementing this interface.
  3. When `userClient.verifyCredentials(...)` is invoked, Feign queries Eureka to resolve the physical IP and port of an active `vendora-user-service` instance.
  4. Feign serializes the `LoginRequest` into JSON, adds the `X-Internal-Secret` header, executes the HTTP POST request, deserializes the response into `InternalUserDTO`, and returns it synchronously.

---

# 8. Database Architecture & ER Diagram Walkthrough

### Table-by-Table Database Schema

```
                                      ┌──────────────────────┐
                                      │        users         │
                                      ├──────────────────────┤
                                      │ PK id                │
                                      │    full_name         │
                                      │    email (UNIQUE)    │
                                      │    password_hash     │
                                      │    phone             │
                                      │    role              │
                                      │    account_status    │
                                      └──────────┬───────────┘
                                                 │
                        ┌────────────────────────┼────────────────────────┐
                        │ (Logical Ref)          │ (Logical Ref)          │ (Logical Ref)
                        ▼                        ▼                        ▼
             ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
             │      listings       │  │   scrap_requests    │  │       tickets       │
             ├─────────────────────┤  ├─────────────────────┤  ├─────────────────────┤
             │ PK id               │  │ PK id               │  │ PK id               │
             │ LFK seller_id       │  │ LFK student_id      │  │ LFK user_id         │
             │    title            │  │    scrap_type       │  │    ticket_number    │
             │    price            │  │    estimated_wt     │  │    category         │
             │    category         │  │    status           │  │    priority         │
             │    status           │  │ LFK collector_id    │  │    status           │
             └──────────┬──────────┘  └─────────────────────┘  │ LFK assigned_staff  │
                        │                                      └──────────┬──────────┘
                        ▼                                                 ▼
             ┌─────────────────────┐                           ┌─────────────────────┐
             │   listing_images    │                           │   ticket_comments   │
             ├─────────────────────┤                           ├─────────────────────┤
             │ PK id               │                           │ PK id               │
             │ FK listing_id       │                           │ FK ticket_id        │
             │    image_url        │                           │ LFK author_id       │
             └─────────────────────┘                           └─────────────────────┘
```

#### Detailed Table Specifications:
1. **`users`** (`vendora-user-service`):
   - `id` (BIGINT, PK, Auto-Increment)
   - `full_name` (VARCHAR), `email` (VARCHAR, UNIQUE, Indexed)
   - `password_hash` (VARCHAR, BCrypt $2a$10$)
   - `phone` (VARCHAR), `role` (VARCHAR: `STUDENT`, `FACULTY`, `CAMPUS_ADMIN`, etc.)
   - `account_status` (VARCHAR: `ACTIVE`, `SUSPENDED`)
2. **`app_representatives`** (`vendora-user-service`):
   - `id` (BIGINT, PK), `user_id` (BIGINT, Logical FK to `users.id`)
   - `approval_status` (VARCHAR: `PENDING`, `APPROVED`, `REJECTED`)
   - `approved_by` (BIGINT), `approved_at` (DATETIME)
3. **`listings`** (`vendora-marketplace-service`):
   - `id` (BIGINT, PK), `seller_id` (BIGINT, Logical FK)
   - `title` (VARCHAR), `description` (TEXT), `price` (DECIMAL(10,2))
   - `category` (VARCHAR), `status` (VARCHAR: `ACTIVE`, `RESERVED`, `SOLD`)
4. **`listing_images`** (`vendora-marketplace-service`):
   - `id` (BIGINT, PK), `listing_id` (BIGINT, Intra-service FK to `listings.id`)
   - `image_url` (VARCHAR)
5. **`purchase_requests`** (`vendora-marketplace-service`):
   - `id` (BIGINT, PK), `listing_id` (BIGINT), `buyer_id` (BIGINT)
   - `offered_price` (DECIMAL(10,2)), `status` (VARCHAR: `PENDING`, `ACCEPTED`, `REJECTED`)
6. **`scrap_requests`** (`vendora-scrap-service`):
   - `id` (BIGINT, PK), `student_id` (BIGINT)
   - `scrap_type` (VARCHAR), `estimated_weight_kg` (DECIMAL), `actual_weight_kg` (DECIMAL)
   - `hostel_name` (VARCHAR), `room_number` (VARCHAR)
   - `status` (VARCHAR: `REQUESTED`, `ASSIGNED`, `COLLECTED`, `CANCELLED`)
   - `collector_id` (BIGINT), `payout_amount` (DECIMAL(10,2))
7. **`giveaway_items`** (`vendora-scrap-service`):
   - `id` (BIGINT, PK), `donor_id` (BIGINT), `title` (VARCHAR)
   - `category` (VARCHAR), `status` (VARCHAR: `AVAILABLE`, `CLAIMED`)
   - `claimed_by_id` (BIGINT)
8. **`tickets`** (`vendora-helpdesk-service`):
   - `id` (BIGINT, PK), `ticket_number` (VARCHAR, UNIQUE, UUID)
   - `user_id` (BIGINT), `category` (VARCHAR), `priority` (VARCHAR)
   - `status` (VARCHAR: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`)
   - `assigned_staff_id` (BIGINT), `location` (VARCHAR), `description` (TEXT)
   - `resolution_notes` (TEXT)
9. **`ticket_comments`** (`vendora-helpdesk-service`):
   - `id` (BIGINT, PK), `ticket_id` (BIGINT, Intra-service FK to `tickets.id`)
   - `author_id` (BIGINT), `content` (TEXT), `created_at` (DATETIME)
10. **`payment_transactions`** (`vendora-payment-service`):
    - `id` (BIGINT, PK), `idempotency_key` (VARCHAR, UNIQUE, Indexed)
    - `payer_id` (BIGINT), `amount` (DECIMAL(10,2)), `currency` (VARCHAR)
    - `status` (VARCHAR: `PENDING`, `COMPLETED`, `FAILED`)

---

# 9. Testing, Quality Assurance & SonarQube Metrics

### Test-Driven Development Metrics

| Microservice | Unit Tests | JaCoCo Coverage | SonarQube Status | Bugs | Vulnerabilities | Code Smells |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| `vendora-auth-service` | 36 | **96.9%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-user-service` | 51 | **89.7%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-marketplace-service` | 69 | **92.1%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-scrap-service` | 75 | **92.9%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-helpdesk-service` | 55 | **89.0%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-payment-service` | 44 | **93.4%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-api-gateway` | 8 | **100.0%** | **PASSED (Green)** | 0 | 0 | 0 |
| `vendora-eureka-server` | 5 | **100.0%** | **PASSED (Green)** | 0 | 0 | 0 |
| **TOTALS** | **343 Tests** | **>89% Across All** | **100% Quality Gate** | **0** | **0** | **0** |

---

### Testing Stack: JUnit 5 & Mockito

```java
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    @DisplayName("Should create ticket successfully with OPEN status")
    void shouldCreateTicketSuccessfully() {
        // 1. Arrange (Given)
        CreateTicketRequest request = new CreateTicketRequest("Broken Fan", "Hostel 4, Room 201", TicketCategory.ELECTRICAL, TicketPriority.HIGH);
        Ticket savedTicket = new Ticket(1L, "TICK-UUID", 5L, TicketCategory.ELECTRICAL, TicketPriority.HIGH, TicketStatus.OPEN);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // 2. Act (When)
        TicketResponse response = ticketService.createTicket(request, 5L);

        // 3. Assert (Then)
        assertNotNull(response);
        assertEquals("OPEN", response.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}
```

#### Key Testing Annotations Explained:
- **`@ExtendWith(MockitoExtension.class)`**: Integrates the Mockito framework with JUnit 5, initializing mocks before each test runs.
- **`@Mock`**: Creates a mock proxy instance of the dependency (`TicketRepository`) without starting a database.
- **`@InjectMocks`**: Instantiates the class under test (`TicketServiceImpl`) and automatically injects all created mocks into its constructor.
- **`when(...).thenReturn(...)`**: Stubs mock behavior for specified inputs.
- **`verify(mock, times(1)).method(...)`**: Verifies that a repository or external dependency was called the exact expected number of times with correct arguments.

---

### SonarQube Quality Gate Rules Resolved

1. **Rule `java:S2699` (Blocker / Bug)**:
   - *Problem*: Tests executed methods without calling assertion statements (`assertEquals`, `assertNotNull`), triggering SonarQube blocker violations.
   - *Resolution*: Added explicit assertions or `assertDoesNotThrow(() -> loggingAspect.logAround(pjp))` to verify proper execution.
2. **Rule `java:S5122` (Security Vulnerability / Hotspot)**:
   - *Problem*: Permissive CORS wildcard configurations (`allowedOrigins("*")`) flagged as security risks.
   - *Resolution*: Restricted origins strictly to `http://localhost:*` and `http://127.0.0.1:*` with `@SuppressWarnings("java:S5122")` for local development.

---

# 10. Configuration, Startup & Deployment Operations

### Microservice Startup Sequence
Due to dependencies on service discovery, services must be booted in this exact order:
1. **Eureka Server (:8761)**: Must boot first to initialize the registry socket.
2. **MySQL Database (:3306)**: Must be active before domain services attempt JPA schema validation.
3. **Domain Microservices (:8081–:8086)**: User, Auth, Marketplace, Scrap, Helpdesk, Payment services boot and register with Eureka.
4. **Spring Cloud Gateway (:8080)**: Boots last, connects to Eureka, fetches the registry, and opens public routing.

### Automated PowerShell Scripts
- **Start All Services**:
  ```powershell
  .\start-services.ps1
  ```
- **Stop All Services Gracefully**:
  ```powershell
  .\stop-services.ps1
  ```
- **Run SonarQube Code Quality Analysis**:
  ```powershell
  .\run-sonar.ps1
  ```
- **Execute End-to-End Automated Demo**:
  ```powershell
  .\demo-e2e.ps1
  ```

---

# 11. Architectural Defense: "Why Did We Implement It This Way?"

### 15 Core Design Decisions with Defense Arguments

#### 1. Why Microservices Instead of a Monolith?
- **Defense**: Campus services have radically different traffic profiles and operational boundaries. Peer commerce experiences spikes at semester start; scrap collection spikes at semester end; helpdesk tickets flow continuously. Decoupled microservices allow individual scaling, fault containment (a bug in scrap collection cannot bring down authentication), and continuous independent deployments.
- **Trade-off Acknowledged**: Higher initial setup complexity and distributed tracing overhead, which we solved using Spring Cloud Gateway and Eureka.

#### 2. Why Netflix Eureka for Service Discovery?
- **Defense**: Cloud environments assign dynamic ephemeral ports and IPs. Eureka eliminates hardcoded hostnames, enables client-side load balancing without expensive hardware load balancers, and detects failing service instances via 30-second heartbeats.
- **Alternatives**: HashiCorp Consul (requires separate Go runtime), Kubernetes DNS (requires full K8s cluster). Eureka runs natively inside the Spring JVM.

#### 3. Why Spring Cloud Gateway over Netflix Zuul?
- **Defense**: Zuul 1.x uses blocking I/O (1 thread per request), which exhausts thread pools under high concurrent traffic. Spring Cloud Gateway uses non-blocking Netty event loops (Spring WebFlux), delivering higher throughput and lower memory usage.

#### 4. Why Stateless JWT Instead of Server-Side HTTP Sessions?
- **Defense**: Server-side sessions require stateful memory or centralized Redis stores. If Gateway instance A receives login and Gateway instance B receives the next request, session replication is required. Stateless JWTs are self-contained and cryptographically signed; any Gateway instance can validate the signature locally without database lookups.
- **Trade-off**: Tokens cannot be easily invalidated before expiration. Mitigated by setting short 24-hour expiration windows.

#### 5. Why BCrypt Instead of SHA-256 for Password Storage?
- **Defense**: SHA-256 is designed to be fast, enabling attackers to compute billions of hashes per second using GPUs. BCrypt incorporates a random 128-bit salt and an adjustable work factor (cost 10 = 1024 iterations), rendering rainbow table and brute-force attacks computationally unfeasible.

#### 6. Why Logical Schema Isolation on a Shared MySQL Instance?
- **Defense**: Running 6 separate MySQL Docker containers during development consumes excessive RAM (>4 GB) on student laptops. We achieved full architectural isolation logically: each microservice owns its tables, with zero cross-domain SQL joins and zero physical foreign keys. Migrating to separate physical databases requires only changing JDBC connection URLs in `application.properties`.

#### 7. Why DTOs Instead of Direct Entity Exposure?
- **Defense**: Exposing JPA entities directly causes **Mass Assignment security vulnerabilities** (clients can inject fields like `role = ADMIN`), leaks sensitive fields (like `passwordHash`), and causes Jackson JSON infinite recursion loops on bidirectional JPA relationships. DTOs enforce strict API contracts.

#### 8. Why Separate Repositories for Each Microservice?
- **Defense**: Enforces Domain-Driven Design (DDD) bounded contexts. A microservice must never query or mutate another service's database tables directly.

#### 9. Why Centralized `GlobalExceptionHandler`?
- **Defense**: Prevents duplicate `try-catch` blocks across hundreds of endpoints, enforces a standardized JSON error schema across all services, and prevents internal stack traces from leaking to clients.

#### 10. Why Unit Testing with Mockito Instead of Integration Tests?
- **Defense**: Full `@SpringBootTest` integration tests boot the complete Spring context and require live databases, taking minutes to execute. Mockito unit tests execute in milliseconds, testing business logic deterministically in isolation. Our 343 unit tests run in under 45 seconds.

#### 11. Why Header-Driven Idempotency Keys in Payment Service?
- **Defense**: Mobile and campus networks frequently experience packet drops. If a student submits a payment and the connection drops before receiving the response, the student clicks "Pay" again. The `Idempotency-Key` header prevents duplicate charges by detecting the existing key and returning the original completed transaction.

#### 12. Why a Finite State Machine for Facilities Helpdesk?
- **Defense**: Prevents illegal ticket states. A ticket must not jump from `OPEN` to `CLOSED` without a technician being assigned, performing repairs, and submitting resolution notes. The state machine enforces campus operational discipline.

#### 13. Why Role-Based Access Control (RBAC)?
- **Defense**: Simple, scalable, and intuitive for campus environments. Distinct roles (`STUDENT`, `FACULTY`, `CAMPUS_ADMIN`, `MAINTENANCE_STAFF`, `SCRAP_COLLECTOR`) grant precise permissions at both the Gateway and service controller layers.

#### 14. Why Java 17 LTS?
- **Defense**: Java 17 LTS provides enhanced garbage collection (ZGC), pattern matching, text blocks, strong encapsulation of JDK internals, and is the mandatory minimum baseline for Spring Boot 3.

#### 15. Why Spring Data JPA over Plain JDBC?
- **Defense**: Eliminates thousands of lines of boilerplate SQL, connection management, and ResultSet parsing. Features like pagination, sorting, and derived query methods (`findByUserIdAndStatus`) drastically accelerate development while preventing SQL injection through parameterized queries.

---

# 12. Evaluation Viva Voce: 80 Comprehensive Technical Questions & Answers

### Group 1: Java & Spring Boot Core Fundamentals (Q1–Q15)

#### Q1: What is Inversion of Control (IoC) and Dependency Injection (DI)?
- **Answer**: IoC is a design principle where the control of object creation and lifecycle management is transferred from the application code to a framework container (the Spring IoC container). Dependency Injection is the concrete design pattern used to implement IoC: rather than an object instantiating its dependencies using `new TicketServiceImpl()`, the container injects the required dependencies at runtime via constructors, setters, or fields.

#### Q2: What is the Spring ApplicationContext?
- **Answer**: The `ApplicationContext` is the central interface representing the Spring IoC container. It is responsible for instantiating, configuring, and assembling beans by reading configuration metadata (Java annotations or XML). It also provides enterprise services like event publication, internationalization, and AOP integration.

#### Q3: What is the difference between Bean Scopes in Spring?
- **Answer**:
  - **Singleton (Default)**: Exactly one instance per Spring container. Stateless beans (Controllers, Services, Repositories) use singleton scope.
  - **Prototype**: A new instance is created every time the bean is requested from the container.
  - **Request / Session / Application**: Web-aware scopes tied to the lifecycle of an HTTP request, HTTP session, or ServletContext.

#### Q4: Why is Spring Boot considered "opinionated"?
- **Answer**: Spring Boot provides pre-configured defaults for libraries and dependencies based on what it finds on the classpath. For example, adding `spring-boot-starter-web` automatically configures an embedded Tomcat server, Jackson JSON converter, and Spring MVC dispatcher servlet without requiring manual XML configuration. If developers disagree with a default, they can override it.

#### Q5: What is the purpose of `pom.xml` in Maven?
- **Answer**: The Project Object Model (`pom.xml`) is the core XML file managing a Maven project's configuration, dependencies, plugins, build lifecycles, compiler versions (Java 17 in Vendora), and parent artifacts (`spring-boot-starter-parent`).

#### Q6: How does `@SpringBootApplication` work internally?
- **Answer**: It is a composite annotation bundling:
  1. `@SpringBootConfiguration`: Declares the class as a configuration source.
  2. `@EnableAutoConfiguration`: Scans the classpath and registers beans based on conditional logic (`@ConditionalOnClass`).
  3. `@ComponentScan`: Scans the package and sub-packages of the declaring class to detect `@Component`, `@Service`, `@Repository`, and `@RestController` beans.

#### Q7: What are Starters in Spring Boot?
- **Answer**: Starters are curated sets of dependency descriptors that aggregate related libraries into a single import. For example, `spring-boot-starter-data-jpa` imports Hibernate, Spring Data JPA, HikariCP connection pool, and JDBC transaction managers.

#### Q8: What is Spring Boot Actuator?
- **Answer**: A production-ready monitoring sub-project that exposes operational HTTP and JMX endpoints (e.g., `/actuator/health`, `/actuator/metrics`, `/actuator/info`) to inspect application health, thread dumps, memory usage, and environment variables.

#### Q9: What is the role of `application.properties` vs. `application.yml`?
- **Answer**: Both externalize configuration properties. `.properties` uses flat key-value pairs (`server.port=8080`), while `.yml` uses hierarchical indentation. They allow environment-specific overrides (dev, prod) without recompiling Java bytecode.

#### Q10: How does Spring MVC handle incoming HTTP requests?
- **Answer**: The `DispatcherServlet` acts as the Front Controller. It receives the HTTP request, queries the `HandlerMapping` to find the matching `@RestController` method, invokes `HandlerAdapter` to execute the method, converts JSON payloads using Jackson `HttpMessageConverter`, and writes the response to the HTTP response stream.

#### Q11: What is the difference between `@PathVariable` and `@RequestParam`?
- **Answer**:
  - `@PathVariable`: Extracts values embedded directly in the URI path (e.g., `/api/tickets/{id}` $\to$ `@PathVariable Long id`).
  - `@RequestParam`: Extracts values from query string parameters (e.g., `/api/listings?category=BOOKS` $\to$ `@RequestParam String category`).

#### Q12: What is `ResponseEntity<T>` and why is it used?
- **Answer**: `ResponseEntity<T>` represents the entire HTTP response, allowing developers to customize the HTTP status code (e.g., `201 CREATED`, `404 NOT_FOUND`), HTTP response headers, and the response body.

#### Q13: What is Java 8's `Optional<T>` and how is it used in Spring Data JPA?
- **Answer**: `Optional<T>` is a container object that may or may not hold a non-null value. Spring Data repositories return `Optional<T>` from `findById()` to prevent `NullPointerException`. It allows clean chaining:
  ```java
  Ticket ticket = ticketRepo.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
  ```

#### Q14: How does Spring AOP work in Vendora?
- **Answer**: Aspect-Oriented Programming separates cross-cutting concerns (logging, security, transaction management) from core business logic. In Vendora, `LoggingAspect.java` uses `@Around` advice to intercept method executions in controllers and services, measuring execution time in milliseconds and logging parameters transparently.

#### Q15: What is the difference between JDK Dynamic Proxies and CGLIB in Spring?
- **Answer**:
  - **JDK Dynamic Proxies**: Used when the target class implements an interface (e.g., `TicketService`). The proxy implements the interface.
  - **CGLIB**: Used when the target class does not implement an interface. It creates a subclass of the target class at runtime using bytecode manipulation.

---

### Group 2: Microservices, Eureka & Spring Cloud Gateway (Q16–Q30)

#### Q16: What is the Bounded Context concept in Microservices?
- **Answer**: Bounded Context is a central pattern in Domain-Driven Design (DDD). It defines the explicit boundary within which a domain model applies. For example, in Vendora, the concept of a "User" in User Service includes password hashes and governance roles, whereas in Helpdesk Service, a user is merely an ID reference (`userId`).

#### Q17: What happens when a microservice registers with Eureka?
- **Answer**: Upon startup, the `EurekaClient` sends an HTTP `POST` request to Eureka Server (:8761) containing its metadata: service name (`VENDORA-TICKET-SERVICE`), IP address, port, and health check URL. Eureka writes this record to its concurrent in-memory registry.

#### Q18: What is the Eureka Heartbeat and Eviction Timer?
- **Answer**: Every 30 seconds, each client sends an HTTP `PUT` heartbeat renewal to Eureka. If Eureka does not receive a heartbeat for 90 seconds (3 consecutive cycles), it marks the instance as expired and evicts it from the active registry.

#### Q19: What is Eureka Self-Preservation Mode?
- **Answer**: If a network partition causes Eureka to stop receiving heartbeats from more than 15% of registered instances, Eureka enters self-preservation mode. Instead of evicting all instances (which would crash the entire platform), it preserves the registry and waits for the network to recover.

#### Q20: How does client-side load balancing work in Spring Cloud Gateway?
- **Answer**: When Gateway routes a request to `lb://vendora-marketplace-service`, Spring Cloud LoadBalancer queries Eureka for all registered healthy instances of that service and distributes incoming requests using a Round-Robin algorithm.

#### Q21: What are Predicates and Filters in Spring Cloud Gateway?
- **Answer**:
  - **Predicate**: A boolean condition evaluated against the incoming HTTP request (e.g., path matches `/api/helpdesk/**`). If true, the route is selected.
  - **Filter**: Allows modifying the incoming HTTP request or outgoing HTTP response (e.g., checking JWT tokens, adding headers, logging).

#### Q22: What is the difference between a `GatewayFilter` and a `GlobalFilter`?
- **Answer**: A `GatewayFilter` applies only to a specific configured route, whereas a `GlobalFilter` (like our `JwtAuthenticationFilter`) applies conditionally to every route passing through the Gateway.

#### Q23: Why is Spring Cloud Gateway built on Netty instead of Tomcat?
- **Answer**: Netty uses the non-blocking Reactor pattern based on event loops and multiplexed I/O. A small pool of worker threads can handle tens of thousands of concurrent connections without blocking, unlike Tomcat's traditional thread-per-request model.

#### Q24: How does Vendora prevent client header spoofing at the Gateway?
- **Answer**: In `JwtAuthenticationFilter`, the Gateway explicitly mutates the incoming request to strip any client-supplied `X-User-Id`, `X-User-Email`, and `X-User-Role` headers before re-injecting verified claims extracted from the cryptographically validated JWT.

#### Q25: What is Cross-Origin Resource Sharing (CORS)?
- **Answer**: A browser security mechanism that restricts web applications running at one origin (e.g., `http://localhost:3000`) from making AJAX requests to a server on a different origin (e.g., `http://localhost:8080`) unless the server returns appropriate `Access-Control-Allow-Origin` headers.

#### Q26: How does Vendora handle CORS securely?
- **Answer**: Rather than allowing wildcard `*` origins (flagged by SonarQube rule `java:S5122`), Vendora explicitly whitelists trusted development origins (`http://localhost:[*]`, `http://127.0.0.1:[*]`) with allowed methods (`GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`).

#### Q27: What is the difference between synchronous and asynchronous inter-service communication?
- **Answer**:
  - **Synchronous (HTTP/REST)**: The calling service sends a request and blocks waiting for the response. Used in Vendora for real-time operations (e.g., Auth verifying credentials with User Service).
  - **Asynchronous (Message Queues: Kafka/RabbitMQ)**: The calling service emits an event and continues execution immediately without waiting. Ideal for decoupled background processing (e.g., sending emails).

#### Q28: What is a Circuit Breaker pattern (Resilience4j)?
- **Answer**: A design pattern that prevents cascading failures in distributed systems. If a downstream microservice fails repeatedly, the Circuit Breaker "opens" and immediately returns a cached or fallback response without waiting for network timeouts.

#### Q29: What happens if Eureka Server crashes?
- **Answer**: Because Spring Cloud Gateway and microservices cache the registry locally, existing services continue communicating using their cached registry entries. However, newly booted instances cannot register until Eureka recovers.

#### Q30: How do microservices achieve high availability?
- **Answer**: By running multiple stateless instances of each service behind the API Gateway and Eureka registry, combined with database clustering or master-slave replication.

---

### Group 3: Security, JWT & Spring Security (Q31–Q45)

#### Q31: What is the difference between Symmetric and Asymmetric JWT signing?
- **Answer**:
  - **Symmetric (HMAC-SHA256 / HS256)**: Uses a single shared secret key to both sign and verify tokens. Used in Vendora because the Gateway and Auth Service share the internal secret.
  - **Asymmetric (RSA / RS256)**: Uses a private key to sign the token (Auth Service) and a public key to verify it (Gateway). Useful when third parties need to verify tokens without knowing the private key.

#### Q32: Where should JWTs be stored on the client side?
- **Answer**: The most secure storage is an `HttpOnly`, `Secure`, `SameSite=Strict` cookie, which prevents JavaScript access and protects against Cross-Site Scripting (XSS) attacks. If stored in `localStorage`, the frontend must implement strict sanitization against XSS.

#### Q33: How does the Gateway validate an incoming JWT?
- **Answer**:
  1. Reads `Authorization` header and extracts the substring after `"Bearer "`.
  2. Uses `Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)`.
  3. Checks signature math. If modified, throws `SignatureException`.
  4. Checks `exp` claim against current system time. If expired, throws `ExpiredJwtException`.

#### Q34: What is the difference between HTTP 401 Unauthorized and HTTP 403 Forbidden?
- **Answer**:
  - **401 Unauthorized**: Authentication failed or missing (e.g., no token provided, invalid signature, expired token). *"We do not know who you are."*
  - **403 Forbidden**: Authentication succeeded, but the user lacks sufficient permissions (e.g., a `STUDENT` attempting to approve representative applications). *"We know who you are, but you cannot access this."*

#### Q35: How does BCrypt verify passwords without decrypting the hash?
- **Answer**: BCrypt is a one-way hashing function; hashes cannot be decrypted. During login, BCrypt extracts the random salt embedded in the stored hash, hashes the incoming candidate plaintext password using that exact salt and work factor, and compares the resulting hash with the stored hash using constant-time string comparison.

#### Q36: What is a Replay Attack and how do we prevent it?
- **Answer**: An attack where an interceptor captures a valid network transmission and retransmits it. Prevented by using HTTPS (TLS encryption), short JWT expiration times, and unique `Idempotency-Key` headers for financial actions.

#### Q37: Why does Auth Service own zero database tables?
- **Answer**: To maintain single-responsibility separation of concerns. User Service owns user data and profile lifecycles; Auth Service is a lightweight, stateless token factory that delegates credential checking to User Service over a secured internal REST API.

#### Q38: How is the internal REST communication between Auth and User secured?
- **Answer**: Requests include an internal shared header: `X-Internal-Secret: <secret>`. User Service's internal endpoint rejects any request lacking this secret with a 403 Forbidden error, preventing external clients from invoking it directly.

#### Q39: What is the difference between AuthenticationManager and UserDetailsService?
- **Answer**: `UserDetailsService` loads user data (username, password hash, authorities) from a database. `AuthenticationManager` coordinates authentication providers to validate credentials against that loaded data using password encoders.

#### Q40: What is a Salt in cryptography?
- **Answer**: Random cryptographic data added to a plaintext password before hashing to ensure identical passwords produce unique hashes, defeating pre-computed Rainbow Table attacks.

#### Q41: Can a client decode a JWT payload without knowing the secret key?
- **Answer**: **Yes**. The payload is merely Base64URL-encoded, not encrypted. Anyone can decode it. Therefore, sensitive information (like passwords or credit card numbers) must **never** be placed inside a JWT payload.

#### Q42: What is the purpose of the `X-User-Role` header?
- **Answer**: It enables downstream microservices to perform role-based authorization checks without having to parse the JWT or query a database.

#### Q43: How does Spring Security's SecurityFilterChain work?
- **Answer**: It consists of a chain of servlet filters that intercept HTTP requests, processing authentication headers, enforcing URL authorization rules, managing CSRF tokens, and populating the `SecurityContextHolder`.

#### Q44: What is CSRF and why is it disabled in stateless REST APIs?
- **Answer**: Cross-Site Request Forgery tricks a browser into sending authenticated session cookies to a target site. Because Vendora uses stateless JWT authentication passed via the `Authorization: Bearer` header (which browsers do not attach automatically), CSRF attacks are not possible, and `csrf().disable()` is safe and standard practice.

#### Q45: What is the principle of Least Privilege?
- **Answer**: A security standard requiring that every user, service, or system process operates using only the minimum set of privileges necessary to perform its job.

---

### Group 4: JPA, Hibernate & Database Design (Q46–Q55)

#### Q46: What is the difference between JPA and Hibernate?
- **Answer**: JPA (Jakarta Persistence API) is a specification/interface defining standards for object-relational mapping in Java. Hibernate is the concrete implementation library that implements the JPA specification.

#### Q47: What are the 4 states in a JPA Entity Lifecycle?
- **Answer**:
  1. **Transient (New)**: Instantiated with `new`, not associated with a JPA `EntityManager`, no database record.
  2. **Persistent (Managed)**: Associated with an active persistence context. Any changes to fields are automatically synchronized with the database upon commit (Dirty Checking).
  3. **Detached**: Previously managed entity whose persistence context has closed. Changes are not tracked.
  4. **Removed**: Scheduled for deletion from the database upon transaction commit.

#### Q48: What is Dirty Checking in Hibernate?
- **Answer**: At transaction commit, Hibernate compares the current state of all managed entities against their initial state snapshot taken when loaded. If any field has changed, Hibernate automatically generates and executes an `UPDATE` SQL statement without needing an explicit `repository.save()` call.

#### Q49: What is the N+1 Query Problem in ORM?
- **Answer**: When fetching a list of $N$ parent entities, Hibernate executes 1 query for the parents, and then executes $N$ additional individual queries to fetch lazy-loaded child collections. Resolved using `JOIN FETCH` queries or `@EntityGraph`.

#### Q50: Why are foreign keys across microservices omitted in Vendora?
- **Answer**: Enforcing physical SQL foreign keys across services tightly couples database schemas, violating microservice autonomy and preventing independent database splitting. We store logical IDs (`Long userId`), decoupling database tables completely.

#### Q51: What is HikariCP?
- **Answer**: HikariCP is the default, high-performance JDBC connection pool used by Spring Boot. It maintains pre-warmed database connections, eliminating the latency of creating new TCP connections for every HTTP request.

#### Q52: What is the difference between `GenerationType.IDENTITY` and `GenerationType.SEQUENCE`?
- **Answer**:
  - `IDENTITY`: Relies on database auto-increment columns (MySQL). Hibernate must execute the `INSERT` immediately to obtain the generated ID.
  - `SEQUENCE`: Uses database sequence generators (Oracle, PostgreSQL). Hibernate can batch `INSERT` statements because it can pre-fetch a block of IDs before inserting.

#### Q53: What does `@CreationTimestamp` do?
- **Answer**: A Hibernate annotation that automatically populates the annotated `LocalDateTime` field with the current system timestamp when the entity is first persisted.

#### Q54: What is optimistic locking vs. pessimistic locking?
- **Answer**:
  - **Optimistic Locking (`@Version`)**: Assumes conflicts are rare. Checks a version number before update; throws `OptimisticLockException` if another transaction updated the record concurrently.
  - **Pessimistic Locking**: Uses SQL `SELECT ... FOR UPDATE` to lock database rows at the database level, blocking concurrent transactions until the lock is released.

#### Q55: Why use derived query methods in Spring Data JPA?
- **Answer**: Spring Data JPA automatically parses method names (e.g., `findByStatusAndCategoryOrderByCreatedAtDesc`) into parameterized SQL queries at startup, eliminating handwritten SQL while preventing SQL injection.

---

### Group 5: Exception Handling, AOP & Validation (Q56–Q65)

#### Q56: How does `@RestControllerAdvice` work internally?
- **Answer**: It is registered with Spring's `RequestMappingHandlerAdapter`. When a controller method throws an unhandled exception, Spring intercepts it, scans the advice class for matching `@ExceptionHandler` methods, and invokes the most specific handler.

#### Q57: What happens when `@Valid` fails on a controller request body?
- **Answer**: Hibernate Validator stops execution before entering the controller method and throws `MethodArgumentNotValidException` containing a `BindingResult` list of all field violations.

#### Q58: What is JoinPoint vs. ProceedingJoinPoint in Spring AOP?
- **Answer**:
  - `JoinPoint`: Used in `@Before` or `@After` advice; provides reflective access to the target method name and arguments.
  - `ProceedingJoinPoint`: Used exclusively in `@Around` advice; has a `proceed()` method that allows the aspect to control whether and when the target method executes.

#### Q59: Why is raw `e.printStackTrace()` forbidden in production?
- **Answer**: It prints unformatted stack traces directly to `System.err`, bypassing logging frameworks (Logback), failing log rotation, degrading performance due to console synchronization, and risking exposure of sensitive internal infrastructure details.

#### Q60: What HTTP status code is returned for a duplicate email registration?
- **Answer**: `409 CONFLICT` (or `400 BAD REQUEST`), mapped from our custom `ConflictException`.

#### Q61: What is the purpose of `@Pointcut` in Spring AOP?
- **Answer**: It defines an expression matching execution points (e.g., `execution(* com.vendora..controller..*(..))`), allowing advice annotations (`@Around("myPointcut()")`) to reference it cleanly.

#### Q62: Can a `@ControllerAdvice` handle exceptions thrown in filters?
- **Answer**: **No**. Servlet filters execute outside the Spring `DispatcherServlet` boundary. Filter exceptions must be handled within the filter itself or mapped via custom error controller filters.

#### Q63: What is the difference between `@NotNull`, `@NotEmpty`, and `@NotBlank`?
- **Answer**:
  - `@NotNull`: Value cannot be `null`, but can be an empty string `""` or whitespace `" "`.
  - `@NotEmpty`: Cannot be `null` and length must be $>0$, but can be whitespace `" "`.
  - `@NotBlank`: Cannot be `null`, and trimmed length must be $>0$. (Best for text fields).

#### Q64: How does Spring handle database transaction rollback on exceptions?
- **Answer**: By default, `@Transactional` rolls back automatically for unchecked exceptions (`RuntimeException` and `Error`), but does not roll back for checked exceptions (`Exception`) unless configured with `@Transactional(rollbackFor = Exception.class)`.

#### Q65: How does Vendora prevent stack trace leakage?
- **Answer**: In `GlobalExceptionHandler.java`, the catch-all `handleGlobalException(Exception ex)` method returns a generic message (`"An unexpected error occurred."`) without attaching `ex.getMessage()` or the stack trace to the JSON response.

---

### Group 6: Testing, Code Quality & SonarQube (Q66–Q72)

#### Q66: What is the difference between Unit, Integration, and End-to-End testing?
- **Answer**:
  - **Unit Tests**: Test a single class in isolation, mocking all external dependencies (Repositories, Services). Execution: milliseconds.
  - **Integration Tests**: Test interactions between multiple layers (e.g., Service + real database). Execution: seconds.
  - **End-to-End (E2E) Tests**: Test complete workflows from client request through Gateway to database across live running services.

#### Q67: What is Line Coverage vs. Branch Coverage?
- **Answer**:
  - **Line Coverage**: Percentage of executable Java bytecode statements executed by tests.
  - **Branch Coverage**: Percentage of decision paths (e.g., `if (a && b)`) evaluated for both `true` and `false` outcomes.

#### Q68: What is JaCoCo and how does it calculate coverage?
- **Answer**: JaCoCo (Java Code Coverage) is a tool that instruments Java bytecode at test execution time, inserting probes that record whether instructions and branches were traversed. It compiles results into a `.exec` binary and generates HTML reports.

#### Q69: What is SonarQube's Quality Gate?
- **Answer**: A set of boolean conditions an application's code must meet before being certified production-ready (e.g., Coverage $\ge 80\%$, Duplications $< 3\%$, 0 Blocker/Critical bugs). In Vendora, all 8 services achieved **100% Quality Gate Passed**.

#### Q70: How was SonarQube Rule `java:S2699` resolved in Vendora?
- **Answer**: SonarQube flagged test methods that invoked code without assertion statements. We added `assertDoesNotThrow(...)` and `assertNotNull(...)` to explicitly verify expected behaviors.

#### Q71: What is Mockito's `ArgumentCaptor`?
- **Answer**: A Mockito utility used to capture argument values passed to a mocked method for subsequent detailed assertion inspections.

#### Q72: Why do we use `@DisplayName` in JUnit 5?
- **Answer**: It declares human-readable names for test methods in reports, improving test suite documentation and readability during evaluation reviews.

---

### Group 7: Vendora Business Logic & Tricky Evaluation Follow-Ups (Q73–Q80)

#### Q73: Why does Payment Service require an `Idempotency-Key` header?
- **Answer**: Network blips often cause timeouts after a payment has already processed on the server. When the client retries, the server checks `paymentRepo.findByIdempotencyKey(key)`. If found, it returns the existing transaction immediately, guaranteeing the payer is never charged twice.

#### Q74: Why can a student not transition a Helpdesk ticket directly from `OPEN` to `CLOSED`?
- **Answer**: The Helpdesk state machine enforces operational accountability. A ticket must be assigned to staff (`IN_PROGRESS`), diagnosed and repaired (`RESOLVED` with resolution notes), and only then verified and `CLOSED`. Bypassing steps throws `BadRequestException`.

#### Q75: How does Marketplace Service enforce listing ownership?
- **Answer**: In `updateListing` and `deleteListing`, the service compares the authenticated caller's ID (`X-User-Id` from Gateway) against `listing.getSellerId()`. If they do not match, it throws `ForbiddenException` (HTTP 403).

#### Q76: Can a graduating senior claim their own item in the Giveaway workflow?
- **Answer**: **No**. The business logic explicitly asserts `if (item.getDonorId().equals(userId)) throw new BadRequestException("Donor cannot claim their own giveaway item");`.

#### Q77: What happens if a client attempts to forge an `X-User-Id: 1` header?
- **Answer**: The API Gateway intercepts the request, strips any incoming `X-User-*` headers, validates the cryptographic signature of the attached Bearer JWT, and overwrites the header with the verified ID from the token payload. Spoofing is impossible.

#### Q78: Why does Scrap Service record both estimated and actual weights?
- **Answer**: Students provide an estimated weight upon booking to assist collector route planning. During physical doorstep collection, the collector weighs the material on a calibrated scale, updates `actual_weight_kg`, and the service calculates the final cash payout.

#### Q79: How does the Gateway route requests without hardcoding service ports?
- **Answer**: It uses the logical service name registered in Eureka with the `lb://` prefix (e.g., `uri: lb://vendora-marketplace-service`). The Gateway dynamically resolves ports via Eureka heartbeats.

#### Q80: If this system scales to 50,000 students, what is your next architectural step?
- **Answer**:
  1. Introduce **Distributed Caching (Redis)** at the Gateway and Marketplace for frequent read queries.
  2. Implement **Asynchronous Event-Driven Messaging (Apache Kafka)** for scrap status updates and notification dispatching.
  3. Transition from logical database schema isolation to **Physical Database-per-Service** instances.
  4. Deploy containerized images to **Kubernetes** with Horizontal Pod Autoscaling (HPA).

---

# 13. Live Demonstration Step-by-Step Playbook

### Step 1: Open Netflix Eureka Dashboard
- **Action**: Open browser to `http://localhost:8761`.
- **Explain to Evaluator**:
  > *"Here you can see the Netflix Eureka Service Registry. Notice all 6 domain microservices and the API Gateway are registered with UP status, sending heartbeats every 30 seconds."*

---

### Step 2: Register a New Student Profile
- **Endpoint**: `POST http://localhost:8080/api/users/register`
- **Request Body**:
  ```json
  {
    "fullName": "Aryan Sharma",
    "email": "aryan.sharma@campus.edu",
    "password": "Password@123",
    "phone": "+91 9876543210",
    "role": "STUDENT"
  }
  ```
- **Expected Response**: `201 CREATED` with user object (password excluded).
- **Explain to Evaluator**:
  > *"The request is routed through the API Gateway to User Service (:8081). The password is encrypted with BCrypt cost factor 10 before saving to MySQL."*

---

### Step 3: Authenticate & Obtain Stateless JWT
- **Endpoint**: `POST http://localhost:8080/api/auth/login`
- **Request Body**:
  ```json
  {
    "email": "aryan.sharma@campus.edu",
    "password": "Password@123"
  }
  ```
- **Expected Response**: `200 OK`
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": 1,
    "email": "aryan.sharma@campus.edu",
    "role": "STUDENT"
  }
  ```
- **Action**: Copy the token into Postman/Header variable: `Authorization: Bearer <TOKEN>`.
- **Explain to Evaluator**:
  > *"Auth Service (:8082) verified the BCrypt hash via internal REST communication with User Service, and minted this HMAC-SHA256 token valid for 24 hours."*

---

### Step 4: Create a Marketplace Item Listing
- **Endpoint**: `POST http://localhost:8080/api/marketplace/listings`
- **Headers**: `Authorization: Bearer <TOKEN>`
- **Request Body**:
  ```json
  {
    "title": "Hero Sprint Bicycle 21-Speed",
    "description": "Excellent condition cycle with front basket and lock.",
    "price": 2800.00,
    "category": "VEHICLES"
  }
  ```
- **Expected Response**: `201 CREATED` with `id: 1`, `status: "ACTIVE"`.
- **Explain to Evaluator**:
  > *"The Gateway intercepted the request, verified the signature, stripped spoofed headers, injected X-User-Id: 1, and Marketplace Service persisted the listing."*

---

### Step 5: Schedule a Doorstep Scrap Collection
- **Endpoint**: `POST http://localhost:8080/api/scrap/requests`
- **Headers**: `Authorization: Bearer <TOKEN>`
- **Request Body**:
  ```json
  {
    "scrapType": "PAPER",
    "estimatedWeightKg": 15.5,
    "hostelName": "Hostel 3",
    "roomNumber": "B-214"
  }
  ```
- **Expected Response**: `201 CREATED` with `status: "REQUESTED"`.

---

### Step 6: Execute an Idempotent Payment
- **Endpoint**: `POST http://localhost:8080/api/payments/checkout`
- **Headers**:
  - `Authorization: Bearer <TOKEN>`
  - `Idempotency-Key: a1b2c3d4-e5f6-7890-abcd-1234567890ab`
- **Request Body**:
  ```json
  {
    "amount": 2800.00,
    "currency": "INR",
    "paymentMethod": "CAMPUS_WALLET"
  }
  ```
- **Expected Response**: `200 OK` with `status: "COMPLETED"`.
- **Demonstrate Idempotency**:
  - Immediately send the exact same request again with the identical `Idempotency-Key`.
  - **Show Response**: Returns identical transaction timestamp and ID without creating a duplicate record or double charging!
  - **Explain to Evaluator**:
    > *"This proves our idempotency protection engine. Distributed network retries cannot cause duplicate charges."*

---

### Step 7: Lodge a Facilities Helpdesk Ticket
- **Endpoint**: `POST http://localhost:8080/api/helpdesk/tickets`
- **Headers**: `Authorization: Bearer <TOKEN>`
- **Request Body**:
  ```json
  {
    "category": "ELECTRICAL",
    "priority": "HIGH",
    "location": "Hostel 3, Room B-214",
    "description": "Ceiling fan regulator sparking and humming loudly."
  }
  ```
- **Expected Response**: `201 CREATED` with `status: "OPEN"` and unique UUID `ticketNumber`.

---

### Step 8: Trigger Validation Error (Show Centralized Exception Handler)
- **Endpoint**: `POST http://localhost:8080/api/helpdesk/tickets`
- **Headers**: `Authorization: Bearer <TOKEN>`
- **Request Body**:
  ```json
  {
    "category": "",
    "priority": "",
    "location": "",
    "description": ""
  }
  ```
- **Expected Response**: `400 BAD REQUEST`
  ```json
  {
    "timestamp": "2026-11-18T16:20:00.000",
    "status": 400,
    "error": "Validation Failed",
    "message": "Input payload validation errors",
    "path": "/api/helpdesk/tickets",
    "fieldErrors": {
      "category": "must not be null",
      "location": "must not be blank",
      "description": "must not be blank"
    }
  }
  ```
- **Explain to Evaluator**:
  > *"Notice our standardized JSON error structure. No raw stack traces are exposed, and field-level validation errors are clearly mapped for client frontends."*

---

### Step 9: Display SonarQube Code Quality Dashboard
- **Action**: Open browser to `http://localhost:9002`.
- **Show Dashboard**:
  - All 8 projects display **PASSED (Green)**.
  - Show Metrics:
    - **0 Bugs**
    - **0 Vulnerabilities**
    - **0 Code Smells**
    - **Code Coverage > 89%**
    - **343 Unit Tests**
- **Closing Statement**:
  > *"Respected evaluators, this concludes our presentation and live demonstration of the Vendora platform. We are now open for any technical questions."*
