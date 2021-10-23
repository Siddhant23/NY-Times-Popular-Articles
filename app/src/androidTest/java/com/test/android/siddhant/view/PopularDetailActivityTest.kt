package com.test.android.siddhant.view

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.test.android.siddhant.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class PopularDetailActivityTest {

    //checking Views
    @Test
    fun testViewsInDetailActivity() {
        ActivityScenario.launch(PopularDetailActivity::class.java)

        onView(ViewMatchers.withId(R.id.parentPopularDetail))  //parent
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvDetailTxt))          //Detail TextView
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    //checking back press to popular screen
    @Test
    fun pressBacktoPopularActivity() {
        ActivityScenario.launch(PopularActivity::class.java)

        pressBack()
        onView(ViewMatchers.withId(R.id.parentPopular))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}