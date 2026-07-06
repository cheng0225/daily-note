# 开发路线

> Android 工程：[`daily-note/`](../../daily-note/)

## Phase 0 — 需求与设计 ✅

- [x] 明确 MVP 功能范围
- [x] 编写需求文档与技术方案
- [x] 确认待决问题（任务管理、空列表起步、样式延后、应用名「每日便签」）
- [x] 确认实现细节（App 打钩、排序、上限 15、重置改时间策略）
- [x] 初始化 Android 工程（Kotlin + Gradle）

## Phase 1 — 数据与设置页 ✅

- [x] 搭建项目骨架、包结构
- [x] 实现任务实体与本地存储（Room）
- [x] MainActivity：任务增删改、打钩、重置时间设置
- [x] 手动「立即重置」按钮

## Phase 2 — 桌面小组件 ✅

- [x] Widget 布局与 `TaskWidgetProvider`
- [x] 展示任务列表
- [x] 点击打钩 / 取消打钩
- [x] 与 Repository 联动刷新

## Phase 3 — 每日自动重置 ✅

- [x] `DailyResetReceiver` + `AlarmManager`
- [x] 修改重置时间后重新注册闹钟
- [x] `BOOT_COMPLETED` 开机补重置
- [x] 打开 App 时日期校验（兜底）

## Phase 4 — 打磨与自测 🔄 当前阶段

- [ ] 在真机（含 vivo / 其他 ROM）验证小组件
- [ ] 边界：空任务列表、任务很多、跨时区、夏令时
- [ ] 应用图标、小组件预览图
- [ ] 签名打包，安装到自用机
- [x] **在线更新**（GitHub Release 检查 + App 内下载安装，v1.3）

## 后续可选（非 MVP）

- 多套便签 / 多 Widget 实例绑定不同任务集
- 主题与字体大小
- 完成进度环、动效
- 导出备份任务列表
- 设置页「手动检查更新」入口

---

*v1.3 在线更新已实现；MVP 代码完成，待在真机验证。*
