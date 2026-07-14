/**
 * 项目入口文件
 * 初始化 Vue 应用，注册 Element Plus 组件库、路由和 Pinia 状态管理
 */
import { createApp } from 'vue'
import '@/style.css'                     // 全局样式
import App from '@/App.vue'              // 根组件
import ElementPlus from 'element-plus'   // UI 组件库
import 'element-plus/dist/index.css'     // UI 组件库样式
import router from '@/router'            // 路由
import { createPinia } from 'pinia'      // 状态管理
import hasPermission from '@/directives/permission' // 权限指令

const app = createApp(App)

// 注册全局插件
app.use(ElementPlus)  // 注册 Element Plus
app.use(router)       // 注册路由
app.use(createPinia()) // 注册 Pinia 状态管理

// 注册全局自定义指令
app.directive('hasPermission', hasPermission)

// 挂载应用
app.mount('#app')
