const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://[::1]:8080', // 使用 IPv6 localhost
        // 或者使用 target: 'http://127.0.0.1:8080', // 强制使用 IPv4
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api': '/api'
        },
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
          'Access-Control-Allow-Headers': 'X-Requested-With, content-type, Authorization'
        }
      }
    },
    allowedHosts: 'all', // 允许所有主机访问
    client: {
      webSocketURL: 'auto://0.0.0.0:0/ws' // 自动处理 WebSocket 连接
    }
  }
}) 