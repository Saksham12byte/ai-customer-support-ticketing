# CAPSTONE PROJECT REPORT

# AI-Powered Customer Support Ticketing System

**Course:** BTech Computer Science & Engineering (Year 2)  
**Academic Specialization:** Full-Stack Web Development & Applied Artificial Intelligence  
**Evaluation Platform:** SkillWallet Capstone Review  
**Author:** 2nd-Year BTech CSE Student  

---

## 1. ABSTRACT

Modern digital enterprises process high volumes of incoming customer inquiries daily across technical, billing, and administrative domains. Traditional helpdesk workflows often suffer from manual triage delays, incorrect categorization, slow initial response times, and inconsistent customer experiences.

This capstone project presents the design and implementation of an **AI-Powered Customer Support Ticketing System**. Developed using **Java (Spring Boot 3)**, **Spring Data JPA**, an embedded **H2 / MySQL database**, and a responsive **HTML5/CSS3/JavaScript Single Page Application (SPA)**, the system manages the complete lifecycle of customer support tickets across five distinct workflow states (`New`, `In Progress`, `Waiting for Customer`, `Resolved`, `Closed`).

A key technical highlight is its **Hybrid Dual-Mode AI Engine**:
1. When connected to the cloud, it leverages **Google Gemini 1.5 Flash** via RESTful APIs for natural language classification and contextual response generation.
2. In offline or zero-configuration environments, it seamlessly defaults to an integrated **Rule-Based NLP Demo Engine** capable of keyword parsing, urgency scoring, priority recommendations, and automated response drafting.

The system incorporates an interactive **Kanban Board** with drag-and-drop workflow progression, an **Analytics Dashboard** powered by Chart.js for real-time SLA monitoring, multi-factor search and filtering, and an automated audit trail for agent collaboration. The project achieves 100% test pass rates and zero-configuration local execution.

---

## 2. PROBLEM STATEMENT

In conventional customer support environments:
- **Triage Bottlenecks:** Customer tickets sit in unassigned queues awaiting human review before priority and category assignment.
- **Inconsistent Response Quality:** Different agents draft varying answers without standard SLA guidelines, leading to repetitive questions for common inquiries.
- **Workflow Opacity:** Customers and team leads struggle to visualize ticket distribution across lifecycle stages in real time.
- **Setup Complexity:** Many enterprise ticketing frameworks require complex external infrastructure, making them inaccessible for lightweight deployment and academic evaluation.

**Proposed Solution:** A unified, lightweight, yet enterprise-ready customer support platform that combines deterministic workflow rules, real-time analytics, automated data seeding, and AI assistance into a single self-contained application.

---

## 3. PROJECT OBJECTIVES

1. **End-to-End Ticket Lifecycle:** Build a robust backend to create, read, update, and resolve support requests with strict status validation and audit history.
2. **Interactive Visual Workflow:** Implement a responsive Kanban board allowing agents to transition tickets across states effortlessly.
3. **Hybrid AI Integration:** Develop a dual-mode artificial intelligence pipeline that classifies incoming ticket text, determines appropriate SLA urgency, and drafts empathetic response templates.
4. **Data Visualization:** Build a live analytics dashboard providing ticket distribution insights by status, priority, and category.
5. **Zero-Friction Local Execution:** Employ an embedded H2 database with pre-configured schemas and realistic demo data so the system runs immediately upon launch.
6. **Academic Rigor & Portability:** Structure code adhering to industry design patterns (Controller-Service-Repository DTO pattern) with full unit and integration test coverage.

---

## 4. SYSTEM REQUIREMENTS

### 4.1 Software Requirements
- **Operating System:** Cross-platform (macOS, Windows 10/11, Ubuntu/Linux)
- **Runtime Environment:** Java Development Kit (JDK 17 or higher)
- **Application Framework:** Spring Boot 3.3.x (Spring Web, Spring Data JPA, Spring Validation)
- **Database Engine:** H2 In-Memory Database (Default) / MySQL 8.x (Optional Production Profile)
- **Build Automation:** Apache Maven 3.9+ / Maven Wrapper (`mvnw`)
- **Frontend Technologies:** HTML5, CSS3 (Modern Flexbox & Grid), Vanilla JavaScript (ES6+), Chart.js 4.4+
- **External API:** Google Gemini API (`gemini-1.5-flash`)

