# 后端项目结构 (Maven 多模块)

```
project/                                              # 父项目 (pom)
├── pom.xml                                           # 父 POM：依赖管理 + 模块声明
│
├── common/                                           # 公共模块 → 依赖: 无内部模块
│   ├── pom.xml                                       # 提供：基础框架、LogAspect、UserContext、监控拦截器
│   └── src/main/java/com/zm/common/
│       ├── annotation/
│       │   └── LogInfo.java                          # 操作日志注解（标注在 Controller 方法上）
│       ├── aspect/
│       │   ├── LogAspect.java                        # 操作日志切面（AOP 记录请求和响应日志）
│       │   └── TimingAspect.java                     # 接口耗时统计切面（记录耗时并持久化到DB）
│       ├── config/
│       │   ├── CorsConfig.java                       # CORS 跨域配置
│       │   ├── MybatisPlusConfig.java                # MyBatis-Plus 分页插件配置
│       │   └── SqlStatsInterceptor.java              # SQL 性能拦截器（超配置阈值 WARN 告警 + 持久化）
│       ├── entity/
│       │   ├── SysUserLog.java                       # 用户操作日志实体
│       │   ├── SlowSqlLog.java                       # 慢SQL日志实体
│       │   └── ApiTimingLog.java                     # 接口耗时日志实体
│       ├── enums/
│       │   ├── DeleteTypeEnum.java                   # 逻辑删除状态枚举
│       │   └── ServiceExceptionEnums.java            # 业务异常码枚举
│       ├── exception/
│       │   ├── GlobalExceptionHandler.java           # 全局异常处理器
│       │   └── ServiceException.java                 # 自定义业务异常
│       ├── mapper/
│       │   ├── UserLogMapper.java                    # 用户操作日志 Mapper
│       │   ├── SlowSqlLogMapper.java                 # 慢SQL日志 Mapper
│       │   └── ApiTimingLogMapper.java               # 接口耗时日志 Mapper
│       ├── util/
│       │   ├── PasswordGenerator.java                # 密码生成工具
│       │   └── UserContext.java                      # 用户上下文（ThreadLocal）
│       ├── valid/
│       │   └── ValidationGroups.java                 # Bean Validation 分组校验接口
│       └── vo/
│           ├── BaseEntity.java                       # 实体基类（审计字段 + 逻辑删除）
│           ├── BasePageVO.java                       # 分页查询参数
│           └── RespData.java                         # 统一响应体
│
├── system/                                           # 系统模块 → 依赖 common
│   ├── pom.xml                                       # 提供：RBAC 权限、JWT 认证、系统管理 API、监控 API
│   └── src/main/
│       ├── java/com/zm/system/
│       │   ├── config/security/
│       │   │   ├── JwtTokenProvider.java             # JWT 令牌生成/解析/校验
│       │   │   ├── JwtAuthenticationFilter.java      # JWT 认证过滤器
│       │   │   ├── CustomUserDetails.java            # 自定义 UserDetails
│       │   │   └── SecurityConfig.java               # Spring Security 配置
│       │   ├── controller/
│       │   │   ├── AuthController.java               # 登录/获取用户信息
│       │   │   ├── UserController.java               # 用户管理 CRUD（@PreAuthorize）
│       │   │   ├── RoleController.java               # 角色管理 CRUD（含分配菜单）
│       │   │   ├── MenuController.java               # 菜单管理 CRUD
│       │   │   ├── LogController.java                # 操作日志查询
│       │   │   ├── MonitorController.java            # 监控数据查询（慢SQL/接口耗时）
│       │   │   ├── DictTypeController.java           # 字典类型 CRUD
│       │   │   └── DictDataController.java           # 字典数据 CRUD
│       │   ├── dto/
│       │   │   ├── LoginRequest.java                 # 登录请求参数
│       │   │   ├── LoginResponse.java                # 登录响应
│       │   │   ├── UserQuery.java                    # 用户分页查询参数
│       │   │   ├── RoleQuery.java                    # 角色分页查询参数
│       │   │   ├── MenuQuery.java                    # 菜单分页查询参数
│       │   │   ├── LogQuery.java                     # 操作日志分页查询参数
│       │   │   ├── SlowSqlQuery.java                 # 慢SQL分页查询参数
│       │   │   └── ApiTimingQuery.java               # 接口耗时查询参数
│       │   ├── entity/
│       │   │   ├── SysUser.java                      # 系统用户实体
│       │   │   ├── SysRole.java                      # 系统角色实体
│       │   │   ├── SysMenu.java                      # 系统菜单实体
│       │   │   ├── SysUserRole.java                  # 用户-角色关联实体
│       │   │   ├── SysRoleMenu.java                  # 角色-菜单关联实体
│       │   │   ├── SysDictType.java                  # 字典类型实体
│       │   │   └── SysDictData.java                  # 字典数据实体
│       │   ├── mapper/
│       │   │   ├── UserMapper.java                   # 用户 Mapper
│       │   │   ├── RoleMapper.java                   # 角色 Mapper
│       │   │   ├── MenuMapper.java                   # 菜单 Mapper
│       │   │   ├── UserRoleMapper.java               # 用户-角色关联 Mapper
│       │   │   ├── RoleMenuMapper.java               # 角色-菜单关联 Mapper
│       │   │   ├── DictTypeMapper.java               # 字典类型 Mapper
│       │   │   └── DictDataMapper.java               # 字典数据 Mapper
│       │   └── service/
│       │       ├── AuthService.java                  # 认证服务接口
│       │       ├── impl/AuthServiceImpl.java         # 认证服务实现（登录/用户信息）
│       │       ├── UserService.java                  # 用户服务接口
│       │       ├── impl/UserServiceImpl.java         # 用户服务实现
│       │       ├── RoleService.java                  # 角色服务接口
│       │       ├── impl/RoleServiceImpl.java         # 角色服务实现
│       │       ├── MenuService.java                  # 菜单服务接口
│       │       ├── impl/MenuServiceImpl.java         # 菜单服务实现
│       │       ├── DictTypeService.java              # 字典类型服务接口
│       │       ├── impl/DictTypeServiceImpl.java     # 字典类型服务实现
│       │       ├── DictDataService.java              # 字典数据服务接口
│       │       └── impl/DictDataServiceImpl.java     # 字典数据服务实现
│       └── resources/
│           └── mapper/
│               ├── UserRoleMapper.xml                # 用户-角色关联 SQL
│               ├── MenuMapper.xml                    # 菜单 SQL
│               ├── RoleMapper.xml                    # 角色 SQL
│               ├── RoleMenuMapper.xml                # 角色-菜单关联 SQL
│               └── DictDataMapper.xml                # 字典数据 SQL
│
├── server/                                           # 业务模块 → 依赖 common
│   ├── pom.xml                                       # 提供：元数据管理功能
│   └── src/main/
│       ├── java/com/zm/server/
│       │   ├── controller/
│       │   │   ├── MetaTableController.java          # 元数据表控制器（CRUD + information_schema 查询）
│       │   │   ├── MetaConfigController.java         # 元数据配置控制器（列表/表单配置 CRUD + 拖拽排序）
│       │   │   └── MetaDynamicController.java        # 动态数据控制器（动态 CRUD + 导出CSV + 导入CSV）
│       │   ├── entity/
│       │   │   ├── MetaTable.java                    # 元数据表定义实体（tb_metadata_table）
│       │   │   ├── MetaColumn.java                   # 元数据列定义实体（tb_metadata_column）
│       │   │   ├── MetaListConfig.java               # 元数据列表配置实体（tb_metadata_list_config）
│       │   │   └── MetaFormConfig.java               # 元数据表单配置实体（tb_metadata_form_config）
│       │   ├── mapper/
│       │   │   ├── MetaTableMapper.java              # 元数据表 Mapper
│       │   │   ├── MetaColumnMapper.java             # 元数据列 Mapper
│       │   │   ├── MetaDbMapper.java                 # 数据库元数据查询 Mapper（information_schema）
│       │   │   ├── MetaListConfigMapper.java         # 元数据列表配置 Mapper
│       │   │   └── MetaFormConfigMapper.java         # 元数据表单配置 Mapper
│       │   └── service/
│       │       ├── MetaTableService.java             # 元数据表服务接口
│       │       ├── impl/MetaTableServiceImpl.java    # 元数据表服务实现
│       │       ├── MetaColumnService.java            # 元数据列服务接口
│       │       ├── impl/MetaColumnServiceImpl.java    # 元数据列服务实现
│       │       ├── MetaListConfigService.java        # 元数据列表配置服务接口
│       │       ├── impl/MetaListConfigServiceImpl.java # 元数据列表配置服务实现
│       │       ├── MetaFormConfigService.java        # 元数据表单配置服务接口
│       │       └── impl/MetaFormConfigServiceImpl.java # 元数据表单配置服务实现
│       └── resources/
│           └── mapper/
│               └── MetaDbMapper.xml                  # information_schema 查询 SQL
│
└── admin/                                            # 启动模块 → 依赖 system + server
    ├── pom.xml                                       # 可运行 JAR 入口（spring-boot-maven-plugin + Actuator）
    └── src/main/
        ├── java/com/zm/
        │   └── MetaBuildApplication.java             # Spring Boot 启动类
        └── resources/
            └── application.yml                       # 主配置（数据源、Redis、JWT、MyBatis-Plus、监控阈值、Actuator）

## 依赖链

admin (可运行 JAR)
  ├── system (JWT安全 + 控制器 + 业务服务 + 监控查询)
  │    └── common (基础框架 + LogAspect + UserContext + 监控实体 & 拦截器)
  └── server (元数据管理)
       └── common

## API 接口

| 模块   | 路径                                   | 方法 | 说明               |
|--------|----------------------------------------|------|--------------------|
| system | `/api/system/log/page`                 | POST | 操作日志分页查询   |
| system | `/api/system/monitor/slow-sql/page`    | POST | 慢SQL日志分页查询  |
| system | `/api/system/monitor/api-timing/page`  | POST | 接口耗时分页查询   |
| system | `/api/system/dict/type/page`            | POST | 字典类型分页查询   |
| system | `/api/system/dict/data/page`            | POST | 字典数据分页查询   |
| server | `/api/server/meta/table/db-tables`      | GET  | 查询数据库表列表（排除 sys_/tb_） |
| server | `/api/server/meta/table/db-columns/{tableName}` | GET | 查询指定表的列信息 |
| server | `/api/server/meta/table/page`           | POST | 元数据表分页查询   |
| server | `/api/server/meta/table`                | POST | 新增元数据表       |
| server | `/api/server/meta/table/{id}`           | GET  | 查询元数据表详情   |
| server | `/api/server/meta/table`                | PUT  | 修改元数据表       |
| server | `/api/server/meta/table/{id}`           | DELETE | 删除元数据表     |
| server | `/api/server/meta/table/{id}/columns`   | GET  | 查询元数据表的列列表 |
| server | `/api/server/meta/table/column`         | PUT  | 修改元数据列       |
| server | `/api/server/meta/table/column/dict-sql`| POST | 执行字典 SQL 查询  |
| server | `/api/server/meta/config/{tableId}/list` | GET  | 查询列表配置       |
| server | `/api/server/meta/config/{tableId}/list` | POST | 保存列表配置       |
| server | `/api/server/meta/config/{tableId}/form` | GET  | 查询表单配置       |
| server | `/api/server/meta/config/{tableId}/form` | POST | 保存表单配置       |
| server | `/api/server/meta/dynamic/{tableId}/page`    | POST | 动态数据分页查询   |
| server | `/api/server/meta/dynamic/{tableId}/{id}`   | GET  | 动态数据详情查询   |
| server | `/api/server/meta/dynamic/{tableId}`         | POST | 动态数据新增       |
| server | `/api/server/meta/dynamic/{tableId}`         | PUT  | 动态数据修改       |
| server | `/api/server/meta/dynamic/{tableId}/{id}`   | DELETE | 动态数据删除     |
| server | `/api/server/meta/dynamic/{tableId}/export`  | POST | 动态数据导出(CSV) |
| server | `/api/server/meta/dynamic/{tableId}/import`  | POST | 动态数据导入(CSV) |

## 关键路径

| 模块 | 源文件 | 配置文件 |
|------|--------|----------|
| common | `common/src/main/java/...` | 无 |
| system | `system/src/main/java/...` | `system/src/main/resources/mapper/*.xml` |
| server | `server/src/main/java/...` | `server/src/main/resources/mapper/*.xml` |
| admin | `admin/src/main/java/...` | `admin/src/main/resources/application.yml` |
| - | `mysql/*.sql`（项目根目录） | 数据库表结构 + 初始化数据 |
```