import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // 关键配置：将 @ 指向项目根目录下的 src 文件夹
      '@': resolve(__dirname, './src')
    }
  },
  server: {
    host: '0.0.0.0', // 允许通过局域网 IP 访问
    port: 5173,      // 前端运行端口
    proxy: {
      // 拦截所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:48080', // 你本地芋道后端的真实启动地址（根据实际情况修改端口）
        changeOrigin: true,              // 允许跨域
        rewrite: (path) => path.replace(/^\/app-api/, '') // 重写路径：去掉请求中的 /api 前缀
      }
    }
  }
})