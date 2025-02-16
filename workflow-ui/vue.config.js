const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        host: '::', // 监听所有 IPv6 和 IPv4 接口
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api': '/api'  // 不重写路径
        },
        logLevel: 'debug'  // 添加调试日志
      }
    }
  }
}) 