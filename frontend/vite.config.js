import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
// Proxy cible le backend ; en dev sans .env c'est localhost:8080
const apiBaseUrl =
  process.env.VITE_API_BASE_URL || "http://localhost:8080";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: apiBaseUrl,
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
