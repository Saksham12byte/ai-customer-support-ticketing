# 🧪 Comprehensive Testing Guide & Test Cases

## AI-Powered Customer Support Ticketing System

This document outlines the complete testing strategy, including automated unit and integration tests, end-to-end manual test cases, and verification data for the SkillWallet Capstone evaluation.

---

## 1. Automated Test Suite (`TicketingApplicationTests.java`)

The automated test suite runs during Maven build execution (`./mvnw test`):

| Test Method | Category | Verification Objective | Expected Result |
|-------------|----------|------------------------|-----------------|
| `contextLoads()` | Sanity / Integration | Verifies Spring Boot ApplicationContext, `TicketService`, and `AiSupportService` beans initialize without errors. | Context initializes; beans are not null. |
| `testAiClassificationForBilling()` | Unit / NLP Engine | Verifies that billing-related text ("Charged twice for my monthly subscription") triggers category `BILLING`, confidence $\ge 0.70$, and customer name in drafted reply. | Category is `BILLING`; drafted response contains customer's name. |
| `testAiClassificationForOutage()` | Unit / NLP Engine | Verifies that critical outage keywords ("System down - Production outage emergency") classify as `TECHNICAL` and priority `CRITICAL` with urgency flag set. | Category is `TECHNICAL`; priority is `CRITICAL`; `isUrgent == true`. |
| `testTicketCreationAndSearch()` | Integration / DB | Verifies end-to-end ticket creation via `TicketService`, database save, system comment audit generation, and retrieval via keyword search. | Ticket receives auto-generated ID & number; search returns created ticket. |

---

## 2. End-to-End Manual Test Cases (Feature Verification)

### Test Case TC-01: Application Launch & Auto-Seeding
- **Objective:** Verify system boot and initial demo data population.
- **Steps:**
  1. Launch app with `./mvnw spring-boot:run`.
  2. Navigate to `http://localhost:8080`.
- **Expected Result:**
  - Header displays "SkillWallet SupportDesk" and AI Mode badge.
  - Dashboard shows 8 pre-seeded tickets across New, In Progress, Waiting for Customer, Resolved, and Closed.
  - Status Donut Chart, Priority Bar Chart, and Category Chart render properly.

---

### Test Case TC-02: Ticket Submission with Form Validation
- **Objective:** Verify client-side and backend validation rules.
- **Steps:**
  1. Click "+ New Ticket" button in header.
  2. Leave Name and Subject blank and click "Create Ticket".
  3. Enter Name: `Maya Lin`, Email: `invalid-email`, Subject: `Hi`.
- **Expected Result:**
  - HTML5 and server-side validation block submission.
  - Informative validation errors instruct the user to provide valid email and min length subject.

---

### Test Case TC-03: Real-Time AI Auto-Classification
- **Objective:** Verify dual-mode AI analysis before ticket submission.
- **Steps:**
  1. In the New Ticket modal, enter:
     - **Subject:** `Production database timeout on checkout page`
     - **Description:** `Customers are seeing 500 error when submitting payments. Production is down and needs urgent attention.`
  2. Click **AI Auto-Classify** button.
- **Expected Result:**
  - Button shows loading state ("Analyzing...").
  - Dropdowns auto-update: Category becomes `Technical Support`, Priority becomes `Critical`.
  - AI Banner displays high confidence ($\ge 90\%$) with rationale matching the keywords.

---

### Test Case TC-04: Kanban Board Workflow & State Transitions
- **Objective:** Verify 5-stage status workflow in Kanban board.
- **Steps:**
  1. Click **Kanban Workflow** tab.
  2. Locate a ticket in `NEW` column (e.g., `TCK-1003`).
  3. Click the `→` advance button, or drag the card into `IN_PROGRESS`.
- **Expected Result:**
  - Card shifts to `IN_PROGRESS` column.
  - Column counter updates immediately.
  - Toast notification appears: `Ticket #TCK-1003 moved to IN_PROGRESS`.
  - Database records an automatic audit entry in the ticket's comment history.

