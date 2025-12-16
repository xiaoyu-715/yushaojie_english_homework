# Requirements Document

## Introduction

本文档定义了英语学习助手 (MyBigHomeWork) Android 应用的高优先级代码优化需求。优化目标是提升代码质量、可维护性和开发效率，主要包括：ViewBinding 全面迁移、Lambda 表达式简化、代码结构整理和导航逻辑统一。

## Glossary

- **ViewBinding**: Android 提供的视图绑定功能，自动生成绑定类替代 findViewById
- **Lambda 表达式**: Java 8 引入的简化匿名内部类的语法
- **Activity**: Android 应用的界面组件
- **NavigationHelper**: 统一管理页面跳转的工具类
- **findViewById**: 传统的视图查找方法，类型不安全且代码冗长

## Requirements

### Requirement 1: ViewBinding 迁移

**User Story:** As a 开发者, I want to 使用 ViewBinding 替代 findViewById, so that 代码更简洁、类型安全且减少空指针异常风险。

#### Acceptance Criteria

1. WHEN 开发者打开 MainActivity THEN the system SHALL 使用 ActivityMainBinding 类访问所有视图元素
2. WHEN 开发者在 Activity 中访问视图 THEN the system SHALL 通过 binding 对象直接访问而非 findViewById
3. WHEN Activity 被销毁 THEN the system SHALL 在 onDestroy 中将 binding 设置为 null 以避免内存泄漏
4. WHEN 编译项目 THEN the system SHALL 自动生成所有布局文件对应的 Binding 类
5. WHEN ViewBinding 迁移完成 THEN the system SHALL 确保所有原有功能正常运行

### Requirement 2: Lambda 表达式简化点击事件

**User Story:** As a 开发者, I want to 使用 Lambda 表达式替代匿名内部类, so that 点击事件代码更简洁易读。

#### Acceptance Criteria

1. WHEN 设置点击监听器 THEN the system SHALL 使用 Lambda 表达式 `v -> {}` 格式而非匿名内部类
2. WHEN 点击事件只包含单行代码 THEN the system SHALL 省略大括号使用单行 Lambda
3. WHEN 点击事件需要多行代码 THEN the system SHALL 使用带大括号的 Lambda 表达式
4. WHEN Lambda 表达式迁移完成 THEN the system SHALL 确保所有点击事件功能正常

### Requirement 3: 统一导航逻辑

**User Story:** As a 开发者, I want to 创建统一的导航工具类, so that 页面跳转逻辑集中管理、减少重复代码。

#### Acceptance Criteria

1. WHEN 需要跳转到任意 Activity THEN the system SHALL 通过 NavigationHelper 的静态方法实现跳转
2. WHEN 跳转需要传递参数 THEN the system SHALL 支持通过方法参数传递 Intent extras
3. WHEN 需要带返回结果的跳转 THEN the system SHALL 提供 startActivityForResult 的封装方法
4. WHEN 导航工具类创建完成 THEN the system SHALL 替换 MainActivity 中所有直接创建 Intent 的代码

### Requirement 4: 代码结构整理

**User Story:** As a 开发者, I want to 将 Activity 文件按功能模块分组到子包中, so that 项目结构更清晰、易于维护。

#### Acceptance Criteria

1. WHEN 查看项目结构 THEN the system SHALL 将所有 Activity 文件移动到 `ui/activity/` 包下
2. WHEN Activity 文件被移动 THEN the system SHALL 更新 AndroidManifest.xml 中的类路径引用
3. WHEN Activity 文件被移动 THEN the system SHALL 更新所有 Intent 跳转中的类引用
4. WHEN 代码结构整理完成 THEN the system SHALL 确保应用编译通过且所有功能正常

### Requirement 5: MainActivity 优化示范

**User Story:** As a 开发者, I want to 将 MainActivity 作为优化示范, so that 其他 Activity 可以参照相同模式进行优化。

#### Acceptance Criteria

1. WHEN 查看优化后的 MainActivity THEN the system SHALL 展示 ViewBinding 的正确使用方式
2. WHEN 查看优化后的 MainActivity THEN the system SHALL 展示 Lambda 表达式的正确使用方式
3. WHEN 查看优化后的 MainActivity THEN the system SHALL 展示 NavigationHelper 的正确使用方式
4. WHEN 优化完成 THEN the system SHALL 使 MainActivity 代码量减少至少 30%
5. WHEN 优化完成 THEN the system SHALL 保持所有原有功能正常运行
