package com.example.mybighomework.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mybighomework.StudyPlan;
import com.example.mybighomework.database.AppDatabase;
import com.example.mybighomework.repository.StudyPlanRepository;

import java.util.List;

/**
 * 学习计划 ViewModel
 * 负责管理学习计划相关的数据和业务逻辑
 */
public class StudyPlanViewModel extends AndroidViewModel {
    
    private final StudyPlanRepository repository;
    
    // LiveData for UI观察
    private final MutableLiveData<List<StudyPlan>> allPlansLiveData;
    private final MutableLiveData<Integer> totalPlansCount;
    private final MutableLiveData<Integer> completedPlansCount;
    private final MutableLiveData<Integer> todayPlansCount;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    
    public StudyPlanViewModel(@NonNull Application application) {
        super(application);
        
        // 初始化Repository
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new StudyPlanRepository(application);
        
        // 初始化LiveData
        allPlansLiveData = new MutableLiveData<>();
        totalPlansCount = new MutableLiveData<>(0);
        completedPlansCount = new MutableLiveData<>(0);
        todayPlansCount = new MutableLiveData<>(0);
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        
        // 加载初始数据
        loadAllPlans();
        loadStatistics();
    }
    
    // ==================== 公共方法 ====================
    
    /**
     * 获取所有学习计划
     */
    public LiveData<List<StudyPlan>> getAllPlans() {
        return allPlansLiveData;
    }
    
    /**
     * 获取总计划数
     */
    public LiveData<Integer> getTotalPlansCount() {
        return totalPlansCount;
    }
    
    /**
     * 获取已完成计划数
     */
    public LiveData<Integer> getCompletedPlansCount() {
        return completedPlansCount;
    }
    
    /**
     * 获取今日计划数
     */
    public LiveData<Integer> getTodayPlansCount() {
        return todayPlansCount;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * 获取错误信息
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 加载所有学习计划
     */
    public void loadAllPlans() {
        isLoading.setValue(true);
        repository.getAllStudyPlansAsync(new StudyPlanRepository.OnPlansLoadedListener() {
            @Override
            public void onPlansLoaded(List<StudyPlan> plans) {
                allPlansLiveData.postValue(plans);
                isLoading.postValue(false);
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("加载计划失败: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * 加载统计数据
     */
    public void loadStatistics() {
        repository.getStatisticsAsync(new StudyPlanRepository.OnStatisticsLoadedListener() {
            @Override
            public void onStatisticsLoaded(int total, int completed, int today) {
                totalPlansCount.postValue(total);
                completedPlansCount.postValue(completed);
                todayPlansCount.postValue(today);
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("加载统计数据失败: " + e.getMessage());
            }
        });
    }
    
    /**
     * 添加学习计划
     */
    public void addStudyPlan(StudyPlan plan, StudyPlanRepository.OnPlanSavedListener listener) {
        isLoading.setValue(true);
        repository.addStudyPlanAsync(plan, new StudyPlanRepository.OnPlanSavedListener() {
            @Override
            public void onPlanSaved(long id) {
                // 重新加载数据
                loadAllPlans();
                loadStatistics();
                isLoading.postValue(false);
                
                if (listener != null) {
                    listener.onPlanSaved(id);
                }
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("添加计划失败: " + e.getMessage());
                isLoading.postValue(false);
                
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }
    
    /**
     * 更新学习计划
     */
    public void updateStudyPlan(StudyPlan plan, StudyPlanRepository.OnPlanUpdatedListener listener) {
        repository.updateStudyPlanAsync(plan, new StudyPlanRepository.OnPlanUpdatedListener() {
            @Override
            public void onPlanUpdated() {
                // 重新加载数据
                loadAllPlans();
                loadStatistics();
                
                if (listener != null) {
                    listener.onPlanUpdated();
                }
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("更新计划失败: " + e.getMessage());
                
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }
    
    /**
     * 删除学习计划
     */
    public void deleteStudyPlan(StudyPlan plan, StudyPlanRepository.OnPlanDeletedListener listener) {
        repository.deleteStudyPlanAsync(plan, new StudyPlanRepository.OnPlanDeletedListener() {
            @Override
            public void onPlanDeleted() {
                // 重新加载数据
                loadAllPlans();
                loadStatistics();
                
                if (listener != null) {
                    listener.onPlanDeleted();
                }
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("删除计划失败: " + e.getMessage());
                
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }
    
    /**
     * 按状态筛选计划
     */
    public void filterPlansByStatus(String status) {
        if ("全部计划".equals(status)) {
            loadAllPlans();
            return;
        }
        
        isLoading.setValue(true);
        repository.getPlansByStatusAsync(status, new StudyPlanRepository.OnPlansLoadedListener() {
            @Override
            public void onPlansLoaded(List<StudyPlan> plans) {
                allPlansLiveData.postValue(plans);
                isLoading.postValue(false);
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("筛选计划失败: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * 搜索计划
     */
    public void searchPlans(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadAllPlans();
            return;
        }
        
        isLoading.setValue(true);
        repository.searchPlansAsync(keyword, new StudyPlanRepository.OnPlansLoadedListener() {
            @Override
            public void onPlansLoaded(List<StudyPlan> plans) {
                allPlansLiveData.postValue(plans);
                isLoading.postValue(false);
            }
            
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("搜索失败: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
}

