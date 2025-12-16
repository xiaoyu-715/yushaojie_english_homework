# Implementation Plan

## Phase 1: VocabularyActivity 优化

- [ ] 1. 迁移 VocabularyActivity 到 ViewBinding
  - [ ] 1.1 修改 VocabularyActivity 使用 ViewBinding
    - 添加 `ActivityVocabularyBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityVocabularyBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 1.1, 1.2_

  - [ ] 1.2 更新 VocabularyActivity 视图访问方式
    - 将所有 View 变量访问替换为 `binding.viewId` 形式
    - 更新词汇显示相关视图（tvWord, tvPhonetic, tvMeaning 等）
    - 更新按钮视图（btnKnow, btnUnknow, btnBack 等）
    - 更新进度相关视图
    - _Requirements: 1.2_

  - [ ] 1.3 重构 VocabularyActivity 点击事件
    - 将所有匿名内部类替换为 Lambda 表达式
    - 简化单行 Lambda（省略大括号）
    - 使用 NavigationHelper 处理页面跳转（如果有）
    - _Requirements: 1.4_

  - [ ] 1.4 添加 onDestroy 清理逻辑
    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 1.3_

  - [ ] 1.5 验证 VocabularyActivity 功能
    - 确保项目编译通过
    - 验证词汇显示正常
    - 验证"认识"/"不认识"按钮功能正常
    - 验证学习进度记录正常
    - _Requirements: 1.5_

## Phase 2: ExamListActivity 优化

- [ ] 2. 迁移 ExamListActivity 到 ViewBinding
  - [ ] 2.1 修改 ExamListActivity 使用 ViewBinding
    - 添加 `ActivityExamListBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityExamListBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 2.1, 2.2_

  - [ ] 2.2 更新 ExamListActivity 视图访问方式
    - 将 RecyclerView 访问替换为 `binding.recyclerView`
    - 更新返回按钮访问为 `binding.btnBack`
    - 更新其他视图访问
    - _Requirements: 2.2_

  - [ ] 2.3 重构 ExamListActivity 点击事件
    - 将所有匿名内部类替换为 Lambda 表达式
    - 更新 Adapter 中的点击事件为 Lambda
    - 使用 NavigationHelper 处理页面跳转
    - _Requirements: 2.4_

  - [ ] 2.4 添加 onDestroy 清理逻辑
    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 2.3_

  - [ ] 2.5 验证 ExamListActivity 功能
    - 确保项目编译通过
    - 验证真题列表显示正常
    - 验证列表项点击跳转正常
    - 验证返回按钮功能正常
    - _Requirements: 2.5_

## Phase 3: ReportActivity 优化

- [ ] 3. 迁移 ReportActivity 到 ViewBinding
  - [ ] 3.1 修改 ReportActivity 使用 ViewBinding
    - 添加 `ActivityReportBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityReportBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 3.1, 3.2_

  - [ ] 3.2 更新 ReportActivity 视图访问方式
    - 更新导航栏视图访问（navHome, navProfile, navMore）
    - 更新统计数据视图访问
    - 更新图表视图访问
    - _Requirements: 3.2_

  - [ ] 3.3 重构 ReportActivity 点击事件
    - 将所有匿名内部类替换为 Lambda 表达式
    - 使用 NavigationHelper 处理导航栏跳转
    - 简化点击事件代码
    - _Requirements: 3.4_

  - [ ] 3.4 添加 onDestroy 清理逻辑
    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 3.3_

  - [ ] 3.5 验证 ReportActivity 功能
    - 确保项目编译通过
    - 验证学习数据统计显示正常
    - 验证图表渲染正常
    - 验证导航栏切换正常
    - _Requirements: 3.5_

## Phase 4: ProfileActivity 优化

- [ ] 4. 迁移 ProfileActivity 到 ViewBinding
  - [ ] 4.1 修改 ProfileActivity 使用 ViewBinding
    - 添加 `ActivityProfileBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityProfileBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 4.1, 4.2_

  - [ ] 4.2 更新 ProfileActivity 视图访问方式
    - 更新导航栏视图访问
    - 更新用户信息视图访问（头像、昵称、邮箱等）
    - 更新功能列表视图访问
    - _Requirements: 4.2_

  - [ ] 4.3 重构 ProfileActivity 点击事件
    - 将所有匿名内部类替换为 Lambda 表达式
    - 使用 NavigationHelper 处理导航栏和功能项跳转
    - 简化点击事件代码
    - _Requirements: 4.4_

  - [ ] 4.4 添加 onDestroy 清理逻辑
    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 4.3_

  - [ ] 4.5 验证 ProfileActivity 功能
    - 确保项目编译通过
    - 验证用户信息显示正常
    - 验证功能项点击跳转正常
    - 验证导航栏切换正常
    - _Requirements: 4.5_

## Phase 5: MoreActivity 优化

- [ ] 5. 迁移 MoreActivity 到 ViewBinding
  - [ ] 5.1 修改 MoreActivity 使用 ViewBinding
    - 添加 `ActivityMoreBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityMoreBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 5.1, 5.2_

  - [ ] 5.2 更新 MoreActivity 视图访问方式
    - 更新导航栏视图访问
    - 更新功能列表视图访问
    - _Requirements: 5.2_

  - [ ] 5.3 重构 MoreActivity 点击事件
    - 将所有匿名内部类替换为 Lambda 表达式
    - 使用 NavigationHelper 处理所有页面跳转
    - 简化点击事件代码
    - _Requirements: 5.4_

  - [ ] 5.4 添加 onDestroy 清理逻辑
    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 5.3_

  - [ ] 5.5 验证 MoreActivity 功能
    - 确保项目编译通过
    - 验证功能列表显示正常
    - 验证所有功能入口跳转正常
    - 验证导航栏切换正常
    - _Requirements: 5.5_

## Phase 6: NavigationHelper 扩展

- [ ] 6. 扩展 NavigationHelper 支持新页面
  - [ ] 6.1 添加缺失的导航方法
    - 检查优化的 Activity 中使用的跳转
    - 添加 `toSettings()` 方法（如果缺失）
    - 添加 `toEditProfile()` 方法（如果缺失）
    - 添加 `toBackup()` 方法（如果缺失）
    - 添加 `toFeedback()` 方法（如果缺失）
    - 添加 `toAbout()` 方法（如果缺失）
    - 添加 `toExamDetail()` 方法（如果缺失）
    - _Requirements: 6.4_

  - [ ] 6.2 更新 NavigationHelper 测试
    - 为新添加的方法编写单元测试
    - 验证所有导航方法正常工作
    - _Requirements: 7.1_

## Phase 7: 代码质量检查和测试

- [ ] 7. 代码质量和一致性检查
  - [ ] 7.1 代码风格统一检查
    - 检查所有优化的 Activity 代码风格一致
    - 确保 ViewBinding 初始化方式统一
    - 确保 Lambda 表达式使用统一
    - 确保 NavigationHelper 使用统一
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [ ] 7.2 注释和文档完善
    - 为优化的代码添加必要注释
    - 更新相关文档
    - _Requirements: 6.5_

  - [ ] 7.3 编译和功能验证
    - 确保项目完整编译通过
    - 运行应用验证所有优化的 Activity
    - 测试所有页面跳转流程
    - 测试所有核心功能
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

  - [ ] 7.4 回归测试
    - 验证原有功能未受影响
    - 验证数据显示准确性
    - 验证用户交互流畅性
    - _Requirements: 7.4_

- [ ] 8. Final Checkpoint - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户。
