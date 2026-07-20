# MetaBuild - 后台管理框架（元数据驱动单表配置化）

基于 Spring Boot 3 + Vue 3 + Element Plus 的后台管理框架，集成了用户管理、角色权限、菜单管理、字典管理、监控告警等基础功能，并在此基础上提供了单表配置化能力 —— 新增数据表后无需编码，通过配置即可自动生成增删改查、搜索、导出、导入等完整功能。

---

## 项目架构

系统采用前后端分离架构，后端使用 Maven 多模块构建。

```
┌─────────────────────────────────────────────────────┐
│                    前端 (Vite + Vue 3)               │
│  page/  ──  Element Plus 后台管理 SPA                │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP (RESTful API)
┌──────────────────────┴──────────────────────────────┐
│                  后端 (Spring Boot 3)                 │
│  admin/  (启动模块 - 可运行 JAR)                      │
│    ├── system/  (系统模块 - RBAC/JWT/监控/字典)       │
│    │    └── common/ (公共模块 - 切面/异常/工具类)      │
│    └── server/  (业务模块 - 元数据管理)               │
│         └── common/                                  │
└──────────────────────┬──────────────────────────────┘
                       │ JDBC / MyBatis-Plus
┌──────────────────────┴──────────────────────────────┐
│                   MySQL 数据库                         │
│  sys_* 系统表 / tb_* 元数据表 / td_* 业务测试表       │
└─────────────────────────────────────────────────────┘
```

---

## 使用技术

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.5.15 | 基础框架 |
| MyBatis-Plus | 3.5.17 | ORM 框架，简化数据库操作 |
| Spring Security | 6.x | 认证与授权（RBAC 权限模型） |
| JWT (jjwt) | 0.12.6 | 无状态 Token 认证 |
| FastJSON2 | 2.0.53 | JSON 序列化/反序列化 |
| MySQL | 8.x | 关系型数据库 |
| Maven | 3.x | 项目构建管理 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.39 | 前端框架 |
| Vite | 8.x | 构建工具 |
| TypeScript | 6.x | 类型安全开发 |
| Element Plus | 2.14.3 | 组件库 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 3.x | 状态管理 |
| Axios | 1.x | HTTP 请求库 |

---

## 目录结构

```
MetaBuild/
├── page/                          # 前端项目
│   └── src/
│       ├── api/                   # API 接口封装
│       ├── directives/            # 自定义指令（v-hasPermission 权限指令）
│       ├── layout/                # 后台布局组件（侧边栏 + 顶部导航）
│       ├── router/                # 路由配置
│       ├── store/                 # Pinia 状态管理
│       ├── utils/                 # 工具函数（Cookie/Token/请求封装）
│       └── views/                 # 页面组件
│           ├── login/             # 登录页
│           ├── system/            # 系统管理（用户/角色/菜单/日志/字典）
│           │   ├── user/          # 用户管理
│           │   ├── role/          # 角色管理
│           │   ├── menu/          # 菜单管理
│           │   ├── log/           # 操作日志
│           │   ├── dict/          # 字典管理
│           │   └── monitor/       # 监控管理（慢SQL/接口耗时）
│           └── metadata/          # 元数据管理
│               ├── table/         # 元数据表列表
│               ├── column/        # 元数据列管理
│               ├── config/        # 拖拽配置（列表/表单布局）
│               └── dynamic/       # 动态数据管理
│
├── project/                       # 后端项目
│   ├── pom.xml                    # 父 POM（依赖管理）
│   ├── common/                    # 公共模块
│   │   ├── annotation/LogInfo.java          # 操作日志注解
│   │   ├── aspect/LogAspect.java            # 操作日志切面
│   │   ├── aspect/TimingAspect.java         # 接口耗时统计切面
│   │   ├── config/SqlStatsInterceptor.java  # SQL 性能拦截器
│   │   ├── entity/SysUserLog.java           # 操作日志实体
│   │   ├── entity/SlowSqlLog.java           # 慢 SQL 日志实体
│   │   ├── entity/ApiTimingLog.java         # 接口耗时日志实体
│   │   ├── mapper/                          # 日志 Mapper
│   │   ├── util/UserContext.java            # 用户上下文（ThreadLocal）
│   │   └── vo/RespData.java                 # 统一响应体
│   ├── system/                    # 系统模块
│   │   ├── config/security/                 # JWT + Security 配置
│   │   ├── controller/                      # 控制器（Auth/User/Role/Menu/Log/Dict/Monitor）
│   │   ├── entity/                          # 实体（SysUser/Role/Menu/DictType/DictData）
│   │   ├── mapper/                          # Mapper 接口
│   │   ├── service/                         # 业务逻辑
│   │   └── resources/mapper/                # XML 映射文件
│   ├── server/                     # 元数据管理模块
│   │   ├── controller/                      # 控制器（MetaTable/MetaConfig/MetaDynamic）
│   │   ├── entity/                          # 实体（MetaTable/Column/ListConfig/FormConfig）
│   │   ├── mapper/                          # Mapper 接口
│   │   ├── service/                         # 业务逻辑
│   │   └── resources/mapper/                # XML 映射文件
│   └── admin/                      # 启动模块
│       └── src/main/resources/
│           └── application.yml              # 主配置文件
│
├── mysql/                         # 数据库脚本
│   ├── sys_*.sql                  # 系统表结构
│   ├── tb_*.sql                   # 元数据表结构
│   └── init_data.sql              # 初始数据（管理员/角色/菜单/按钮权限）
│
└── readme.md                      # 本文件
```

