package com.example.mybighomework;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybighomework.adapter.ChatMessageAdapter;
import com.example.mybighomework.api.Glm46vApiService;
import com.example.mybighomework.dialog.PlanSelectionDialog;
import com.example.mybighomework.model.ChatMessage;
import com.example.mybighomework.repository.StudyPlanRepository;
import com.example.mybighomework.utils.StudyPlanExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * GLM-4.6V-Flash（智谱）AI 聊天界面
 * 提供与 GLM-4.6V-Flash 大模型的对话功能
 *
 * 功能：
 * 1. AI 对话 - 与 GLM-4.6V-Flash 进行智能对话
 * 2. 英语学习助手 - 可用于翻译、语法纠错、作文批改等
 * 3. 学习建议 - 获取个性化学习建议
 * 4. 问答解惑 - 解答英语相关问题
 */
public class GlmChatActivity extends AppCompatActivity {
    
    private static final String TAG = "GlmChatActivity";
    private static final String PREF_NAME = "glm46v_config";
    private static final String KEY_API_KEY = "api_key";
    
    // UI 组件
    private RecyclerView rvMessages;
    private EditText etInput;
    private ImageButton btnSend, btnBack, btnSettings, btnGeneratePlan;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    
    // 进度对话框
    private androidx.appcompat.app.AlertDialog progressDialog;
    
    // 适配器和数据
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messageList;
    
    // GLM-4.6V-Flash API 服务
    private Glm46vApiService apiService;
    
    // 主线程 Handler
    private Handler mainHandler;
    
    // 当前 AI 回复的消息（用于流式更新）
    private ChatMessage currentAiMessage;
    
