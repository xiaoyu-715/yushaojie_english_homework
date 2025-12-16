package com.example.mybighomework.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mybighomework.database.AppDatabase;
import com.example.mybighomework.database.entity.UserSettingsEntity;
import com.example.mybighomework.databinding.ActivityMainBinding;
import com.example.mybighomework.repository.UserSettingsRepository;
import com.example.mybighomework.repository.ExamRecordRepository;
import com.example.mybighomework.repository.VocabularyRecordRepository;
import com.example.mybighomework.viewmodel.MainViewModel;
import com.example.mybighomework.utils.NavigationHelper;
import com.example.mybighomework.utils.QuestionDataInitializer;

public class MainActivity extends AppCompatActivity {

    // ViewBinding 替代多个 View 变量
    private ActivityMainBinding binding;
    
    // ViewModel（推荐使用）
    private MainViewModel viewModel;
    
    // Repository实例（保留用于其他功能）
    private UserSettingsRepository userSettingsRepository;
    private VocabularyRecordRepository vocabularyRecordRepository;
    private ExamRecordRepository examRecordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // ViewBinding 初始化
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            android.util.Log.d("MainActivity", "开始初始化...");
            
            // 初始化 ViewModel（自动管理生命周期）
            android.util.Log.d("MainActivity", "初始化 ViewModel...");
            viewModel = new ViewModelProvider(this).get(MainViewModel.class);
            
            // 初始化 Repository（用于其他功能）
            android.util.Log.d("MainActivity", "初始化 Repository...");
            userSettingsRepository = new UserSettingsRepository(this);
            android.util.Log.d("MainActivity", "获取数据库实例...");
            AppDatabase database = AppDatabase.getInstance(this);
            android.util.Log.d("MainActivity", "创建 Repository...");
            vocabularyRecordRepository = new VocabularyRecordRepository(database.vocabularyDao());
            examRecordRepository = new ExamRecordRepository(database.examDao());
            
            // 初始化题目数据（首次运行或数据更新时）
            android.util.Log.d("MainActivity", "初始化题目数据...");
            QuestionDataInitializer.initializeIfNeeded(getApplication());
            
            android.util.Log.d("MainActivity", "设置监听器...");
            setupClickListeners();
            observeViewModel();
            updateTaskProgress();
            
