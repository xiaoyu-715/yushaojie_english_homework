package com.example.mybighomework;

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

import com.example.mybighomework.database.AppDatabase;
import com.example.mybighomework.database.entity.TranslationHistoryEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        // TODO: 实现真正的文本翻译API调用
        // 可以使用有道文本翻译API: https://openapi.youdao.com/api
        // 或者使用其他翻译服务
        
        // 临时返回提示信息
        return "翻译功能开发中，请使用拍照翻译功能";
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

