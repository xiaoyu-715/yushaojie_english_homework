# Implementation Plan

## Phase 1: 基础设施准备

- [x] 1. 创建 NavigationHelper 工具类





  - [x] 1.1 创建 `utils/NavigationHelper.java` 文件


    - 创建私有构造函数防止实例化
    - 实现通用 `navigateTo()` 方法
    - 实现 `navigateForResult()` 方法
    - 添加异常处理和日志记录
    - _Requirements: 3.1, 3.2, 3.3_
  - [x] 1.2 添加核心功能页面跳转方法


    - 实现 `toVocabulary()` 方法
    - 实现 `toExamList()` 方法
    - 实现 `toMockExam()` 方法
    - 实现 `toWrongQuestion()` 方法
    - 实现 `toStudyPlan()` 方法
    - 实现 `toDailySentence()` 方法
    - 实现 `toDailyTask()` 方法
    - 实现 `toCameraTranslation()` 方法
    - 实现 `toGlmChat()` 方法
    - 实现 `toTextTranslation()` 方法
    - _Requirements: 3.1_
  - [x] 1.3 添加导航栏页面跳转方法


    - 实现 `toReport()` 方法
    - 实现 `toProfile()` 方法
    - 实现 `toMore()` 方法
    - _Requirements: 3.1_
  - [x] 1.4 编写 NavigationHelper 单元测试


    - 测试各跳转方法是否正确创建 Intent
    - 测试参数传递是否正确
    - **Property 1: NavigationHelper 参数传递正确性**
    - **Validates: Requirements 3.2**
    - _Requirements: 3.2_

## Phase 2: MainActivity ViewBinding 迁移

- [x] 2. 迁移 MainActivity 到 ViewBinding





  - [x] 2.1 修改 MainActivity 使用 ViewBinding


    - 添加 `ActivityMainBinding binding` 成员变量
    - 在 `onCreate()` 中使用 `ActivityMainBinding.inflate()` 初始化
    - 使用 `setContentView(binding.getRoot())` 设置内容视图
    - 删除所有 `findViewById()` 调用
    - 删除所有单独的 View 成员变量声明
    - _Requirements: 1.1, 1.2_
  - [x] 2.2 更新 MainActivity 视图访问方式


    - 将 `tvStudyDays` 替换为 `binding.tvStudyDays`
    - 将 `tvVocabularyCount` 替换为 `binding.tvVocabularyCount`
    - 将 `tvExamScore` 替换为 `binding.tvExamScore`
    - 将 `tvTaskProgress` 替换为 `binding.tvTaskProgress`
    - 更新所有其他视图访问
    - _Requirements: 1.2_
  - [x] 2.3 添加 onDestroy 清理逻辑


    - 重写 `onDestroy()` 方法
    - 在 `onDestroy()` 中设置 `binding = null`
    - _Requirements: 1.3_

## Phase 3: Lambda 表达式迁移

- [x] 3. 迁移 MainActivity 点击事件到 Lambda 表达式





  - [x] 3.1 重构 setupClickListeners 方法


    - 将所有匿名内部类 `new View.OnClickListener()` 替换为 Lambda
    - 使用 NavigationHelper 替代直接创建 Intent
    - 简化单行 Lambda 表达式（省略大括号）
    - _Requirements: 2.1, 3.4_

  - [x] 3.2 更新核心功能点击事件





    - 更新 `llVocabulary` 点击事件
    - 更新 `llRealExam` 点击事件
    - 更新 `llMockExam` 点击事件
    - 更新 `llErrorBook` 点击事件
    - 更新 `llStudyPlan` 点击事件
    - 更新 `llDailySentence` 点击事件
    - 更新 `llDailyTask` 点击事件
    - 更新 `llCameraTranslation` 点击事件
    - 更新 `llAiAssistant` 点击事件
    - 更新 `llTextTranslation` 点击事件

    - _Requirements: 2.1, 3.4_
  - [x] 3.3 更新导航栏点击事件





    - 更新 `navReport` 点击事件
    - 更新 `navProfile` 点击事件
    - 更新 `navMore` 点击事件
    - _Requirements: 2.1, 3.4_

- [x] 4. Checkpoint - 确保所有测试通过






  - 确保所有测试通过，如有问题请询问用户。

## Phase 4: 代码清理和验证

- [x] 5. 清理和验证 MainActivity





  - [x] 5.1 删除冗余代码


    - 删除 `initViews()` 方法（不再需要）
    - 删除所有单独的 View 成员变量
    - 合并或简化重复逻辑
    - _Requirements: 5.4_
  - [x] 5.2 验证编译和功能


    - 确保项目编译通过
    - 验证所有点击事件正常工作
    - 验证数据显示正常
    - **Property 2: 编译验证**
    - **Validates: Requirements 4.4**
    - _Requirements: 1.5, 2.4, 5.5_
  - [x] 5.3 编写 MainActivity UI 测试


    - 测试各功能按钮点击跳转
    - 测试数据显示正确性
    - _Requirements: 5.5_

## Phase 5: 代码结构整理（可选）

- [x] 6. 整理 Activity 文件结构









  - [x] 6.1 创建 ui/activity 包结构


    - 创建 `ui/activity/` 目录
    - _Requirements: 4.1_
  - [x] 6.2 移动 MainActivity 到新包


    - 移动 `MainActivity.java` 到 `ui/activity/`
    - 更新包声明
    - 更新 AndroidManifest.xml 中的类路径
    - _Requirements: 4.1, 4.2_
  - [x] 6.3 更新所有引用


    - 更新 NavigationHelper 中的类引用
    - 更新其他文件中的 MainActivity 引用
    - _Requirements: 4.3_
  - [x] 6.4 验证编译和功能










    - 确保项目编译通过
    - 验证应用正常启动
    - _Requirements: 4.4_

- [x] 7. Final Checkpoint - 确保所有测试通过





  - 确保所有测试通过，如有问题请询问用户。