            android.util.Log.d("MainActivity", "初始化完成");
        } catch (RuntimeException e) {
            android.util.Log.e("MainActivity", "初始化失败 (RuntimeException)", e);
            android.util.Log.e("MainActivity", "错误详情: " + e.getMessage());
            if (e.getCause() != null) {
                android.util.Log.e("MainActivity", "根本原因: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
            e.printStackTrace();
            android.widget.Toast.makeText(this, "应用初始化失败: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            // 延迟 finish，让用户看到错误信息
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> finish(), 3000);
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "初始化失败 (Exception)", e);
            android.util.Log.e("MainActivity", "错误详情: " + e.getMessage());
            e.printStackTrace();
            android.widget.Toast.makeText(this, "应用初始化失败: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            // 延迟 finish，让用户看到错误信息
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> finish(), 3000);
        }
    }
    
    private void setupClickListeners() {
        // ===== 核心功能点击事件 (Lambda + NavigationHelper) =====
        
        // 词汇训练
        binding.llVocabulary.setOnClickListener(v -> NavigationHelper.toVocabulary(this));
        
        // 真题练习
        binding.llRealExam.setOnClickListener(v -> NavigationHelper.toExamList(this));
        
        // 模拟考试
        binding.llMockExam.setOnClickListener(v -> NavigationHelper.toMockExam(this));
        
        // 错题本
        binding.llErrorBook.setOnClickListener(v -> NavigationHelper.toWrongQuestion(this));
        
        // 学习计划
        binding.llStudyPlan.setOnClickListener(v -> NavigationHelper.toStudyPlan(this));
        
        // 每日一句
        binding.llDailySentence.setOnClickListener(v -> NavigationHelper.toDailySentence(this));
        
        // 今日任务
        binding.llDailyTask.setOnClickListener(v -> NavigationHelper.toDailyTask(this));
        
        // 拍照翻译
        binding.llCameraTranslation.setOnClickListener(v -> NavigationHelper.toCameraTranslation(this));
        
        // AI学习助手
        binding.llAiAssistant.setOnClickListener(v -> NavigationHelper.toGlmChat(this));
        
        // 输入翻译
        binding.llTextTranslation.setOnClickListener(v -> NavigationHelper.toTextTranslation(this));
        
        // ===== 导航栏点击事件 (Lambda + NavigationHelper) =====
        
        // 学习报告
        binding.navReport.setOnClickListener(v -> NavigationHelper.toReport(this));
        
        // 个人中心
        binding.navProfile.setOnClickListener(v -> NavigationHelper.toProfile(this));
        
        // 更多功能
        binding.navMore.setOnClickListener(v -> NavigationHelper.toMore(this));
    }
    
    private void updateTaskProgress() {
        SharedPreferences sharedPreferences = getSharedPreferences("daily_tasks", MODE_PRIVATE);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());
        
        // 定义任务类型列表，与DailyTaskActivity中的保持一致（只保留三个核心任务）
        String[] taskTypes = {"vocabulary", "exam_practice", "daily_sentence"};
        
        int completedTasks = 0;
        for (String taskType : taskTypes) {
            if (sharedPreferences.getBoolean(today + "_" + taskType, false)) {
                completedTasks++;
            }
        }
        
        int totalTasks = taskTypes.length;
        binding.tvTaskProgress.setText(completedTasks + "/" + totalTasks);
    }

    /**
     * 观察 ViewModel 的数据变化
     * LiveData 会自动在后台线程查询数据，在主线程更新UI
     */
    private void observeViewModel() {
        // 观察词汇掌握数量（LiveData 自动异步查询和更新）
        viewModel.getMasteredVocabularyCount().observe(this, count -> {
            if (binding == null) return; // 避免内存泄漏
            if (count != null) {
                binding.tvVocabularyCount.setText(String.valueOf(count));
            } else {
                binding.tvVocabularyCount.setText("0");
            }
        });
        
        // 获取学习天数（使用异步方法）
        loadUserSettingsAsync();
        
        // 获取平均考试分数（使用异步方法）
        viewModel.getAverageExamScore(new MainViewModel.OnResultListener<Double>() {
            @Override
            public void onSuccess(Double result) {
                runOnUiThread(() -> {
                    if (binding == null) return; // 避免内存泄漏
                    if (result != null && result > 0) {
                        binding.tvExamScore.setText(String.valueOf(result.intValue()));
                    } else {
                        binding.tvExamScore.setText("--");
                    }
                });
            }
            
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (binding != null) {
                        binding.tvExamScore.setText("--");
                    }
                });
            }
        });
    }
    
    /**
     * 异步加载用户设置
     */
    private void loadUserSettingsAsync() {
        new Thread(() -> {
            try {
                UserSettingsEntity settings = userSettingsRepository.getUserSettings();
                runOnUiThread(() -> {
                    if (binding == null) return; // 避免内存泄漏
                    if (settings != null) {
                        binding.tvStudyDays.setText(String.valueOf(settings.getStudyStreak()));
                    } else {
                        binding.tvStudyDays.setText("0");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (binding != null) {
                        binding.tvStudyDays.setText("0");
                    }
                });
            }
        }).start();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 当从其他Activity返回时，刷新所有学习进度数据
        refreshAllStudyProgress();
    }
    
    /**
     * 刷新所有学习进度数据
     */
    private void refreshAllStudyProgress() {
        // 更新任务进度
        updateTaskProgress();
        
        // 刷新学习天数
        loadUserSettingsAsync();
        
        // 由于使用了LiveData，词汇掌握量会自动更新
        // 但为了确保及时性，我们可以手动触发一次查询
        new Thread(() -> {
            try {
                // 强制刷新词汇掌握量
                int masteredCount = vocabularyRecordRepository.getMasteredVocabularyCount();
                runOnUiThread(() -> {
                    if (binding != null) {
                        binding.tvVocabularyCount.setText(String.valueOf(masteredCount));
                    }
                });
                
                // 强制刷新平均考试分数
                double averageScore = examRecordRepository.getAverageScore();
                if (Double.isNaN(averageScore) || Double.isInfinite(averageScore)) {
                    averageScore = 0.0;
                }
                final double finalScore = averageScore;
                runOnUiThread(() -> {
                    if (binding != null) {
                        if (finalScore > 0) {
                            binding.tvExamScore.setText(String.valueOf((int) finalScore));
                        } else {
                            binding.tvExamScore.setText("--");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 避免内存泄漏
    }
}