---

### Test Case TC-05: Real-Time Search, Filtering & Sorting
- **Objective:** Verify multi-factor search and filter queries.
- **Steps:**
  1. Switch to **All Tickets** tab.
  2. Type `refund` in search bar.
  3. Change Priority dropdown to `High`.
  4. Change Sort to `Oldest First`.
- **Expected Result:**
  - Table instantly filters down to tickets matching the search keyword and priority.
  - Clearing the search restores full ticket list.

---

### Test Case TC-06: Ticket Details & AI-Assisted Response Drafting
- **Objective:** Verify detail view and AI response generation.
- **Steps:**
  1. Click on `TCK-1002` (Double charge invoice).
  2. In the detail modal, click **Generate AI Reply**.
  3. Click **Copy into Reply Box**.
  4. Click **Send Reply**.
- **Expected Result:**
  - AI generates a polite, contextual draft tailored to Elena Rostova regarding invoice `#INV-9281`.
  - Text copies into comment box.
  - Posting the comment appends it to the activity timeline with timestamp and agent attribution.

---

### Test Case TC-07: Internal Agent Note Logging
- **Objective:** Verify private internal notes for support team collaboration.
- **Steps:**
  1. In ticket detail modal, type: `Contacted payment processor. Batch transaction trace #99482 verified.`
  2. Check the box: **Internal Agent Note (Not visible to customer)**.
  3. Click **Send Reply**.
- **Expected Result:**
  - Comment renders with distinctive dashed amber border and `[Internal Note]` label.

---

### Test Case TC-08: Resolution & Ticket Closure
- **Objective:** Verify SLA resolution notes and resolution timestamp logging.
- **Steps:**
  1. In ticket controls on the right panel, set Status to `Resolved`.
  2. Set Assigned Agent to `Alex Rivera`.
  3. Enter Resolution Notes: `Issue investigated and resolved after verifying customer bank statement.`
  4. Click **Save Ticket Changes**.
- **Expected Result:**
  - Ticket status updates to `Resolved`.
  - Audit comment logged: `Ticket updated: Status changed from In Progress to Resolved. Assigned agent updated to Alex Rivera.`
  - `resolvedAt` timestamp is recorded in database.

---

### Test Case TC-09: H2 Web Console Verification
- **Objective:** Verify database schema and relational persistence directly.
- **Steps:**
  1. Open browser to `http://localhost:8080/h2-console`.
  2. Set JDBC URL to `jdbc:h2:mem:ticketdb`. Username `sa`, empty password.
  3. Run: `SELECT ticket_number, customer_name, status, priority FROM tickets;`
- **Expected Result:**
  - Table displays all saved tickets matching the frontend state.

---

## 3. Sample API Payloads for Postman / cURL Testing

### 3.1 Create Ticket (`POST /api/tickets`)
```json
{
  "customerName": "Samantha Reed",
  "customerEmail": "samantha.reed@example.com",
  "subject": "Unable to receive SMS two-factor authentication code",
  "description": "I have been locked out of my developer account since morning because the 2FA SMS code does not reach my registered phone number.",
  "category": "ACCOUNT_ACCESS",
  "priority": "HIGH"
}
```

### 3.2 AI Analysis Request (`POST /api/ai/analyze`)
```json
{
  "customerName": "Marcus Vance",
  "subject": "System crash during bulk CSV export",
  "description": "Whenever we attempt to export more than 10,000 records, the application crashes with 500 error."
}
```

**Expected Response:**
```json
{
  "suggestedCategory": "TECHNICAL",
  "suggestedPriority": "HIGH",
  "confidenceScore": 0.90,
  "reasoning": "Rule-Based Engine matched 2 context keywords for Technical Support with HIGH urgency markers.",
  "mode": "RULE_BASED_DEMO",
  "urgent": true
}
```