    // 学习计划相关
    private StudyPlanRepository studyPlanRepository;
    private StudyPlanExtractor planExtractor;
    private int regenerateCount = 0;  // 重新生成次数计数
    private static final int MAX_REGENERATE_COUNT = 3;  // 最大重新生成次数
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glm_chat);
        
        initViews();
        initData();
        setupListeners();
        
        // 显示欢迎消息
        showWelcomeMessage();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        btnSettings = findViewById(R.id.btn_settings);
        btnGeneratePlan = findViewById(R.id.btn_generate_plan);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        
        // 设置 RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
    }
    
    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化消息列表
        messageList = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, messageList);
        rvMessages.setAdapter(adapter);
        
        // 初始化 Handler
        mainHandler = new Handler(Looper.getMainLooper());
        
        // 初始化学习计划仓库
        studyPlanRepository = new StudyPlanRepository(this);
        
        // 获取 API Key
        String apiKey = getApiKey();
        if (TextUtils.isEmpty(apiKey)) {
            showApiKeyDialog();
        } else {
            apiService = new Glm46vApiService(apiKey);
            planExtractor = new StudyPlanExtractor(apiService);
        }
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());
        
        // 设置按钮
        btnSettings.setOnClickListener(v -> showApiKeyDialog());
        
        // 手动生成学习计划按钮
        btnGeneratePlan.setOnClickListener(v -> {
            if (messageList.isEmpty()) {
                Toast.makeText(this, "请先与AI助手进行对话", Toast.LENGTH_SHORT).show();
                return;
            }
            generateStudyPlanFromMessage(-1);
        });
        
        // 发送按钮
        btnSend.setOnClickListener(v -> sendMessage());
        
        // 输入框回车发送
        etInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
        
        // 生成学习计划按钮点击监听
        adapter.setOnGeneratePlanClickListener(position -> {
            generateStudyPlanFromMessage(position);
        });
    }
    
    /**
     * 显示欢迎消息
     */
    private void showWelcomeMessage() {
        String welcomeText = "👋 你好！我是你的英语学习 AI 助手。\n\n" +
                "我可以帮你：\n" +
                "• 翻译英文句子或文章\n" +
                "• 纠正语法错误\n" +
                "• 批改英语作文\n" +
                "• 解释词汇用法\n" +
                "• 提供学习建议\n" +
                "• 解答英语相关问题\n\n" +
                "请问有什么可以帮到你的吗？";
        
        ChatMessage welcomeMessage = new ChatMessage(
                ChatMessage.TYPE_RECEIVED,
                welcomeText,
                System.currentTimeMillis()
        );
        
        messageList.add(welcomeMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        updateEmptyView();
    }
    
    /**
     * 发送消息
     */
    private void sendMessage() {
        String input = etInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "请输入消息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (apiService == null) {
            Toast.makeText(this, "请先配置 API Key", Toast.LENGTH_SHORT).show();
            showApiKeyDialog();
            return;
        }
        
        // 清空输入框
        etInput.setText("");
        
        // 添加用户消息
        ChatMessage userMessage = new ChatMessage(
                ChatMessage.TYPE_SENT,
                input,
                System.currentTimeMillis()
        );
        messageList.add(userMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.smoothScrollToPosition(messageList.size() - 1);
        updateEmptyView();
        
        // 显示加载状态
        showLoading(true);
        
        // 构建消息历史
        List<Glm46vApiService.ChatMessage> apiMessages = buildApiMessages();
        
        // 发送请求（使用流式输出）
        apiService.chatStream(apiMessages, new Glm46vApiService.StreamCallback() {
            @Override
            public void onChunk(String chunk) {
                mainHandler.post(() -> {
                    if (currentAiMessage == null) {
                        // 创建新的 AI 消息
                        currentAiMessage = new ChatMessage(
                                ChatMessage.TYPE_RECEIVED,
                                chunk,
                                System.currentTimeMillis()
                        );
                        messageList.add(currentAiMessage);
                        adapter.notifyItemInserted(messageList.size() - 1);
                        rvMessages.smoothScrollToPosition(messageList.size() - 1);
                    } else {
                        // 追加内容到现有消息
                        currentAiMessage.setContent(currentAiMessage.getContent() + chunk);
                        adapter.notifyItemChanged(messageList.size() - 1);
                        rvMessages.smoothScrollToPosition(messageList.size() - 1);
                    }
                    updateEmptyView();
                });
            }
            
            @Override
            public void onComplete() {
                mainHandler.post(() -> {
                    showLoading(false);
                    
                    // 智能检测：如果AI回复包含学习建议，自动显示生成按钮
                    if (currentAiMessage != null && isStudyAdviceMessage(currentAiMessage.getContent())) {
                        currentAiMessage.setShowGeneratePlanButton(true);
                        adapter.notifyItemChanged(messageList.size() - 1);
                    }
                    
                    currentAiMessage = null;
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    showLoading(false);
                    currentAiMessage = null;
                    Toast.makeText(GlmChatActivity.this, 
                            "发送失败: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * 构建 API 消息列表
     */
    private List<Glm46vApiService.ChatMessage> buildApiMessages() {
        List<Glm46vApiService.ChatMessage> apiMessages = new ArrayList<>();
        
        // 添加系统提示（定义 AI 角色）
        String systemPrompt = "你是一个专业的英语学习助手，擅长帮助学生提高英语水平。" +
                "你可以进行翻译、语法纠错、作文批改、词汇解释等。" +
                "请用简洁、友好的方式回答问题。";
        apiMessages.add(new Glm46vApiService.ChatMessage("system", systemPrompt));
        
        // 添加历史消息（最近10条）
        int startIndex = Math.max(0, messageList.size() - 10);
        for (int i = startIndex; i < messageList.size(); i++) {
            ChatMessage msg = messageList.get(i);
            String role = msg.getType() == ChatMessage.TYPE_SENT ? "user" : "assistant";
            apiMessages.add(new Glm46vApiService.ChatMessage(role, msg.getContent()));
        }
        
        return apiMessages;
    }
    
    /**
     * 显示/隐藏加载状态
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!show);
        etInput.setEnabled(!show);
    }
    
    /**
     * 更新空状态视图
     */
    private void updateEmptyView() {
        tvEmpty.setVisibility(messageList.isEmpty() ? View.VISIBLE : View.GONE);
    }
    
    /**
     * 显示 API Key 配置对话框
     */
    private void showApiKeyDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("配置 GLM-4.6V-Flash (智谱) API Key");
        
        final EditText input = new EditText(this);
        input.setHint("请输入 API Key");
        input.setText(getApiKey());
        input.setPadding(50, 20, 50, 20);
        builder.setView(input);
        
        builder.setPositiveButton("确定", (dialog, which) -> {
            String apiKey = input.getText().toString().trim();
            if (!TextUtils.isEmpty(apiKey)) {
                saveApiKey(apiKey);
                if (apiService == null) {
                    apiService = new Glm46vApiService(apiKey);
                    planExtractor = new StudyPlanExtractor(apiService);
                } else {
                    apiService.setApiKey(apiKey);
                    if (planExtractor == null) {
                        planExtractor = new StudyPlanExtractor(apiService);
                    }
                }
                Toast.makeText(this, "API Key 已保存", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("取消", null);
        
        builder.setNeutralButton("如何获取？", (dialog, which) -> {
            Toast.makeText(this, "请访问 https://open.bigmodel.cn 注册并获取 API Key", 
                    Toast.LENGTH_LONG).show();
        });
        
        builder.show();
    }
    
    /**
     * 获取 API Key
     */
    private String getApiKey() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_API_KEY, "");
    }
    
    /**
     * 保存 API Key
     */
    private void saveApiKey(String apiKey) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
        if (studyPlanRepository != null) {
            studyPlanRepository.shutdown();
        }
        dismissProgressDialog();
    }
    
    // ==================== 学习计划生成功能 ====================
    
    /**
     * 从消息生成学习计划
     */
    private void generateStudyPlanFromMessage(int position) {
        if (planExtractor == null) {
            Toast.makeText(this, "服务未初始化，请先配置API Key", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取对话上下文
        String context = getConversationContext();
        
        // 显示进度对话框
        showProgressDialog();
        
        // 调用提取器生成学习计划（带进度回调）
        planExtractor.extractPlans(context, 
            new StudyPlanExtractor.OnPlanExtractedListener() {
                @Override
                public void onSuccess(List<StudyPlan> plans) {
                    mainHandler.post(() -> {
                        dismissProgressDialog();
                        regenerateCount = 0;  // 重置重新生成计数
                        showPlanSelectionDialog(plans);
                    });
                }
                
                @Override
                public void onError(String error) {
                    mainHandler.post(() -> {
                        dismissProgressDialog();
                        Toast.makeText(GlmChatActivity.this,
                            getString(R.string.generation_failed) + ": " + error, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            },
            new StudyPlanExtractor.OnProgressUpdateListener() {
                @Override
                public void onProgressUpdate(String message, int progress) {
                    mainHandler.post(() -> {
                        updateProgressDialog(message, progress);
                    });
                }
            });
    }
    
    /**
     * 获取对话上下文（最近5轮对话，即10条消息）
     */
    private String getConversationContext() {
        StringBuilder context = new StringBuilder();
        
        // 获取最近10条消息（5轮对话）
        int start = Math.max(0, messageList.size() - 10);
        for (int i = start; i < messageList.size(); i++) {
            ChatMessage msg = messageList.get(i);
            String role = msg.getType() == ChatMessage.TYPE_SENT ? "用户" : "AI助手";
            context.append(role).append(": ").append(msg.getContent()).append("\n\n");
        }
        
        return context.toString();
    }
    
    /**
     * 显示学习计划选择对话框
     */
    private void showPlanSelectionDialog(List<StudyPlan> plans) {
        if (plans == null || plans.isEmpty()) {
            Toast.makeText(this, "未能生成有效的学习计划", Toast.LENGTH_SHORT).show();
            return;
        }
        
        PlanSelectionDialog dialog = PlanSelectionDialog.newInstance(new ArrayList<>(plans));
        
        // 设置计划选择监听器
        dialog.setOnPlansSelectedListener(selectedPlans -> {
            saveSelectedPlans(selectedPlans);
        });
        
        // 设置重新生成监听器
        dialog.setOnRegenerateClickListener(() -> {
            handleRegeneratePlans();
        });
        
        dialog.show(getSupportFragmentManager(), "PlanSelectionDialog");
    }
    
    /**
     * 保存选中的学习计划
     */
    private void saveSelectedPlans(List<StudyPlan> plans) {
        if (plans == null || plans.isEmpty()) {
            return;
        }
        
        final int totalCount = plans.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};
        
        // 显示进度提示
        Toast.makeText(this, "正在保存学习计划...", Toast.LENGTH_SHORT).show();
        
        for (StudyPlan plan : plans) {
            studyPlanRepository.addStudyPlanAsync(plan, 
                new StudyPlanRepository.OnPlanSavedListener() {
                    @Override
                    public void onPlanSaved(long id) {
                        savedCount[0]++;
                        checkSaveComplete(savedCount[0], failedCount[0], totalCount);
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        failedCount[0]++;
                        checkSaveComplete(savedCount[0], failedCount[0], totalCount);
                    }
                });
        }
    }
    
    /**
     * 检查保存是否完成
     */
    private void checkSaveComplete(int savedCount, int failedCount, int totalCount) {
        if (savedCount + failedCount == totalCount) {
            // 全部完成
            if (savedCount > 0) {
                showSuccessDialog(savedCount);
            } else {
                Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 显示成功对话框
     */
    private void showSuccessDialog(int count) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.plan_generated_title)
            .setMessage(getString(R.string.plan_generated_message, count))
            .setPositiveButton(R.string.view_plans, (dialog, which) -> {
                Intent intent = new Intent(this, StudyPlanActivity.class);
                startActivity(intent);
            })
            .setNegativeButton(R.string.later, null)
            .show();
    }
    
    /**
     * 检测消息是否包含学习建议
     */
    private boolean isStudyAdviceMessage(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        
        String[] keywords = {"建议", "计划", "学习", "步骤", "阶段", "目标", "练习", 
                            "复习", "掌握", "提高", "强化", "备考", "方法"};
        
        String lowerContent = content.toLowerCase();
        int matchCount = 0;
        
        for (String keyword : keywords) {
            if (lowerContent.contains(keyword)) {
                matchCount++;
            }
        }
        
        // 如果包含3个或以上关键词，认为是学习建议
        return matchCount >= 3;
    }
    
    // ==================== 进度对话框管理 ====================
    
    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        
        View progressView = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(progressView);
        builder.setCancelable(false);
        
        progressDialog = builder.create();
        
        // 设置取消按钮
        android.widget.Button btnCancel = progressView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            dismissProgressDialog();
            Toast.makeText(this, "已取消生成", Toast.LENGTH_SHORT).show();
        });
        
        progressDialog.show();
    }
    
    /**
     * 更新进度对话框
     */
    private void updateProgressDialog(String message, int progress) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        
        android.widget.ProgressBar progressBar = progressDialog.findViewById(R.id.progress_bar);
        android.widget.TextView tvMessage = progressDialog.findViewById(R.id.tv_progress_message);
        android.widget.TextView tvPercent = progressDialog.findViewById(R.id.tv_progress_percent);
        
        // 更新进度条
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        
        // 更新文字
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
        
        if (tvPercent != null) {
            tvPercent.setText(progress + "%");
        }
        
        // 更新步骤指示器
        updateStepIndicators(progress);
    }
    
    /**
     * 更新步骤指示器
     */
    private void updateStepIndicators(int progress) {
        if (progressDialog == null) return;
        
        View step1 = progressDialog.findViewById(R.id.step1_indicator);
        View step2 = progressDialog.findViewById(R.id.step2_indicator);
        View step3 = progressDialog.findViewById(R.id.step3_indicator);
        
        // 根据进度更新步骤状态
        if (step1 != null) {
            if (progress >= 10) {
                step1.setBackgroundResource(R.drawable.bg_gradient_primary);
            } else {
                step1.setBackgroundColor(getColor(R.color.separator));
            }
        }
        
        if (step2 != null) {
            if (progress >= 40) {
                step2.setBackgroundResource(R.drawable.bg_gradient_primary);
            } else {
                step2.setBackgroundColor(getColor(R.color.separator));
            }
        }
        
        if (step3 != null) {
            if (progress >= 80) {
                step3.setBackgroundResource(R.drawable.bg_gradient_primary);
            } else {
                step3.setBackgroundColor(getColor(R.color.separator));
            }
        }
    }
    
    /**
     * 关闭进度对话框
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    /**
     * 处理重新生成学习计划
     */
    private void handleRegeneratePlans() {
        // 检查重新生成次数
        if (regenerateCount >= MAX_REGENERATE_COUNT) {
            Toast.makeText(this, R.string.regenerate_limit_reached, Toast.LENGTH_LONG).show();
            return;
        }
        
        regenerateCount++;
        Toast.makeText(this, getString(R.string.regenerating) + " (第" + regenerateCount + "次)", 
                      Toast.LENGTH_SHORT).show();
        
        // 重新生成
        generateStudyPlanFromMessage(-1);
    }
}

