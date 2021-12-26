package com.test.android.siddhant.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.test.android.siddhant.TestCoroutineRule
import com.test.android.siddhant.di.ApplicationScope
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = Config.NONE)
class PopularVMTest {

	@get:Rule
	var hiltRule = HiltAndroidRule(this)

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()

	@get:Rule
	val testCoroutineRule = TestCoroutineRule()

	@get:Rule
	val rule: MockitoRule = MockitoJUnit.rule()

	@Mock
	private lateinit var apiResultObserver: Observer<Resource<ArrayList<ResultsItem>?>>

	@Mock
	private lateinit var viewModel: PopularVM

	@Mock
	private lateinit var repo: PopularRepo

	@Mock
	private lateinit var listsItemModel: ArrayList<ResultsItem>

	@ApplicationScope
	private lateinit var testCoroutineScope: TestScope

	@Before
	fun setUp() {
		hiltRule.inject()
		testCoroutineScope = TestScope()
		viewModel = PopularVM(repo, testCoroutineScope).apply {
			articlesListLiveData.observeForever(apiResultObserver)
		}
	}

	@After
	fun tearDown() {
		viewModel.articlesListLiveData.removeObserver(apiResultObserver)
	}

	@Test
	fun `check LiveData not null`() {
		testCoroutineRule.runBlockingTest {
			viewModel = spy(PopularVM(repo, testCoroutineScope))
			viewModel.articlesListLiveData.observeForever(apiResultObserver)
			launch(testCoroutineScope.coroutineContext) {
				viewModel.fetchArticlesList()
				assertNotNull(viewModel.articlesListLiveData)
			}
		}
	}

	@Test
	fun fetchArticlesList() {
		testCoroutineRule.runBlockingTest {
			// Given
			`when`(repo.getPopularData()).thenReturn(listsItemModel)
			launch(testCoroutineScope.coroutineContext) {
				// When
				viewModel.fetchArticlesList()
				// Then
				verify(apiResultObserver).onChanged(Resource.Loading())
				verify(apiResultObserver).onChanged(Resource.Success(listsItemModel))
			}
		}
	}

	@Test(expected = Throwable::class)
	fun `verify failure when data returns fetchArticlesList`() {
		val errorMsg = Throwable().message
		testCoroutineRule.runBlockingTest {
			// Given
			`when`(repo.getPopularData()).thenThrow(Throwable::class.java)
			launch(testCoroutineScope.coroutineContext) {
				// When
				viewModel.fetchArticlesList()
				// Then
				verify(apiResultObserver).onChanged(Resource.Loading())
				verify(apiResultObserver).onChanged(errorMsg?.let { Resource.Error(it) })
			}
		}
	}
}
