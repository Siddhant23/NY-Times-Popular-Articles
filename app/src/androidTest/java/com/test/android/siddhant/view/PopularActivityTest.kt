package com.test.android.siddhant.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.test.android.siddhant.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class PopularActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(PopularActivity::class.java)

    //checking Views
    @Test
    fun testViewsInPopularActivity() {

        onView(withId(R.id.parentPopular)).check(matches(isDisplayed()))   //parent
        onView(withId(R.id.rvPopular)).check(matches(isDisplayed()))       //recylerview
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))     //progressBar

        onView(withId(R.id.rvPopular)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))  //visibility

    }

    @Test
    fun testItemViewClick() {
        onView(withId(R.id.rvPopular)).perform(click())   //clickable
    }
}