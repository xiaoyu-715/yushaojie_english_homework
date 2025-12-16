package com.example.mybighomework.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.mybighomework.StudyPlan;
import com.example.mybighomework.database.AppDatabase;
import com.example.mybighomework.database.dao.StudyPlanDao;
import com.example.mybighomework.database.entity.StudyPlanEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 学习计划Repository
 * 负责处理学习计划的数据访问逻辑，使用异步操作避免阻塞主线程
 */
public class StudyPlanRepository {
    private final StudyPlanDao studyPlanDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public StudyPlanRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        studyPlanDao = database.studyPlanDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    // ==================== 异步操作接口 ====================
    
    /**
     * 计划保存回调接口
     */
    public interface OnPlanSavedListener {
        void onPlanSaved(long id);
        void onError(Exception e);
    }
    
    /**
     * 计划更新回调接口
     */
    public interface OnPlanUpdatedListener {
        void onPlanUpdated();
        void onError(Exception e);
    }
    
    /**
     * 计划删除回调接口
     */
    public interface OnPlanDeletedListener {
        void onPlanDeleted();
        void onError(Exception e);
    }
    
    /**
     * 计划列表加载回调接口
     */
    public interface OnPlansLoadedListener {
        void onPlansLoaded(List<StudyPlan> plans);
        void onError(Exception e);
    }
    
    /**
     * 统计数据加载回调接口
     */
    public interface OnStatisticsLoadedListener {
        void onStatisticsLoaded(int total, int completed, int today);
        void onError(Exception e);
    }
    
    // ==================== 异步操作方法 ====================
    
