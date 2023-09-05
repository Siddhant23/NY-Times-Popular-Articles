package com.test.android.siddhant.view

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
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

	// checking Views
	@Test
	fun testViewsInDetailActivity() {
		ActivityScenario.launch(PopularDetailActivity::class.java)

		onView(ViewMatchers.withId(R.id.parentPopularDetail)) // parent
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
		onView(ViewMatchers.withId(R.id.tvDetailTxt)) // Detail TextView
			.check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
	}

	// checking back press to popular screen
	@Test
	fun pressBacktoPopularActivity() {
		ActivityScenario.launch(PopularActivity::class.java)

		pressBack()
		onView(ViewMatchers.withId(R.id.parentPopular))
			.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
	}
}
