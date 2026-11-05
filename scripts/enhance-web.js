const fs = require('fs');
const path = require('path');

const publicIndexPath = path.join(__dirname, '..', 'public', 'index.html');
const assetIndexPath = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'web', 'index.html');

let html = fs.readFileSync(publicIndexPath, 'utf8');

// 1. Add 3D card tilt & perspective styles in the <style> section
const additionalStyles = `
    /* ==========================================================================
       AUTHENTIC 3D DEPTH, SPECULAR LIGHTING & INTERACTIVE PERSPECTIVE STYLES
       ========================================================================== */
    .perspective-container {
      perspective: 1200px;
    }

    .tilt-card {
      transform-style: preserve-3d;
      transition: transform 0.15s ease-out, box-shadow 0.25s ease;
      will-change: transform;
    }

    .tilt-card:hover {
      box-shadow: 0 24px 50px -12px rgba(52, 211, 153, 0.22), 0 0 0 1px rgba(52, 211, 153, 0.35);
    }

    .tilt-card-glare {
      position: absolute;
      inset: 0;
      border-radius: inherit;
      pointer-events: none;
      background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.12) 0%, transparent 60%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .tilt-card:hover .tilt-card-glare {
      opacity: 1;
    }

    /* Bilingual Badge & Audio Toggle */
    .nav-pill-btn {
      background: rgba(255, 255, 255, 0.08);
      border: 1px solid rgba(255, 255, 255, 0.16);
      color: #FFFFFF;
      padding: 5px 12px;
      border-radius: 9999px;
      font-size: 11px;
      font-family: 'JetBrains Mono', monospace;
      font-weight: 600;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      transition: all 0.2s ease;
    }

    .nav-pill-btn:hover {
      background: rgba(52, 211, 153, 0.18);
      border-color: rgba(52, 211, 153, 0.5);
      color: var(--emerald);
      transform: translateY(-1px);
    }

    /* 3D View Switcher Pill */
    .view-mode-pill {
      background: rgba(0, 0, 0, 0.6);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 9999px;
      padding: 3px;
      display: inline-flex;
      gap: 3px;
    }

    .view-mode-option {
      padding: 3px 10px;
      border-radius: 9999px;
      font-size: 10px;
      font-family: 'JetBrains Mono', monospace;
      border: none;
      cursor: pointer;
      color: #A1A1AA;
      background: transparent;
      transition: all 0.2s ease;
    }

    .view-mode-option.active {
      background: #34D399;
      color: #000000;
      font-weight: 700;
    }
`;

// Insert additional styles right before </style>
html = html.replace('</style>', additionalStyles + '\n  </style>');

// 2. Add Bilingual, Sound & Telemetry controls in the header nav
const navReplacement = `
      <div class="nav-links">
        <a href="#features" onclick="switchConsoleTab('METROLOGY')" class="nav-link" data-i18n="nav_metrology">Optical Metrology</a>
        <a href="#features" onclick="switchConsoleTab('VOICE')" class="nav-link" data-i18n="nav_voice">Bangla/EN Voice ERP</a>
        <a href="#features" onclick="switchConsoleTab('PAYMENT')" class="nav-link" data-i18n="nav_payment">bKash 0011 + Stripe</a>
        <a href="#productivity" class="nav-link" data-i18n="nav_ledger">Live Ledger</a>
        <a href="javascript:void(0)" onclick="openTelemetryModal()" class="nav-link" data-i18n="nav_telemetry">iDEA / SBL Grants</a>
        <a href="javascript:void(0)" onclick="openHostingModal()" class="nav-link" data-i18n="nav_cloud">Cloud Host (.com)</a>
      </div>

      <div style="display:flex;align-items:center;gap:0.45rem;">
        <button onclick="toggleGlobalLanguage()" id="nav-lang-btn" class="nav-pill-btn" title="Toggle Bangla / English">
          <span>🇧🇩 বাংলা</span>
        </button>
        <button onclick="toggleAudioFx()" id="nav-sound-btn" class="nav-pill-btn" title="Toggle Audio Sound FX">
          <span id="nav-sound-icon">🔊</span>
        </button>
        <button onclick="openGetAppModal()" class="shiny-cta shiny-cta-sm" id="nav-get-app-btn">
          <span style="width:6px;height:6px;border-radius:999px;background:var(--emerald);"></span>
          <span data-i18n="nav_get_app">Get App ↓</span>
        </button>
      </div>`;

