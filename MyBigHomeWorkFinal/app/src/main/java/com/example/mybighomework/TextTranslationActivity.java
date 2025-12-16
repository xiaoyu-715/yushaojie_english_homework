package com.example.mybighomework;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mybighomework.api.ZhipuAIService;
import com.example.mybighomework.api.ZhipuAIService.TranslateCallback;
import com.example.mybighomework.database.AppDatabase;
import com.example.mybighomework.database.entity.TranslationHistoryEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 文本翻译Activity
 * 支持中英文文本翻译，并保存翻译历史
 */
public class TextTranslationActivity extends AppCompatActivity {
    
    private static final String TAG = "TextTranslation";
    
    // UI组件
    private EditText etSourceText;
    private TextView tvTranslatedText;
    private Button btnTranslate;
    private Button btnSwitchLanguage;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvLanguageDirection;
    
    // 语言设置
    private String sourceLanguage = "zh-CHS";  // 默认中文
    private String targetLanguage = "en";        // 默认英文
    
    // 数据库和线程
    private AppDatabase database;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_translation);
        
        initViews();
        initDatabase();
        setupListeners();
        updateLanguageDisplay();
    }
    
    private void initViews() {
        etSourceText = findViewById(R.id.et_source_text);
        tvTranslatedText = findViewById(R.id.tv_translated_text);
        btnTranslate = findViewById(R.id.btn_translate);
        btnSwitchLanguage = findViewById(R.id.btn_switch_language);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);
        tvLanguageDirection = findViewById(R.id.tv_language_direction);
    }
    
    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnTranslate.setOnClickListener(v -> performTranslation());
        
        btnSwitchLanguage.setOnClickListener(v -> switchLanguage());
    }
    
    private void switchLanguage() {
        // 切换语言方向
        String temp = sourceLanguage;
        sourceLanguage = targetLanguage;
        targetLanguage = temp;
        
        // 交换输入和输出文本
        String sourceText = etSourceText.getText().toString();
        String translatedText = tvTranslatedText.getText().toString();
        
        etSourceText.setText(translatedText);
        tvTranslatedText.setText("");
        
        updateLanguageDisplay();
        Toast.makeText(this, "已切换语言方向", Toast.LENGTH_SHORT).show();
    }
    
    private void updateLanguageDisplay() {
        String fromText = sourceLanguage.equals("zh-CHS") ? "中文" : "英文";
        String toText = targetLanguage.equals("zh-CHS") ? "中文" : "英文";
        String displayText = fromText + " → " + toText;
        tvLanguageDirection.setText(displayText);
    }
    
    private void performTranslation() {
        String sourceText = etSourceText.getText().toString().trim();
        
        if (TextUtils.isEmpty(sourceText)) {
            Toast.makeText(this, "请输入要翻译的文本", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 预处理文本
        sourceText = TranslationTextProcessor.preprocessText(sourceText);
        etSourceText.setText(sourceText);
        
        showProgress(true);
        tvTranslatedText.setText("");
        
        // 创建 final 变量供 lambda 使用
        final String finalSourceText = sourceText;
        final String finalSourceLanguage = sourceLanguage;
        final String finalTargetLanguage = targetLanguage;
        
        // 执行翻译（使用简单的离线翻译或API）
        executor.execute(() -> {
            try {
                // 这里可以调用有道文本翻译API或其他翻译服务
                // 目前使用简单的占位实现
                String translatedText = translateText(finalSourceText, finalSourceLanguage, finalTargetLanguage);
                
                final String finalTranslatedText = translatedText;
                runOnUiThread(() -> {
                    showProgress(false);
                    if (finalTranslatedText != null && !finalTranslatedText.isEmpty()) {
                        tvTranslatedText.setText(finalTranslatedText);
                        // 保存翻译历史
                        saveTranslationHistory(finalSourceText, finalTranslatedText);
                    } else {
                        Toast.makeText(this, "翻译失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                final String errorMessage = e.getMessage();
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(this, "翻译出错: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * 翻译文本（占位实现）
     * 实际应用中应该调用有道文本翻译API或其他翻译服务
     */
    private String translateText(String text, String from, String to) {
        String apiKey = loadZhipuApiKey();
        if (TextUtils.isEmpty(apiKey)) {
            return "未配置智谱AI API Key";
        }

        String src = from.equalsIgnoreCase("zh-CHS") ? "zh" : from;
        String tgt = to.equalsIgnoreCase("zh-CHS") ? "zh" : to;

        ZhipuAIService service = new ZhipuAIService(apiKey);
        CountDownLatch latch = new CountDownLatch(1);
        final String[] resultHolder = {null};
        final String[] errorHolder = {null};

        service.translate(text, src, tgt, new TranslateCallback() {
            @Override
            public void onSuccess(String content) {
                resultHolder[0] = TranslationTextProcessor.formatTranslationResult(content);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                errorHolder[0] = error;
                latch.countDown();
            }
        });

        try {
            boolean ok = latch.await(15, TimeUnit.SECONDS);
            service.shutdown();
            if (!ok) {
                return "翻译超时，请稍后重试";
            }
            if (errorHolder[0] != null) {
                return "翻译失败：" + errorHolder[0];
            }
            return resultHolder[0] == null ? "未获取到翻译结果" : resultHolder[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "翻译被中断";
        }
    }

    private String loadZhipuApiKey() {
        SharedPreferences prefs = getSharedPreferences("zhipuai_config", MODE_PRIVATE);
        String apiKey = prefs.getString("api_key", "");
        if (TextUtils.isEmpty(apiKey)) {
            // 兼容 ExamAnswerActivity 中的默认值，方便快速体验
            apiKey = "e1b0c0c6ee7942908b11119e8fca3efa.w86kmtMVZLXo1vjE";
            prefs.edit().putString("api_key", apiKey).apply();
        }
        return apiKey;
    }
    
    private void saveTranslationHistory(String sourceText, String translatedText) {
        executor.execute(() -> {
            try {
                TranslationHistoryEntity history = new TranslationHistoryEntity(
                    sourceText,
                    translatedText,
                    sourceLanguage,
                    targetLanguage
                );
                database.translationHistoryDao().insert(history);
            } catch (Exception e) {
                android.util.Log.e(TAG, "保存翻译历史失败", e);
            }
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnTranslate.setEnabled(!show);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}

