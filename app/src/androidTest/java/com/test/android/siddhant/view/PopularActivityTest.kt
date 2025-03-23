package com.test.android.siddhant.view

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.test.android.siddhant.R
import com.test.android.siddhant.utils.AppConstant
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PopularActivityTest {
    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val activityRule = ActivityScenarioRule(PopularActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    // checking Views
    @Test
    fun testViewsInPopularActivity() {
        onView(withId(R.id.parentPopular)).check(matches(isDisplayed())) // parent
        onView(withId(R.id.rvPopular)).check(matches(isDisplayed())) // recyclerview
        onView(withId(R.id.progressBar)).check(matches(isDisplayed())) // progressBar

        onView(withId(R.id.rvPopular)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))) // visibility
    }

    @Test
    fun testItemViewClickOpenDetailActivity() {
        val scenario = ActivityScenario.launch(PopularActivity::class.java)
        onView(withId(R.id.rvPopular)).perform(click()) // clickable
        scenario.onActivity { activity ->
            startActivity(
                activity,
                Intent(activity, PopularDetailActivity::class.java),
                bundleOf(Pair(AppConstant.KEY_INTENT_DATA, "item"))


            )
        }
    }
}