---

## 实现功能

### 1. 系统管理

#### 1.1 RBAC 权限控制
- 基于角色的访问控制模型（用户 ↔ 角色 ↔ 菜单/按钮）
- JWT 无状态 Token 认证，登录后自动返回 Token
- 前端 `v-hasPermission` 自定义指令控制按钮级显示权限
- 后端 `@PreAuthorize` 注解控制接口级访问权限

#### 1.2 用户管理
- 用户 CRUD（分页查询、新增、编辑、删除）
- 用户状态管理（启用/禁用）
- 密码重置（默认密码 123456）
- 角色分配（支持多角色）

#### 1.3 角色管理
- 角色 CRUD（分页查询、新增、编辑、删除）
- 角色状态管理
- 菜单权限分配（树形菜单选择，精确到按钮级）

#### 1.4 菜单管理
- 菜单 CRUD（目录/菜单/按钮 三级结构）
- 树形展示，支持拖拽选择父级菜单
- 权限标识管理（perms 字段用于接口鉴权）

#### 1.5 字典管理
- 字典类型 CRUD
- 字典数据 CRUD（支持按字典类型分类）
- 字典数据按类型查询接口（供前端下拉框等组件使用）

#### 1.6 操作日志
- 记录所有增删改操作的日志（操作名称、URL、参数、结果码）
- 按操作名称模糊搜索，按操作时间倒序排列
- 查询类操作不记录日志

### 2. 监控管理

#### 2.1 慢 SQL 监控
- 通过 MyBatis-Plus 拦截器拦截所有 SQL 执行
- 超过配置阈值（默认 2000ms）自动 WARN 日志告警并持久化到数据库
- 阈值可通过 `application.yml` 的 `monitor.slow-sql.threshold-ms` 配置

#### 2.2 接口耗时监控
- 通过 AOP 切面拦截所有 `@RestController` 接口
- 记录每个接口的请求方式、URL、方法名、耗时
- 超过配置阈值（默认 3000ms）自动持久化到数据库
- 阈值可通过 `application.yml` 的 `monitor.api-timing.threshold-ms` 配置

### 3. 元数据管理（低代码核心）

#### 3.1 元数据表管理
- 从 `information_schema` 读取数据库表结构（排除 `sys_` 和 `tb_` 前缀的表）
- 保存元数据表定义（表名、备注）和列定义（列名、数据类型、是否主键、是否必填）
- 列的展示类型支持：文本框、多行文本框、下拉框、单选框、复选框、日期选择器、时间选择器、日期时间选择器
- 下拉框/单选框/复选框支持绑定字典数据或动态 SQL 查询

#### 3.2 元数据配置（拖拽配置）
- **列表配置**：拖拽排序，配置列显示、列宽、对齐方式、是否可排序、是否搜索
- **表单配置**：拖拽排序，配置字段标签、占位符、默认值、是否必填、跨列宽度
- 自动生成默认配置，支持批量保存

#### 3.3 动态数据管理
- 根据元数据配置，动态生成业务数据的管理页面
- 支持功能：分页查询、新增、编辑、删除、详情查看
- 搜索条件：文本类使用 LIKE 模糊查询，下拉框/单选使用等值查询，日期/时间范围使用范围查询
- 导出 CSV（带 BOM 头，支持中文）
- 导入 CSV（自动校验列名，写入审计字段）
- 支持按表粒度的权限控制（可为每个元数据表独立配置增删改导出导入权限）

#### 3.4 元数据菜单创建
- 在元数据表列表页面，点击"创建菜单"自动生成系统菜单
- 自动生成菜单下的增、删、改、导出、导入 5 个按钮权限
- 权限标识格式：`meta:dynamic:{tableId}:{action}`

### 4. 数据库脚本

所有 SQL 脚本位于 `mysql/` 目录下：
- `sys_*.sql` - 系统表结构（用户、角色、菜单、字典、日志等）
- `tb_*.sql` - 元数据配置表结构（元数据表、列、列表配置、表单配置）
- `init_data.sql` - 初始化数据（管理员账号 admin/admin123、角色、菜单、按钮权限）

---

## 快速启动

### 前置条件
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 启动步骤

1. **创建数据库**
   ```sql
   CREATE DATABASE `meta_build` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **初始化数据库**
   ```bash
   # 按顺序执行 mysql/ 目录下的 SQL 文件
   mysql -u root -p meta_build < mysql/sys_user.sql
   mysql -u root -p meta_build < mysql/init_data.sql
   # ... 其他 SQL 脚本
   ```

3. **配置数据库连接**
   ```bash
   # 修改 project/admin/src/main/resources/application.yml
   # 配置 spring.datasource.url/username/password
   ```

4. **启动后端**
   ```bash
   cd project
   mvn clean install -DskipTests
   mvn -pl admin spring-boot:run
   ```

5. **启动前端**
   ```bash
   cd page
   npm install
   npm run dev
   ```

6. **访问系统**
   - 前端地址：http://localhost:5173
   - 默认账号：admin / admin123