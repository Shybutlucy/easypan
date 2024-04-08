import './assets/main.css'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { setupStore } from './store'
import App from './App.vue'
import router, { setupRouter } from './router'

async function bootstrap() {
  const app = createApp(App)
  // 挂载状态管理
  setupStore(app)
  // 注册全局常用的 element-plus 组件
  app.use(ElementPlus)

  // 挂载路由
  setupRouter(app)

  // 路由准备就绪后挂载 APP 实例
  // https://router.vuejs.org/api/interfaces/router.html#isready
  await router.isReady()

  app.mount('#app', true)
}
bootstrap()
