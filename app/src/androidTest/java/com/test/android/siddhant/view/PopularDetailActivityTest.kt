package com.test.android.siddhant.view

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.test.android.siddhant.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PopularDetailActivityTest {

	@get:Rule(order = 1)
	var hiltRule = HiltAndroidRule(this)

	@get:Rule(order = 2)
	val activityRule = ActivityScenarioRule(PopularDetailActivity::class.java)

	@Before
	fun setUp() {
		hiltRule.inject()
	}

	// Checking Views in Detail Activity
	@Test
	fun testViewsInDetailActivity() {
		ActivityScenario.launch(PopularDetailActivity::class.java)

		onView(withId(R.id.parentPopularDetail)) // parent
			.check(matches(isDisplayed()))
		onView(withId(R.id.tvDetailTxt)) // Detail TextView
			.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
	}

	// checking back press to popular screen
	@Test
	fun pressBacktoPopularActivity() {
		// Perform back press action
		pressBackUnconditionally()

		//open PopularActivity
		ActivityScenario.launch(PopularActivity::class.java)

		// Check if the parent view of PopularActivity is displayed
		onView(withId(R.id.parentPopular))
			.check(matches(isDisplayed()))
	}
}
