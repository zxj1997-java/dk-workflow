import axios from 'axios'
import {ElMessage} from 'element-plus'

// 确保 baseURL 格式正确
const getBaseUrl = () => {
  const url = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
  return url.startsWith('http') ? url : `http://${url}`
}

// 创建 axios 实例
const service = axios.create({
    baseURL: getBaseUrl(),
    timeout: 15000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 可以在这里添加 token 等认证信息
        // const token = store.state.user.token
        // if (token) {
        //   config.headers['Authorization'] = `Bearer ${token}`
        // }
        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 直接返回 response.data，不做额外处理
        return response.data.data;
    },
    error => {
        console.error('响应错误:', error)
        ElMessage.error(error.response?.data?.message || '请求失败')
        return Promise.reject(error)
    }
)

export default service 