# AuralisOS — Dual-Market Agentic AI Operations, Vision Metrology & Tokenized Commerce Platform

AuralisOS is a category-defining dual-market enterprise operations platform engineered for Bangladesh's manufacturing and SME ecosystem (domestic wedge) and scalable to international supply chains (global scale). It combines Gemini-powered computer-vision defect inspection, a bilingual (Bangla/English) voice-to-action ERP agent, dual-rail tokenized payments (bKash Agreement/Auto-Pay + Stripe USD), and iDEA / Startup Bangladesh Limited governance telemetry, delivered through a native Android application paired with a responsive cross-device Web companion portal.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions incorporate the choices confirmed in Phase 1 and establish production defaults for the dual-market system:

- **Core Blueprint (Confirmed)**: Prioritizing Blueprint 1/2 Hybrid ("AuralisOS") encompassing Computer-Vision Metrology & Defect Grid (garment stitching, electronic solder, fabric weave inspection) plus a Bangla/English Voice-to-Action ERP Agent (autonomous invoice generation, inventory ledgers, CNC/sewing station rescheduling).
- **Dual Platform Delivery (Confirmed)**: High-performance native Android application (Jetpack Compose, Material 3, CameraX/Gemini multimodal) integrated with an embedded responsive Web companion portal that can be viewed in-app or accessed by any browser on the local network/desktop via built-in web serving and live QR code connectivity.
- **Monetization & Grant Telemetry (Confirmed)**: Dual payment rails featuring bKash Tokenized Checkout (v1.2.0-beta Agreement creation mode "0011", persistent agreementId binding, scheduled 30-day recurring auto-pay) alongside Stripe USD SaaS billing, coupled with an interactive iDEA PMC (Innovation Design and Entrepreneurship Academy) / SBL (Startup Bangladesh Ltd) KPI telemetry dashboard.
- **Bilingual Interface**: Instant one-tap toggle between English and conversational Bangla (বাংলা) across all vocal prompts, defect overlays, voice command recognition, and audit exports.

---

## 1. Overview & Core Concept

### What It Does
AuralisOS bridges the gap between floor-level factory/merchant operations and global enterprise compliance:
1. **Multimodal Vision Metrology**: Uses camera frames to identify structural defects (e.g., stitch deviations, seam puckering, color variance, fabric tear, PCB solder bridging) with bounding boxes, confidence scoring, severity ratings, and automated machine rerouting recommendations.
2. **Bilingual Voice ERP**: Allows factory line supervisors and Dokandars to speak natural voice commands in Bangla or English (e.g., *"লাইন ৩ এর জন্য ৫০ গজ ডেনিম রিস্টক অর্ডার করো"* or *"Create invoice for 500 polo shirts with 5% discount"*), triggering agentic tool execution with structured JSON state updates.
3. **Scope-3 & Supply Chain Oracle**: Generates predictive bottleneck alerts and Scope-3 carbon/compliance audit cards formatted for multinational retail buyers (US/EU/Asia).
4. **Dual-Market Payments**: Seamless tokenized subscription agreements via bKash (simulating single-flight cached token grants, SMS OTP + PIN agreement binding, and scheduled recurring debits) and Stripe multi-currency checkout.
5. **iDEA & SBL Governance Suite**: Tracks Daily Active Factories, Defect Reduction %, Gross Merchandise Value (GMV), Net Revenue Retention (NRR), and LTV:CAC ratios to satisfy government pre-seed grant tranches and institutional VC due diligence.
6. **Cross-Device Web Portal**: A responsive web portal accessible in-app and across any laptop, tablet, or browser connected to the local network for desktop monitoring and executive reporting.

