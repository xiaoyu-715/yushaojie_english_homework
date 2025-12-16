## 概览
- 模型：GLM-4.6V-Flash（多模态，当前以文本流式返回为主）
- 端点：`https://open.bigmodel.cn/api/paas/v4/chat/completions`
- 默认模型名：`glm-4.6v-flash`
- 主要代码：`app/src/main/java/com/example/mybighomework/api/Glm46vApiService.java`
- 聊天界面：`app/src/main/java/com/example/mybighomework/GlmChatActivity.java`
- API Key 存储：`SharedPreferences` 名称 `glm46v_config`，键 `api_key`

## 配置步骤
1) 打开应用 → 更多 → AI 学习助手（GLM-4.6V-Flash）。  
2) 首次进入会弹出 “配置 GLM-4.6V-Flash (智谱) API Key” 对话框。  
3) 粘贴从 https://open.bigmodel.cn 获取的 API Key，保存后即可对话。  

## 请求要点（已在封装内处理）
- Header：`Authorization: Bearer <API_KEY>`，`Content-Type: application/json`
- Body 字段：`model` 设置为 `glm-4.6v-flash`，`messages` 为数组（role/content）
- 流式：请求体添加 `"stream": true`；客户端解析 `data:` SSE 分片。

## 主要类说明
- `Glm46vApiService`
  - `chat(...)`：同步拉取完整回答。
  - `chatStream(...)`：流式回调 `onChunk / onComplete / onError`。
  - 可调用 `setApiKey` 动态替换 Key。
- `GlmChatActivity`
  - 入口界面、流式展示、API Key 配置弹窗、学习计划入口。
  - 使用布局 `app/src/main/res/layout/activity_glm_chat.xml`。
- `StudyPlanExtractor`
  - 依赖 `Glm46vApiService` 从对话中提取学习计划。

## 如何替换/升级模型
- 调整 `Glm46vApiService.DEFAULT_MODEL`，或在调用时传入自定义 `model`。
- 端点若有变更（如新版路径），同步更新 `API_ENDPOINT`。

## 测试要点
- 基本对话：输入英文句子，确认流式返回正常。
- Key 错误：应弹 Toast 显示错误。
- 重新配置 Key：设置对话框重复打开并保存，验证可用。

