[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
$assetsDir = "c:\Users\tankar\Downloads\Vendora\presentation_assets"
$pptxPath = "c:\Users\tankar\Downloads\Vendora\Vendora_Final_Presentation.pptx"

if (Test-Path $pptxPath) {
    Remove-Item $pptxPath -Force
}

Write-Host "Initializing PowerPoint COM Object..." -ForegroundColor Cyan
$ppt = New-Object -ComObject PowerPoint.Application
$pres = $ppt.Presentations.Add([Microsoft.Office.Core.MsoTriState]::msoFalse)

# Set 16:9 widescreen dimensions (960 pt x 540 pt)
$pres.PageSetup.SlideWidth = 960
$pres.PageSetup.SlideHeight = 540

$slideCount = 0

function New-Slide([string]$title, [string]$subtitle, [string]$notes) {
    $global:slideCount++
    $slide = $pres.Slides.Add($global:slideCount, 12) # 12 = ppLayoutBlank
    
    # Header Title
    $titleBox = $slide.Shapes.AddTextbox(1, 40, 18, 880, 36)
    $titleBox.TextFrame.TextRange.Text = $title
    $titleBox.TextFrame.TextRange.Font.Name = "Segoe UI"
    $titleBox.TextFrame.TextRange.Font.Size = 21
    $titleBox.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $titleBox.TextFrame.TextRange.Font.Color.RGB = 0x2A170F # #0f172a (BGR in COM: 0x2A170F)
    $titleBox.TextFrame.MarginLeft = 0
    $titleBox.TextFrame.MarginTop = 0

    # Header Subtitle
    $subBox = $slide.Shapes.AddTextbox(1, 40, 52, 880, 22)
    $subBox.TextFrame.TextRange.Text = $subtitle
    $subBox.TextFrame.TextRange.Font.Name = "Segoe UI"
    $subBox.TextFrame.TextRange.Font.Size = 11.5
    $subBox.TextFrame.TextRange.Font.Color.RGB = 0x8B7464 # #64748b
    $subBox.TextFrame.MarginLeft = 0
    $subBox.TextFrame.MarginTop = 0

    # Divider Line
    $line = $slide.Shapes.AddLine(40, 76, 920, 76)
    $line.Line.ForeColor.RGB = 0xC78402 # #0284c7
    $line.Line.Weight = 2

    # Speaker Notes
    if ($notes) {
        $slide.NotesPage.Shapes.Placeholders.Item(2).TextFrame.TextRange.Text = $notes
    }

    return $slide
}

function Add-Card($slide, [double]$left, [double]$top, [double]$width, [double]$height, [string]$title, [string[]]$bullets, [int]$headerColor = 0xC78402) {
    # Card background shape
    $card = $slide.Shapes.AddShape(5, $left, $top, $width, $height) # 5 = msoShapeRoundedRectangle
    $card.Fill.ForeColor.RGB = 0xFCFAF8 # #f8fafc
    $card.Line.ForeColor.RGB = 0xE0E0E0
    $card.Line.Weight = 1

    # Card Title Box
    $tBox = $slide.Shapes.AddTextbox(1, $left + 14, $top + 12, $width - 28, 28)
    $tBox.TextFrame.TextRange.Text = $title
    $tBox.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tBox.TextFrame.TextRange.Font.Size = 14
    $tBox.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $tBox.TextFrame.TextRange.Font.Color.RGB = $headerColor
    $tBox.TextFrame.MarginLeft = 0

    # Bullet Points
    $bBox = $slide.Shapes.AddTextbox(1, $left + 14, $top + 42, $width - 28, $height - 52)
    $tf = $bBox.TextFrame
    $tf.WordWrap = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $tf.MarginLeft = 0
    $tf.MarginTop = 0

    $tr = $tf.TextRange
    $textCombined = ($bullets | ForEach-Object { "$([char]0x2022)  $_`n" }) -join ""
    $tr.Text = $textCombined
    $tr.Font.Name = "Segoe UI"
    $tr.Font.Size = 11.5
    $tr.Font.Color.RGB = 0x333333
    
    # Adjust line spacing
    $tr.ParagraphFormat.SpaceAfter = 6
}

# -------------------------------------------------------------
# SLIDE 1: Title Slide (Cover)
# -------------------------------------------------------------
Write-Host "Creating Slide 1: Cover..." -ForegroundColor Yellow
$global:slideCount++
$s1 = $pres.Slides.Add($slideCount, 12)

# Background Accent Card
$bgCard = $s1.Shapes.AddShape(1, 40, 30, 880, 480) # Rectangle
$bgCard.Fill.ForeColor.RGB = 0xFAF5F0
$bgCard.Line.ForeColor.RGB = 0xCBD5E1

# Category Pill
$pill = $s1.Shapes.AddShape(5, 340, 70, 280, 30)
$pill.Fill.ForeColor.RGB = 0xFED2C3 # Light Sky Blue
$pill.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse
$pill.TextFrame.TextRange.Text = "CAPSTONE PROJECT DEFENSE"
$pill.TextFrame.TextRange.Font.Name = "Segoe UI"
$pill.TextFrame.TextRange.Font.Size = 11
$pill.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$pill.TextFrame.TextRange.Font.Color.RGB = 0x803000

# Main Title
$tMain = $s1.Shapes.AddTextbox(1, 80, 115, 800, 70)
$tMain.TextFrame.TextRange.Text = "VENDORA"
$tMain.TextFrame.TextRange.Font.Name = "Segoe UI"
$tMain.TextFrame.TextRange.Font.Size = 48
$tMain.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$tMain.TextFrame.TextRange.Font.Color.RGB = 0x2A170F
$tMain.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center

# Subtitle
$tSub = $s1.Shapes.AddTextbox(1, 80, 190, 800, 50)
$tSub.TextFrame.TextRange.Text = "Your Campus. Your Community. One Platform.`nAn Enterprise Microservices Platform for Campus Commerce, Recycling, and Facilities Operations"
$tSub.TextFrame.TextRange.Font.Name = "Segoe UI"
$tSub.TextFrame.TextRange.Font.Size = 14
$tSub.TextFrame.TextRange.Font.Color.RGB = 0x555555
$tSub.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center

# Tech Badges Container
$badgeText = "Java 17 LTS   |   Spring Boot 3.x   |   Spring Cloud Gateway   |   Netflix Eureka   |   MySQL 8.0   |   SonarQube 26.11"
$tBadges = $s1.Shapes.AddTextbox(1, 80, 260, 800, 36)
$tBadges.TextFrame.TextRange.Text = $badgeText
$tBadges.TextFrame.TextRange.Font.Name = "Segoe UI"
$tBadges.TextFrame.TextRange.Font.Size = 12
$tBadges.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$tBadges.TextFrame.TextRange.Font.Color.RGB = 0xC78402
$tBadges.TextFrame.TextRange.ParagraphFormat.Alignment = 2

# Status Box
$statusBox = $s1.Shapes.AddShape(5, 180, 320, 600, 60)
$statusBox.Fill.ForeColor.RGB = 0xE6FCEB # Soft Green
$statusBox.Line.ForeColor.RGB = 0x86EFAC
$statusBox.TextFrame.TextRange.Text = "100% Quality Gate PASSED  *  >89% Code Coverage (343 Unit Tests)  *  Grade A Security & Reliability"
$statusBox.TextFrame.TextRange.Font.Name = "Segoe UI"
$statusBox.TextFrame.TextRange.Font.Size = 12
$statusBox.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$statusBox.TextFrame.TextRange.Font.Color.RGB = 0x1B6B2B
$statusBox.TextFrame.TextRange.ParagraphFormat.Alignment = 2

$s1.NotesPage.Shapes.Placeholders.Item(2).TextFrame.TextRange.Text = "Good morning respected evaluators. Today we are presenting Vendora, an enterprise-grade backend microservices ecosystem built specifically for university campuses. Rather than a generic e-commerce clone, Vendora integrates campus identity, peer-to-peer resource exchange, hostel waste recycling, facilities maintenance, and idempotent payment persistence across 8 independently deployable microservices with 100% passed SonarQube quality gates."

# -------------------------------------------------------------
# SLIDE 2: Requirements & Problem Statement
# -------------------------------------------------------------
Write-Host "Creating Slide 2: Requirements..." -ForegroundColor Yellow
$s2 = New-Slide "1. Requirement Understanding & Problem Statement" "Operational challenges in traditional campuses vs. the Vendora unified solution" `
"Our project addresses the operational fragmentation found in university campuses. Currently, campus commerce happens in unstructured chat groups, hostel maintenance is tracked on paper, and recyclable waste goes into landfills. Vendora solves all four problems within a single platform governed by authenticated university roles."

$probBullets = @(
    "Unregulated P2P Exchange: Students buy/sell books, lab supplies, and electronics via informal WhatsApp/Telegram groups with scam risks and zero accountability.",
    "Hostel Waste Accumulation: Large volumes of recyclable paper registers and usable items are discarded in landfills at semester-end without collection logistics.",
    "Manual Maintenance Tracking: Hostel maintenance issues rely on paper logbooks, leading to lost complaints and zero transparency on technician resolution SLAs.",
    "Identity & Trust Void: Existing commercial public apps lack university domain verification (@campus.edu) or campus role governance."
)
Add-Card $s2 40 90 425 410 "The Problem in Today's Campuses" $probBullets 0x1E1EB4 # Reddish

$solBullets = @(
    "Campus Identity & RBAC: Strict university email binding and 6 role profiles (Student, Faculty, Admin, Maintenance Staff, Collection Staff, Representative).",
    "Campus Marketplace: Peer-to-peer resource exchange with categorized search, buyer purchase requests, and seller-ownership security.",
    "Scrap & Giveaway Hub: Scheduled doorstep recyclable pickup by staff and free senior-to-junior giveaway claims.",
    "Facilities Helpdesk: End-to-end ticketing state machine with category SLA tracking and discussion threads.",
    "Simulated Payments: Idempotent transaction persistence with double-charge protection."
)
Add-Card $s2 495 90 425 410 "The Vendora Platform Solution" $solBullets 0x389E05 # Green

# -------------------------------------------------------------
# SLIDE 3: System Architecture (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 3: Architecture..." -ForegroundColor Yellow
$s3 = New-Slide "2. High-Level Microservices Architecture" "Event-driven, reactive perimeter gateway with dynamic service discovery and logical database isolation" `
"Our architecture features three clear tiers: the Edge Layer with Eureka Server and Spring Cloud Gateway, the Domain Microservices Tier with 6 specialized business services, and the Data Tier with MySQL. Eureka eliminates hardcoded hostnames, while our reactive Gateway handles centralized security filtering before routing requests downstream."

$imgArch = Join-Path $assetsDir "diag_arch.png"
if (Test-Path $imgArch) {
    $s3.Shapes.AddPicture($imgArch, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 4: Use Case Diagram (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 4: Use Case..." -ForegroundColor Yellow
$s4 = New-Slide "3. Platform Comprehensive Use Case Diagram" "Role-based functional interactions across system actors (Student, Faculty, Staff, Admin)" `
"This use case diagram illustrates our role-based authorization model. Students and faculty participate in commerce, recycling, and ticket creation. Maintenance and collection staff have restricted dashboards to accept and resolve tasks, while Administrators hold governance oversight for staff assignment and role elevations."

$imgUC = Join-Path $assetsDir "diag_usecase.png"
if (Test-Path $imgUC) {
    $s4.Shapes.AddPicture($imgUC, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 5: Security & Auth Sequence Diagram (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 5: Security & Auth..." -ForegroundColor Yellow
$s5 = New-Slide "4. Zero-Trust Security & Authentication Flow" "Perimeter header sanitization, BCrypt hashing, and identity claim propagation" `
"Our security architecture guarantees that downstream microservices never trust client-supplied identity. The API Gateway acts as our zero-trust perimeter: it strips any injected identity headers, cryptographically validates the token's HMAC-SHA256 signature, and injects trusted identity headers. Password hashes are verified statelessly using BCrypt through an internal secured channel."

$imgAuth = Join-Path $assetsDir "diag_auth_seq.png"
if (Test-Path $imgAuth) {
    $s5.Shapes.AddPicture($imgAuth, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 6: User & Auth Services
# -------------------------------------------------------------
Write-Host "Creating Slide 6: User & Auth..." -ForegroundColor Yellow
$s6 = New-Slide "5. Implementation: User & Authentication Services" "Decoupled identity governance, BCrypt encryption, and representative approval" `
"The User Service manages campus identity and profile governance, while the Auth Service remains completely stateless. Notice that the Auth Service owns zero database tables; it verifies credentials via an internal REST API. Both services achieve approximately 90% and 97% code coverage respectively."

$authBullets = @(
    "Stateless Architecture: Contains 0 database tables to maintain strict microservice decoupling; delegates lookup to User Service.",
    "Token Minting: Issues HMAC-SHA256 tokens using JJWT 0.12.6 encoding userId, email, and role with 1-hour expiration.",
    "Internal Security: Verifies credentials over secured REST using X-Internal-Secret: VendoraLocalInternalSecret2026.",
    "Endpoints: POST /api/auth/login.",
    "Quality Metrics: 36 Unit Tests | 96.9% Coverage | 98.6% Line Coverage | Grade A Security & Maintainability."
)
Add-Card $s6 40 90 425 410 "Vendora Auth Service (:8082)" $authBullets 0xC78402

$userBullets = @(
    "Campus Identity Profiles: Manages student/faculty profiles with strict university email validation (@campus.edu).",
    "Password Protection: One-way cryptographic password hashing via BCryptPasswordEncoder (strength 10).",
    "App Representative Workflow: Student applies for representative status; Admin reviews and approves elevation.",
    "Endpoints: POST /api/registration/register, GET /api/users/profile, POST /api/users/representative/apply, PATCH /api/users/representative/approve.",
    "Quality Metrics: 51 Unit Tests | 89.7% Coverage | 92.6% Line Coverage | Grade A Ratings."
)
Add-Card $s6 495 90 425 410 "Vendora User Service (:8081)" $userBullets 0x0284C7

# -------------------------------------------------------------
# SLIDE 7: Marketplace Service
# -------------------------------------------------------------
Write-Host "Creating Slide 7: Marketplace..." -ForegroundColor Yellow
$s7 = New-Slide "6. Implementation: Marketplace Service" "Campus resource exchange, multi-attribute dynamic search, and buyer-seller chat" `
"Marketplace Service facilitates peer-to-peer student commerce. It features multi-attribute dynamic search across categories and price ranges, buyer purchase requests, and threaded chat. Ownership checks prevent any student from altering or deleting listings they do not own. It is backed by 69 passing unit tests with 92.1% coverage."

$mktEntityBullets = @(
    "Listing Entity: Item title, description, category (BOOKS, ELECTRONICS, CYCLES, LAB_EQUIPMENT, FURNITURE), price, condition, status (AVAILABLE, SOLD, INACTIVE).",
    "MarketplaceRequest Entity: Buyer purchase offer, requested quantity, negotiation notes, status (PENDING, ACCEPTED, REJECTED).",
    "Conversation & Message Entities: Threaded in-app chat facilitating safe campus meetup locations.",
    "Key APIs: POST /api/listings, GET /api/listings (Dynamic Search), POST /api/listings/{id}/requests, PATCH /api/listings/requests/{id}/status, POST /api/listings/conversations/{id}/messages."
)
Add-Card $s7 40 90 425 410 "Domain Entities & Contracts" $mktEntityBullets 0x0284C7

$mktLogicBullets = @(
    "Multi-Attribute Search Engine: Filter listings simultaneously by category, minPrice, maxPrice, condition, and keyword query.",
    "Seller Ownership Guard: Only the listing creator (seller_id == X-User-Id) can modify item attributes or mark as SOLD.",
    "Boundary Validation: Enforces positive price (@Positive), quantity >= 1, non-empty titles, and valid enum states.",
    "Quality Metrics: 69 Unit Tests | 92.1% Coverage | 96.8% Line Coverage | Grade A Ratings."
)
Add-Card $s7 495 90 425 410 "Business Rules & Security" $mktLogicBullets 0x389E05

# -------------------------------------------------------------
# SLIDE 8: Scrap Hub & Giveaways (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 8: Scrap & Giveaways..." -ForegroundColor Yellow
$s8 = New-Slide "7. Implementation: Scrap Hub & Giveaways" "Circular economy logistics and senior-to-junior reusable item donations" `
"The Scrap and Giveaway Service implements two distinct circular economy workflows. Workflow 1 coordinates physical doorstep collection of recyclables like old notebooks and registers with staff assignment. Workflow 2 allows seniors to give away useful hostel items to juniors for free. It is backed by 75 unit tests with 92.9% coverage."

$imgScrap = Join-Path $assetsDir "diag_scrap.png"
if (Test-Path $imgScrap) {
    $s8.Shapes.AddPicture($imgScrap, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 9: Facilities Helpdesk (DIAGRAM + DETAILS)
# -------------------------------------------------------------
Write-Host "Creating Slide 9: Helpdesk..." -ForegroundColor Yellow
$s9 = New-Slide "8. Implementation: Facilities Helpdesk" "Digital maintenance ticketing state machine with threaded technician communication" `
"The Helpdesk Service replaces paper registers with an automated ticketing workflow. It enforces a strict state machine from OPEN to CLOSED, allowing technicians to add resolution notes and students to exchange comments with assigned staff. With 55 unit tests, it maintains 89% code coverage."

$imgTicket = Join-Path $assetsDir "diag_ticket_state.png"
if (Test-Path $imgTicket) {
    $s9.Shapes.AddPicture($imgTicket, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 95, 450, 400) | Out-Null
}

$helpBullets = @(
    "Category Coverage: ELECTRICAL, PLUMBING, FURNITURE, INTERNET_WIFI, LAB_EQUIPMENT, SANITATION, AC_COOLING, OTHER.",
    "Priority SLAs: LOW, MEDIUM, HIGH, URGENT.",
    "Multi-Filter Search API: GET /api/helpdesk/tickets?status=OPEN&category=ELECTRICAL&staffId=10.",
    "Interactive Discussion: POST /api/helpdesk/tickets/{id}/comments enables student-technician chat on tickets.",
    "Quality Metrics: 55 Unit Tests | 89.0% Coverage | 96.3% Line Coverage | Grade A Ratings."
)
Add-Card $s9 505 95 415 400 "Features & Access Controls" $helpBullets 0x0284C7

# -------------------------------------------------------------
# SLIDE 10: Payment Service & Idempotency (DIAGRAM + DETAILS)
# -------------------------------------------------------------
Write-Host "Creating Slide 10: Payment..." -ForegroundColor Yellow
$s10 = New-Slide "9. Implementation: Simulated Payment & Idempotency" "Financial transaction ledger and client-driven duplicate charge prevention" `
"Payment Service ensures financial transaction integrity through idempotency protection. If a network blip causes a student to submit a payment twice with the same idempotency key, the service detects the duplicate and returns the original transaction record without re-executing charges. It boasts 93.4% test coverage."

$imgPay = Join-Path $assetsDir "diag_payment_flow.png"
if (Test-Path $imgPay) {
    $s10.Shapes.AddPicture($imgPay, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 95, 450, 400) | Out-Null
}

$payBullets = @(
    "Network Timeout Safeguard: Duplicate requests caused by client retries or double clicks are intercepted by the unique idempotencyKey.",
    "Cross-Domain References: Binds transactions to MARKETPLACE purchases or SCRAP payouts.",
    "Transaction State Lifecycle: Transitions PENDING -> COMPLETED / FAILED with audit timestamp.",
    "Endpoints: POST /api/payments/process, GET /api/payments/{transactionRef}, GET /api/payments/user/{userId}.",
    "Quality Metrics: 44 Unit Tests | 93.4% Coverage | 98.8% Line Coverage | Grade A Ratings."
)
Add-Card $s10 505 95 415 400 "Idempotency Architecture" $payBullets 0x389E05

# -------------------------------------------------------------
# SLIDE 11: End-to-End Sequence Diagram (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 11: E2E Sequence..." -ForegroundColor Yellow
$s11 = New-Slide "10. End-to-End Campus Workflow Sequence Diagram" "Cross-service coordination during a complete campus commerce & facilities scenario" `
"This sequence diagram shows how our microservices collaborate in real campus scenarios: a student negotiates a cycle purchase through Marketplace Service, executes an idempotent payment through Payment Service, and raises a maintenance ticket with Helpdesk Service—all routed securely through the central API Gateway."

$imgE2E = Join-Path $assetsDir "diag_e2e_seq.png"
if (Test-Path $imgE2E) {
    $s11.Shapes.AddPicture($imgE2E, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 12: Database Architecture (ER DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 12: ER Diagram..." -ForegroundColor Yellow
$s12 = New-Slide "11. Database Architecture & Entity-Relationship Diagram" "Pragmatic Shared Schema with strict logical domain ownership and zero cross-domain foreign keys" `
"Our database architecture balances academic practicality with strict microservice design. We use one MySQL instance named vendora_db, but enforce absolute logical isolation: there are zero cross-domain SQL joins and zero physical foreign keys between service domains. Moving to 6 separate databases in production requires only changing connection strings without altering a single line of Java code."

$imgERD = Join-Path $assetsDir "diag_erd.png"
if (Test-Path $imgERD) {
    $s12.Shapes.AddPicture($imgERD, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 13: Core Class Diagram (DIAGRAM)
# -------------------------------------------------------------
Write-Host "Creating Slide 13: Class Diagram..." -ForegroundColor Yellow
$s13 = New-Slide "12. Core Class Diagram & Layered Design" "Standard enterprise Controller -> Service -> Repository -> Entity hierarchy with safe DTO contracts" `
"Every microservice follows strict separation of concerns across Controllers, Service interfaces, implementations, and Repositories. Request and Response DTOs guarantee that internal database fields are never leaked to external clients."

$imgClass = Join-Path $assetsDir "diag_class.png"
if (Test-Path $imgClass) {
    $s13.Shapes.AddPicture($imgClass, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 40, 85, 880, 420) | Out-Null
}

# -------------------------------------------------------------
# SLIDE 14: Exception Handling & AOP
# -------------------------------------------------------------
Write-Host "Creating Slide 14: Exception Handling & AOP..." -ForegroundColor Yellow
$s14 = New-Slide "13. Exception Handling & Cross-Cutting Concerns" "Standardized JSON error schemas, Aspect-Oriented Logging, and Bean Validation" `
"We handle cross-cutting concerns uniformly across all services. Our GlobalExceptionHandler maps domain errors to standard HTTP status codes without leaking internal stack traces. Meanwhile, Spring AOP Logging intercepts execution across all layers to benchmark performance in real time."

$errBullets = @(
    "Standardized Error Model: timestamp, status, error, message, path.",
    "ResourceNotFoundException: Maps to 404 Not Found (e.g. ticket ID missing).",
    "BadRequestException / Validation: Maps to 400 Bad Request (invalid status transitions).",
    "UnauthorizedException: Maps to 401 Unauthorized (invalid or expired token).",
    "ForbiddenException: Maps to 403 Forbidden (non-owner editing listing).",
    "ConflictException: Maps to 409 Conflict (duplicate email registration).",
    "Zero Stack Trace Leakage: Generic 500 error sanitizes all unhandled server exceptions."
)
Add-Card $s14 40 90 425 410 "Centralized Exception Handling" $errBullets 0x1E1EB4

$aopBullets = @(
    "Around Advice (@Around): Intercepts execution across all Controller and Service methods.",
    "Wall-Clock Benchmarking: Calculates execution time in milliseconds via Spring StopWatch to detect slow queries.",
    "Contextual Failure Logging: @AfterThrowing records method names, argument values, and exact exception causes in Logback.",
    "Jakarta Bean Validation: Enforces constraints (@NotBlank, @Email, @Positive, @NotNull) at perimeter controllers before service execution.",
    "CORS Configuration: Restricted to localhost and 127.0.0.1 patterns with credentials enabled."
)
Add-Card $s14 495 90 425 410 "Spring AOP Logging & Validation" $aopBullets 0x0284C7

# -------------------------------------------------------------
# SLIDE 15: Quality Assurance & SonarQube (TABLE)
# -------------------------------------------------------------
Write-Host "Creating Slide 15: SonarQube Metrics..." -ForegroundColor Yellow
$s15 = New-Slide "14. Quality Assurance, SonarQube & Test Coverage" "Continuous inspection verification on SonarQube Server 26.11 (Port 9002)" `
"Our project undergoes continuous code quality inspection. All 8 microservices achieve a 100% PASSED green Quality Gate in SonarQube with 0 bugs, 0 vulnerabilities, 0 code smells, and Grade A across all categories. Total test coverage ranges from 89.0% to 100.0% across 343 automated unit tests."

# Add Table for SonarQube
$tableShape = $s15.Shapes.AddTable(9, 8, 40, 95, 880, 270)
$tbl = $tableShape.Table

$headers = @("Microservice", "Quality Gate", "Coverage", "Unit Tests", "Security", "Reliability", "Maintainability", "Duplications")
for ($c = 0; $c -lt 8; $c++) {
    $cell = $tbl.Cell(1, $c + 1)
    $cell.Shape.Fill.ForeColor.RGB = 0x2A170F
    $tr = $cell.Shape.TextFrame.TextRange
    $tr.Text = $headers[$c]
    $tr.Font.Name = "Segoe UI"
    $tr.Font.Size = 11
    $tr.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $tr.Font.Color.RGB = 0xFFFFFF
    $tr.ParagraphFormat.Alignment = 2
}

$rowsData = @(
    @("vendora-eureka-server", "PASSED (OK)", "100.0%", "2", "Grade A", "Grade A", "Grade A", "0.0%"),
    @("vendora-auth-service", "PASSED (OK)", "96.9%", "36", "Grade A", "Grade A", "Grade A", "0.0%"),
    @("vendora-payment-service", "PASSED (OK)", "93.4%", "44", "Grade A", "Grade A", "Grade A", "19.6%"),
    @("vendora-scrap-service", "PASSED (OK)", "92.9%", "75", "Grade A", "Grade A", "Grade A", "22.5%"),
    @("vendora-marketplace-service", "PASSED (OK)", "92.1%", "69", "Grade A", "Grade A", "Grade A", "22.6%"),
    @("vendora-api-gateway", "PASSED (OK)", "90.0%", "11", "Grade A", "Grade A", "Grade A", "0.0%"),
    @("vendora-user-service", "PASSED (OK)", "89.7%", "51", "Grade A", "Grade A", "Grade A", "14.9%"),
    @("vendora-helpdesk-service", "PASSED (OK)", "89.0%", "55", "Grade A", "Grade A", "Grade A", "20.9%")
)

for ($r = 0; $r -lt 8; $r++) {
    $data = $rowsData[$r]
    for ($c = 0; $c -lt 8; $c++) {
        $cell = $tbl.Cell($r + 2, $c + 1)
        if ($r % 2 -eq 1) {
            $cell.Shape.Fill.ForeColor.RGB = 0xFAF5F0
        }
        $tr = $cell.Shape.TextFrame.TextRange
        $tr.Text = $data[$c]
        $tr.Font.Name = "Segoe UI"
        $tr.Font.Size = 10.5
        $tr.Font.Color.RGB = 0x333333
        if ($c -eq 1) {
            $tr.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
            $tr.Font.Color.RGB = 0x2B6B1B # Green
        }
        $tr.ParagraphFormat.Alignment = 2
    }
}

# Summary Card below table
$sonarSummary = @(
    "All 343 automated unit tests execute with 0 failures and 0 errors across controllers, services, DTOs, entities, and exception handlers.",
    "Resolved Blocker Code Smell java:S2699 (pointcut assertion validation) and Security Rules java:S5122 (CORS) & java:S4502 (CSRF).",
    "Duplications represent standard JPA Entity & DTO field mirroring, accurately recognized by SonarQube with Grade A Maintainability."
)
Add-Card $s15 40 380 880 120 "Quality Assurance Highlights" $sonarSummary 0x389E05

# -------------------------------------------------------------
# SLIDE 16: Conclusion & Live Demo
# -------------------------------------------------------------
Write-Host "Creating Slide 16: Conclusion..." -ForegroundColor Yellow
$s16 = New-Slide "15. Conclusion, Future Roadmap & Live Demonstration" "Enterprise milestones achieved and demonstration readiness" `
"In conclusion, Vendora delivers a production-grade, highly tested campus microservices platform that exceeds all architectural, security, and quality benchmarks. We are now ready to demonstrate the live system across Eureka, Swagger UI, and SonarQube, and welcome any questions from the panel."

$delivBullets = @(
    "8 Active Application Runtimes: Netflix Eureka, Spring Cloud Gateway, and 6 specialized business domain microservices.",
    "Zero-Trust Security Perimeter: Cryptographic HMAC-SHA256 tokens with header sanitization and RBAC across 6 campus roles.",
    "Testing Rigor: 343 automated unit tests with >89% to 100% coverage and Grade A ratings on port 9002.",
    "1-Click Tooling: Automated scripts for service startup (start-services.ps1), shutdown, and E2E verification (demo-e2e.ps1)."
)
Add-Card $s16 40 90 425 290 "Key Accomplishments" $delivBullets 0x389E05

$roadBullets = @(
    "Real Payment Gateway Integration: Razorpay / UPI webhook callbacks and campus escrow hold-and-release mechanisms.",
    "WebSocket Real-Time Chat: Replacing HTTP polling with STOMP/WebSocket protocols for instantaneous peer messaging.",
    "Textbook Specialization: Google Books / OpenLibrary ISBN auto-population of book editions and author metadata.",
    "Responsive Web Client: Modern single-page application built with React / Next.js and Tailwind CSS."
)
Add-Card $s16 495 90 425 290 "Future Roadmap (Deferred Scope)" $roadBullets 0x0284C7

# Bottom Demo Banner
$demoBanner = $s16.Shapes.AddShape(5, 40, 395, 880, 85)
$demoBanner.Fill.ForeColor.RGB = 0xE6FCEB
$demoBanner.Line.ForeColor.RGB = 0x86EFAC
$demoBanner.TextFrame.TextRange.Text = "READY FOR LIVE DEMONSTRATION`nEureka Registry: http://localhost:8761   *   Swagger UI: http://localhost:8080/swagger-ui/index.html   *   SonarQube: http://localhost:9002"
$demoBanner.TextFrame.TextRange.Font.Name = "Segoe UI"
$demoBanner.TextFrame.TextRange.Font.Size = 13
$demoBanner.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$demoBanner.TextFrame.TextRange.Font.Color.RGB = 0x1B6B2B
$demoBanner.TextFrame.TextRange.ParagraphFormat.Alignment = 2

# Save Presentation
Write-Host "Saving presentation to $pptxPath..." -ForegroundColor Green
$pres.SaveAs($pptxPath)
$pres.Close()
$ppt.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($ppt) | Out-Null

Write-Host "Successfully generated PowerPoint presentation!" -ForegroundColor Green
Get-Item $pptxPath | Select-Object Name, Length, LastWriteTime