### Target Audience & Persona
- **Factory Supervisors & RMG Line Managers (Bangladesh)**: Need quick zero-friction camera inspection and spoken voice dispatch without complex keyboard data entry.
- **Micro-Merchants / Dokandars (Bangladesh)**: Seeking voice-automated inventory tracking, instant digital invoices, and bKash auto-pay without manual OTP entry on every renewal.
- **International Enterprise Retail Buyers (Global)**: Requiring live Scope-3 ESG audit proofs, station health telemetry, and multi-currency Stripe billing.
- **Grant Auditors & Investors (iDEA / SBL / Seed VCs)**: Auditing KPI progress, unit economics, and governance compliance before releasing subsequent tranches.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Vision Inspection Flow**:
   - User points device camera at a garment, fabric, or component (or selects high-res defect sample / camera upload).
   - Gemini Vision analyzes the frame in real time, generating an interactive HUD with colored bounding boxes, defect classification (e.g., "Broken Stitching - 94% confidence"), and an agentic recommendation ("Reroute Station 4 to Line 7 backup sewing machine").
2. **Voice ERP Command Flow**:
   - Operator taps the Bangla/English microphone button and speaks or selects a common voice prompt.
   - Voice agent processes audio transcript through Gemini Function Calling tools, automatically generating an invoice, updating inventory ledgers, or issuing supplier restock orders.
3. **Dual Payment & Subscription Flow**:
   - Switch between Domestic Rail (BDT / bKash Tokenized) and Global Rail (USD / Stripe).
   - In bKash mode: User initiates 1-click agreement binding (Mode 0011), enters simulated wallet number, OTP, and PIN once to generate a persistent `agreementId`, activating 30-day recurring auto-pay.
   - In Stripe mode: Enterprise SaaS tiered checkout with 10% shared-savings performance calculation.
4. **iDEA / SBL Grant Telemetry Flow**:
   - Executive dashboard presenting milestone progress against iDEA SMART KPIs (BDT 10 Lakh pre-seed grant validation) and SBL governance metrics with real-time filtering and exportable JSON audit reports.
5. **Web Companion Portal Flow**:
   - Users can tap the "Web Portal" tab to view the live responsive web interface directly inside the app, or open the local IP/port on any desktop browser for multi-screen operations.

### Visual Identity & Theme
- **Aesthetic Direction**: High-contrast modern industrial & fintech aesthetic. Precision telemetry, clean typography, dark-slate background with energetic safety amber and teal accents.
- **Color Palette**:
  - `Background`: Obsidian Slate (`#0B0F19`) and Deep Navy Card Surface (`#131B2E`)
  - `Primary / Accent`: Precision Teal (`#00E5FF` / `#00B4D8`)
  - `Warning / Industrial`: Safety Amber (`#FF9100`)
  - `Success / Domestic Rail`: bKash Pink (`#E2136E`) & Emerald Green (`#00E676`)
  - `Text`: Ultra-crisp High-Contrast White (`#F8FAFC`) and Muted Steel (`#94A3B8`)
- **Typography & Hierarchy**:
  - High-density monospace and crisp sans-serif headings for industrial metrology metrics.
  - Large Bangla-friendly legible typography with generous line heights.
- **Interactive Feedback**:
  - Real-time pulse animations on active inspection bounding boxes.
  - Haptic touch feedback on voice trigger and wallet binding.
  - Animated progress bars for iDEA grant disbursement thresholds.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Dual-Market Hybrid Feature Suite**
  - *Chosen Approach*: Unify Factory Metrology (Blueprint 1) and Voice SME ERP (Blueprint 2) into a cohesive industrial OS with a role/mode switcher ("Factory Floor" vs "Merchant Dokan").
  - *Why*: Satisfies both high-ticket enterprise buyer needs and high-volume local merchant adoption as outlined in the strategic report.
- **Decision 2: Realistic bKash Tokenized Checkout Simulation**
  - *Chosen Approach*: Implement full lifecycle state machine matching bKash v1.2.0-beta API specifications: single-flight token caching, Mode "0011" Create Agreement, Execute Agreement, and subsequent PIN-only recurring auto-debit without OTP repeats.
  - *Why*: Eliminates the #1 checkout drop-off cause in Bangladesh SaaS without requiring active sandbox credentials to demonstrate the complete architecture.
