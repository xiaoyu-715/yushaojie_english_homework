package com.example.mybighomework;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.mybighomework.ui.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * MainActivity UI 测试
 * 测试各功能按钮点击跳转和数据显示正确性
 * 
 * _Requirements: 5.5_
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    // ===== 数据显示测试 =====

    @Test
    public void taskProgress_shouldBeDisplayed() {
        onView(withId(R.id.tv_task_progress))
                .check(matches(isDisplayed()));
    }

    @Test
    public void studyDays_shouldBeDisplayed() {
        onView(withId(R.id.tv_study_days))
                .check(matches(isDisplayed()));
    }

    @Test
    public void vocabularyCount_shouldBeDisplayed() {
        onView(withId(R.id.tv_vocabulary_count))
                .check(matches(isDisplayed()));
    }

    @Test
    public void examScore_shouldBeDisplayed() {
        onView(withId(R.id.tv_exam_score))
                .check(matches(isDisplayed()));
    }

    // ===== 核心功能按钮点击跳转测试 =====

    @Test
    public void clickVocabulary_shouldNavigateToVocabularyActivity() {
        onView(withId(R.id.ll_vocabulary)).perform(click());
        intended(hasComponent(VocabularyActivity.class.getName()));
    }

    @Test
    public void clickRealExam_shouldNavigateToExamListActivity() {
        onView(withId(R.id.ll_real_exam)).perform(click());
        intended(hasComponent(ExamListActivity.class.getName()));
    }

    @Test
    public void clickMockExam_shouldNavigateToMockExamActivity() {
        onView(withId(R.id.ll_mock_exam)).perform(click());
        intended(hasComponent(MockExamActivity.class.getName()));
    }

    @Test
    public void clickErrorBook_shouldNavigateToWrongQuestionActivity() {
        onView(withId(R.id.ll_error_book)).perform(click());
        intended(hasComponent(WrongQuestionActivity.class.getName()));
    }

    @Test
    public void clickStudyPlan_shouldNavigateToStudyPlanActivity() {
        onView(withId(R.id.ll_study_plan)).perform(click());
        intended(hasComponent(StudyPlanActivity.class.getName()));
    }

    @Test
    public void clickDailySentence_shouldNavigateToDailySentenceActivity() {
        onView(withId(R.id.ll_daily_sentence)).perform(click());
        intended(hasComponent(DailySentenceActivity.class.getName()));
    }

    @Test
    public void clickDailyTask_shouldNavigateToDailyTaskActivity() {
        onView(withId(R.id.ll_daily_task)).perform(click());
        intended(hasComponent(DailyTaskActivity.class.getName()));
    }

    @Test
    public void clickCameraTranslation_shouldNavigateToCameraTranslationActivity() {
        onView(withId(R.id.ll_camera_translation)).perform(click());
        intended(hasComponent(CameraTranslationActivity.class.getName()));
    }

    @Test
    public void clickAiAssistant_shouldNavigateToDeepSeekChatActivity() {
        onView(withId(R.id.ll_ai_assistant)).perform(click());
        intended(hasComponent(DeepSeekChatActivity.class.getName()));
    }

    @Test
    public void clickTextTranslation_shouldNavigateToTextTranslationActivity() {
        onView(withId(R.id.ll_text_translation)).perform(click());
        intended(hasComponent(TextTranslationActivity.class.getName()));
    }

    // ===== 导航栏按钮点击跳转测试 =====

    @Test
    public void clickNavReport_shouldNavigateToReportActivity() {
        onView(withId(R.id.nav_report)).perform(click());
        intended(hasComponent(ReportActivity.class.getName()));
    }

    @Test
    public void clickNavProfile_shouldNavigateToProfileActivity() {
        onView(withId(R.id.nav_profile)).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void clickNavMore_shouldNavigateToMoreActivity() {
        onView(withId(R.id.nav_more)).perform(click());
        intended(hasComponent(MoreActivity.class.getName()));
    }
}
