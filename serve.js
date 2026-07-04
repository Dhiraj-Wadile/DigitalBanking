const http = require('http');
const fs = require('fs');
const path = require('path');
const root = path.join(__dirname, 'frontend', 'dist', 'digital-banking', 'browser');
const mimeTypes = {
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.ico': 'image/x-icon',
  '.svg': 'image/svg+xml',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.map': 'application/json'
};
const server = http.createServer((req, res) => {
  let url = req.url.split('?')[0];
  let filePath = path.join(root, url === '/' ? 'index.html' : url);
  if (!fs.existsSync(filePath)) {
    filePath = path.join(root, 'index.html');
  }
  const ext = path.extname(filePath);
  const contentType = mimeTypes[ext] || 'application/octet-stream';
  try {
    const content = fs.readFileSync(filePath);
    res.writeHead(200, { 'Content-Type': contentType });
    res.end(content);
  } catch (e) {
    res.writeHead(404);
    res.end('Not found');
  }
});
server.listen(4200, () => {
  console.log('Digital Banking Platform running at http://localhost:4200');
});