    /**
     * 异步添加学习计划
     */
    public void addStudyPlanAsync(StudyPlan studyPlan, OnPlanSavedListener listener) {
        executorService.execute(() -> {
            try {
                StudyPlanEntity entity = convertToEntity(studyPlan);
                long id = studyPlanDao.insert(entity);
                
                // 在主线程回调
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlanSaved(id);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步更新学习计划
     */
    public void updateStudyPlanAsync(StudyPlan studyPlan, OnPlanUpdatedListener listener) {
        executorService.execute(() -> {
            try {
                StudyPlanEntity entity = convertToEntity(studyPlan);
                studyPlanDao.update(entity);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlanUpdated();
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步删除学习计划
     */
    public void deleteStudyPlanAsync(StudyPlan studyPlan, OnPlanDeletedListener listener) {
        executorService.execute(() -> {
            try {
                StudyPlanEntity entity = convertToEntity(studyPlan);
                studyPlanDao.delete(entity);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlanDeleted();
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步获取所有学习计划
     */
    public void getAllStudyPlansAsync(OnPlansLoadedListener listener) {
        executorService.execute(() -> {
            try {
                List<StudyPlanEntity> entities = studyPlanDao.getAllStudyPlans();
                List<StudyPlan> plans = convertToStudyPlans(entities);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlansLoaded(plans);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步获取今日计划
     */
    public void getTodayPlansAsync(OnPlansLoadedListener listener) {
        executorService.execute(() -> {
            try {
                List<StudyPlanEntity> entities = studyPlanDao.getTodayPlans();
                List<StudyPlan> plans = convertToStudyPlans(entities);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlansLoaded(plans);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步根据状态获取计划
     */
    public void getPlansByStatusAsync(String status, OnPlansLoadedListener listener) {
        executorService.execute(() -> {
            try {
                List<StudyPlanEntity> entities = studyPlanDao.getPlansByStatus(status);
                List<StudyPlan> plans = convertToStudyPlans(entities);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlansLoaded(plans);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步获取统计数据
     */
    public void getStatisticsAsync(OnStatisticsLoadedListener listener) {
        executorService.execute(() -> {
            try {
                int total = studyPlanDao.getTotalPlansCount();
                int completed = studyPlanDao.getCompletedPlansCount();
                int today = studyPlanDao.getTodayPlansCount();
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onStatisticsLoaded(total, completed, today);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * 异步搜索计划
     */
    public void searchPlansAsync(String keyword, OnPlansLoadedListener listener) {
        executorService.execute(() -> {
            try {
                List<StudyPlanEntity> entities = studyPlanDao.searchPlans(keyword);
                List<StudyPlan> plans = convertToStudyPlans(entities);
                
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onPlansLoaded(plans);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    // ==================== 同步方法（供旧代码兼容使用，不推荐） ====================
    
    /**
     * @deprecated 使用addStudyPlanAsync代替
     */
    @Deprecated
    public long addStudyPlan(StudyPlan studyPlan) {
        StudyPlanEntity entity = convertToEntity(studyPlan);
        return studyPlanDao.insert(entity);
    }
    
    /**
     * @deprecated 使用updateStudyPlanAsync代替
     */
    @Deprecated
    public void updateStudyPlan(StudyPlan studyPlan) {
        StudyPlanEntity entity = convertToEntity(studyPlan);
        studyPlanDao.update(entity);
    }
    
    /**
     * @deprecated 使用getAllStudyPlansAsync代替
     */
    @Deprecated
    public List<StudyPlan> getAllStudyPlans() {
        List<StudyPlanEntity> entities = studyPlanDao.getAllStudyPlans();
        return convertToStudyPlans(entities);
    }
    
    /**
     * @deprecated 使用getPlansByStatusAsync代替
     */
    @Deprecated
    public List<StudyPlan> getPlansByStatus(String status) {
        List<StudyPlanEntity> entities = studyPlanDao.getPlansByStatus(status);
        return convertToStudyPlans(entities);
    }
    
    /**
     * @deprecated 使用getStatisticsAsync代替
     */
    @Deprecated
    public int getTotalPlansCount() {
        return studyPlanDao.getTotalPlansCount();
    }
    
    /**
     * @deprecated 使用getStatisticsAsync代替
     */
    @Deprecated
    public int getCompletedPlansCount() {
        return studyPlanDao.getCompletedPlansCount();
    }
    
    /**
     * @deprecated 使用getStatisticsAsync代替
     */
    @Deprecated
    public int getTodayPlansCount() {
        return studyPlanDao.getTodayPlansCount();
    }
    
    // ==================== 转换方法 ====================
    
    /**
     * 转换方法：Entity -> StudyPlan
     */
    private StudyPlan convertToStudyPlan(StudyPlanEntity entity) {
        return new StudyPlan(
            entity.getId(),
            entity.getTitle(),
            entity.getCategory(),
            entity.getDescription(),
            entity.getTimeRange(),
            entity.getDuration(),
            entity.getProgress(),
            entity.getPriority(),
            entity.getStatus(),
            entity.isActiveToday()
        );
    }
    
    /**
     * 转换方法：StudyPlan -> Entity
     */
    private StudyPlanEntity convertToEntity(StudyPlan plan) {
        StudyPlanEntity entity = new StudyPlanEntity(
            plan.getTitle(),
            plan.getCategory(),
            plan.getDescription(),
            plan.getTimeRange(),
            plan.getDuration(),
            plan.getPriority()
        );
        
        // 如果StudyPlan有ID，设置到Entity中
        if (plan.getId() > 0) {
            entity.setId(plan.getId());
        }
        
        entity.setProgress(plan.getProgress());
        entity.setStatus(plan.getStatus());
        entity.setActiveToday(plan.isActiveToday());
        
        return entity;
    }
    
    /**
     * 批量转换：Entity List -> StudyPlan List
     */
    private List<StudyPlan> convertToStudyPlans(List<StudyPlanEntity> entities) {
        List<StudyPlan> plans = new ArrayList<>();
        for (StudyPlanEntity entity : entities) {
            plans.add(convertToStudyPlan(entity));
        }
        return plans;
    }
    
    /**
     * 关闭线程池（在应用退出时调用）
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}