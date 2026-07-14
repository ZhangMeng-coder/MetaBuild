---
name: "project-structure"
description: "维护前后端项目结构文档 (page/structure.md & project/structure.md)。修改前端或后端代码时调用，检查已有内容、正确放置新文件、更新结构文档。"
---

# 项目结构维护

确保项目保持良好组织结构，自动维护结构文档的准确性。

## 调用时机

在以下情况**必须**调用此 skill：
- 在 `page/src/` 或 `project/src/` 中添加新文件或目录时
- 修改前端或后端的已有文件时
- 创建新的业务模块、组件、服务或工具时
- 新增或修改数据库表结构时

## 规则

1. **先检查再创建** — 添加任何新文件前，先查看对应的 `structure.md` 和已有代码，确认是否已有可复用的内容。尽量复用和扩展已有代码，而不是重复创建。

2. **正确归类放置** — 新文件必须按其类型和功能放入正确的目录：
   - **page/** — 前端项目 (Vue3 + TypeScript + Element Plus)
   - **project/** — 后端项目 (Spring Boot + MyBatis-Plus)

3. **更新结构文档** — 对 `page/src/` 或 `project/src/` 做任何修改后，必须同步更新对应的 `structure.md` 文件（增/删/改文件条目）。

4. **数据库表结构管理** — 数据库表结构 SQL 保存在 `mysql/` 目录下：
   - 每个表一个独立的 `.sql` 文件，文件名为表名，如 `user.sql`
   - 文件中包含该表的 `CREATE TABLE` 语句
   - 初始化 `INSERT` 语句统一保存在 `mysql/init_data.sql` 文件中，不分散到各表文件

## 结构文件

| 项目 | 结构文件 |
|------|---------|
| 前端 | `page/structure.md` |
| 后端 | `project/structure.md` |
