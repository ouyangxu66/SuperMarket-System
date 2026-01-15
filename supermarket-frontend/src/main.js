import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './assets/main.css'

/**
 * 应用入口文件
 * 负责初始化 Vue 应用实例，注册插件（Pinia, Router, ElementPlus）及全局组件
 */
const app = createApp(App)

app.use(createPinia()) // 注册 Pinia 状态管理
app.use(router)        // 注册 Vue Router 路由
app.use(ElementPlus)   // 注册 Element Plus UI 组件库

// Register all icons
// 全局注册 Element Plus 的图标组件，以便在任何地方直接使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app') // 挂载 Vue 应用到 DOM 节点 #app