- **Decision 3: Embedded + Network-Accessible Web Companion Portal**
  - *Chosen Approach*: Package the complete responsive HTML5/Tailwind/React-grade Web portal directly into Android assets and serve it via an internal Android web engine and WebView, with network sharing capability.
  - *Why*: Delivers seamless accessibility across all devices, mobile phones, tablets, and desktop browsers without requiring external cloud deployment during local testing.
- **Decision 4: Local Room Database with Seed Data**
  - *Chosen Approach*: Leverage Android Room for instant offline-first reliability on factory floors, storing inspection logs, voice ERP actions, tokenized payment agreements, and production nodes.

---

## 4. Technical Architecture & Data Strategy

### Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        AuralisOS Architecture                          │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   ┌───────────────────────────┐      ┌─────────────────────────────┐   │
│   │   Native Android App      │      │    Responsive Web Portal    │   │
│   │   (Jetpack Compose M3)    │      │    (Desktop & Tablet UI)    │   │
│   └─────────────┬─────────────┘      └──────────────┬──────────────┘   │
│                 │                                   │                  │
│                 ▼                                   ▼                  │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │               AuralisOS Presentation & State Layer             │   │
│   │   - MainViewModel (Bilingual State: EN / বাংলা)                 │   │
│   │   - VisionInspectionState (Bounding boxes, confidence, action)  │   │
│   │   - VoiceAgentState (Speech-to-tool, JSON function calling)     │   │
│   │   - PaymentEngineState (bKash Agreement Mode 0011 + Stripe)    │   │
│   │   - TelemetryState (iDEA Pre-Seed & SBL Governance KPIs)       │   │
│   └───────────────────────────────┬────────────────────────────────┘   │
│                                   │                                    │
│                 ┌─────────────────┴─────────────────┐                  │
│                 ▼                                   ▼                  │
│   ┌─────────────────────────────┐     ┌────────────────────────────┐   │
│   │   Gemini AI Multimodal Core │     │   Local Persistence Layer  │   │
│   │   - Vision Metrology API    │     │   - Room Database          │   │
│   │   - ERP Tool / Function Call│     │   - ProductionNodeDao      │   │
│   │   - Scope-3 ESG Oracle      │     │   - InspectionLogDao       │   │
│   └─────────────────────────────┘     │   - PaymentAgreementDao    │   │
│                                       │   - VoiceActionDao         │   │
│                                       └────────────────────────────┘   │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Model & State Entities
- **ProductionNode**: Node ID, name (e.g., Sewing Line 4, CNC Milling B), status (Active, Degraded, Stalled), throughput rate, carbon load index.
- **AIInspectionLog**: Timestamp, inspection target (Garment, Seam, Circuit), defect detected (Yes/No), defect label, confidence score, bounding box coordinates, recommended corrective action.
- **VoiceAgentAction**: Spoken command, detected language (Bangla/English), resolved intent, tool invoked (CreateInvoice, RestockSupplier, RerouteStation), structured JSON payload, execution status.
- **TokenizedPaymentAgreement**: Agreement ID, customer MSISDN (e.g., 017xxxxxxxx), mode ("0011"), status (BOUND, ACTIVE, RENEWAL_PENDING), recurring cycle (THIRTY_DAYS), token cache expiry timestamp.
- **TelemetryKpiRecord**: Active factories, defect reduction rate (%), GMV processed, NRR, LTV:CAC, iDEA PMC tranche qualification flag.

---

## Next Steps Upon Plan Approval

1. Configure application identity, names, and custom launcher assets.
2. Build the Room persistence database and repositories for nodes, inspections, voice actions, and payment agreements.
3. Implement the Gemini Multimodal Vision and Bangla/English Voice ERP engine with structured tool routing.
4. Build the bKash Tokenized Checkout (v1.2.0-beta) and Stripe payment orchestration engine.
5. Create the complete Jetpack Compose user interface featuring bilingual navigation, Vision Metrology HUD, Voice ERP simulator, Payment Manager, and iDEA/SBL Governance Telemetry.
6. Build and bundle the responsive cross-platform Web companion portal for desktop and multi-device browser access.
7. Verify build compilation and functionality.
