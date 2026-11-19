$htmlPath = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$sw = [System.IO.StreamWriter]::new($htmlPath, $true, [System.Text.Encoding]::UTF8)

$sw.WriteLine(@"
<!-- COVER PAGE -->
<div class="cover-page">
    <div class="cover-header">
        <h1 class="cover-brand no-break-before">VENDORA</h1>
        <div class="cover-motto">Your Campus. Your Community. One Platform.</div>
    </div>
    
    <div class="cover-title-block">
        <div class="cover-title">Comprehensive Technical Architecture &amp; Master Project Documentation</div>
        <div class="cover-subtitle">Complete Microservices Blueprint, Distributed System Design, Implementation Verification, Database ERD, REST API Contract, and Final Evaluation Defense</div>
        
        <table class="cover-meta-table">
            <tr>
                <td class="label">Project Name</td>
                <td><strong>Vendora Campus Community Ecosystem</strong></td>
            </tr>
            <tr>
                <td class="label">Architecture Model</td>
                <td>Distributed Microservices Architecture (Spring Cloud &amp; Netflix Eureka)</td>
            </tr>
            <tr>
                <td class="label">Baseline Version</td>
                <td><strong>2.0 — Consolidated Implementation Baseline</strong></td>
            </tr>
            <tr>
                <td class="label">Target Runtime Stack</td>
                <td>Java 17 / 25 LTS • Spring Boot 4.1.2 • Spring Cloud 2025.1.4 • MySQL 8.x • JJWT 0.12.6</td>
            </tr>
            <tr>
                <td class="label">Perimeter Security</td>
                <td>Stateless JWT (HMAC-SHA256), Spring Cloud Gateway WebFlux, BCrypt, RBAC</td>
            </tr>
            <tr>
                <td class="label">Database Architecture</td>
                <td>Consolidated Schema (<code>vendora_db</code>) with Logical Entity Isolation</td>
            </tr>
            <tr>
                <td class="label">Test Execution Status</td>
                <td><strong>59 of 59 Automated Unit Tests Passing (0 Failures, 0 Errors)</strong></td>
            </tr>
            <tr>
                <td class="label">Candidate / Author</td>
                <td><strong>Tanisha Kar</strong> (Campus Identity: <code>tanisha.kar.test@campus.edu</code>)</td>
            </tr>
            <tr>
                <td class="label">Publication Date</td>
                <td>November 16, 2026</td>
            </tr>
        </table>
    </div>
    
    <div class="cover-footer">
        <div><strong>Vendora Ecosystem</strong> • Autonomous University Infrastructure</div>
        <div>Confidential &amp; Proprietary • Academic Evaluation Master Document</div>
    </div>
</div>

<!-- TABLE OF CONTENTS (Single Page Layout) -->
<div class="page-break">
    <h1 class="no-break-before" style="margin-top: 10px; margin-bottom: 12px;">Table of Contents</h1>
    <ul class="toc-list" style="margin: 8px 0;">
        <li class="toc-item level-1"><span>1. Project Overview</span><span>Page 3</span></li>
        <li class="toc-item level-2"><span>1.1 Concept &amp; Value Proposition</span><span>3</span></li>
        <li class="toc-item level-2"><span>1.2 Core Project Objectives</span><span>3</span></li>
        <li class="toc-item level-2"><span>1.3 Target Users &amp; Role Taxonomy</span><span>3</span></li>
        
        <li class="toc-item level-1"><span>2. System Architecture</span><span>Page 5</span></li>
        <li class="toc-item level-2"><span>2.1 Microservices Roster &amp; Runtime Topology</span><span>5</span></li>
        <li class="toc-item level-2"><span>2.2 Security Architecture &amp; Perimeter Header Propagation</span><span>5</span></li>
        <li class="toc-item level-2"><span>2.3 Inter-Service Communication (Auth &harr; User Service)</span><span>6</span></li>
        
        <li class="toc-item level-1"><span>3. Implementation Status: Service-by-Service Breakdown</span><span>Page 7</span></li>
        <li class="toc-item level-2"><span>3.1 vendora-eureka-server (:8761)</span><span>7</span></li>
        <li class="toc-item level-2"><span>3.2 vendora-api-gateway (:8080)</span><span>7</span></li>
        <li class="toc-item level-2"><span>3.3 vendora-auth-service (:8082)</span><span>7</span></li>
        <li class="toc-item level-2"><span>3.4 vendora-user-service (:8081)</span><span>8</span></li>
        <li class="toc-item level-2"><span>3.5 vendora-marketplace-service (:8083)</span><span>8</span></li>
        <li class="toc-item level-2"><span>3.6 vendora-scrap-service (:8085)</span><span>8</span></li>
        <li class="toc-item level-2"><span>3.7 vendora-payment-service (:8086)</span><span>9</span></li>
        <li class="toc-item level-2"><span>3.8 vendora-helpdesk-service (:8084)</span><span>9</span></li>
        
        <li class="toc-item level-1"><span>4. Database Design &amp; Entity Relationship Architecture</span><span>Page 10</span></li>
        <li class="toc-item level-2"><span>4.1 Consolidated Schema Strategy &amp; Boundary Rules</span><span>10</span></li>
        <li class="toc-item level-2"><span>4.2 Comprehensive Entity Relationship Diagram (ERD)</span><span>10</span></li>
        <li class="toc-item level-2"><span>4.3 Complete Data Dictionary &amp; Table Constraints</span><span>11</span></li>
        
        <li class="toc-item level-1"><span>5. Comprehensive REST API Documentation</span><span>Page 13</span></li>
        <li class="toc-item level-2"><span>5.1 Authentication Service Endpoints (:8082)</span><span>13</span></li>
        <li class="toc-item level-2"><span>5.2 User &amp; Identity Service Endpoints (:8081)</span><span>13</span></li>
        <li class="toc-item level-2"><span>5.3 Marketplace &amp; Commerce Service Endpoints (:8083)</span><span>14</span></li>
        <li class="toc-item level-2"><span>5.4 Scrap Collection &amp; Giveaways Service Endpoints (:8085)</span><span>15</span></li>
        <li class="toc-item level-2"><span>5.5 Payment &amp; Settlement Service Endpoints (:8086)</span><span>16</span></li>
        
        <li class="toc-item level-1"><span>6. Pending Implementation &amp; Gaps Analysis</span><span>Page 17</span></li>
        <li class="toc-item level-1"><span>7. Remaining Features to Achieve 100% Project Completion</span><span>Page 19</span></li>
        <li class="toc-item level-1"><span>8. Testing Status &amp; Quality Verification</span><span>Page 20</span></li>
        <li class="toc-item level-1"><span>9. Project Completion Roadmap</span><span>Page 22</span></li>
        <li class="toc-item level-1"><span>10. Final Evaluation Preparation: Technical Justifications &amp; Viva Defense</span><span>Page 23</span></li>
    </ul>
</div>

<!-- SECTION 1: PROJECT OVERVIEW -->
<div class="page-break">
    <h1>1. Project Overview</h1>
    
    <h2>1.1 Concept &amp; Value Proposition</h2>
    <p><strong>Vendora</strong> (<em>"Your Campus. Your Community. One Platform."</em>) is a hyper-local, campus-centric digital ecosystem designed to unify fractured university student and faculty interactions into a coherent, high-trust, and sustainable platform.</p>
    
    <p>In traditional campus environments, student commerce and services are fragmented across unverified WhatsApp groups, Discord servers, notice boards, and informal scrap dealers. This leads to rampant scams, price exploitation, untracked waste generation, and lost academic resources. Vendora addresses these challenges by combining:</p>
    <ol>
        <li><strong>Verified Academic Identity &amp; Role Governance:</strong> A single source of truth for student, faculty, and administrative identities.</li>
        <li><strong>Circular Peer-to-Peer Marketplace:</strong> Facilitating exchange of second-hand academic textbooks, laboratory equipment, electronics, study furniture, and bicycles.</li>
        <li><strong>Campus Circular Economy &amp; Scrap Hub:</strong> Facilitating recyclable paper/cardboard/e-waste pickups and zero-cost item giveaways.</li>
        <li><strong>Idempotent Financial Settlement Engine:</strong> Providing auditable, transparent simulated financial transactions.</li>
        <li><strong>Operational Facilities &amp; Administrative Workflows:</strong> Student representative verification, staff assignment, and maintenance workflows.</li>
    </ol>
    
    <pre><code>+-------------------------------------------------------------+
|                           VENDORA                           |
|             Hyper-Local Campus Digital Ecosystem            |
+-------------------------------------------------------------+
                              |
 +----------------------------+----------------------------+
 |                            |                            |
 v                            v                            v
+------------------+ +------------------+ +------------------+
| Campus Commerce  | | Circular Economy | | Trust &amp; Identity |
|  &amp; Marketplace   | |    &amp; Scrap Hub   | |    Governance    |
+------------------+ +------------------+ +------------------+
| • Used Textbooks | | • Paper / E-Waste| | • Verified .edu  |
| • Lab Drafters   | | • Free Giveaways | | • RBAC &amp; Reps    |
| • P2P In-App Chat| | • Staff Pickups  | | • Gateway Auth   |
| • Purchase Bids  | | • Material Weight| | • Audit Logging  |
+------------------+ +------------------+ +------------------+</code></pre>

    <h2>1.2 Core Project Objectives</h2>
    <ul>
        <li><strong>Zero-Trust Perimeter Security:</strong> Authenticate every request at the perimeter gateway while keeping internal services loosely coupled and lightweight.</li>
        <li><strong>Resource Reusability &amp; Affordability:</strong> Lower textbook and laboratory kit costs for junior engineering and university students by 50–70% through peer resale.</li>
        <li><strong>Campus Sustainability:</strong> Divert recyclable paper (registers, notebooks, exams) and electronic waste away from general landfills into structured recycling streams.</li>
        <li><strong>Microservices Isolation:</strong> Ensure independent scalability, deployment, and technology flexibility across domain modules while retaining a pragmatic database design for MVP evaluation.</li>
    </ul>

    <h2>1.3 Target Users &amp; Role Taxonomy</h2>
    <p>The platform's role-based access control (RBAC) model defines explicit privileges across domain operations:</p>
    
    <table>
        <thead>
            <tr>
                <th style="width: 25%;">Role Identifier</th>
                <th>Description &amp; Access Scope</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><code>STUDENT</code></td>
                <td>Default authenticated campus user. Can browse/publish listings, submit purchase offers, engage in P2P chats, request scrap pickups, claim giveaways, and apply for representative status.</td>
            </tr>
            <tr>
                <td><code>FACULTY</code></td>
                <td>Verified academic staff. Same baseline commerce access as students, with priority tagging for research and curriculum-aligned course literature.</td>
            </tr>
            <tr>
                <td><code>ADMIN</code></td>
                <td>Institutional administrator. Full system visibility: manages user accounts (status activation/suspension), reviews representative applications, assigns scrap staff, and audits payment logs.</td>
            </tr>
            <tr>
                <td><code>MAINTENANCE_STAFF</code></td>
                <td>Campus physical facility staff. Assigned to maintenance and facilities resolution (Helpdesk module).</td>
            </tr>
            <tr>
                <td><code>COLLECTION_STAFF</code></td>
                <td>Logistics/waste coordination personnel. Assigned to scrap pickup requests, updates collection statuses (<code>ASSIGNED</code> &rarr; <code>WEIGHED</code> &rarr; <code>COMPLETED</code>).</td>
            </tr>
            <tr>
                <td><code>APP_REPRESENTATIVE</code></td>
                <td>Specially authorized student liaisons (e.g., hostel wing or branch coordinators) who bridge administrative approvals and moderate local listings.</td>
            </tr>
        </tbody>
    </table>
</div>

<!-- SECTION 2: SYSTEM ARCHITECTURE -->
<div class="page-break">
    <h1>2. System Architecture</h1>
    <p>Vendora is architected as a distributed, cloud-native microservices ecosystem utilizing the Spring Boot and Spring Cloud stacks.</p>
    
    <div class="mermaid-container">
        <div class="mermaid">
graph TD
    Client["Client Applications (Web / Mobile / Postman)"]
    Gateway["Vendora API Gateway (:8080)<br/>Perimeter Security & Routing"]
    Eureka["Vendora Eureka Server (:8761)<br/>Service Registry & Discovery"]

    Client --> Gateway
    Gateway -.-> Eureka

    Auth["Vendora Auth Service (:8082)<br/>Login & Token Validation"]
    User["Vendora User Service (:8081)<br/>Profiles & Registration"]
    Market["Vendora Marketplace Service (:8083)<br/>Listings, Bids & P2P Chat"]
    Scrap["Vendora Scrap Service (:8085)<br/>Recyclables & Giveaways"]
    Payment["Vendora Payment Service (:8086)<br/>Idempotent Transactions"]

    Gateway --> Auth
    Gateway --> User
    Gateway --> Market
    Gateway --> Scrap
    Gateway --> Payment

    Auth -.->|Feign Client: /internal/users/auth-details| User

    DB[("MySQL Database: vendora_db (:3306)<br/>Consolidated Logical Schema")]

    User --> DB
    Market --> DB
    Scrap --> DB
    Payment --> DB
        </div>
    </div>
    
    <h2>2.1 Microservices Roster &amp; Runtime Topology</h2>
    <table>
        <thead>
            <tr>
                <th>Service Artifact</th>
                <th style="width: 10%; text-align: center;">Port</th>
                <th>Tech Stack</th>
                <th>Primary Responsibilities</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>vendora-eureka-server</code></strong></td>
                <td style="text-align: center;"><code>8761</code></td>
                <td>Spring Cloud Netflix Eureka Server</td>
                <td>Service registry, continuous heartbeat monitoring, and instance metadata management.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-api-gateway</code></strong></td>
                <td style="text-align: center;"><code>8080</code></td>
                <td>Spring Cloud Gateway (Reactive WebFlux), Spring Security, JJWT 0.12.6</td>
                <td>Single public perimeter reverse proxy, cryptographic JWT validation, token stripping, header forwarding, CORS handling, centralized Swagger aggregator.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-auth-service</code></strong></td>
                <td style="text-align: center;"><code>8082</code></td>
                <td>Spring Web MVC, OpenFeign, JJWT 0.12.6, BCrypt</td>
                <td>User authentication, credential validation via Feign, stateless JWT issuance, token claims inspection. <strong>Stateless (No Direct Database Connection)</strong>.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-user-service</code></strong></td>
                <td style="text-align: center;"><code>8081</code></td>
                <td>Spring Web MVC, Spring Data JPA, Hibernate, BCrypt</td>
                <td>User registration, password hashing, identity profile management, representative application lifecycle, protected internal auth endpoints.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-marketplace-service</code></strong></td>
                <td style="text-align: center;"><code>8083</code></td>
                <td>Spring Web MVC, Spring Data JPA, Hibernate</td>
                <td>Second-hand listings, category filtering, keyword search, buyer-to-seller request workflows, in-app P2P chat messages.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-scrap-service</code></strong></td>
                <td style="text-align: center;"><code>8085</code></td>
                <td>Spring Web MVC, Spring Data JPA, Hibernate</td>
                <td>Physical scrap collection scheduling, staff assignment, material weighing lifecycle, free item giveaways, interest claims, giveaway public discussion.</td>
            </tr>
            <tr>
                <td><strong><code>vendora-payment-service</code></strong></td>
                <td style="text-align: center;"><code>8086</code></td>
                <td>Spring Web MVC, Spring Data JPA, Hibernate</td>
                <td>Financial transaction tracking, simulation engine, UUID-based idempotency replay protection, reference resolution.</td>
            </tr>
        </tbody>
    </table>

    <h2>2.2 Security Architecture &amp; Perimeter Header Propagation</h2>
    <p>Vendora implements a <strong>Zero-Trust Perimeter</strong> security model:</p>
    <ol>
        <li><strong>Client Request:</strong> The client sends an HTTP request containing an <code>Authorization: Bearer &lt;JWT&gt;</code> header to the API Gateway (<code>:8080</code>).</li>
        <li><strong>Gateway Interception:</strong> The Gateway's <code>JwtAuthenticationFilter</code> (executing at <code>Ordered.HIGHEST_PRECEDENCE (-1)</code>) intercepts the call:
            <ul>
                <li>Verifies if the request matches public routes (e.g., <code>/api/registration/register</code>, <code>/api/auth/login</code>, Actuator, Swagger UI).</li>
                <li>If protected, extracts the Bearer token and verifies the HMAC-SHA256 signature using the shared secret (<code>VmVuZG9yYUxvY2FsSnd0U2VjcmV0S2V5MzJCeXRlcyE=</code>).</li>
                <li>Extracts subject (<code>userId</code>), <code>email</code>, and <code>role</code> claims.</li>
            </ul>
        </li>
        <li><strong>Header Sanitization &amp; Injection (Anti-Spoofing):</strong>
            <ul>
                <li>The Gateway explicitly strips any client-provided <code>X-User-Id</code>, <code>X-User-Email</code>, or <code>X-User-Role</code> headers to prevent identity spoofing.</li>
                <li>It mutates the downstream request by injecting trusted headers directly from verified claims:
                    <br/><code>X-User-Id: &lt;id&gt;</code>, <code>X-User-Email: &lt;email&gt;</code>, <code>X-User-Role: &lt;role&gt;</code>.</li>
            </ul>
        </li>
        <li><strong>Downstream Consumption:</strong> Microservices (<code>user-service</code>, <code>marketplace-service</code>, <code>scrap-service</code>, <code>payment-service</code>) read the <code>X-User-*</code> headers to enforce role and ownership checks without re-parsing or querying the database for identity verification.</li>
    </ol>

    <div class="mermaid-container">
        <div class="mermaid">
sequenceDiagram
    autonumber
    actor Client as Client / Mobile App
    participant Gateway as API Gateway (:8080)
    participant Auth as Auth Service (:8082)
    participant Downstream as Downstream Microservice

    Client->>Gateway: POST /api/auth/login {email, password}
    Gateway->>Auth: Route to Auth Service
    Auth-->>Gateway: Return 200 OK + JWT Bearer Token
    Gateway-->>Client: 200 OK + Bearer Token
    
    Client->>Gateway: POST /api/listings {title, price...}<br/>Headers: [Authorization: Bearer token, Spoofed X-User-Id: 99]
    Note over Gateway: 1. Validate JWT signature & expiration<br/>2. Extract claims (sub=2, role=STUDENT)<br/>3. Strip incoming X-User-* headers<br/>4. Inject verified X-User-Id: 2, X-User-Role: STUDENT
    Gateway->>Downstream: Forward mutated request
    Note over Downstream: Enforce ownerId = X-User-Id (2)
    Downstream-->>Gateway: 201 Created
    Gateway-->>Client: 201 Created
        </div>
    </div>

    <h2>2.3 Inter-Service Communication: Auth &harr; User Service</h2>
    <p><code>vendora-auth-service</code> has no direct database connection. When a user logs in:</p>
    <ol>
        <li><code>vendora-auth-service</code> receives <code>LoginRequest(email, password)</code>.</li>
        <li>It invokes <code>UserServiceClient</code> (Spring Cloud OpenFeign), which makes an HTTP GET request to <code>http://VENDORA-USER-SERVICE/internal/users/auth-details?email=...</code>.</li>
        <li>The request includes a shared internal secret header: <code>X-Service-Secret: VendoraLocalInternalSecret2026</code>.</li>
        <li><code>InternalAuthController</code> in <code>vendora-user-service</code> validates the secret:
            <ul>
                <li>If invalid/missing, returns <code>403 Forbidden</code>.</li>
                <li>If valid, fetches the user by email, returns <code>AuthUserDetailsResponse</code> containing <code>id</code>, <code>email</code>, <code>passwordHash</code>, <code>role</code>, and <code>accountStatus</code>.</li>
            </ul>
        </li>
        <li><code>vendora-auth-service</code> compares raw credentials using <code>BCryptPasswordEncoder.matches()</code>, verifies <code>accountStatus == ACTIVE</code>, and issues the signed JWT.</li>
    </ol>
</div>
"@)
$sw.Flush()
$sw.Close()
Write-Host "Updated Part 1 TOC with single-page layout."
