$htmlPath = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$sw = [System.IO.StreamWriter]::new($htmlPath, $true, [System.Text.Encoding]::UTF8)

$sw.WriteLine(@"
<!-- SECTION 3: IMPLEMENTATION STATUS -->
<div class="page-break">
    <h1>3. Implementation Status: Service-by-Service Breakdown</h1>
    <p>To provide total clarity on what is actually written in the codebase versus what was conceptualized, this section outlines the current state across all modules:</p>
    
    <pre><code>+-------------------------------------------------------------------------+
|                  VENDORA IMPLEMENTATION HEALTH MATRIX                   |
+-------------------------------------------------------------------------+
| Module / Capability     | Status       | Production Gap                 |
+-------------------------+--------------+--------------------------------+
| Service Registry        | COMPLETED    | Needs multi-node peer replica  |
| API Gateway Security    | COMPLETED    | Rate limiting &amp; Circuit breaker|
| Auth Service            | COMPLETED    | Refresh token &amp; password reset |
| User Service            | COMPLETED    | Email verification &amp; ID upload |
| Marketplace Service     | COMPLETED    | S3 image upload &amp; pagination   |
| Scrap Service           | COMPLETED    | Dynamic pricing &amp; staff geo    |
| Payment Service         | COMPLETED    | Mock simulation -&gt; Razorpay    |
| Helpdesk Service        | NOT STARTED  | Entire microservice missing    |
| Frontend Client         | NOT STARTED  | UI Web / Mobile application    |
+-------------------------------------------------------------------------+</code></pre>

    <h2>3.1 vendora-eureka-server (:8761)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Artifact:</strong> <code>vendora-eureka-server-0.0.1-SNAPSHOT</code></li>
        <li><strong>Configuration:</strong> Configured as standalone Eureka Server (<code>register-with-eureka: false</code>, <code>fetch-registry: false</code>).</li>
        <li><strong>Health Actuator:</strong> Exposed at <code>/actuator/health</code>.</li>
        <li><strong>Testing:</strong> Automated test <code>VendoraEurekaServerApplicationTests.contextLoads()</code> passing.</li>
    </ul>

    <h2>3.2 vendora-api-gateway (:8080)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Reactive Spring Cloud Gateway (WebFlux), Spring Security, JJWT.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>Dynamic route discovery via Eureka (<code>lb://VENDORA-*</code>).</li>
                <li>Perimeter Global Filter: <code>JwtAuthenticationFilter</code> with public endpoint bypass.</li>
                <li>Header stripping and trusted claim injection (<code>X-User-Id</code>, <code>X-User-Email</code>, <code>X-User-Role</code>).</li>
                <li>Global CORS handling for frontend origins.</li>
                <li>Centralized OpenAPI / Swagger UI aggregator (<code>/swagger-ui.html</code> aggregating endpoints from all 5 downstream services).</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>JwtServiceTest</code> (2 unit tests) and <code>VendoraApiGatewayApplicationTests</code> passing.</li>
    </ul>

    <h2>3.3 vendora-auth-service (:8082)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Spring Web MVC, OpenFeign, JJWT 0.12.6, BCrypt.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>Login endpoint (<code>POST /api/auth/login</code>): BCrypt authentication via Feign, returns JWT access token (1-hour expiration).</li>
                <li>Token validation endpoint (<code>GET /api/auth/validate</code>): Returns verification status and extracted claims.</li>
                <li>Zero-database dependency architecture (stateless worker).</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>AuthServiceTest</code> (8 unit tests with Mockito) passing.</li>
    </ul>

    <h2>3.4 vendora-user-service (:8081)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Spring Web MVC, Spring Data JPA, Hibernate, MySQL Driver, BCrypt.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>User registration (<code>POST /api/registration/register</code>): Unique email and registration number checks, BCrypt hashing, default <code>ACTIVE</code> status.</li>
                <li>User profile endpoints (<code>GET /api/users/profile</code>, <code>PUT /api/users/profile</code>, <code>GET /api/users/{id}</code>).</li>
                <li>Account status administration (<code>PATCH /api/users/{id}/status</code>): Restricted to <code>ADMIN</code>.</li>
                <li>Representative lifecycle (<code>/api/users/representative/apply</code>, <code>/status</code>, <code>/all</code>, <code>/{id}/status</code>): Allows students to apply for campus representative roles, with admin approval/rejection.</li>
                <li>Protected internal authentication endpoint (<code>/internal/users/auth-details</code>) for Auth Service.</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>UserServiceTest</code> (9 unit tests) and <code>RepresentativeServiceTest</code> (8 unit tests) passing.</li>
    </ul>

    <h2>3.5 vendora-marketplace-service (:8083)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Spring Web MVC, Spring Data JPA, Hibernate, MySQL Driver.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>Listing CRUD (<code>POST /api/listings</code>, <code>GET /api/listings</code>, <code>GET /api/listings/{id}</code>, <code>PUT /api/listings/{id}</code>, <code>DELETE /api/listings/{id}</code>).</li>
                <li>Search &amp; Filtering (<code>/api/listings/search?keyword=...</code>, <code>/api/listings/category/{category}</code>, <code>/api/listings/seller/{sellerId}</code>).</li>
                <li>Listing lifecycle management (<code>AVAILABLE</code>, <code>RESERVED</code>, <code>SOLD</code>).</li>
                <li>Buyer-Seller offer workflow (<code>POST /api/listings/{id}/requests</code>, <code>/api/listings/requests/my</code>, seller review, accept, reject, cancel).</li>
                <li>In-App peer-to-peer chat system (<code>/api/listings/conversations/start</code>, <code>/my</code>, <code>/{id}/messages</code>).</li>
                <li>Strict seller ownership checks on update, status change, and deletion.</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>ListingServiceTest</code> (5 unit tests), <code>MarketplaceRequestServiceTest</code> (4 unit tests), <code>ChatServiceTest</code> (4 unit tests) passing.</li>
    </ul>

    <h2>3.6 vendora-scrap-service (:8085)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Spring Web MVC, Spring Data JPA, Hibernate, MySQL Driver.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>Physical scrap collection request submission (<code>POST /api/scrap</code>): Accepts material type (<code>PAPER</code>, <code>PLASTIC</code>, <code>METAL</code>, <code>E_WASTE</code>, <code>GLASS</code>, <code>OTHER</code>), quantity, unit, pickup location, and preferred time.</li>
                <li>Scrap lifecycle management: Admin staff assignment (<code>PATCH /api/scrap/{id}/assign</code>), collector status updates (<code>REQUESTED</code>, <code>ASSIGNED</code>, <code>IN_PROGRESS</code>, <code>WEIGHED</code>, <code>COMPLETED</code>, <code>CANCELLED</code>).</li>
                <li>Campus Free Giveaways (<code>/api/giveaways</code>): Students list zero-cost items (<code>OPEN</code>, <code>CLAIMED</code>, <code>CANCELLED</code>).</li>
                <li>Giveaway Interest Workflow: Prospective recipients apply with a statement/reason; owners review and accept winners (<code>PATCH .../accept</code>).</li>
                <li>Public discussion messages on giveaways (<code>POST /api/giveaways/{id}/messages</code>).</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>ScrapServiceTest</code> (6 unit tests) and <code>GiveawayServiceTest</code> (5 unit tests) passing.</li>
    </ul>

    <h2>3.7 vendora-payment-service (:8086)</h2>
    <p><span class="badge badge-completed">COMPLETED</span></p>
    <ul>
        <li><strong>Core Technology:</strong> Spring Web MVC, Spring Data JPA, Hibernate, MySQL Driver.</li>
        <li><strong>Implemented Features:</strong>
            <ul>
                <li>Idempotent payment recording (<code>POST /api/payments</code>): Supports <code>Idempotency-Key</code> header or request body; prevents double charges on retry by returning existing records.</li>
                <li>Automatic transaction reference generation (<code>TXN-YYYYMMDD-XXXXXX</code>).</li>
                <li>Query transactions by ID, reference, buyer, seller, or admin overview.</li>
                <li>Transaction lifecycle simulation (<code>PATCH /api/payments/{id}/status?status=REFUNDED</code>).</li>
            </ul>
        </li>
        <li><strong>Testing:</strong> <code>PaymentServiceTest</code> (5 unit tests) and <code>VendoraPaymentServiceApplicationTests</code> passing.</li>
    </ul>

    <h2>3.8 vendora-helpdesk-service (:8084)</h2>
    <p><span class="badge badge-notstarted">NOT STARTED</span></p>
    <ul>
        <li><strong>Details:</strong> Conceptualized for campus facility tickets (hostel electrical, plumbing, lab equipment repair). Not yet created in codebase to allow full focus on commerce and circular sustainability MVP modules.</li>
    </ul>
</div>

<!-- SECTION 4: DATABASE DESIGN & ERD -->
<div class="page-break">
    <h1>4. Database Design &amp; Entity Relationship Architecture</h1>
    
    <h2>4.1 Database Architecture: The Consolidated Schema Strategy</h2>
    <p>Vendora utilizes a <strong>Single Consolidated MySQL Database Schema</strong> named <code>vendora_db</code> running on port <code>3306</code>.</p>
    
    <h3>Architectural Rationale &amp; Trade-off Analysis</h3>
    <p>In pure microservices theory, each service owns an isolated physical database (Database-per-Service pattern). For the Vendora MVP, a <strong>pragmatic shared-schema with strict logical boundaries</strong> was implemented:</p>
    <ul>
        <li><strong>Operational Simplicity:</strong> Eliminates the overhead of configuring, hosting, and migrating 5 separate database servers in a campus development or evaluator environment.</li>
        <li><strong>Logical Data Independence:</strong> Although tables coexist in <code>vendora_db</code>, <strong>no microservice shares JPA entity models or repository classes</strong>. Cross-service references are stored purely as scalar IDs (<code>sellerId</code>, <code>buyerId</code>, <code>ownerId</code>, <code>userId</code>, <code>listingId</code>).</li>
        <li><strong>Zero Cross-Service Table Joins:</strong> Services access data strictly through REST APIs and Gateway tokens, never via SQL joins across service domains.</li>
    </ul>

    <h2>4.2 Entity Relationship Diagram (ERD)</h2>
    <div class="mermaid-container">
        <div class="mermaid">
erDiagram
    USERS {
        bigint id PK
        string full_name
        string email
        string password_hash
        string phone
        string registration_number
        string role
        string account_status
        datetime created_at
        datetime updated_at
    }
    APP_REPRESENTATIVES {
        bigint id PK
        bigint user_id
        string approval_status
        bigint approved_by
        datetime approved_at
        string reason
        datetime created_at
    }
    LISTINGS {
        bigint id PK
        bigint seller_id
        string title
        string description
        decimal price
        int quantity
        string category
        string condition
        string status
        string image_url
        datetime created_at
        datetime updated_at
    }
    MARKETPLACE_REQUESTS {
        bigint id PK
        bigint listing_id
        bigint buyer_id
        int requested_quantity
        string message
        string status
        datetime created_at
        datetime updated_at
    }
    CONVERSATIONS {
        bigint id PK
        bigint listing_id
        bigint buyer_id
        bigint seller_id
        datetime created_at
    }
    MESSAGES {
        bigint id PK
        bigint conversation_id
        bigint sender_id
        string message
        datetime sent_at
    }
    SCRAP_REQUESTS {
        bigint id PK
        bigint user_id
        string material_type
        string description
        decimal estimated_quantity
        string quantity_unit
        string pickup_location
        datetime preferred_pickup_time
        bigint assigned_staff_id
        string status
        datetime created_at
        datetime updated_at
    }
    GIVEAWAYS {
        bigint id PK
        bigint owner_id
        string title
        string description
        string category
        string status
        datetime created_at
        datetime updated_at
    }
    GIVEAWAY_INTERESTS {
        bigint id PK
        bigint giveaway_id
        bigint user_id
        string message
        string status
        datetime created_at
    }
    GIVEAWAY_MESSAGES {
        bigint id PK
        bigint giveaway_id
        bigint sender_id
        string message
        datetime created_at
    }
    PAYMENTS {
        bigint id PK
        string transaction_reference
        string idempotency_key
        string reference_type
        bigint reference_id
        bigint buyer_id
        bigint seller_id
        bigint listing_id
        decimal amount
        string currency
        string payment_method
        string payment_status
        string description
        datetime created_at
        datetime updated_at
    }

    USERS ||--o{ APP_REPRESENTATIVES : applies_for
    USERS ||--o{ LISTINGS : publishes
    USERS ||--o{ MARKETPLACE_REQUESTS : submits
    USERS ||--o{ CONVERSATIONS : participates
    USERS ||--o{ SCRAP_REQUESTS : schedules
    USERS ||--o{ GIVEAWAYS : donates
    USERS ||--o{ PAYMENTS : executes

    LISTINGS ||--o{ MARKETPLACE_REQUESTS : receives
    LISTINGS ||--o{ CONVERSATIONS : context_for
    CONVERSATIONS ||--o{ MESSAGES : contains

    GIVEAWAYS ||--o{ GIVEAWAY_INTERESTS : receives
    GIVEAWAYS ||--o{ GIVEAWAY_MESSAGES : discussion_on
        </div>
    </div>

    <h2>4.3 Data Dictionary &amp; Constraints</h2>
    
    <h3>Table: <code>users</code> (Owner: <code>vendora-user-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>full_name</code> (VARCHAR(150), NOT NULL)</li>
        <li><code>email</code> (VARCHAR(150), UNIQUE, NOT NULL)</li>
        <li><code>phone</code> (VARCHAR(20), NULL)</li>
        <li><code>registration_number</code> (VARCHAR(50), UNIQUE, NULL)</li>
        <li><code>password_hash</code> (VARCHAR(255), NOT NULL) &mdash; Stored using Spring Security BCrypt</li>
        <li><code>role</code> (VARCHAR(40), NOT NULL) &mdash; Enum: <code>STUDENT</code>, <code>FACULTY</code>, <code>ADMIN</code>, <code>MAINTENANCE_STAFF</code>, <code>COLLECTION_STAFF</code>, <code>APP_REPRESENTATIVE</code></li>
        <li><code>account_status</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>ACTIVE</code>, <code>SUSPENDED</code></li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>app_representatives</code> (Owner: <code>vendora-user-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>user_id</code> (BIGINT, UNIQUE, NOT NULL) &mdash; Scalar reference to <code>users.id</code></li>
        <li><code>approval_status</code> (VARCHAR(30), NOT NULL) &mdash; <code>PENDING</code>, <code>APPROVED</code>, <code>REJECTED</code></li>
        <li><code>approved_by</code> (BIGINT, NULL) &mdash; Admin user ID</li>
        <li><code>approved_at</code> (DATETIME, NULL)</li>
        <li><code>reason</code> (VARCHAR(500), NULL) &mdash; Applicant's statement of intent</li>
        <li><code>created_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>listings</code> (Owner: <code>vendora-marketplace-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>seller_id</code> (BIGINT, NOT NULL) &mdash; Injected from verified <code>X-User-Id</code></li>
        <li><code>title</code> (VARCHAR(200), NOT NULL)</li>
        <li><code>description</code> (TEXT, NULL)</li>
        <li><code>price</code> (DECIMAL(12,2), NOT NULL, &ge; 0)</li>
        <li><code>quantity</code> (INT, NOT NULL, &ge; 1)</li>
        <li><code>category</code> (VARCHAR(100), NOT NULL) &mdash; Enum: <code>BOOKS</code>, <code>ELECTRONICS</code>, <code>STATIONERY</code>, <code>FURNITURE</code>, <code>CYCLES</code>, <code>OTHER</code></li>
        <li><code>condition</code> (VARCHAR(50), NOT NULL) &mdash; Enum: <code>NEW</code>, <code>LIKE_NEW</code>, <code>GOOD</code>, <code>FAIR</code></li>
        <li><code>status</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>AVAILABLE</code>, <code>RESERVED</code>, <code>SOLD</code></li>
        <li><code>image_url</code> (VARCHAR(500), NULL)</li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>marketplace_requests</code> (Owner: <code>vendora-marketplace-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>listing_id</code> (BIGINT, NOT NULL) &mdash; Reference to <code>listings.id</code></li>
        <li><code>buyer_id</code> (BIGINT, NOT NULL) &mdash; Scalar reference to buyer <code>users.id</code></li>
        <li><code>requested_quantity</code> (INT, NOT NULL, &ge; 1)</li>
        <li><code>message</code> (TEXT, NULL)</li>
        <li><code>status</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>PENDING</code>, <code>ACCEPTED</code>, <code>REJECTED</code>, <code>CANCELLED</code></li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>conversations</code> (Owner: <code>vendora-marketplace-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>listing_id</code> (BIGINT, NOT NULL)</li>
        <li><code>buyer_id</code> (BIGINT, NOT NULL)</li>
        <li><code>seller_id</code> (BIGINT, NOT NULL)</li>
        <li><code>created_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>messages</code> (Owner: <code>vendora-marketplace-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>conversation_id</code> (BIGINT, NOT NULL) &mdash; Reference to <code>conversations.id</code></li>
        <li><code>sender_id</code> (BIGINT, NOT NULL) &mdash; Validated against conversation participants</li>
        <li><code>message</code> (TEXT, NOT NULL)</li>
        <li><code>sent_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>scrap_requests</code> (Owner: <code>vendora-scrap-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>user_id</code> (BIGINT, NOT NULL) &mdash; Injected from <code>X-User-Id</code></li>
        <li><code>material_type</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>PAPER</code>, <code>PLASTIC</code>, <code>METAL</code>, <code>E_WASTE</code>, <code>GLASS</code>, <code>OTHER</code></li>
        <li><code>description</code> (VARCHAR(1000), NULL)</li>
        <li><code>estimated_quantity</code> (DECIMAL(10,2), NOT NULL, &gt; 0)</li>
        <li><code>quantity_unit</code> (VARCHAR(20), NOT NULL) &mdash; Enum: <code>KG</code>, <code>PIECES</code>, <code>BOXES</code></li>
        <li><code>pickup_location</code> (VARCHAR(500), NOT NULL)</li>
        <li><code>preferred_pickup_time</code> (DATETIME, NULL)</li>
        <li><code>assigned_staff_id</code> (BIGINT, NULL) &mdash; Admin assigned collection staff</li>
        <li><code>status</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>REQUESTED</code>, <code>ASSIGNED</code>, <code>IN_PROGRESS</code>, <code>WEIGHED</code>, <code>COMPLETED</code>, <code>CANCELLED</code></li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>giveaways</code> (Owner: <code>vendora-scrap-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>owner_id</code> (BIGINT, NOT NULL)</li>
        <li><code>title</code> (VARCHAR(200), NOT NULL)</li>
        <li><code>description</code> (TEXT, NULL)</li>
        <li><code>category</code> (VARCHAR(100), NULL)</li>
        <li><code>status</code> (VARCHAR(30), NOT NULL) &mdash; <code>AVAILABLE</code>, <code>RESERVED</code>, <code>CLAIMED</code>, <code>CANCELLED</code></li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>giveaway_interests</code> (Owner: <code>vendora-scrap-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>giveaway_id</code> (BIGINT, NOT NULL)</li>
        <li><code>user_id</code> (BIGINT, NOT NULL)</li>
        <li><code>message</code> (TEXT, NULL)</li>
        <li><code>status</code> (VARCHAR(30), NOT NULL) &mdash; <code>PENDING</code>, <code>ACCEPTED</code>, <code>REJECTED</code></li>
        <li><code>created_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>giveaway_messages</code> (Owner: <code>vendora-scrap-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>giveaway_id</code> (BIGINT, NOT NULL)</li>
        <li><code>sender_id</code> (BIGINT, NOT NULL)</li>
        <li><code>message</code> (TEXT, NOT NULL)</li>
        <li><code>created_at</code> (DATETIME)</li>
    </ul>

    <h3>Table: <code>payments</code> (Owner: <code>vendora-payment-service</code>)</h3>
    <ul>
        <li><code>id</code> (BIGINT, PK, AUTO_INCREMENT)</li>
        <li><code>transaction_reference</code> (VARCHAR(100), UNIQUE, NOT NULL)</li>
        <li><code>idempotency_key</code> (VARCHAR(100), UNIQUE, NULL)</li>
        <li><code>reference_type</code> (VARCHAR(50), NULL) &mdash; <code>LISTING</code>, <code>SCRAP</code>, <code>GENERAL</code></li>
        <li><code>reference_id</code> (BIGINT, NULL)</li>
        <li><code>buyer_id</code> (BIGINT, NOT NULL)</li>
        <li><code>seller_id</code> (BIGINT, NOT NULL)</li>
        <li><code>listing_id</code> (BIGINT, NULL)</li>
        <li><code>amount</code> (DECIMAL(12,2), NOT NULL, &gt; 0)</li>
        <li><code>currency</code> (VARCHAR(10), NOT NULL, Default <code>'INR'</code>)</li>
        <li><code>payment_method</code> (VARCHAR(50), NOT NULL) &mdash; Enum: <code>UPI</code>, <code>CARD</code>, <code>NET_BANKING</code>, <code>WALLET</code>, <code>CASH</code></li>
        <li><code>payment_status</code> (VARCHAR(30), NOT NULL) &mdash; Enum: <code>PENDING</code>, <code>SUCCESS</code>, <code>FAILED</code>, <code>REFUNDED</code></li>
        <li><code>description</code> (VARCHAR(500), NULL)</li>
        <li><code>created_at</code>, <code>updated_at</code> (DATETIME)</li>
    </ul>
</div>
"@)
$sw.Flush()
$sw.Close()
Write-Host "Appended Part 2 (Sec 3, Sec 4)."
