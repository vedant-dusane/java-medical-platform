/**
 * config.js
 * Central configuration file.
 * Resolves to the current origin dynamically, or falls back to http://localhost:8080.
 */

export const API_BASE_URL = (typeof window !== "undefined" && window.location && window.location.origin && window.location.origin !== "null")
  ? window.location.origin
  : "http://localhost:8080";
