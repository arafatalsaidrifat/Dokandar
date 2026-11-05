/**
 * Synchronizes public/index.html with app/src/main/assets/web/index.html
 */
const fs = require('fs');
const path = require('path');

const src = path.join(__dirname, '..', 'public', 'index.html');
const dest = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'web', 'index.html');

if (!fs.existsSync(src)) {
  console.error('[sync-web] Source not found:', src);
  process.exit(1);
}

const content = fs.readFileSync(src, 'utf8');
fs.mkdirSync(path.dirname(dest), { recursive: true });
fs.writeFileSync(dest, content, 'utf8');

console.log(`[sync-web] Synchronized ${Buffer.byteLength(content)} bytes from ${path.relative(process.cwd(), src)} to ${path.relative(process.cwd(), dest)}`);
