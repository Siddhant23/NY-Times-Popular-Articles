package com.test.android.siddhant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class TestCoroutineRule : TestRule {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher)

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                Dispatchers.setMain(testCoroutineDispatcher)

                // everything above this happens before the test
                base.evaluate()
                // everything below this happens after the test
                Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
            }
        }

    fun runBlockingTest(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest { block() }
}
