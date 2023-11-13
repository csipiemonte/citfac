/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { fileURLToPath, URL } from 'node:url'
const path = require('path')
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { BASE_URL } from './src/configuration'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    
    },
    extensions: ['.js','.css','.html','.ico','.png','.svg'],
  },
  base: '/admin/',
  build: {
    sourcemap: false,
    rollupOptions: {
      external: [  /^\/statics\/.*/],
      
    },
  },
  globPattern: "**/*.{js,css,html,ico,png,svg}",
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/scss/_variables.scss" as *;`,
        
      },
    },
  },
  server:{
    proxy:{
      "/admin/statics": {
        target: BASE_URL,
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/admin/, ''),
    }
  }
  }
})
