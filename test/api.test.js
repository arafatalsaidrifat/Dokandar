const test = require('node:test');
const assert = require('node:assert');
const http = require('node:http');
const server = require('../server');

let testPort;
let baseUrl;

test.before(async () => {
  await new Promise((resolve) => {
    server.listen(0, '127.0.0.1', () => {
      testPort = server.address().port;
      baseUrl = `http://127.0.0.1:${testPort}`;
      resolve();
    });
  });
});

test.after(async () => {
  await new Promise((resolve) => server.close(resolve));
});

function request(path, options = {}) {
  return new Promise((resolve, reject) => {
    const url = new URL(path, baseUrl);
    const req = http.request(url, options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        let json = null;
        try { json = JSON.parse(data); } catch (_) {}
        resolve({ status: res.statusCode, headers: res.headers, body: data, json });
      });
    });
    req.on('error', reject);
    if (options.body) {
      req.write(typeof options.body === 'string' ? options.body : JSON.stringify(options.body));
    }
    req.end();
  });
}

test('GET /api/status returns ONLINE status and Bazarly metadata', async () => {
  const res = await request('/api/status');
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.status, 'ONLINE');
  assert.strictEqual(res.json.app, 'Bazarly');
  assert.strictEqual(res.json.version, '4.2.0');
});

test('GET /api/state returns initial inspections, voice actions, and agreements', async () => {
  const res = await request('/api/state');
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.status, 'LIVE');
  assert(Array.isArray(res.json.inspections));
  assert(Array.isArray(res.json.voiceActions));
  assert(Array.isArray(res.json.agreements));
});

test('GET /api/telemetry returns iDEA & SBL metrics', async () => {
  const res = await request('/api/telemetry');
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.ideaPreSeedTrancheStatus, 'TRANCHE_1_VERIFIED');
  assert.strictEqual(res.json.sblGovernanceScore, 96);
  assert(res.json.gmvTotalBdt > 0);
});

test('POST /api/metrology/inspect classifies skipped stitch when RMS > 0.030mm', async () => {
  const res = await request('/api/metrology/inspect', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      targetName: 'Denim Test Seam',
      measuredRmsMm: 0.045,
      edgeDensity: 0.92
    }
  });
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.hasDefect, true);
  assert(res.json.defectLabel.includes('Skipped Stitch'));
  assert(res.json.banglaDefectLabel.includes('বাদ পড়া সেলাই'));
  assert.strictEqual(res.json.severity, 'HIGH');
});

test('POST /api/metrology/inspect classifies zero defect when RMS <= 0.030mm', async () => {
  const res = await request('/api/metrology/inspect', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      targetName: 'Cotton Collar Seam',
      measuredRmsMm: 0.015,
      edgeDensity: 0.98
    }
  });
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.hasDefect, false);
  assert(res.json.defectLabel.includes('Zero Defect'));
  assert.strictEqual(res.json.severity, 'NORMAL');
});

test('POST /api/voice/execute handles Bangla invoice command', async () => {
  const res = await request('/api/voice/execute', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      transcript: 'মিরপুর দোকানের জন্য ২০০ পিস শার্ট ইনভয়েস তৈরি করো',
      isBangla: true
    }
  });
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.intent, 'CREATE_INVOICE');
  assert.strictEqual(res.json.language, 'bn');
  const structured = JSON.parse(res.json.structuredJson);
  assert.strictEqual(structured.quantity, 200);
  assert.strictEqual(structured.tool, 'CreateInvoice');
});

test('POST /api/voice/execute handles restock supplier command', async () => {
  const res = await request('/api/voice/execute', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      transcript: 'লাইন ২ এর জন্য ১০০ রোল সুতি কাপড় রিস্টক করো',
      isBangla: true
    }
  });
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.json.intent, 'RESTOCK_SUPPLIER');
  const structured = JSON.parse(res.json.structuredJson);
  assert.strictEqual(structured.tool, 'RestockSupplier');
  assert.strictEqual(structured.quantityRolls, 100);
});

test('POST /api/payments/bind validates bKash MSISDN and binds agreement', async () => {
  const validRes = await request('/api/payments/bind', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      rail: 'BKASH_0011',
      msisdn: '01723994120',
      pin: '12345',
      payerReference: 'Apex Garments',
      amountBdt: 30000
    }
  });
  assert.strictEqual(validRes.status, 200);
  assert(validRes.json.agreementId.startsWith('AGR-BKASH-'));
  assert.strictEqual(validRes.json.status, 'BOUND_ACTIVE');
  assert.strictEqual(validRes.json.amountBdt, 30000);

  const invalidRes = await request('/api/payments/bind', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: {
      rail: 'BKASH_0011',
      msisdn: '12345'
    }
  });
  assert.strictEqual(invalidRes.status, 400);
});

test('GET /download/bazarly.apk serves APK with correct headers', async () => {
  const res = await request('/download/bazarly.apk');
  assert.strictEqual(res.status, 200);
  assert.strictEqual(res.headers['content-type'], 'application/vnd.android.package-archive');
  assert(res.headers['content-disposition'].includes('Bazarly-v4.2-universal.apk'));
});
