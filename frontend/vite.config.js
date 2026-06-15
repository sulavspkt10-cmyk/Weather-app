import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/public',
    emptyOutDir: true,
  },
  server: {
    port: 3000,
    proxy: {
      '/api':     { target: 'http://localhost:4567', changeOrigin: true },
      '/weather': { target: 'http://localhost:4567', changeOrigin: true },
      '/login':   { target: 'http://localhost:4567', changeOrigin: true },
      '/logout':  { target: 'http://localhost:4567', changeOrigin: true },
      '/setup':   { target: 'http://localhost:4567', changeOrigin: true },
    },
  },
})
