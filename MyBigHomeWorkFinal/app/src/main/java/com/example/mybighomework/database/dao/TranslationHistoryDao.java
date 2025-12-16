package com.example.mybighomework.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mybighomework.database.entity.TranslationHistoryEntity;

import java.util.List;

/**
 * 翻译历史数据访问对象
 */
@Dao
public interface TranslationHistoryDao {
    
    /**
     * 插入一条翻译历史记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TranslationHistoryEntity history);
    
    /**
     * 批量插入翻译历史记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TranslationHistoryEntity> histories);
    
    /**
     * 更新翻译历史记录
     */
    @Update
    void update(TranslationHistoryEntity history);
    
    /**
     * 删除翻译历史记录
     */
    @Delete
    void delete(TranslationHistoryEntity history);
    
    /**
     * 根据ID查询翻译历史
     */
    @Query("SELECT * FROM translation_history WHERE id = :id")
    TranslationHistoryEntity getById(int id);
    
    /**
     * 获取所有翻译历史（按创建时间降序）
     */
    @Query("SELECT * FROM translation_history ORDER BY createTime DESC")
    List<TranslationHistoryEntity> getAll();
    
    /**
     * 获取所有已收藏的翻译历史
     */
    @Query("SELECT * FROM translation_history WHERE isFavorited = 1 ORDER BY lastViewTime DESC")
    List<TranslationHistoryEntity> getFavorited();
    
    /**
     * 获取最近N条翻译历史
     */
    @Query("SELECT * FROM translation_history ORDER BY createTime DESC LIMIT :limit")
    List<TranslationHistoryEntity> getRecent(int limit);
    
    /**
     * 根据源语言和目标语言查询
     */
    @Query("SELECT * FROM translation_history WHERE sourceLanguage = :sourceLang AND targetLanguage = :targetLang ORDER BY createTime DESC")
    List<TranslationHistoryEntity> getByLanguages(String sourceLang, String targetLang);
    
    /**
     * 根据原文搜索（模糊匹配）
     */
    @Query("SELECT * FROM translation_history WHERE sourceText LIKE :keyword OR translatedText LIKE :keyword ORDER BY createTime DESC")
    List<TranslationHistoryEntity> search(String keyword);
    
    /**
     * 更新收藏状态
     */
    @Query("UPDATE translation_history SET isFavorited = :isFavorited WHERE id = :id")
    void updateFavoriteStatus(int id, boolean isFavorited);
    
    /**
     * 更新查看信息（最后查看时间和查看次数）
     */
    @Query("UPDATE translation_history SET lastViewTime = :lastViewTime, viewCount = viewCount + 1 WHERE id = :id")
    void updateViewInfo(int id, long lastViewTime);
    
    /**
     * 获取总数
     */
    @Query("SELECT COUNT(*) FROM translation_history")
    int getCount();
    
    /**
     * 获取收藏数
     */
    @Query("SELECT COUNT(*) FROM translation_history WHERE isFavorited = 1")
    int getFavoriteCount();
    
    /**
     * 删除所有数据
     */
    @Query("DELETE FROM translation_history")
    void deleteAll();
    
    /**
     * 根据时间范围查询
     */
    @Query("SELECT * FROM translation_history WHERE createTime BETWEEN :startTime AND :endTime ORDER BY createTime DESC")
    List<TranslationHistoryEntity> getByTimeRange(long startTime, long endTime);
    
    /**
     * 删除指定时间之前的数据
     */
    @Query("DELETE FROM translation_history WHERE createTime < :beforeTime")
    void deleteBefore(long beforeTime);
}

