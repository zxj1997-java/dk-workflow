import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import store from './store'

// 打印当前环境信息
console.log('当前环境:', process.env.NODE_ENV)
console.log('API地址:', process.env.VUE_APP_BASE_API)

const app = createApp(App)

app.use(ElementPlus)
app.use(router)
app.use(store)

// 全局配置（可选）
app.config.globalProperties.$baseUrl = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : 'http://your-production-api.com'

app.mount('#app') 