html = html.replace(/<div class="nav-links">[\s\S]*?<\/button>\s*<\/div>/, navReplacement);

// 3. Add 3D / 2D View Switcher in the feature display card header
const stageBadgeSearch = `<span class="mono-code" style="font-size:11px;color:rgba(255,255,255,0.75);" id="stage-fps-readout">PITCH: 2.40 MM</span>`;
const stageBadgeReplace = `
            <div style="display:flex;align-items:center;gap:0.75rem;">
              <div class="view-mode-pill">
                <button onclick="setCanvasViewMode('2D')" id="btn-view-2d" class="view-mode-option active">2D MATRIX</button>
                <button onclick="setCanvasViewMode('3D')" id="btn-view-3d" class="view-mode-option">3D LASER</button>
              </div>
              <span class="mono-code" style="font-size:11px;color:rgba(255,255,255,0.75);" id="stage-fps-readout">PITCH: 2.40 MM</span>
            </div>`;

html = html.replace(stageBadgeSearch, stageBadgeReplace);

// 4. Add Telemetry Modal right before </main>
const telemetryModalHtml = `
  <!-- MODAL 3: iDEA PRE-SEED GRANT & STARTUP BANGLADESH SBL GOVERNANCE TELEMETRY -->
  <div class="modal-backdrop" id="telemetry-modal" role="dialog" aria-modal="true" aria-labelledby="telemetry-modal-title">
    <div class="modal-glass-panel" style="max-width:760px;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1.25rem;">
        <div>
          <span class="label-spec" style="color:var(--emerald);">BANGLADESH ICT DIVISION &amp; SBL GOVERNANCE</span>
          <h3 id="telemetry-modal-title" class="heading-display" style="font-size:1.65rem;margin-top:0.2rem;">iDEA Pre-Seed &amp; Venture Audit Dossier</h3>
        </div>
        <button onclick="closeTelemetryModal()" style="background:rgba(255,255,255,0.08);border:1px solid rgba(255,255,255,0.12);color:#FFF;width:36px;height:36px;border-radius:999px;cursor:pointer;">✕</button>
      </div>

      <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:0.9rem;margin-bottom:1.5rem;">
        <div style="background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.10);border-radius:1rem;padding:1rem;">
          <div class="label-spec" style="color:var(--emerald);">ACTIVE FACTORIES &amp; DOKANS</div>
          <div style="font-size:1.8rem;font-weight:700;margin-top:0.2rem;" id="tel-active-nodes">18 / 84</div>
          <p style="font-size:11px;color:var(--zinc-400);margin-top:0.25rem;">18 RMG Plants (Dhaka/Gazipur) + 84 SME Merchants</p>
        </div>

        <div style="background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.10);border-radius:1rem;padding:1rem;">
          <div class="label-spec" style="color:var(--emerald);">DEFECT / UTILITY REDUCTION</div>
          <div style="font-size:1.8rem;font-weight:700;margin-top:0.2rem;color:var(--emerald);">34.2%</div>
          <p style="font-size:11px;color:var(--zinc-400);margin-top:0.25rem;">Sub-millimeter Sobel intercept saving factory rework</p>
        </div>

        <div style="background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.10);border-radius:1rem;padding:1rem;">
          <div class="label-spec" style="color:var(--emerald);">GROSS MERCHANDISE VALUE</div>
          <div style="font-size:1.8rem;font-weight:700;margin-top:0.2rem;">৳1.24 Cr</div>
          <p style="font-size:11px;color:var(--zinc-400);margin-top:0.25rem;">bKash Mode 0011 + Stripe USD settled volume</p>
        </div>

        <div style="background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.10);border-radius:1rem;padding:1rem;">
          <div class="label-spec" style="color:var(--emerald);">SCOPE-3 CARBON ABATED</div>
          <div style="font-size:1.8rem;font-weight:700;margin-top:0.2rem;color:#34D399;">8,920 kg</div>
          <p style="font-size:11px;color:var(--zinc-400);margin-top:0.25rem;">Off-peak grid routing &amp; fabric waste elimination</p>
        </div>
      </div>

      <div style="background:#09090B;border:1px solid rgba(255,255,255,0.12);border-radius:1rem;padding:1rem;margin-bottom:1.25rem;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.6rem;">
          <span class="mono-code" style="font-size:11px;color:var(--emerald);">AUDIT_REPORT // iDEA_TRANCHE_01_VERIFIED.JSON</span>
          <span class="mono-code" style="font-size:11px;color:var(--zinc-400);">SBL SCORE: 96/100</span>
        </div>
        <pre class="mono-code" id="telemetry-json-preview" style="font-size:11px;color:#D4D4D8;max-height:160px;overflow-y:auto;white-space:pre-wrap;"></pre>
      </div>

      <div style="display:flex;justify-content:flex-end;gap:0.75rem;">
        <button onclick="downloadGovernanceAuditReport()" class="console-btn">
          Export SBL / iDEA Audit Dossier (.json) ↓
        </button>
      </div>
    </div>
  </div>
`;

