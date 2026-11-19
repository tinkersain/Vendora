$htmlPath = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$sw = [System.IO.StreamWriter]::new($htmlPath, $true, [System.Text.Encoding]::UTF8)

$sw.WriteLine(@"
<!-- SECTION 7: REMAINING FEATURES -->
<div class="page-break">
    <h1>7. Remaining Features to Achieve 100% Project Completion</h1>
    <p>To transition Vendora from its current backend MVP into a complete, end-to-end commercial-grade campus platform, the following planned modules must be developed:</p>

    <h2>7.1 Second-Hand Book &amp; Academic Courseware Specialization</h2>
    <p>Currently, books are treated as generic marketplace listings (<code>category: BOOKS</code>). To make Vendora truly campus-specialized:</p>
    <ol>
        <li><strong>Academic Taxonomy Schema:</strong> Add fields: <code>isbn</code>, <code>author</code>, <code>edition</code>, <code>academicDepartment</code> (e.g., Computer Science, Mechanical), <code>semester</code> (Sem 1 to 8), and <code>courseCode</code> (e.g., CS-301).</li>
        <li><strong>OpenLibrary / Google Books API Integration:</strong> When a student enters a 10- or 13-digit ISBN during listing creation, auto-populate the official book title, author, cover thumbnail, and publisher metadata.</li>
        <li><strong>Condition Grading &amp; Defect Check:</strong> Specific flags: Highlighting extent (None, Light, Heavy), missing pages check, binding condition (Intact, Loose).</li>
    </ol>

    <h2>7.2 eBook Purchases &amp; Digital Course Notes</h2>
    <ol>
        <li><strong>Digital Asset Management:</strong> Allow students and faculty to upload academic notes, lab manuals, and open-source study guides in PDF/EPUB formats.</li>
        <li><strong>Security &amp; Dynamic Watermarking:</strong> Embed dynamic watermark overlays on downloaded PDF pages (containing the buyer's Student ID and email) to prevent unauthorized file sharing outside the campus.</li>
        <li><strong>Time-Limited Signed Download URLs:</strong> Deliver digital files via presigned S3 URLs valid for 15 minutes after purchase verification.</li>
    </ol>

    <h2>7.3 Scrap Management: Automated Valuation &amp; Payout</h2>
    <ol>
        <li><strong>Scrap Rate Engine:</strong> Admin-managed price list per campus:
            <ul>
                <li>Old Examination Registers / Notebooks: ₹14 / kg</li>
                <li>Cardboard / Packaging Boxes: ₹9 / kg</li>
                <li>Iron / Broken Lab Fixtures: ₹28 / kg</li>
                <li>Electronic Scrap / PC Components: ₹50 / kg</li>
            </ul>
        </li>
        <li><strong>Collector Mobile Weighing Workflow:</strong> Collection staff inputs verified weight on site. App computes total payout: <em>Amount = Actual Weight &times; Rate</em>. System calls <code>vendora-payment-service</code> to trigger an instant UPI payout or credit campus cafeteria points.</li>
    </ol>

    <h2>7.4 Real Payment Gateway &amp; Escrow Safeguard</h2>
    <ol>
        <li><strong>Razorpay / UPI Gateway Integration:</strong> Create order via Razorpay API, capture client payment signatures, and verify webhook callbacks.</li>
        <li><strong>Escrow Safeguard for High-Value Goods (Cycles / Laptops):</strong> Buyer pays &rarr; Funds locked in Escrow &rarr; Buyer meets Seller on campus &rarr; Buyer verifies item and shares 4-digit Delivery OTP &rarr; Seller submits OTP &rarr; Escrow funds released to Seller.</li>
    </ol>

    <h2>7.5 Real-Time Notifications &amp; In-App Alerts</h2>
    <ol>
        <li><strong>WebSocket / STOMP Broker:</strong> Replace HTTP polling for in-app chat with real-time bidirectional WebSocket messaging.</li>
        <li><strong>Push &amp; Email Notifications:</strong> Send notifications when a purchase offer is submitted, accepted, or when a scrap collector arrives at the hostel.</li>
    </ol>

    <h2>7.6 Client Frontend Application</h2>
    <p>Develop responsive Web and Mobile interfaces (React / Next.js + Tailwind CSS for Web; Flutter for iOS/Android).</p>
</div>

<!-- SECTION 8: TESTING STATUS & QUALITY VERIFICATION -->
<div class="page-break">
    <h1>8. Testing Status &amp; Quality Verification</h1>
    
    <h2>8.1 Implemented Test Suites (All Verified Passing)</h2>
    <p>Vendora contains <strong>59 automated unit tests</strong> across its microservices, built with <strong>JUnit 5</strong>, <strong>Mockito</strong>, and <strong>Spring Boot Test</strong>. All 59 tests execute with zero failures, zero errors, and zero skipped tests.</p>
    
    <table>
        <thead>
            <tr>
                <th style="width: 25%;">Microservice</th>
                <th style="width: 45%;">Test Class</th>
                <th style="width: 15%; text-align: center;">Tests Run</th>
                <th style="width: 15%; text-align: center;">Passed</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>API Gateway</td>
                <td><code>JwtServiceTest</code></td>
                <td style="text-align: center;">2</td>
                <td style="text-align: center;"><strong>2</strong></td>
            </tr>
            <tr>
                <td>API Gateway</td>
                <td><code>VendoraApiGatewayApplicationTests</code></td>
                <td style="text-align: center;">1</td>
                <td style="text-align: center;"><strong>1</strong></td>
            </tr>
            <tr>
                <td>Auth Service</td>
                <td><code>AuthServiceTest</code></td>
                <td style="text-align: center;">8</td>
                <td style="text-align: center;"><strong>8</strong></td>
            </tr>
            <tr>
                <td>Eureka Server</td>
                <td><code>VendoraEurekaServerApplicationTests</code></td>
                <td style="text-align: center;">1</td>
                <td style="text-align: center;"><strong>1</strong></td>
            </tr>
            <tr>
                <td>Marketplace Service</td>
                <td><code>ChatServiceTest</code></td>
                <td style="text-align: center;">4</td>
                <td style="text-align: center;"><strong>4</strong></td>
            </tr>
            <tr>
                <td>Marketplace Service</td>
                <td><code>ListingServiceTest</code></td>
                <td style="text-align: center;">5</td>
                <td style="text-align: center;"><strong>5</strong></td>
            </tr>
            <tr>
                <td>Marketplace Service</td>
                <td><code>MarketplaceRequestServiceTest</code></td>
                <td style="text-align: center;">4</td>
                <td style="text-align: center;"><strong>4</strong></td>
            </tr>
            <tr>
                <td>Scrap Service</td>
                <td><code>ScrapServiceTest</code></td>
                <td style="text-align: center;">6</td>
                <td style="text-align: center;"><strong>6</strong></td>
            </tr>
            <tr>
                <td>Scrap Service</td>
                <td><code>GiveawayServiceTest</code></td>
                <td style="text-align: center;">5</td>
                <td style="text-align: center;"><strong>5</strong></td>
            </tr>
            <tr>
                <td>Payment Service</td>
                <td><code>PaymentServiceTest</code></td>
                <td style="text-align: center;">5</td>
                <td style="text-align: center;"><strong>5</strong></td>
            </tr>
            <tr>
                <td>Payment Service</td>
                <td><code>VendoraPaymentServiceApplicationTests</code></td>
                <td style="text-align: center;">1</td>
                <td style="text-align: center;"><strong>1</strong></td>
            </tr>
            <tr>
                <td>User Service</td>
                <td><code>UserServiceTest</code></td>
                <td style="text-align: center;">9</td>
                <td style="text-align: center;"><strong>9</strong></td>
            </tr>
            <tr>
                <td>User Service</td>
                <td><code>RepresentativeServiceTest</code></td>
                <td style="text-align: center;">8</td>
                <td style="text-align: center;"><strong>8</strong></td>
            </tr>
            <tr style="background-color: #f1f5f9; font-weight: bold;">
                <td colspan="2">TOTAL AUTOMATED TESTS</td>
                <td style="text-align: center;">59</td>
                <td style="text-align: center; color: #16a34a;">59 (100% Pass)</td>
            </tr>
        </tbody>
    </table>

    <h3>Detailed Test Coverage Scenarios</h3>
    <ol>
        <li><strong><code>UserServiceTest</code> (9 Tests):</strong> User registration with valid attributes; duplicate email rejection (<code>409 Conflict</code>); duplicate registration number rejection (<code>409 Conflict</code>); password encoding verification; get user by ID; user not found (<code>404 Not Found</code>); update profile success; update profile invalid ID; admin status update (<code>ACTIVE</code> &rarr; <code>SUSPENDED</code>).</li>
        <li><strong><code>RepresentativeServiceTest</code> (8 Tests):</strong> Representative application submission; duplicate application rejection; fetch application status by user ID; user without application handling; admin review approval (<code>APPROVED</code>); admin review rejection (<code>REJECTED</code>); review non-existent application; get all applications.</li>
        <li><strong><code>AuthServiceTest</code> (8 Tests):</strong> Successful login with correct credentials; incorrect password rejection (<code>401 Unauthorized</code>); unknown user rejection; suspended user login rejection; valid token validation; expired token rejection; corrupted signature rejection; missing Bearer prefix rejection.</li>
        <li><strong><code>ListingServiceTest</code> (5 Tests):</strong> Create listing; get all available listings; search by keyword; update listing by seller; unauthorized update rejection by non-seller (<code>403 Forbidden</code>).</li>
        <li><strong><code>MarketplaceRequestServiceTest</code> (4 Tests):</strong> Create purchase offer; buyer cancel offer; seller accept offer; seller reject offer.</li>
        <li><strong><code>ChatServiceTest</code> (4 Tests):</strong> Start conversation; get user conversations; send chat message; get message history.</li>
        <li><strong><code>ScrapServiceTest</code> (6 Tests):</strong> Create scrap pickup request; get all requests; get requests by user; admin assign collection staff; staff update status (<code>COMPLETED</code>); non-staff status update rejection (<code>403 Forbidden</code>).</li>
        <li><strong><code>GiveawayServiceTest</code> (5 Tests):</strong> Publish giveaway item; browse available; express interest; owner accept winner; post public discussion message.</li>
        <li><strong><code>PaymentServiceTest</code> (5 Tests):</strong> Create simulated payment; idempotency replay validation (identical response, no duplicate DB insertion); get payment by transaction reference; get payments by buyer; status update (<code>REFUNDED</code>).</li>
        <li><strong><code>JwtServiceTest</code> (2 Tests):</strong> Cryptographic signature generation and claims extraction; expired token rejection.</li>
    </ol>

    <h2>8.2 Pending Testing Requirements</h2>
    <ul>
        <li><strong>Controller-Layer Unit Tests (<code>@WebMvcTest</code>):</strong> Current unit tests focus on service-layer logic with Mockito. MockMvc controller tests should be added to explicitly verify HTTP serialization, <code>@Valid</code> bean validations, and Spring Security filters.</li>
        <li><strong>Database Integration Tests (<code>@DataJpaTest</code> + Testcontainers):</strong> Execute integration tests against a live containerized MySQL instance to verify Hibernate SQL dialect and foreign key cascading.</li>
        <li><strong>End-to-End API Automated Pipeline (Newman / Postman):</strong> An automated CLI pipeline executing sequential multi-service scenarios (Register &rarr; Login &rarr; Create Listing &rarr; Submit Request &rarr; Chat &rarr; Pay).</li>
    </ul>
</div>

<!-- SECTION 9: PROJECT COMPLETION ROADMAP -->
<div class="page-break">
    <h1>9. Project Completion Roadmap</h1>
    <p>To achieve 100% completion of the Vendora platform, the remaining work is divided into a structured 6-phase implementation roadmap:</p>

    <div class="mermaid-container">
        <div class="mermaid">
gantt
    title Vendora Implementation Roadmap
    dateFormat YYYY-MM-DD
    section Phase 1 Hardening
    File Upload Engine        :a1, 2026-11-17, 4d
    Gateway Rate Limiting     :a2, after a1, 3d
    RefreshToken Blacklist    :a3, after a2, 3d
    section Phase 2 Books
    ISBN Google Books API     :b1, after a3, 4d
    Academic Taxonomy         :b2, after b1, 3d
    eBook PDF Watermarking    :b3, after b2, 4d
    section Phase 3 Scrap Pay
    Scrap Pricing Engine      :c1, after b3, 3d
    Automated Scrap Payout    :c2, after c1, 3d
    Razorpay PG Integration   :c3, after c2, 5d
    section Phase 4 Helpdesk
    Helpdesk Microservice     :d1, after c3, 5d
    WebSocket Real-Time Chat  :d2, after d1, 4d
    section Phase 5 UI
    React Web App MVP         :e1, after d2, 10d
    Mobile Optimization       :e2, after e1, 4d
    section Phase 6 Final
    Docker Compose Multi-Cont :f1, after e2, 3d
    End-to-End Verification   :f2, after f1, 4d
        </div>
    </div>

    <h2>Phase Breakdown &amp; Work Estimates</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 25%;">Phase</th>
                <th style="width: 45%;">Milestone / Work Item</th>
                <th style="width: 15%;">Dependencies</th>
                <th style="width: 15%; text-align: center;">Estimated Effort</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>Phase 1: Production Hardening &amp; Media</strong></td>
                <td>Multipart file upload engine (AWS S3 / MinIO), Gateway Redis rate limiting, Resilience4j circuit breakers, JWT refresh token rotation, and token revocation blacklist.</td>
                <td>Existing Gateway &amp; Auth</td>
                <td style="text-align: center;">10 Working Days</td>
            </tr>
            <tr>
                <td><strong>Phase 2: Academic Books &amp; eBook Module</strong></td>
                <td>Specialized book attributes (<code>isbn</code>, <code>branch</code>, <code>semester</code>), Google Books API metadata auto-fill, PDF study notes upload, dynamic PDF watermarking with Student ID, and signed presigned URLs.</td>
                <td>S3 storage engine (Phase 1)</td>
                <td style="text-align: center;">11 Working Days</td>
            </tr>
            <tr>
                <td><strong>Phase 3: Scrap Payout &amp; Real Payment Gateway</strong></td>
                <td>Scrap material rate pricing table, automated collection payout trigger, Razorpay / Cashfree UPI integration replacing simulation, and marketplace escrow hold-and-release mechanism.</td>
                <td>Payment &amp; Scrap services</td>
                <td style="text-align: center;">11 Working Days</td>
            </tr>
            <tr>
                <td><strong>Phase 4: Facilities Helpdesk &amp; Real-Time Comms</strong></td>
                <td>Creation of <code>vendora-helpdesk-service</code> (:8084), maintenance ticket lifecycle (<code>OPEN</code> &rarr; <code>ASSIGNED</code> &rarr; <code>RESOLVED</code>), and WebSocket / STOMP real-time messaging for P2P chat.</td>
                <td>Eureka &amp; Gateway</td>
                <td style="text-align: center;">9 Working Days</td>
            </tr>
            <tr>
                <td><strong>Phase 5: User Interface (Frontend MVP)</strong></td>
                <td>Modern responsive web frontend (React / Next.js, Tailwind CSS), state management (Zustand / Redux Toolkit), API integration via Axios/Fetch, and JWT session handling.</td>
                <td>Implemented REST APIs</td>
                <td style="text-align: center;">14 Working Days</td>
            </tr>
            <tr>
                <td><strong>Phase 6: Containerization &amp; Final Hardening</strong></td>
                <td><code>docker-compose.yml</code> for unified multi-container local bootstrapping (all 7 microservices + MySQL + Redis), Newman automated integration test runs, and evaluator demonstration script.</td>
                <td>All services &amp; UI</td>
                <td style="text-align: center;">7 Working Days</td>
            </tr>
        </tbody>
    </table>
</div>

<!-- SECTION 10: FINAL EVALUATION PREPARATION -->
<div class="page-break">
    <h1>10. Final Evaluation Preparation: Technical Justifications &amp; Viva Questions</h1>
    <p>This section provides technical defenses and comprehensive answers to questions that academic evaluators, technical mentors, or system architecture examiners are likely to ask regarding Vendora.</p>

    <h2>10.1 Key Architectural Decisions &amp; Defenses</h2>

    <div class="callout">
        <span class="callout-title">Q1: "Why did you build Vendora using a Microservices Architecture instead of a Monolithic Spring Boot application?"</span>
        <p><strong>Examiner's Concern:</strong> Microservices introduce network overhead, eventual consistency issues, and operational complexity. Was it overkill for a campus platform?</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Independent Lifecycle &amp; Domain Isolation:</strong> In an academic institution, different services experience vastly different traffic profiles. The Marketplace and Chat modules experience intense spikes during the start and end of semesters (book exchange periods), while the User Profile and Scrap modules maintain low, steady traffic. Microservices allow the Marketplace service to scale horizontally without duplicating the entire monolithic heap.</li>
            <li><strong>Failure Isolation:</strong> If the real-time chat or scrap scheduling service encounters an unhandled exception or memory leak, the core authentication and campus security perimeter (Gateway/Auth/User) remain completely unaffected.</li>
            <li><strong>Polyglot &amp; Integration Readiness:</strong> External integrations (e.g., IoT smart weighing scales for scrap collection or third-party payment gateways) can be modified, tested, and redeployed within their respective microservices without rebuilding or risking the integrity of user identity and academic credentials.</li>
        </ol>
    </div>

    <div class="callout">
        <span class="callout-title">Q2: "You are using a single shared MySQL database (vendora_db). Isn't a shared database considered an anti-pattern in Microservices?"</span>
        <p><strong>Examiner's Concern:</strong> Microservices should follow the Database-per-Service pattern. Does sharing <code>vendora_db</code> violate service autonomy?</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Pragmatic Shared Schema for MVP:</strong> In an academic evaluation and early staging environment, provisioning, migrating, and monitoring 5 distinct physical database instances causes unnecessary resource consumption on local development machines.</li>
            <li><strong>Strict Logical Isolation (Zero Database-Level Coupling):</strong> We enforce the core philosophy of Database-per-Service at the application layer:
                <ul>
                    <li><strong>No Cross-Service Repositories:</strong> No service injects a repository belonging to another service (e.g., <code>vendora-marketplace-service</code> contains zero references to <code>UserRepository</code>).</li>
                    <li><strong>No SQL Joins Across Service Boundaries:</strong> All cross-domain relationships are stored as scalar numeric identifiers (<code>seller_id</code>, <code>buyer_id</code>, <code>listing_id</code>). If the marketplace needs user data, it reads trusted claims passed in HTTP headers or calls the user service via REST.</li>
                    <li><strong>Seamless Migration Path:</strong> Because there are no physical foreign key constraints or SQL joins between service tables, decomposing <code>vendora_db</code> into 5 separate physical databases (<code>vendora_user_db</code>, <code>vendora_market_db</code>, etc.) requires only changing connection strings in <code>application.properties</code> without rewriting any business logic or SQL queries.</li>
                </ul>
            </li>
        </ol>
    </div>

    <div class="callout">
        <span class="callout-title">Q3: "How do you prevent a malicious user from spoofing identity by manually passing X-User-Id: 1 (Admin) in Postman?"</span>
        <p><strong>Examiner's Concern:</strong> Downstream microservices trust the <code>X-User-Id</code> and <code>X-User-Role</code> headers. Can an attacker simply inject these headers directly?</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Perimeter Sanitization at the Gateway:</strong> The <code>JwtAuthenticationFilter</code> in <code>vendora-api-gateway</code> operates before any request reaches internal routing. It <strong>explicitly removes</strong> any client-supplied <code>X-User-Id</code>, <code>X-User-Email</code>, and <code>X-User-Role</code> headers before inspecting the JWT token:
<pre><code>headers.remove("X-User-Id");
headers.remove("X-User-Email");
headers.remove("X-User-Role");</code></pre>
            </li>
            <li><strong>Cryptographic Claim Extraction:</strong> Only after verifying the cryptographic HMAC-SHA256 signature and expiration timestamp does the gateway extract the claims from the tamper-proof JWT payload and inject them into the mutated request stream:
<pre><code>headers.set("X-User-Id", String.valueOf(userId));
headers.set("X-User-Email", email);
headers.set("X-User-Role", role);</code></pre>
            </li>
            <li><strong>Internal Network Isolation:</strong> In production, downstream microservices run inside a private virtual network (VPC/Docker network) that is inaccessible from the public internet. The only path into the network is through the Gateway, guaranteeing that no request can reach a downstream service without having its headers scrubbed and verified.</li>
        </ol>
    </div>

    <div class="callout">
        <span class="callout-title">Q4: "Why does vendora-auth-service have no database connection? How does it verify credentials?"</span>
        <p><strong>Examiner's Concern:</strong> Most authentication microservices connect directly to a user database. Why is Auth stateless here?</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Single Source of Truth for Data Ownership:</strong> <code>vendora-user-service</code> is the exclusive owner of the <code>users</code> table and user credential data. Allowing <code>vendora-auth-service</code> to connect directly to the database would create two separate applications modifying or reading the same table, leading to schema coupling.</li>
            <li><strong>Feign Client with Internal Security Secret:</strong> <code>vendora-auth-service</code> communicates with <code>vendora-user-service</code> via a Spring Cloud OpenFeign client over a dedicated internal endpoint (<code>/internal/users/auth-details</code>). This endpoint is secured using a shared internal secret (<code>X-Service-Secret</code>), ensuring that only internal platform components can request credential hashes.</li>
            <li><strong>Stateless Scalability:</strong> Because <code>vendora-auth-service</code> holds no database connections, it is extremely lightweight, boots in seconds, and can be scaled horizontally to handle high volumes of concurrent login and token validation requests without exhausting database connection pools.</li>
        </ol>
    </div>

    <div class="callout">
        <span class="callout-title">Q5: "How does your Payment Service prevent duplicate charges if a student clicks 'Pay' multiple times or encounters a network glitch?"</span>
        <p><strong>Examiner's Concern:</strong> Network retries can cause duplicate payment records in distributed systems.</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Idempotency Key Mechanism:</strong> The client generates a unique UUID <code>Idempotency-Key</code> (transmitted via HTTP header or payload) for each distinct checkout intent.</li>
            <li><strong>Pre-Persistence Deduplication:</strong> Before creating a new transaction, <code>PaymentService</code> queries the repository by <code>idempotencyKey</code>:
                <ul>
                    <li>If a record with that key already exists, the service <strong>skips new transaction creation</strong> and immediately returns the previously recorded payment details with an HTTP <code>200 OK</code>.</li>
                    <li>If the key is new, it persists the transaction with a unique transaction reference and returns HTTP <code>201 Created</code>.</li>
                </ul>
            </li>
            <li><strong>Unique Database Constraint:</strong> The <code>idempotency_key</code> column has a database-level <code>UNIQUE</code> constraint, providing an absolute guarantee against race conditions even under concurrent duplicate HTTP requests.</li>
        </ol>
    </div>

    <div class="callout">
        <span class="callout-title">Q6: "What happens if the Eureka Server crashes? Does the entire platform stop functioning?"</span>
        <p><strong>Examiner's Concern:</strong> Is the discovery server a single point of failure (SPOF)?</p>
        <p><strong>Technical Defense:</strong></p>
        <ol>
            <li><strong>Client-Side Cache Resilience:</strong> Spring Cloud Netflix Eureka clients (including the API Gateway) cache the service registry locally in memory. If Eureka crashes, the API Gateway continues routing traffic to known instances using its local registry cache until instances change.</li>
            <li><strong>Heartbeat &amp; Self-Preservation:</strong> Eureka operates in "Self-Preservation Mode." If network jitter occurs, it does not prematurely deregister instances.</li>
            <li><strong>High Availability Production Setup:</strong> In production, Eureka is deployed in a peer-aware, multi-node clustered configuration across availability zones, ensuring zero downtime if a single node fails.</li>
        </ol>
    </div>

    <h2>10.2 Evaluation Demonstration Guide: Step-by-Step Viva Demo</h2>
    <p>When presenting the live system to evaluators, execute the following sequence to showcase the complete architectural lifecycle:</p>
    <ol>
        <li><strong>Service Registration Verification:</strong> Open browser at <code>http://localhost:8761</code>. Show the Eureka dashboard displaying all active microservices: <code>VENDORA-API-GATEWAY</code>, <code>VENDORA-AUTH-SERVICE</code>, <code>VENDORA-USER-SERVICE</code>, <code>VENDORA-MARKETPLACE-SERVICE</code>, <code>VENDORA-SCRAP-SERVICE</code>, and <code>VENDORA-PAYMENT-SERVICE</code>.</li>
        <li><strong>Centralized Swagger Documentation:</strong> Open <code>http://localhost:8080/swagger-ui/index.html</code>. Demonstrate the dropdown selector aggregating API contracts from all 5 business services through the single gateway entry point.</li>
        <li><strong>Public User Registration:</strong> Execute <code>POST http://localhost:8080/api/registration/register</code> with student details. Show that a <code>201 Created</code> response is returned and the password is stored as a BCrypt hash in MySQL (<code>vendora_db.users</code>).</li>
        <li><strong>Authentication &amp; Token Issuance:</strong> Execute <code>POST http://localhost:8080/api/auth/login</code>. Show the returned JWT token, user ID, email, role (<code>STUDENT</code>), and expiration duration.</li>
        <li><strong>Perimeter Security &amp; Anti-Spoofing Demonstration:</strong>
            <ul>
                <li>Call a protected endpoint (e.g., <code>POST http://localhost:8080/api/listings</code>) <strong>without</strong> a token &rarr; Show immediate <code>401 Unauthorized</code> response generated by the Gateway filter.</li>
                <li>Call the same endpoint with an invalid/tampered token &rarr; Show <code>401 Unauthorized</code> with signature failure message.</li>
                <li>Call with a valid Bearer token and intentionally inject a false header <code>X-User-Id: 999</code> &rarr; Show that the created listing in MySQL correctly attributes ownership to the user ID verified in the token, proving that the gateway stripped the spoofed header.</li>
            </ul>
        </li>
        <li><strong>Peer-to-Peer Marketplace Workflow:</strong>
            <ul>
                <li>User A posts an engineering textbook (<code>POST /api/listings</code>).</li>
                <li>User B searches for it (<code>GET /api/listings/search?keyword=...</code>).</li>
                <li>User B submits a purchase request (<code>POST /api/listings/{id}/requests</code>).</li>
                <li>User A accepts the request (<code>PATCH .../status?status=ACCEPTED</code>).</li>
                <li>Both users initiate a chat conversation and exchange messages (<code>POST /api/listings/conversations/...</code>).</li>
            </ul>
        </li>
        <li><strong>Scrap Collection &amp; Pickup Lifecycle:</strong>
            <ul>
                <li>User schedules paper/e-waste pickup (<code>POST /api/scrap</code>).</li>
                <li>Admin assigns collection staff member (<code>PATCH /api/scrap/{id}/assign</code>).</li>
                <li>Collector updates status to <code>WEIGHED</code> and <code>COMPLETED</code>.</li>
            </ul>
        </li>
        <li><strong>Idempotent Settlement Demonstration:</strong>
            <ul>
                <li>Execute <code>POST http://localhost:8080/api/payments</code> with an <code>Idempotency-Key</code> &rarr; Returns <code>201 Created</code> and transaction reference.</li>
                <li>Resend the exact same request &rarr; Returns <code>200 OK</code> with the identical transaction reference and no duplicate database record.</li>
            </ul>
        </li>
        <li><strong>Automated Test Results:</strong> Run <code>mvn test</code> in the terminal to display the 59 passing unit tests with 0 failures, proving baseline code quality.</li>
    </ol>
</div>

</body>
</html>
"@)
$sw.Flush()
$sw.Close()
Write-Host "Appended Part 4 with tested Gantt syntax."
