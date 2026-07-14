# 前端项目结构

```
src/
├── main.ts                                # 项目入口（Element Plus + Router + Pinia）
├── App.vue                                # 根组件
├── style.css                              # 全局样式
├── api/
│   ├── auth.ts                            # 登录/用户信息 API
│   ├── user.ts                            # 用户管理 API
│   ├── role.ts                            # 角色管理 API
│   ├── menu.ts                            # 菜单管理 API
│   ├── log.ts                             # 操作日志 API
│   ├── monitor.ts                         # 监控数据 API（慢SQL/接口耗时）
│   └── dict.ts                            # 字典管理 API（字典类型+字典数据）
├── assets/
│   ├── hero.png                           # 主页图片资源
│   ├── vite.svg                           # Vite 图标
│   └── vue.svg                            # Vue 图标
├── components/
│   └── HelloWorld.vue                     # 示例组件
├── directives/
│   └── permission.ts                      # 权限指令（v-hasPermission）
├── layout/
│   └── index.vue                          # 后台布局（侧边栏菜单 + 顶部导航）
├── router/
│   └── index.ts                           # 路由配置（含登录鉴权守卫）
├── store/
│   └── auth.ts                            # 认证状态管理（Pinia）
├── utils/
│   ├── request.ts                         # Axios 请求封装（含Token注入、401/403拦截）
│   └── cookie.ts                          # Cookie 操作工具（token存取）
└── views/
    ├── login/
    │   └── index.vue                      # 登录页面
    └── system/
        ├── user/index.vue                 # 用户管理（列表/分页/搜索/新增编辑/分配角色）
        ├── role/index.vue                 # 角色管理（列表/分页/搜索/新增编辑/分配菜单）
        ├── menu/index.vue                 # 菜单管理（树形表格/新增编辑/目录菜单按钮）
        ├── log/index.vue                  # 操作日志（分页查询/搜索）
        ├── dict/
        │   ├── type/index.vue             # 字典管理（分页查询/搜索/新增编辑/查看字典数据）
        │   └── data/index.vue             # 字典数据（从字典类型页面进入）
        └── monitor/
            ├── slow-sql.vue               # 慢SQL监控（分页查询/耗时筛选）
            └── api-timing.vue             # 接口耗时监控（分页查询/耗时筛选）
```
