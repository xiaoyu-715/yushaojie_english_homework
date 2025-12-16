## 文本翻译接入智谱AI调整记录

- **默认模型切换**：`ZhipuAIService` 的默认模型从 `glm-4.5-flash` 更改为 `glm-4-flash-250414`，对应智谱最新免费快速模型，路径 `app/src/main/java/com/example/mybighomework/api/ZhipuAIService.java`。
- **文本翻译调用链**：`TextTranslationActivity` 调用 `ZhipuAIService.translate` 完成文本翻译，结果用 `TranslationTextProcessor.formatTranslationResult` 规范化后展示并写入翻译历史。
- **API Key 获取**：从 `SharedPreferences("zhipuai_config", "api_key")` 读取；若为空，会写入与 `ExamAnswerActivity` 一致的默认测试值，便于快速体验。实际使用请在设置界面填入自己的 Key。
- **超时策略**：调用等待最长 15 秒，超时或异常会提示用户；可在 `TextTranslationActivity` 调整等待时间。
- **切换模型方法**：如需改用其他型号（如 `glm-4-flash-lite`），只需修改 `ZhipuAIService.DEFAULT_MODEL` 常量即可，其他代码无需改动。

### 输入翻译功能实现（全面说明）

1) 入口与流程（TextTranslationActivity）
- 预处理：`TranslationTextProcessor.preprocessText()` 清理输入文本。
- 调用：`translateText(sourceText, sourceLanguage, targetLanguage)`（后台线程执行）。
- 展示与存档：成功后 `tvTranslatedText.setText(...)`，并 `saveTranslationHistory(...)` 入库。
- 语言切换：按钮切换 `zh-CHS` / `en`，UI 显示方向 `中文 → 英文` 或 `英文 → 中文`。
- 进度与状态：`showProgress(true/false)` 控制进度条、按钮可用状态。
- 超时：等待智谱响应最长 15s，超时提示“翻译超时，请稍后重试”。

2) 模型调用（ZhipuAIService）
- 默认模型：`glm-4-flash-250414`（免费快速版），常量位置：
  `app/src/main/java/com/example/mybighomework/api/ZhipuAIService.java`
- API 端点：`https://open.bigmodel.cn/api/paas/v4/chat/completions`
- 调用方式：`ZhipuAIService.translate(text, srcLang, tgtLang, TranslateCallback)`
  - `onSuccess(String content)`：返回译文字符串（已是模型输出）。
  - `onError(String error)`：返回错误信息。
- 线程：服务内部使用线程池并通过回调返回结果。
- 关闭：调用完毕后 `service.shutdown()` 释放线程池。

3) API Key 读取与回退
```
app/src/main/java/com/example/mybighomework/TextTranslationActivity.java
private String loadZhipuApiKey() {
    SharedPreferences prefs = getSharedPreferences("zhipuai_config", MODE_PRIVATE);
    String apiKey = prefs.getString("api_key", "");
    if (TextUtils.isEmpty(apiKey)) {
        // 兼容 ExamAnswerActivity 的默认测试值
        apiKey = "e1b0c0c6ee7942908b11119e8fca3efa.w86kmtMVZLXo1vjE";
        prefs.edit().putString("api_key", apiKey).apply();
    }
    return apiKey;
}
```
- 正式使用请在设置界面写入自己的 Key（SharedPreferences 名：`zhipuai_config`，键：`api_key`）。

4) 文本翻译核心调用与超时
```
// 调用智谱翻译，最长等待 15s
boolean ok = latch.await(15, TimeUnit.SECONDS);
if (!ok) return "翻译超时，请稍后重试";
if (errorHolder[0] != null) return "翻译失败：" + errorHolder[0];
return resultHolder[0] == null ? "未获取到翻译结果" : resultHolder[0];
```
- 结果规范化：`TranslationTextProcessor.formatTranslationResult(content)`
- 语言映射：`zh-CHS` → `zh`，`en` 原样，便于模型识别。

5) 翻译历史存储
- 表：`translation_history`（Room 实体 `TranslationHistoryEntity`）
- DAO：`TranslationHistoryDao.insert(...)`
- 逻辑：翻译成功后后台线程写入原文、译文、语言方向、时间戳等。

6) 切换模型
- 仅需修改一处常量：
```
app/src/main/java/com/example/mybighomework/api/ZhipuAIService.java
private static final String DEFAULT_MODEL = "glm-4-flash-250414";
```
- 可替换为其它可用型号（如 `glm-4-flash-lite` / `glm-3-turbo`），无需改调用逻辑。

### 关键代码变更摘录

1) 默认模型
```
app/src/main/java/com/example/mybighomework/api/ZhipuAIService.java
// 默认模型（使用免费的 glm-4-Flash-250414）
private static final String DEFAULT_MODEL = "glm-4-flash-250414";
```

2) 文本翻译调用链（核心调用与超时）
```
app/src/main/java/com/example/mybighomework/TextTranslationActivity.java
// 等待智谱翻译结果，最长 15s
boolean ok = latch.await(15, TimeUnit.SECONDS);
if (!ok) {
    return "翻译超时，请稍后重试";
}

// 翻译入口
String translatedText = translateText(finalSourceText, finalSourceLanguage, finalTargetLanguage);
tvTranslatedText.setText(translatedText);
saveTranslationHistory(finalSourceText, translatedText);
```

3) API Key 读取与回退
```
app/src/main/java/com/example/mybighomework/TextTranslationActivity.java
private String loadZhipuApiKey() {
    SharedPreferences prefs = getSharedPreferences("zhipuai_config", MODE_PRIVATE);
    String apiKey = prefs.getString("api_key", "");
    if (TextUtils.isEmpty(apiKey)) {
        // 兼容 ExamAnswerActivity 的默认测试值
        apiKey = "e1b0c0c6ee7942908b11119e8fca3efa.w86kmtMVZLXo1vjE";
        prefs.edit().putString("api_key", apiKey).apply();
    }
    return apiKey;
}
```

