import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

/**
 * Vite 配置文件
 * 用于配置前端项目的构建工具 Vite
 * 包括插件配置、别名设置、以及开发服务器代理等
 */
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()], // 使用 Vue 插件，支持 .vue 文件编译
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src') // 配置 '@' 指向 'src' 目录，方便引入文件
    }
  },
  server: {
    port: 3000, // 前端开发服务器启动端口
    proxy: {
      // 配置 API 代理，解决开发环境跨域问题
      '/api': {
        target: 'http://localhost:8080', // 后端接口地址
        changeOrigin: true, // 允许跨域
        // 重写路径：将请求路径中的 '/api' 前缀去掉
        // 例如：前端请求 /api/auth/login -> 代理转发到 backend/auth/login
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
