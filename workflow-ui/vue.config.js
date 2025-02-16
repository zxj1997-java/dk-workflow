const { defineConfig } = require('@vue/cli-service')

// 获取正确的 API URL
const getApiUrl = () => {
  const url = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
  // 确保 URL 格式正确
  return url.startsWith('http') ? url : `http://${url}`
}

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: getApiUrl(),
        changeOrigin: true,
        ws: false,
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
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  }
}) 