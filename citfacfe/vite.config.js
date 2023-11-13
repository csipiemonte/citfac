/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { VitePWA } from "vite-plugin-pwa";
import { BASE_URL, MOON_BASE_URL } from "./src/configuration";

const { resolve } = require("path");
/**
 * @type {import('vite').UserConfig}
 */

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      injectRegister: null, // registrazione manuale del service worker
      registerType: "autoUpdate",
      strategies: "injectManifest", // creazione manuale del service worker
      workbox: {
        clientsClaim: true,
        skipWaiting: true,
        globPatterns: ["**/*.{js,css,html,ico,png,svg}"],
      },
      injectManifest: {
        injectionPoint: undefined,
        rollupFormat: "iife",
      },
      devOptions: {
        enabled: true,
      },
      compilerOptions: {
        types: ["vite-plugin-pwa/client"],
      },
    }),
  ],
  
  build: {
    sourcemap: false,
    rollupOptions: {
      external: [  /^\/statics\/.*/],
      
    },
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    
    },
    extensions: ['.js','.css','.html','.ico','.png','.svg'],
  },
  globPattern: "**/*.{js,css,html,ico,png,svg}",
  server: {
    proxy: {
      "^/statics": {
        target: BASE_URL,
        ws: true,
        changeOrigin: true,
        secure: false,
      },
      "/moonfobl": {
        target: MOON_BASE_URL,
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
