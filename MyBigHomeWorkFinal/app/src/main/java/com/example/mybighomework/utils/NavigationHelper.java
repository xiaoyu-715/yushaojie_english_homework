package com.example.mybighomework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 统一导航工具类
 * 集中管理所有页面跳转逻辑，减少重复代码
 * 
 * @author NavigationHelper
 */
public final class NavigationHelper {
    
    private static final String TAG = "NavigationHelper";
    
    /**
     * 私有构造函数，防止实例化
     */
    private NavigationHelper() {
        throw new UnsupportedOperationException("NavigationHelper cannot be instantiated");
    }
    
    // ===== 通用方法 =====
    
    /**
     * 通用跳转方法
     * 
     * @param context 上下文
     * @param targetActivity 目标Activity类
     */
    public static void navigateTo(Context context, Class<?> targetActivity) {
        navigateTo(context, targetActivity, null);
    }
    
    /**
     * 通用跳转方法（带参数）
     * 
     * @param context 上下文
     * @param targetActivity 目标Activity类
     * @param extras 传递的参数Bundle
     */
    public static void navigateTo(Context context, Class<?> targetActivity, Bundle extras) {
        if (context == null || targetActivity == null) {
            Log.e(TAG, "Invalid navigation parameters: context or targetActivity is null");
            return;
        }
        
        try {
            Intent intent = new Intent(context, targetActivity);
            if (extras != null) {
                intent.putExtras(extras);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Navigation failed to " + targetActivity.getSimpleName(), e);
        }
    }

    
    /**
     * 带返回结果的跳转
     * 
     * @param activity 当前Activity
     * @param targetActivity 目标Activity类
     * @param requestCode 请求码
     */
    public static void navigateForResult(Activity activity, Class<?> targetActivity, int requestCode) {
        navigateForResult(activity, targetActivity, requestCode, null);
    }
    
    /**
     * 带返回结果的跳转（带参数）
     * 
     * @param activity 当前Activity
     * @param targetActivity 目标Activity类
     * @param requestCode 请求码
     * @param extras 传递的参数Bundle
     */
    public static void navigateForResult(Activity activity, Class<?> targetActivity, int requestCode, Bundle extras) {
        if (activity == null || targetActivity == null) {
            Log.e(TAG, "Invalid navigation parameters: activity or targetActivity is null");
            return;
        }
        
        try {
            Intent intent = new Intent(activity, targetActivity);
            if (extras != null) {
                intent.putExtras(extras);
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Log.e(TAG, "Navigation for result failed to " + targetActivity.getSimpleName(), e);
        }
    }
    
    // ===== 核心功能页面跳转方法 =====
    
    /**
     * 跳转到词汇训练页面
     * 
     * @param context 上下文
     */
    public static void toVocabulary(Context context) {
        navigateTo(context, com.example.mybighomework.VocabularyActivity.class);
    }
    
    /**
     * 跳转到真题练习列表页面
     * 
     * @param context 上下文
     */
    public static void toExamList(Context context) {
        navigateTo(context, com.example.mybighomework.ExamListActivity.class);
    }
    
    /**
     * 跳转到模拟考试页面
     * 
     * @param context 上下文
     */
    public static void toMockExam(Context context) {
        navigateTo(context, com.example.mybighomework.MockExamActivity.class);
    }
    
    /**
     * 跳转到错题本页面
     * 
     * @param context 上下文
     */
    public static void toWrongQuestion(Context context) {
        navigateTo(context, com.example.mybighomework.WrongQuestionActivity.class);
    }
    
    /**
     * 跳转到学习计划页面
     * 
     * @param context 上下文
     */
    public static void toStudyPlan(Context context) {
        navigateTo(context, com.example.mybighomework.StudyPlanActivity.class);
    }
    
    /**
     * 跳转到每日一句页面
     * 
     * @param context 上下文
     */
    public static void toDailySentence(Context context) {
        navigateTo(context, com.example.mybighomework.DailySentenceActivity.class);
    }
    
    /**
     * 跳转到今日任务页面
     * 
     * @param context 上下文
     */
    public static void toDailyTask(Context context) {
        navigateTo(context, com.example.mybighomework.DailyTaskActivity.class);
    }
    
    /**
     * 跳转到拍照翻译页面
     * 
     * @param context 上下文
     */
    public static void toCameraTranslation(Context context) {
        navigateTo(context, com.example.mybighomework.CameraTranslationActivity.class);
    }
    
    /**
     * 跳转到AI助手页面
     * 
     * @param context 上下文
     */
    public static void toGlmChat(Context context) {
        navigateTo(context, com.example.mybighomework.GlmChatActivity.class);
    }
    
    /**
     * 跳转到文本翻译页面
     * 
     * @param context 上下文
     */
    public static void toTextTranslation(Context context) {
        navigateTo(context, com.example.mybighomework.TextTranslationActivity.class);
    }
    
    // ===== 导航栏页面跳转方法 =====
    
    /**
     * 跳转到学习报告页面
     * 
     * @param context 上下文
     */
    public static void toReport(Context context) {
        navigateTo(context, com.example.mybighomework.ReportActivity.class);
    }
    
    /**
     * 跳转到个人中心页面
     * 
     * @param context 上下文
     */
    public static void toProfile(Context context) {
        navigateTo(context, com.example.mybighomework.ProfileActivity.class);
    }
    
    /**
     * 跳转到更多功能页面
     * 
     * @param context 上下文
     */
    public static void toMore(Context context) {
        navigateTo(context, com.example.mybighomework.MoreActivity.class);
    }
    
    /**
     * 跳转到主页面
     * 
     * @param context 上下文
     */
    public static void toMain(Context context) {
        navigateTo(context, com.example.mybighomework.ui.activity.MainActivity.class);
    }
    
    /**
     * 跳转到主页面（清除栈顶）
     * 
     * @param context 上下文
     */
    public static void toMainClearTop(Context context) {
        if (context == null) {
            Log.e(TAG, "Invalid navigation parameters: context is null");
            return;
        }
        
        try {
            Intent intent = new Intent(context, com.example.mybighomework.ui.activity.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Navigation failed to MainActivity", e);
        }
    }
}
