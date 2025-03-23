package com.test.android.siddhant.view

import androidx.test.filters.LargeTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@LargeTest
@RunWith(Suite::class)
@Suite.SuiteClasses(
     PopularActivityTest::class,
    PopularDetailActivityTest::class,
)
class ActivityTestSuite