### 4.2 Hardware Requirements
- **Processor:** Dual-Core 2.0 GHz or higher (x86_64 or ARM64 / Apple Silicon)
- **Memory (RAM):** Minimum 4 GB (8 GB recommended)
- **Storage:** Minimum 500 MB free disk space

---

## 5. SYSTEM ARCHITECTURE

The application adopts a **Tiered Architecture (Layered Model)** ensuring high cohesion and low coupling:

```mermaid
flowchart TD
    subgraph PresentationTier ["1. Presentation Layer (Browser)"]
        UI["Modern Responsive UI\n(HTML5 / CSS3 / Vanilla JS)"]
        Kanban["Drag & Drop Kanban Board"]
        Dash["Chart.js Real-Time Visuals"]
        Modal["AI Auto-Classification & Reply Modal"]
    end

    subgraph ServiceTier ["2. Application / Backend Layer (Spring Boot 3)"]
        API["REST API Layer\n(TicketController, DashboardController, AiController)"]
        Auth["DTO Validation & Global Exception Handler"]
        Logic["Service Layer\n(TicketService, DashboardService)"]
        AI["Hybrid AI Support Service\n- Mode A: Gemini 1.5 Flash Live API\n- Mode B: Rule-Based NLP Demo Engine"]
    end

    subgraph DataTier ["3. Data Persistence Layer"]
        JPA["Spring Data JPA / Hibernate ORM"]
        Repo["TicketRepository & CommentRepository"]
        H2DB[("Embedded H2 Database\n(In-Memory / File Persistent)")]
        MySQLDB[("MySQL Database\n(Production Profile)")]
    end

    UI --> API
    Kanban --> API
    Dash --> API
    Modal --> API
    API --> Auth
    Auth --> Logic
    Logic --> AI
    Logic --> Repo
    Repo --> JPA
    JPA --> H2DB
    JPA -.->|Config Switch| MySQLDB
```

---

## 6. DATABASE DESIGN & DATA MODEL

### 6.1 Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    TICKETS ||--o{ COMMENTS : "contains (1 to Many)"
    
    TICKETS {
        bigint id PK "Auto Increment"
        varchar ticket_number UK "Unique TCK-XXXX"
        varchar customer_name "Customer Full Name"
        varchar customer_email "Customer Contact Email"
        varchar subject "Ticket Subject Summary"
        text description "Detailed Issue Description"
        varchar category "TECHNICAL, BILLING, etc."
        varchar priority "LOW, MEDIUM, HIGH, CRITICAL"
        varchar status "NEW, IN_PROGRESS, etc."
        varchar assigned_agent "Alex Rivera, etc."
        text resolution_notes "Root cause & resolution"
        double ai_confidence "Confidence score"
        varchar ai_category_suggestion "AI Category"
        varchar ai_priority_suggestion "AI Priority"
        timestamp created_at "Created Timestamp"
        timestamp updated_at "Last Updated Timestamp"
        timestamp resolved_at "Resolution Timestamp"
    }

    COMMENTS {
        bigint id PK "Auto Increment"
        bigint ticket_id FK "References TICKETS(id)"
        varchar author_name "Author Name"
        varchar author_role "CUSTOMER, AGENT, SYSTEM"
        text message "Comment or note message"
        boolean internal_note "Agent Internal Note Flag"
        timestamp created_at "Created Timestamp"
    }
