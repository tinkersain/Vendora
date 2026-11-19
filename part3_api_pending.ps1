$htmlPath = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$sw = [System.IO.StreamWriter]::new($htmlPath, $true, [System.Text.Encoding]::UTF8)

$sw.WriteLine(@"
<!-- SECTION 5: COMPREHENSIVE REST API DOCUMENTATION -->
<div class="page-break">
    <h1>5. Comprehensive REST API Documentation</h1>
    <p>All client requests are directed to the <strong>API Gateway</strong> (<code>http://localhost:8080</code>).</p>
    
    <h2>5.1 Authentication Service Endpoints (:8082)</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 10%;">Method</th>
                <th style="width: 22%;">Gateway Path</th>
                <th style="width: 14%;">Access Level</th>
                <th style="width: 22%;">Description</th>
                <th style="width: 16%;">Key Parameters</th>
                <th style="width: 16%;">Success Response</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/auth/login</code></td>
                <td>Public</td>
                <td>Authenticates credentials and returns signed JWT</td>
                <td>Body: <code>{email, password}</code></td>
                <td><code>200 OK</code> + <code>{accessToken, tokenType, userId, email, role, expiresIn}</code></td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/auth/validate</code></td>
                <td>Authenticated</td>
                <td>Validates JWT token and outputs claims</td>
                <td>Header: <code>Authorization: Bearer &lt;token&gt;</code></td>
                <td><code>200 OK</code> + <code>{valid: true, userId, email, role}</code></td>
            </tr>
        </tbody>
    </table>

    <h2>5.2 User &amp; Identity Service Endpoints (:8081)</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 10%;">Method</th>
                <th style="width: 24%;">Gateway Path</th>
                <th style="width: 14%;">Access Level</th>
                <th style="width: 22%;">Description</th>
                <th style="width: 15%;">Key Parameters</th>
                <th style="width: 15%;">Success Response</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/registration/register</code></td>
                <td>Public</td>
                <td>Registers a new student or faculty account</td>
                <td>Body: <code>{fullName, email, phone, registrationNumber, password}</code></td>
                <td><code>201 Created</code> + User profile</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/users/profile</code></td>
                <td>Authenticated</td>
                <td>Retrieves current logged-in user profile</td>
                <td>Injected header: <code>X-User-Id</code></td>
                <td><code>200 OK</code> + User profile</td>
            </tr>
            <tr>
                <td><strong><code>PUT</code></strong></td>
                <td><code>/api/users/profile</code></td>
                <td>Authenticated</td>
                <td>Updates current user's editable details</td>
                <td>Body: <code>{fullName, phone}</code></td>
                <td><code>200 OK</code> + Updated user</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/users/{id}</code></td>
                <td>Authenticated</td>
                <td>Fetches user public profile by ID</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + User profile</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/users/{id}/status</code></td>
                <td>Admin Only</td>
                <td>Updates account status (<code>ACTIVE</code>, <code>SUSPENDED</code>)</td>
                <td>Query: <code>?status=SUSPENDED</code>, Role: <code>ADMIN</code></td>
                <td><code>200 OK</code> + User profile</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/users/representative/apply</code></td>
                <td>Student</td>
                <td>Submits application for representative role</td>
                <td>Body: <code>{reason}</code></td>
                <td><code>201 Created</code> + Application details</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/users/representative/status</code></td>
                <td>Student</td>
                <td>Checks current representative application status</td>
                <td>Injected header: <code>X-User-Id</code></td>
                <td><code>200 OK</code> + Application status</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/users/representative/all</code></td>
                <td>Admin Only</td>
                <td>Lists all submitted representative applications</td>
                <td>Role: <code>ADMIN</code></td>
                <td><code>200 OK</code> + Application list</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/users/representative/{id}/status</code></td>
                <td>Admin Only</td>
                <td>Approves or rejects representative application</td>
                <td>Query: <code>?status=APPROVED</code>, Role: <code>ADMIN</code></td>
                <td><code>200 OK</code> + Updated application</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/internal/users/auth-details</code></td>
                <td>Internal</td>
                <td>Provides auth details to Auth Service</td>
                <td>Query: <code>email</code>, Header: <code>X-Service-Secret</code></td>
                <td><code>200 OK</code> + <code>{id, email, passwordHash, role, accountStatus}</code></td>
            </tr>
        </tbody>
    </table>

    <h2>5.3 Marketplace &amp; Commerce Service Endpoints (:8083)</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 10%;">Method</th>
                <th style="width: 25%;">Gateway Path</th>
                <th style="width: 14%;">Access Level</th>
                <th style="width: 21%;">Description</th>
                <th style="width: 15%;">Key Parameters</th>
                <th style="width: 15%;">Success Response</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/listings</code></td>
                <td>Authenticated</td>
                <td>Creates a new marketplace item listing</td>
                <td>Body: <code>{title, description, price, quantity, category, condition, imageUrl}</code></td>
                <td><code>201 Created</code> + Listing details</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings</code></td>
                <td>Public / Auth</td>
                <td>Retrieves all available marketplace listings</td>
                <td>None</td>
                <td><code>200 OK</code> + Array of listings</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/{id}</code></td>
                <td>Public / Auth</td>
                <td>Fetches a specific listing by ID</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Listing details</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/seller/{sellerId}</code></td>
                <td>Authenticated</td>
                <td>Retrieves listings published by a specific seller</td>
                <td>Path variable: <code>sellerId</code></td>
                <td><code>200 OK</code> + Array of listings</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/search</code></td>
                <td>Authenticated</td>
                <td>Keyword search on listing titles and descriptions</td>
                <td>Query: <code>?keyword=textbook</code></td>
                <td><code>200 OK</code> + Matching listings</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/category/{category}</code></td>
                <td>Authenticated</td>
                <td>Filters catalog by category (<code>BOOKS</code>, etc.)</td>
                <td>Path variable: <code>category</code></td>
                <td><code>200 OK</code> + Filtered listings</td>
            </tr>
            <tr>
                <td><strong><code>PUT</code></strong></td>
                <td><code>/api/listings/{id}</code></td>
                <td>Seller Only</td>
                <td>Updates an existing listing</td>
                <td>Body: <code>{title, description, price...}</code></td>
                <td><code>200 OK</code> + Updated listing</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/listings/{id}/status</code></td>
                <td>Seller Only</td>
                <td>Updates listing status (<code>AVAILABLE</code>, <code>SOLD</code>)</td>
                <td>Query: <code>?status=SOLD</code></td>
                <td><code>200 OK</code> + Updated listing</td>
            </tr>
            <tr>
                <td><strong><code>DELETE</code></strong></td>
                <td><code>/api/listings/{id}</code></td>
                <td>Seller Only</td>
                <td>Deletes/withdraws a listing from marketplace</td>
                <td>Path variable: <code>id</code></td>
                <td><code>204 No Content</code></td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/listings/{listingId}/requests</code></td>
                <td>Buyer</td>
                <td>Submits purchase offer/request for a listing</td>
                <td>Body: <code>{requestedQuantity, message}</code></td>
                <td><code>201 Created</code> + Request object</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/{listingId}/requests</code></td>
                <td>Seller Only</td>
                <td>Seller reviews purchase requests on listing</td>
                <td>Path variable: <code>listingId</code></td>
                <td><code>200 OK</code> + Request list</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/requests/my</code></td>
                <td>Buyer</td>
                <td>Buyer reviews all submitted purchase requests</td>
                <td>Injected header: <code>X-User-Id</code></td>
                <td><code>200 OK</code> + Array of requests</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/listings/requests/{id}/status</code></td>
                <td>Seller Only</td>
                <td>Seller accepts or rejects buyer offer</td>
                <td>Query: <code>?status=ACCEPTED</code></td>
                <td><code>200 OK</code> + Updated request</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/listings/requests/{id}/cancel</code></td>
                <td>Buyer Only</td>
                <td>Buyer cancels a pending purchase request</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Cancelled request</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/listings/conversations/start</code></td>
                <td>Buyer / Seller</td>
                <td>Initiates or fetches P2P conversation</td>
                <td>Query: <code>?listingId=501</code></td>
                <td><code>201 Created</code> / <code>200 OK</code> + Conversation</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/conversations/my</code></td>
                <td>Authenticated</td>
                <td>Lists user's active chat conversations</td>
                <td>Injected header: <code>X-User-Id</code></td>
                <td><code>200 OK</code> + Array of conversations</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/listings/conversations/{id}/messages</code></td>
                <td>Participant</td>
                <td>Sends a chat message in a conversation</td>
                <td>Body: <code>{message}</code></td>
                <td><code>201 Created</code> + Message record</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/listings/conversations/{id}/messages</code></td>
                <td>Participant</td>
                <td>Fetches message history for a conversation</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Message array</td>
            </tr>
        </tbody>
    </table>

    <h2>5.4 Scrap Collection &amp; Giveaways Service Endpoints (:8085)</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 10%;">Method</th>
                <th style="width: 25%;">Gateway Path</th>
                <th style="width: 14%;">Access Level</th>
                <th style="width: 21%;">Description</th>
                <th style="width: 15%;">Key Parameters</th>
                <th style="width: 15%;">Success Response</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/scrap</code></td>
                <td>Authenticated</td>
                <td>Schedules a recyclable scrap pickup request</td>
                <td>Body: <code>{materialType, description, estimatedQuantity, quantityUnit, pickupLocation, preferredPickupTime}</code></td>
                <td><code>201 Created</code> + Scrap request</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/scrap</code></td>
                <td>Staff / Admin</td>
                <td>Lists all submitted scrap collection requests</td>
                <td>None</td>
                <td><code>200 OK</code> + Array of requests</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/scrap/{id}</code></td>
                <td>Authenticated</td>
                <td>Retrieves specific scrap request details</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Request details</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/scrap/user/{userId}</code></td>
                <td>Owner / Admin</td>
                <td>Lists scrap requests created by a specific user</td>
                <td>Path variable: <code>userId</code></td>
                <td><code>200 OK</code> + Array of requests</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/scrap/staff/{staffId}</code></td>
                <td>Staff / Admin</td>
                <td>Lists requests assigned to a collection staff member</td>
                <td>Path variable: <code>staffId</code></td>
                <td><code>200 OK</code> + Array of requests</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/scrap/{id}/assign</code></td>
                <td>Admin Only</td>
                <td>Assigns collection staff to a pickup request</td>
                <td>Body: <code>{assignedStaffId, scheduledPickupTime, notes}</code></td>
                <td><code>200 OK</code> + Updated request</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/scrap/{id}/status</code></td>
                <td>Staff / Admin</td>
                <td>Updates scrap status (<code>WEIGHED</code>, <code>COMPLETED</code>)</td>
                <td>Query: <code>?status=COMPLETED</code></td>
                <td><code>200 OK</code> + Updated request</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/giveaways</code></td>
                <td>Student / Staff</td>
                <td>Publishes a zero-cost free giveaway item</td>
                <td>Body: <code>{title, description, category}</code></td>
                <td><code>201 Created</code> + Giveaway object</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/giveaways</code></td>
                <td>Public / Auth</td>
                <td>Lists all available free giveaway items</td>
                <td>None</td>
                <td><code>200 OK</code> + Array of giveaways</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/giveaways/{id}</code></td>
                <td>Public / Auth</td>
                <td>Fetches a giveaway item by ID</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Giveaway object</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/giveaways/{id}/interest</code></td>
                <td>Authenticated</td>
                <td>Submits statement of interest for free item</td>
                <td>Body: <code>{message}</code></td>
                <td><code>201 Created</code> + Interest object</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/giveaways/{id}/interests</code></td>
                <td>Owner Only</td>
                <td>Owner reviews submitted claims on item</td>
                <td>Injected header: <code>X-User-Id</code></td>
                <td><code>200 OK</code> + Array of interests</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/giveaways/{id}/interests/{intId}/accept</code></td>
                <td>Owner Only</td>
                <td>Owner accepts recipient; reserves giveaway</td>
                <td>Path variables: <code>id</code>, <code>intId</code></td>
                <td><code>200 OK</code> + Accepted interest</td>
            </tr>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/giveaways/{id}/messages</code></td>
                <td>Authenticated</td>
                <td>Submits public inquiry or message on giveaway</td>
                <td>Body: <code>{message}</code></td>
                <td><code>201 Created</code> + Message object</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/giveaways/{id}/messages</code></td>
                <td>Authenticated</td>
                <td>Views public discussion messages on giveaway</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Message array</td>
            </tr>
            <tr>
                <td><strong><code>DELETE</code></strong></td>
                <td><code>/api/giveaways/{id}</code></td>
                <td>Owner Only</td>
                <td>Cancels / withdraws a giveaway listing</td>
                <td>Path variable: <code>id</code></td>
                <td><code>204 No Content</code></td>
            </tr>
        </tbody>
    </table>

    <h2>5.5 Payment &amp; Settlement Service Endpoints (:8086)</h2>
    <table>
        <thead>
            <tr>
                <th style="width: 10%;">Method</th>
                <th style="width: 25%;">Gateway Path</th>
                <th style="width: 14%;">Access Level</th>
                <th style="width: 21%;">Description</th>
                <th style="width: 15%;">Key Parameters</th>
                <th style="width: 15%;">Success Response</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong><code>POST</code></strong></td>
                <td><code>/api/payments</code></td>
                <td>Authenticated Buyer</td>
                <td>Processes simulated payment with idempotency</td>
                <td>Body: <code>{amount, paymentMethod, referenceType, referenceId, sellerId, idempotencyKey}</code></td>
                <td><code>201 Created</code> (or <code>200 OK</code> on replay) + Payment</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/payments</code></td>
                <td>Admin Only</td>
                <td>Audits all recorded transactions</td>
                <td>Role: <code>ADMIN</code></td>
                <td><code>200 OK</code> + Transaction list</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/payments/{id}</code></td>
                <td>Authenticated</td>
                <td>Fetches payment record by numeric ID</td>
                <td>Path variable: <code>id</code></td>
                <td><code>200 OK</code> + Payment record</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/payments/reference/{ref}</code></td>
                <td>Authenticated</td>
                <td>Fetches transaction by reference code</td>
                <td>Path variable: <code>ref</code></td>
                <td><code>200 OK</code> + Payment record</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/payments/buyer/{buyerId}</code></td>
                <td>Buyer / Admin</td>
                <td>Retrieves payment history for a buyer</td>
                <td>Path variable: <code>buyerId</code></td>
                <td><code>200 OK</code> + Payment array</td>
            </tr>
            <tr>
                <td><strong><code>GET</code></strong></td>
                <td><code>/api/payments/seller/{sellerId}</code></td>
                <td>Seller / Admin</td>
                <td>Retrieves sales receipt history for a seller</td>
                <td>Path variable: <code>sellerId</code></td>
                <td><code>200 OK</code> + Payment array</td>
            </tr>
            <tr>
                <td><strong><code>PATCH</code></strong></td>
                <td><code>/api/payments/{id}/status</code></td>
                <td>Admin / System</td>
                <td>Updates status (simulates refunds / reconciliation)</td>
                <td>Query: <code>?status=REFUNDED</code></td>
                <td><code>200 OK</code> + Updated payment</td>
            </tr>
        </tbody>
    </table>
</div>

<!-- SECTION 6: PENDING IMPLEMENTATION & GAPS ANALYSIS -->
<div class="page-break">
    <h1>6. Pending Implementation &amp; Gaps Analysis</h1>
    <p>This section provides an honest, granular audit of what is currently incomplete, missing, or requires production hardening within each service:</p>

    <h2>6.1 vendora-api-gateway</h2>
    <ul>
        <li><strong>Missing Rate Limiting:</strong> No Redis or Bucket4j rate-limiting filter is currently active. The gateway does not throttle brute-force login attempts or high-frequency listing queries.</li>
        <li><strong>Missing Circuit Breakers:</strong> Resilience4j is not configured. If a downstream service (e.g., <code>marketplace-service</code>) crashes or encounters thread starvation, the gateway waits for timeout rather than triggering an immediate fallback response.</li>
        <li><strong>TLS/SSL Termination:</strong> Runs on plain HTTP (<code>:8080</code>). Needs HTTPS certificate binding for real-world deployment.</li>
    </ul>

    <h2>6.2 vendora-auth-service</h2>
    <ul>
        <li><strong>No Refresh Token Flow:</strong> JWT tokens are issued with a fixed 1-hour expiration. When a token expires, users must re-authenticate with credentials. A secure refresh-token rotation mechanism (HTTP-only cookies) is not implemented.</li>
        <li><strong>Stateless Token Invalidation / Blacklisting:</strong> When a user logs out, the JWT remains cryptographically valid until its expiration time. A Redis token blacklist or token version counter in <code>vendora_db</code> is needed for immediate revocation.</li>
        <li><strong>No Password Reset / Forgot Password:</strong> There is no endpoint or email delivery mechanism for OTP-based password resets.</li>
    </ul>

    <h2>6.3 vendora-user-service</h2>
    <ul>
        <li><strong>No Email Verification Workflow:</strong> Newly registered users are immediately set to <code>ACTIVE</code>. A campus verification step (sending a verification code to their <code>@campus.edu</code> email) is not implemented.</li>
        <li><strong>File Upload for Student ID Cards:</strong> The <code>RepresentativeApplicationRequest</code> accepts a static string URL (<code>studentIdProofUrl</code>). Multipart file upload directly to cloud storage (e.g., AWS S3 or MinIO) is missing.</li>
        <li><strong>No Avatar / Profile Picture Upload:</strong> Profiles only hold textual metadata.</li>
    </ul>

    <h2>6.4 vendora-marketplace-service</h2>
    <ul>
        <li><strong>No Physical Binary Image Upload:</strong> The <code>ListingRequest</code> expects a pre-hosted image URL string (<code>imageUrl: "https://..."</code>). An image upload endpoint (<code>multipart/form-data</code>) storing to an S3-compatible bucket is pending.</li>
        <li><strong>No Pagination / Pageable Sorting:</strong> <code>GET /api/listings</code> and <code>/api/listings/search</code> return unfiltered <code>List&lt;ListingResponse&gt;</code>. In a large campus catalog with thousands of books, this will cause memory spikes; Spring Data <code>Pageable</code> (<code>page</code>, <code>size</code>, <code>sort</code>) is missing.</li>
        <li><strong>Order / Payment Linkage:</strong> When a seller accepts a request (<code>status = ACCEPTED</code>), the transaction is not automatically linked to the Payment Service. The buyer must initiate payment independently without an atomic order state machine.</li>
    </ul>

    <h2>6.5 vendora-scrap-service</h2>
    <ul>
        <li><strong>No Dynamic Material Price Table:</strong> The system tracks quantity and unit (<code>15.5 KG PAPER</code>), but does not calculate automated monetary payouts based on daily scrap rates (e.g., ₹14/kg for newspaper, ₹10/kg for cardboard).</li>
        <li><strong>No Automated Payment Payout Trigger:</strong> When a scrap request transitions to <code>COMPLETED</code>, it does not automatically generate a payout record in <code>vendora-payment-service</code> to credit the student's UPI ID.</li>
    </ul>

    <h2>6.6 vendora-payment-service</h2>
    <ul>
        <li><strong>Simulation Only (No Third-Party Gateway Integration):</strong> The service validates input and persists payment records, but does not interact with Razorpay, Cashfree, or Stripe webhooks. No real money or sandbox bank transactions are processed.</li>
        <li><strong>No Escrow / Hold-and-Release Logic:</strong> For P2P book buying, funds are immediately marked <code>SUCCESS</code> rather than held in escrow until the buyer inspects the textbook and enters an OTP confirmation.</li>
    </ul>

    <h2>6.7 vendora-helpdesk-service (:8084)</h2>
    <ul>
        <li><strong>Completely Unimplemented:</strong> The service directory does not exist. Ticket entities, repositories, controllers, and staff assignment logic for campus facility repairs remain to be built.</li>
    </ul>
</div>
"@)
$sw.Flush()
$sw.Close()
Write-Host "Appended Part 3 (Sec 5, Sec 6)."
