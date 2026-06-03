import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
// 1. 引入刚刚配置好的路由实例
import { router } from './router'

// 2. 引入 Element Plus 组件库及其完整样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')