import { createApp } from 'vue'
import App from './App.vue'

// 1. 引入刚刚配置好的路由实例
import { router } from './router'

// 2. 引入 Element Plus 组件库及其完整样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

// 3. 核心：通过 .use() 全局注册路由和 UI 组件库
app.use(router)
app.use(ElementPlus)

app.mount('#app')