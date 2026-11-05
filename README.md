# BAZARLY (Dokandar / AuralisOS)
### Sub-Millimeter Garment Optical Metrology, Bilingual Voice ERP & Tokenized Settlement

[![Node.js Tests](https://img.shields.io/badge/Node.js-v26%20Tested-34D399?style=flat-square&logo=node.js)](package.json)
[![Android](https://img.shields.io/badge/Android-API%2024%2B%20%7C%20Compose-0D0D0F?style=flat-square&logo=android)](app/build.gradle.kts)
[![Firebase](https://img.shields.io/badge/Firebase-AI%20Logic%20%7C%20Firestore-FFA000?style=flat-square&logo=firebase)](firebase.json)
[![bKash](https://img.shields.io/badge/bKash-Mode%200011%20Tokenized-E2136E?style=flat-square)](server.js)
[![Stripe](https://img.shields.io/badge/Stripe-Global%20USD%20Billing-635BFF?style=flat-square)](server.js)

---

## 1. Executive Summary & Product Architecture

**Bazarly** is an AI-native industrial and merchant operating system architected for a high-conviction dual-market opportunity:
1. **Domestic Bangladesh Market (The High-Density Sandbox)**:
   - **RMG Textile & Apparel Plants**: Sub-millimeter telecentric optical metrology analyzing fabric weave, seam tension, and skipped stitches down to 0.018 mm RMS tolerance. Provides autonomous machine rerouting to backup stations when line bottlenecks or defects occur.
   - **Micro-Merchants & Retail SMEs (Dokandar)**: Voice-first Bangla (`bn-BD`) and English (`en-US`) ERP copilot powered by Gemini 3.8 Flash function calling. Generates sales invoices, purchase orders for yarn and fabric restocks, and reconciles MFS transaction ledgers without manual keyboard entry.
   - **MFS Payment Engineering**: Single-flight cached bKash Tokenized Checkout API (`Mode 0011` / `CREATE_AGREEMENT`) eliminating repetitive 3-step checkout friction (wallet + OTP + PIN), allowing seamless 30-day recurring auto-pay.
2. **Global Enterprise Market**:
   - Cross-border Scope-3 carbon abatement auditing and supply chain risk forecasting for EU and US buyers (complying with EU CSDDD and US UFLPA).
   - Multi-currency global B2B SaaS licensing via Stripe USD with 10% performance-based shared savings on defect and utility reduction.

---

## 2. Repository Structure

```
Bazarly/
├── api/
│   └── index.js                        # Vercel serverless function router
├── app/                                # Native Android Application
│   ├── build.gradle.kts                # Android build script (Compose, Room, Firebase AI)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # Camera, Audio & Network permissions
│       │   ├── assets/web/index.html   # Synchronized offline web console
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt     # App entry, edge-to-edge, auth binding
│       │   │   ├── ai/
│       │   │   │   └── GeminiAgentEngine.kt       # Gemini 3.8 Flash structured JSON schemas
│       │   │   ├── auth/
│       │   │   │   ├── AppCheckHelper.kt          # Play Integrity / ReCAPTCHA Enterprise
│       │   │   │   └── GoogleAuthHelper.kt        # Google Credential Manager flow
│       │   │   ├── data/
│       │   │   │   ├── local/AppDatabase.kt       # Room SQLite persistence
│       │   │   │   ├── model/DataModels.kt        # Inspection, VoiceAction, PaymentAgreement
│       │   │   │   └── repository/                # Offline-first Firestore + Room repository
│       │   │   ├── payments/
│       │   │   │   └── PaymentEngines.kt          # bKash 0011 single-flight & Stripe simulator
│       │   │   ├── ui/
│       │   │   │   ├── screens/                   # Poster landing & sub-console drawers
│       │   │   │   └── theme/                     # Achromatic + Signal Accent design tokens
│       │   │   └── web/
│       │   │       └── WebCompanionServer.kt      # Embedded HTTP daemon on port 8080
│       └── test/java/                  # Unit and Firestore repository rule tests
├── gradle/
│   ├── libs.versions.toml              # Version catalog for Android dependencies
│   └── wrapper/
│       └── gradle-wrapper.properties   # Gradle 9.3.1 distribution config
├── public/                             # Standalone Web Application
│   ├── index.html                      # Noir + Emerald glassmorphic console (2D & 3D)
│   └── manifest.json                   # Web app manifest
├── scripts/
│   ├── enhance-web.js                  # 3D spatial canvas & synthesizer builder
│   └── sync-web.js                     # Asset synchronization script
├── test/
│   └── api.test.js                     # Node.js automated test suite (9 passing tests)
├── .env.example                        # Firebase App Check attestation environment notes
├── firebase.json                       # Firebase Firestore & Hosting emulator config
├── firestore.rules                     # Zero-trust user-scoped security rules
├── firestore.test.js                   # Node unit tests for Firestore rules
├── gradlew & gradlew.bat               # Gradle wrapper startup scripts
├── package.json                        # Node project manifest & test scripts
├── server.js                           # Standalone zero-dependency Node.js HTTP backend
├── vercel.json                         # Vercel deployment configuration
└── netlify.toml                        # Netlify deployment configuration
```

---

## 3. Web Application: Features & Capabilities

The web console (`public/index.html`) is built in an **AI-native Noir & Emerald Glassmorphism** aesthetic with authentic operational mechanics:

1. **Dual-Mode Optical Metrology Stage**:
   - **2D Micro-Matrix Mode**: Live canvas rendering physical warp/weft textile threads and lockstitch seam line. Computes horizontal/vertical luminance gradients and sub-millimeter RMS pitch variance.
   - **3D Spatial Hologram Mode**: Real-time 60 FPS isometric 3D wireframe projection with laser sweep illumination and dynamic topological z-axis displacement on defect spikes.
   - **Click-to-Perturb**: Clicking anywhere on the canvas dynamically injects physical seam drift (`0.042 mm RMS`), triggering autonomous machine reroute recommendations.
   - **File Upload & Live Camera**: Drag-and-drop fabric photos or stream live video from your device camera.
2. **Bilingual Voice ERP Agent**:
   - Integrated Web Speech API (`bn-BD` and `en-US`) with speech recognition pulse.
   - Gemini Function Calling tool dispatch parser (`CreateInvoice`, `RestockSupplier`, `RerouteStation`, `LedgerAudit`).
   - Handles Bangla digits (`০-৯`) and BDT currency arithmetic.
3. **Dual-Rail Tokenized Checkout**:
   - **bKash Mode 0011**: 3-step interactive wallet binding (MSISDN `01723-994120` → 6-digit SMS OTP `123456` → 5-digit PIN).
   - Single-flight token cache refreshing 60s before 3,600s expiration to prevent rate-limit outages.
   - Persistent `AGR-BKASH-...` agreement binding with one-click recurring auto-pay execution without OTP.
   - **Stripe USD Global Billing**: Multi-currency escrow checkout with 10% Scope-3 shared-savings performance pricing.
4. **iDEA Project & Startup Bangladesh (SBL) Governance Telemetry**:
   - Interactive KPI dashboard tracking 18 active factories, 84 Dokans, 34.2% defect reduction, ৳1.24 Cr GMV, and 8,920 kg Scope-3 carbon saved.
   - Exportable governance audit dossier (`bazarly_idea_sbl_governance_audit.json`).
5. **Universal Android APK Distribution**:
   - Deterministic QR code generator, live APK size (`18.4 MB`), and SHA-256 integrity verification hash.

---

## 4. Getting Started

### Prerequisites
- **Web App**: Node.js v18+ (tested on Node v26)
- **Android App**: Android Studio Ladybug / Meerkat (JDK 17+, Android SDK 36)

### Running the Web Application Locally
1. Start the backend server:
   ```bash
   npm start
   # or
   node server.js
   ```
2. Open your browser at:
   ```
   http://127.0.0.1:8080
   ```

### Running the Automated Test Suite
Run the built-in test suite (all 9 tests execute in under 250ms with zero external dependencies):
```bash
npm test
```

### Synchronizing Web Updates to Android
Whenever modifying `public/index.html`, synchronize it into the Android embedded assets:
```bash
npm run sync:web
```

---

## 5. Deployment Guide

### Deploying to Vercel
Push the repository to GitHub and import it into Vercel. Vercel automatically uses `vercel.json`:
- `public/` is served as the static frontend.
- `/api/*` and `/download/*` requests are routed to the serverless function `api/index.js`.

### Deploying to Netlify
Connect the repository to Netlify. `netlify.toml` automatically configures `public/` as the publish directory and injects APK MIME headers for direct mobile downloads.

### Deploying to Firebase Hosting
```bash
firebase deploy --only hosting
```

---

## 6. Android App Architecture & Development

The Android app is built using modern Jetpack Compose and architecture best practices:
- **UI Layer**: Poster-scale technical landing page (`BazarlyPosterLandingScreen.kt`) with sub-console drawers (`VisionInspectionScreen.kt`, `VoiceErpScreen.kt`, `DualPaymentScreen.kt`, `GrantTelemetryScreen.kt`, `WebCompanionScreen.kt`).
- **Data Layer**: Room SQLite database (`AppDatabase.kt`) caching inspections, voice actions, and payment agreements for offline operation.
- **Cloud Layer**: Firestore real-time listeners and repository (`FirestoreAuralisRepository.kt`) scoped to the authenticated user's UID.
- **AI Engine**: `GeminiAgentEngine.kt` integrating Firebase AI Logic with structured JSON schemas and offline mathematical fallback.
- **Embedded Web Server**: `WebCompanionServer.kt` serving the web dashboard over localhost port 8080.

To build and run on Android:
```bash
./gradlew assembleDebug
```
Or open the project root in **Android Studio** and click **Run**.

---

## 7. Security & Compliance
- **Zero-Trust Rules**: `firestore.rules` enforces UID ownership on all collections (`/inspections`, `/voice_actions`, `/payment_agreements`) and strict schema validation.
- **App Check Attestation**: Validates device integrity via Play Integrity / ReCAPTCHA Enterprise before calling Firebase AI Logic.
- **Token Security**: bKash API credentials and token caches remain strictly server-side; user PINs and OTPs are never stored or logged.

---

## 8. License
Apache License 2.0. Copyright (c) 2026 Bazarly Contributors.
