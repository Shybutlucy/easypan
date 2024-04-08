import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import { createProxy } from './build/proxy'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({ mode, command}) => {
  const env = loadEnv(mode, process.cwd(), '')
  const { VITE_PORT, VITE_PROXY } = env
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: VITE_PORT,
      hmr: true,
      proxy: createProxy(VITE_PROXY)
    }
  }
})
