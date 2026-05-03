import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// `https://vite.dev/config/`
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    // 增加超时时间，避免开发服务器过早关闭
    timeout: 60000,
    proxy: {
      // 管理员API - 只代理具体的API路径
      '/admin/login': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/logout': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/checkCode': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/index': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/video': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/user': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/category': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/danmu': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/comment': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/file': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/settings': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/admin/saveListSettings': {
        target: 'http://localhost:7071',
        changeOrigin: true
      },
      '/api/admin': {
        target: 'http://localhost:7071',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/admin/, '/admin')
      },
      '/api/user/userInfo': {
        target: 'http://localhost:7074',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user\/userInfo/, '/userInfo')
      },
      '/api/userInfo': {
        target: 'http://localhost:7074',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/userInfo/, '/userInfo')
      },
      '/api/user': {
        target: 'http://localhost:7074',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/user/, '')
      },
      '/api/video/danmaku/add': {
        target: 'http://localhost:7072',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/video\/danmaku/, '/danmu/video/danmaku')
      },
      '/api/video/danmaku/list': {
        target: 'http://localhost:7072',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/video\/danmaku/, '/danmu/video/danmaku')
      },
      '/api/video': {
        target: 'http://localhost:7074',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/video/, '/video')
      },
      '/api/chat': {
        target: 'http://localhost:7072',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/chat/, '/chat')
      },
      '/api/message': {
        target: 'http://localhost:7072',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/message/, '/message')
      },
      '/api/notification': {
        target: 'http://localhost:7072',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/notification/, '/notification')
      },
      '/api/file': {
         target: 'http://localhost:7073',
         changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/file/, '/file')
      },
      '/api/payment': {
        target: 'http://localhost:7074',
        changeOrigin: true
      },
      '/api/recharge': {
        target: 'http://localhost:7071',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/recharge/, '/web')
      },
      '/api/coupon': {
        target: 'http://localhost:7071',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/coupon/, '/web')
      },
      '/userAction': {
        target: 'http://localhost:7072',
        changeOrigin: true
      },
      '/userCollection': {
        target: 'http://localhost:7072',
        changeOrigin: true
      },
      '/comment': {
        target: 'http://localhost:7074',
        changeOrigin: true
      },
      '/history/': {
        target: 'http://localhost:7072',
        changeOrigin: true
      },
      '/resource/hls': {
        target: 'http://localhost:7073',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/resource\/hls/, '/hls')
      }
    }
  },
  // 优化依赖预构建
  optimizeDeps: {
    // 只预构建必要的依赖
    include: ['vue', 'axios'],
    // 排除不需要预构建的依赖
    exclude: [],
    // 减少并发构建数量，降低内存使用
    maxWorkers: 2
  },
  // 构建优化
  build: {
    // 启用代码分割
    rollupOptions: {
      output: {
        manualChunks: {
          // 将大型依赖分离到单独的 chunk
          vendor: ['vue'],
          axios: ['axios']
        }
      }
    },
    // 压缩选项
    minify: 'esbuild'
  },
  // 缓存配置
  cacheDir: '.vite/cache',
  // 环境变量配置
  define: {
    'process.env.NODE_ENV': JSON.stringify(process.env.env || 'development')
  }
})