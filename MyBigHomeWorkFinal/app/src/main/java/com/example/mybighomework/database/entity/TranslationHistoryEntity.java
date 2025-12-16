package com.example.mybighomework.database.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 翻译历史数据库实体
 */
@Entity(
    tableName = "translation_history",
    indices = {
        @Index("createTime"),      // 创建时间索引（用于按时间排序）
        @Index("isFavorited")      // 收藏状态索引（用于查询收藏）
    }
)
public class TranslationHistoryEntity {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String sourceText;        // 原文
    private String translatedText;    // 译文
    private String sourceLanguage;     // 源语言（如：en, zh）
    private String targetLanguage;    // 目标语言（如：en, zh）
    private boolean isFavorited;      // 是否收藏
    private long createTime;          // 创建时间
    private long lastViewTime;        // 最后查看时间
    private int viewCount;            // 查看次数
    
    public TranslationHistoryEntity() {
        this.createTime = System.currentTimeMillis();
        this.lastViewTime = System.currentTimeMillis();
        this.isFavorited = false;
        this.viewCount = 0;
    }
    
    @Ignore
    public TranslationHistoryEntity(String sourceText, String translatedText, 
                                   String sourceLanguage, String targetLanguage) {
        this();
        this.sourceText = sourceText;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSourceText() {
        return sourceText;
    }
    
    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }
    
    public String getTranslatedText() {
        return translatedText;
    }
    
    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
    
    public String getSourceLanguage() {
        return sourceLanguage;
    }
    
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    public String getTargetLanguage() {
        return targetLanguage;
    }
    
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
    
    public boolean isFavorited() {
        return isFavorited;
    }
    
    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getLastViewTime() {
        return lastViewTime;
    }
    
    public void setLastViewTime(long lastViewTime) {
        this.lastViewTime = lastViewTime;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}

