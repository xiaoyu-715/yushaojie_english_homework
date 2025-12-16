package com.example.mybighomework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.mybighomework.repository.StudyPlanRepository;
import com.example.mybighomework.viewmodel.StudyPlanViewModel;

/**
 * 学习计划Activity
 * 使用MVVM架构，通过ViewModel管理数据
 */
public class StudyPlanActivity extends AppCompatActivity {

    // UI组件
    private ImageView ivBack;
    private TextView tvTodayCount, tvCompletedCount, tvTotalCount;
    private Button btnAddPlan, btnViewCalendar;
    private Spinner spinnerFilter;
    private RecyclerView rvStudyPlans;
    private ProgressBar progressLoading;
    
    // Adapter和数据
    private StudyPlanAdapter adapter;
    private List<StudyPlan> studyPlanList;
    
    // ViewModel
    private StudyPlanViewModel viewModel;
    
    // ActivityResultLauncher
    private ActivityResultLauncher<Intent> addPlanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(StudyPlanViewModel.class);
        
        // 初始化ActivityResultLauncher
        initActivityResultLauncher();
        
        // 初始化UI
        initViews();
        setupClickListeners();
        setupRecyclerView();
        setupSpinner();
        
        // 观察ViewModel数据
        observeViewModel();
    }
    
    /**
     * 初始化ActivityResultLauncher
     */
    private void initActivityResultLauncher() {
        addPlanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        
                        // 获取新添加的计划数据
                        String title = data.getStringExtra("new_plan_title");
                        String category = data.getStringExtra("new_plan_category");
                        String description = data.getStringExtra("new_plan_description");
                        String timeRange = data.getStringExtra("new_plan_time_range");
                        String duration = data.getStringExtra("new_plan_duration");
                        String priority = data.getStringExtra("new_plan_priority");
                        
                        // 创建新的学习计划
                        StudyPlan newPlan = new StudyPlan(title, category, description, timeRange, 
                            duration, 0, priority, "未开始", false);
                        
                        // 通过ViewModel保存到数据库
                        viewModel.addStudyPlan(newPlan, new StudyPlanRepository.OnPlanSavedListener() {
                            @Override
                            public void onPlanSaved(long id) {
                                Toast.makeText(StudyPlanActivity.this, "学习计划已添加", Toast.LENGTH_SHORT).show();
                            }
                            
                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(StudyPlanActivity.this, "添加失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        );
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTodayCount = findViewById(R.id.tv_today_count);
        tvCompletedCount = findViewById(R.id.tv_completed_count);
        tvTotalCount = findViewById(R.id.tv_total_count);
        btnAddPlan = findViewById(R.id.btn_add_plan);
        btnViewCalendar = findViewById(R.id.btn_view_calendar);
        spinnerFilter = findViewById(R.id.spinner_filter);
        rvStudyPlans = findViewById(R.id.rv_study_plans);
        
        // 进度条（如果有的话）
        progressLoading = findViewById(R.id.progress_loading);
        if (progressLoading != null) {
            progressLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 设置点击事件
     */
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStudyPlanActivity.class);
            addPlanLauncher.launch(intent);
        });

        btnViewCalendar.setOnClickListener(v -> {
            Toast.makeText(this, "日历功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        studyPlanList = new ArrayList<>();
        adapter = new StudyPlanAdapter(this, studyPlanList, new StudyPlanRepository(this));
        
        rvStudyPlans.setLayoutManager(new LinearLayoutManager(this));
        rvStudyPlans.setAdapter(adapter);
        
        // 设置状态变化监听器
        adapter.setOnStatusChangeListener(new StudyPlanAdapter.OnStatusChangeListener() {
            @Override
            public void onStatusChanged() {
                // 状态改变后，重新加载统计数据
                viewModel.loadStatistics();
            }
        });
    }

    /**
     * 设置筛选Spinner
     */
    private void setupSpinner() {
        String[] filterOptions = {"全部计划", "进行中", "已完成", "已暂停", "未开始"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);
        
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = filterOptions[position];
                viewModel.filterPlansByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    /**
     * 观察ViewModel的LiveData
     */
    private void observeViewModel() {
        // 观察学习计划列表
        viewModel.getAllPlans().observe(this, plans -> {
            if (plans != null) {
                studyPlanList.clear();
                studyPlanList.addAll(plans);
                adapter.updateData(studyPlanList);
                
                // 如果列表为空且不是首次加载，显示空状态
                if (plans.isEmpty() && tvTotalCount.getText().toString().equals("0")) {
                    // 可以显示空状态视图
                }
            }
        });
        
        // 观察总计划数
        viewModel.getTotalPlansCount().observe(this, count -> {
            if (count != null) {
                tvTotalCount.setText(String.valueOf(count));
            }
        });
        
        // 观察已完成计划数
        viewModel.getCompletedPlansCount().observe(this, count -> {
            if (count != null) {
                tvCompletedCount.setText(String.valueOf(count));
            }
        });
        
        // 观察今日计划数
        viewModel.getTodayPlansCount().observe(this, count -> {
            if (count != null) {
                tvTodayCount.setText(String.valueOf(count));
            }
        });
        
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && progressLoading != null) {
                progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 刷新统计数据
        viewModel.loadStatistics();
    }
}