```

### 6.2 Normalization
The database schema satisfies **Third Normal Form (3NF)**:
1. **1NF:** All attributes are atomic. No multi-valued fields exist.
2. **2NF:** All non-key attributes are fully dependent on the primary keys.
3. **3NF:** No transitive dependencies exist. Audit comments are decoupled into a dedicated table with an explicit foreign key relationship and cascading lifecycle management.

---

## 7. DETAILED IMPLEMENTATION MODULES

### 7.1 Ticket Lifecycle Management (`TicketService.java`)
- Generates human-friendly ticket numbers (`TCK-1001`, `TCK-1002`).
- Enforces state transitions: `NEW` → `IN_PROGRESS` → `WAITING_FOR_CUSTOMER` → `RESOLVED` → `CLOSED`.
- Automatically logs audit comments upon status modifications or agent reassignments.
- Captures resolution timestamps when tickets reach `RESOLVED` or `CLOSED`.

### 7.2 Hybrid Dual-Mode AI Support Engine (`AiSupportService.java`)
- **Cloud Mode (Gemini 1.5 Flash):** Interacts with Google's generative language API using structured JSON schema prompts to classify intent, recommend priority, and draft tailored replies.
- **Rule-Based Demo Mode (Deterministic NLP):**
  - Parses subject and description against curated keyword vocabularies across 5 categories (`TECHNICAL`, `BILLING`, `ACCOUNT_ACCESS`, `FEATURE_REQUEST`, `GENERAL_INQUIRY`).
  - Scans for urgency indicators (`outage`, `production down`, `security breach`, `urgent`, `asap`) to assign priority (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
  - Generates personalized customer responses with dynamic placeholders, troubleshooting steps, and SLA commitments.
  - Transparently indicates active mode and confidence scores in the user interface.

### 7.3 Real-Time Dashboard & Analytics (`DashboardService.java`)
- Aggregates system metrics into lightweight Data Transfer Objects (`DashboardStatsDto`).
- Computes distribution maps across Status, Priority, and Category for Chart.js rendering without performance lag.

### 7.4 Interactive Frontend User Experience (`index.html`, `styles.css`, `app.js`)
- Single Page Application (SPA) architecture with view tab switching (Dashboard, Kanban, All Tickets).
- HTML5 Drag-and-Drop Kanban columns with one-click advance status controls.
- Real-time debounced search bar and multi-field dropdown filters.
- Responsive modal forms with real-time AI auto-classification and one-click response copy.

---

## 8. TESTING & QUALITY ASSURANCE

### 8.1 Automated Unit & Integration Tests
The project includes automated JUnit 5 and Spring Boot integration tests verifying:
1. **Application Context Initialization:** Confirms all dependency injection beans and repositories wire up cleanly.
2. **AI Classification Accuracy:** Verifies billing and critical outage inputs correctly yield `BILLING` and `TECHNICAL` classifications with high confidence.
3. **End-to-End Ticket Persistence & Search:** Creates a ticket through `TicketService`, queries it with the keyword search pipeline, and verifies data integrity.

### 8.2 Test Execution Results
All test suites passed successfully with zero failures and zero regressions.

---

## 9. LIMITATIONS & CHALLENGES OVERCOME

| Challenge Encountered | Solution Implemented |
|-----------------------|----------------------|
| **Dependency on External AI APIs** | Built the High-Accuracy Rule-Based NLP Demo Engine as a transparent, zero-latency fallback ensuring 100% offline availability for demonstrations and grading. |
| **Complex Database Setup for Evaluators** | Utilized embedded H2 with in-memory persistence and web console enabled, allowing anyone to evaluate the system with a single command without installing MySQL. |
| **Dual View Synchronization** | Synchronized state between the Kanban board, Table view, and Dashboard charts through a centralized JavaScript application controller. |

---

## 10. FUTURE SCOPE & ENHANCEMENTS

1. **Email / Webhook Notifications:** Integrating Spring Boot Mail or SendGrid to send real-time email notifications to customers on status updates.
2. **Customer Satisfaction (CSAT) Surveys:** Prompting customers for 1-5 star ratings after ticket resolution.
3. **Multi-Agent Role-Based Access Control (RBAC):** Implementing Spring Security with JWT tokens for granular Agent, Supervisor, and Administrator roles.
4. **Vector Database & RAG Search:** Enhancing the AI response generator by connecting historical resolution notes to a vector database (e.g., Pinecone or pgvector) for Retrieval-Augmented Generation.

---

## 11. CONCLUSION

The **AI-Powered Customer Support Ticketing System** successfully demonstrates a complete full-stack web application integrating contemporary enterprise backend practices with practical artificial intelligence. By combining automated classification, drag-and-drop workflow visualization, and responsive design, the platform streamlines customer service operations while maintaining clean architectural principles suitable for academic excellence.