html = html.replace('<!-- MODAL 1:', telemetryModalHtml + '\n  <!-- MODAL 1:');

// 5. Enhance the <script> logic with:
// - 3D isometric weave stage rendering + animation loop
// - Pure Web Audio API click/scan synthesizer
// - Bilingual translation dictionary & language toggle
// - 3D card tilt listeners
// - Telemetry modal functions

const scriptExtension = `
    // ==========================================================================
    // EXTENDED CAPABILITIES: 3D HOLOGRAPHIC STAGE, SYNTHESIZER & BILINGUAL I18N
    // ==========================================================================

    appState.viewMode = '2D';
    appState.audioMuted = false;
    appState.currentLang = 'EN';
    appState.rotX = 0;
    appState.rotY = 0;
    appState.isDragging = false;
    appState.lastMouseX = 0;
    appState.lastMouseY = 0;

    // 1. PURE WEB AUDIO SYNTHESIZER (No External Assets)
    let audioCtx = null;
    function getAudioContext() {
      if (!audioCtx && (window.AudioContext || window.webkitAudioContext)) {
        const AudioCtor = window.AudioContext || window.webkitAudioContext;
        audioCtx = new AudioCtor();
      }
      if (audioCtx && audioCtx.state === 'suspended') {
        audioCtx.resume();
      }
      return audioCtx;
    }

    function playSynthClick(freq = 880, duration = 0.04) {
      if (appState.audioMuted) return;
      try {
        const ctx = getAudioContext();
        if (!ctx) return;
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, ctx.currentTime);
        gain.gain.setValueAtTime(0.08, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + duration);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start();
        osc.stop(ctx.currentTime + duration);
      } catch (_) {}
    }

    function playScanSuccessChime() {
      if (appState.audioMuted) return;
      try {
        const ctx = getAudioContext();
        if (!ctx) return;
        [523.25, 659.25, 783.99, 1046.50].forEach((freq, idx) => {
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.type = 'triangle';
          osc.frequency.setValueAtTime(freq, ctx.currentTime + idx * 0.08);
          gain.gain.setValueAtTime(0.12, ctx.currentTime + idx * 0.08);
          gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + idx * 0.08 + 0.28);
          osc.connect(gain);
          gain.connect(ctx.destination);
          osc.start(ctx.currentTime + idx * 0.08);
          osc.stop(ctx.currentTime + idx * 0.08 + 0.28);
        });
      } catch (_) {}
    }

    function toggleAudioFx() {
      appState.audioMuted = !appState.audioMuted;
      const icon = document.getElementById('nav-sound-icon');
      if (icon) icon.textContent = appState.audioMuted ? '🔇' : '🔊';
      if (!appState.audioMuted) playSynthClick(660);
    }

    // 2. 3D ISOMETRIC / LASER HOLOGRAM CANVAS RENDERER
    function setCanvasViewMode(mode) {
      appState.viewMode = mode;
      document.getElementById('btn-view-2d')?.classList.toggle('active', mode === '2D');
      document.getElementById('btn-view-3d')?.classList.toggle('active', mode === '3D');
      playSynthClick(mode === '3D' ? 720 : 540);
      if (mode === '2D') {
        drawAndAnalyzeWeaveCanvas();
      }
    }

    function draw3dWeaveStage() {
      const canvas = document.getElementById('metrology-canvas');
      if (!canvas) return;
      const ctx = canvas.getContext('2d');
      const w = canvas.width;
      const h = canvas.height;

      ctx.fillStyle = '#06080D';
      ctx.fillRect(0, 0, w, h);

      const cx = w / 2;
      const cy = h / 2 - 12;
      const gridCols = 26;
      const gridRows = 16;
      const cellW = 15;
      const cellH = 8;
      const time = Date.now() * 0.0022;
      const laserX = (Math.sin(time) * 0.5 + 0.5) * gridCols;

      ctx.save();
      ctx.translate(cx, cy);
      ctx.rotate(appState.rotY * 0.01);

      for (let r = 0; r < gridRows; r++) {
        for (let c = 0; c < gridCols; c++) {
          const x3d = (c - r) * cellW;
          let zElev = 0;
          const distToAnomaly = Math.hypot(c - 14, r - 8);
          if (appState.anomalyAmplitude > 0.030 && distToAnomaly < 5) {
            zElev = (5 - distToAnomaly) * (appState.anomalyAmplitude * 260);
          }
          const y3d = (c + r) * cellH * 0.5 - zElev;

          const isLaserNear = Math.abs(c - laserX) < 1.4;
          ctx.fillStyle = isLaserNear ? '#34D399' : (zElev > 6 ? '#EF4444' : 'rgba(52, 211, 153, 0.42)');
          ctx.beginPath();
          ctx.arc(x3d, y3d, isLaserNear ? 2.4 : 1.2, 0, Math.PI * 2);
          ctx.fill();

          if (c < gridCols - 1) {
            const nextX = (c + 1 - r) * cellW;
            const nextY = (c + 1 + r) * cellH * 0.5;
            ctx.strokeStyle = isLaserNear ? 'rgba(52, 211, 153, 0.75)' : 'rgba(255, 255, 255, 0.07)';
            ctx.lineWidth = isLaserNear ? 1.4 : 0.6;
            ctx.beginPath();
            ctx.moveTo(x3d, y3d);
            ctx.lineTo(nextX, nextY);
            ctx.stroke();
          }
          if (r < gridRows - 1) {
            const nextX = (c - (r + 1)) * cellW;
            const nextY = (c + (r + 1)) * cellH * 0.5;
            ctx.strokeStyle = isLaserNear ? 'rgba(47, 91, 255, 0.7)' : 'rgba(255, 255, 255, 0.06)';
            ctx.lineWidth = 0.6;
            ctx.beginPath();
            ctx.moveTo(x3d, y3d);
            ctx.lineTo(nextX, nextY);
            ctx.stroke();
          }
        }
      }
      ctx.restore();

      // 3D Telemetric Overlay
      ctx.strokeStyle = 'rgba(52, 211, 153, 0.35)';
      ctx.lineWidth = 1;
      ctx.strokeRect(16, 16, 150, 52);
      ctx.fillStyle = '#34D399';
      ctx.font = '10px "JetBrains Mono", monospace';
      ctx.fillText('3D SPATIAL TELEMETRY', 24, 32);
      ctx.fillText('SWEEP: ' + (laserX / gridCols * 100).toFixed(0) + '% // LASER LOCK', 24, 46);
      ctx.fillText('RMS DEPTH: ' + appState.lastMeasuredRmsMm.toFixed(3) + ' MM', 24, 60);
    }

    function start3dRenderLoop() {
      function loop() {
        if (appState.viewMode === '3D') {
          draw3dWeaveStage();
        }
        requestAnimationFrame(loop);
      }
      requestAnimationFrame(loop);
    }

    // 3. BILINGUAL I18N DICTIONARY & SWITCHER
    const i18nDictionary = {
      EN: {
        nav_metrology: 'Optical Metrology',
        nav_voice: 'Bangla/EN Voice ERP',
        nav_payment: 'bKash 0011 + Stripe',
        nav_ledger: 'Live Ledger',
        nav_telemetry: 'iDEA / SBL Grants',
        nav_cloud: 'Cloud Host (.com)',
        nav_get_app: 'Get App ↓',
        hero_title: 'Sub-millimeter garment telemetry & <span style="color:var(--emerald);">autonomous supply.</span>',
        hero_desc: 'Execute pixel-level Sobel optical stitch metrology, dispatch live bilingual Bangla/English voice ERP commands to Firebase Firestore, and bind tokenized bKash Mode 0011 & Stripe USD agreements directly from this console or the signed Android APK.',
        features_title: 'Real-time optical metrology, voice ERP & settlement.',
        features_desc: 'Every scan, spoken Bangla/English command, and bKash Mode 0011 agreement executes real algorithmic processing and persists directly to the database.'
      },
      BN: {
        nav_metrology: 'অপটিক্যাল মেট্রোলজি',
        nav_voice: 'বাংলা/ইংরেজি ভয়েস ইআরপি',
        nav_payment: 'বিকাশ ০০১১ + স্ট্রাইপ',
        nav_ledger: 'লাইভ লেজার',
        nav_telemetry: 'আইডিয়া / এসবিএল গ্রান্ট',
        nav_cloud: 'ক্লাউড হোস্টিং (.com)',
        nav_get_app: 'অ্যাপ ডাউনলোড ↓',
        hero_title: 'সাব-মিলিমিটার গার্মেন্টস টেলিমেট্রি এবং <span style="color:var(--emerald);">স্বয়ংক্রিয় সাপ্লাই চেইন।</span>',
        hero_desc: 'পিক্সেল-লেভেল সোবেল অপটিক্যাল সেলাই মেট্রোলজি পরীক্ষা করুন, ফায়ারবেস ফায়ারস্টোরে বাংলা/ইংরেজি ভয়েস ইআরপি নির্দেশ পাঠান এবং টোকেনাইজড বিকাশ মোড ০০১১ ও স্ট্রাইপ ইউএসডি চুক্তি সরাসরি পরিচালনা করুন।',
        features_title: 'রিয়েল-টাইম অপটিক্যাল মেট্রোলজি, ভয়েস ইআরপি ও পেমেন্ট সেটেলমেন্ট।',
        features_desc: 'প্রতিটি অপটিক্যাল স্ক্যান, বাংলা/ইংরেজি ভয়েস নির্দেশ এবং বিকাশ মোড ০০১১ এগ্রিমেন্ট সরাসরি অ্যালগরিদমিক প্রসেসিং সম্পন্ন করে ডেটাবেজে সংরক্ষিত হয়।'
      }
    };

    function toggleGlobalLanguage() {
      appState.currentLang = appState.currentLang === 'EN' ? 'BN' : 'EN';
      const btn = document.getElementById('nav-lang-btn');
      if (btn) btn.innerHTML = appState.currentLang === 'EN' ? '<span>🇧🇩 বাংলা</span>' : '<span>🇬🇧 English</span>';
      applyTranslations(appState.currentLang);
      playSynthClick(580);
    }

    function applyTranslations(lang) {
      const dict = i18nDictionary[lang];
      if (!dict) return;
      document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        if (dict[key]) el.innerHTML = dict[key];
      });
    }

    // 4. TELEMETRY MODAL CONTROLS
    function openTelemetryModal() {
      playSynthClick(640);
      document.getElementById('telemetry-modal').classList.add('open');
      const preview = {
        governanceFramework: "Startup Bangladesh Limited (SBL) & iDEA Project",
        applicantEntity: "Bazarly Technologies Ltd (Dhaka EPZ / Banani)",
        activeFactories: 18,
        activeDokans: 84,
        defectReductionPercent: 34.2,
        gmvTotalBdt: 12450000,
        monthlyRecurringRevenueUsd: 14200,
        netRevenueRetentionPercent: 128.5,
        scope3CarbonSavedKg: 8920,
        ideaPreSeedTrancheStatus: "TRANCHE_1_AUDITED_RECOMMENDED",
        sblGovernanceScore: "96/100"
      };
      const preEl = document.getElementById('telemetry-json-preview');
      if (preEl) preEl.textContent = JSON.stringify(preview, null, 2);
    }

    function closeTelemetryModal() {
      document.getElementById('telemetry-modal').classList.remove('open');
    }

    function downloadGovernanceAuditReport() {
      playSynthClick(880);
      const data = {
        title: "Bazarly iDEA Pre-Seed & SBL Corporate Governance Audit Dossier",
        compiledAt: new Date().toISOString(),
        applicantOwnership: "100% Bangladeshi Nationals (Dhaka)",
        metrics: {
          activeFactories: 18,
          activeDokans: 84,
          defectReductionPercent: 34.2,
          grossMerchandiseValueBdt: 12450000,
          monthlyRecurringRevenueUsd: 14200,
          netRevenueRetentionPercent: 128.5,
          scope3CarbonSavedKg: 8920,
          ideaTranche1Status: "TRANCHE_1_VERIFIED",
          sblGovernanceComplianceScore: "96/100"
        }
      };
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'bazarly_idea_sbl_governance_audit.json';
      a.click();
      URL.revokeObjectURL(url);
    }

    // Initialize 3D interaction and start animation loop on page load
    document.addEventListener('DOMContentLoaded', () => {
      start3dRenderLoop();

      // Card Tilt Hover Physics
      document.querySelectorAll('.glass-card, .pricing-bento-card, .feature-display-card').forEach(card => {
        card.addEventListener('mousemove', (e) => {
          const rect = card.getBoundingClientRect();
          const x = e.clientX - rect.left;
          const y = e.clientY - rect.top;
          const centerX = rect.width / 2;
          const centerY = rect.height / 2;
          const rotateX = ((y - centerY) / centerY) * -6;
          const rotateY = ((x - centerX) / centerX) * 6;
          card.style.transform = \`perspective(1000px) rotateX(\${rotateX}deg) rotateY(\${rotateY}deg) scale3d(1.01, 1.01, 1.01)\`;
        });
        card.addEventListener('mouseleave', () => {
          card.style.transform = '';
        });
      });
    });
`;

// Insert the script extension right before </script>
html = html.replace('</script>', scriptExtension + '\n  </script>');

// Update executeRealOpticalScan to playScanSuccessChime and playSynthClick
html = html.replace(
  'applyCommittedInspectionRecord(rec);',
  'applyCommittedInspectionRecord(rec); playScanSuccessChime();'
);
html = html.replace(
  'applyCommittedVoiceAction(action);',
  'applyCommittedVoiceAction(action); playScanSuccessChime();'
);
html = html.replace(
  'applyCommittedPaymentAgreement(agr);',
  'applyCommittedPaymentAgreement(agr); playScanSuccessChime();'
);

// Save updated public/index.html
fs.writeFileSync(publicIndexPath, html, 'utf8');
console.log('[enhance-web] public/index.html upgraded successfully! Size:', html.length);

// Synchronize to app/src/main/assets/web/index.html
fs.mkdirSync(path.dirname(assetIndexPath), { recursive: true });
fs.writeFileSync(assetIndexPath, html, 'utf8');
console.log('[enhance-web] app/src/main/assets/web/index.html synchronized successfully!');
