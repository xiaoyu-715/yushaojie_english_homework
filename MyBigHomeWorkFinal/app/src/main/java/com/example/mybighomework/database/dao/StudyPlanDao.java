package com.example.mybighomework.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mybighomework.database.entity.StudyPlanEntity;

import java.util.List;

@Dao
public interface StudyPlanDao {
    
    @Insert
    long insert(StudyPlanEntity studyPlan);
    
    @Update
    void update(StudyPlanEntity studyPlan);
    
    @Delete
    void delete(StudyPlanEntity studyPlan);
    
    @Query("SELECT * FROM study_plans ORDER BY createdTime DESC")
    List<StudyPlanEntity> getAllStudyPlans();
    
    @Query("SELECT * FROM study_plans WHERE id = :id")
    StudyPlanEntity getStudyPlanById(int id);
    
    @Query("SELECT * FROM study_plans WHERE activeToday = 1")
    List<StudyPlanEntity> getTodayPlans();
    
    @Query("SELECT * FROM study_plans WHERE status = :status")
    List<StudyPlanEntity> getPlansByStatus(String status);
    
    @Query("SELECT * FROM study_plans WHERE priority = :priority")
    List<StudyPlanEntity> getPlansByPriority(String priority);
    
    @Query("SELECT * FROM study_plans WHERE category = :category")
    List<StudyPlanEntity> getPlansByCategory(String category);
    
    @Query("SELECT COUNT(*) FROM study_plans")
    int getTotalPlansCount();
    
    @Query("SELECT COUNT(*) FROM study_plans WHERE status = '已完成'")
    int getCompletedPlansCount();
    
    @Query("SELECT COUNT(*) FROM study_plans WHERE activeToday = 1")
    int getTodayPlansCount();
    
    @Query("UPDATE study_plans SET progress = :progress, status = :status WHERE id = :id")
    void updateProgress(int id, int progress, String status);
    
    @Query("UPDATE study_plans SET activeToday = :activeToday WHERE id = :id")
    void updateActiveToday(int id, boolean activeToday);
    
    @Query("DELETE FROM study_plans WHERE status = '已完成' AND lastModifiedTime < :timestamp")
    void deleteOldCompletedPlans(long timestamp);
    
    @Query("SELECT * FROM study_plans WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    List<StudyPlanEntity> searchPlans(String keyword);
}