/**
 * Vercel Serverless Function Handler
 * Routes all /api/* requests to Bazarly Operational Server
 */

const server = require('../server');

module.exports = (req, res) => {
  server.emit('request', req, res);
};
