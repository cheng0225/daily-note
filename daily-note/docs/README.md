# 学习文档索引

欢迎阅读「每日便签」源码。建议按下面顺序学习。

## 推荐阅读顺序

| 顺序 | 文档 | 内容 |
|------|------|------|
| 1 | [项目总览](01-项目总览.md) | 目录结构、技术栈、运行方式 |
| 2 | [数据层](02-数据层.md) | Room 数据库、Repository、数据怎么存 |
| 3 | [主界面](03-主界面.md) | MainActivity、列表、增删改查 |
| 4 | [桌面小组件](04-桌面小组件.md) | App Widget、透明样式、点击打钩 |
| 5 | [每日重置](05-每日重置.md) | 闹钟、开机补重置、改时间逻辑 |
| 6 | [设置页](06-设置.md) | 便签透明度、小组件样式 |
| 7 | [在线更新](07-在线更新.md) | GitHub Release 检查、下载、安装 |

## 源码文件对照

```
app/src/main/java/com/vibecoding/dailytasks/
├── DailyTasksApp.kt       → 应用入口，全局单例
├── MainActivity.kt        → 主界面（见 03）
├── SettingsActivity.kt    → 设置页（见 06）
├── ResetScheduler.kt      → 闹钟调度（见 05）
├── data/                  → 数据层（见 02）
├── ui/            # 列表适配器
├── widget/                → 小组件（见 04）
├── update/                → 在线更新（见 07）
└── receiver/              → 广播接收器（见 05）
```

## 需求文档

产品需求在上级目录：[vibe-coding](../../vibe-coding/README.md)

## 改代码时

- 改 UI 布局：`app/src/main/res/layout/`
- 改小组件样式：`widget_task_list.xml`、`widget_task_item.xml`、`colors.xml`
- 改业务逻辑：优先看 `TaskRepository.kt`
