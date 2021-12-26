package com.test.android.siddhant.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.Resource
import com.test.android.siddhant.utils.showToast
import com.test.android.siddhant.utils.startActivity
import com.test.android.siddhant.viewmodel.PopularVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularActivity : AppCompatActivity() {
	private val binding by lazy { ActivityPopularBinding.inflate(layoutInflater) }
	private val viewModel: PopularVM by viewModels()
	private var adapter = PopularAdapter(::onPopularItemClicked)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		initView()
		initAdapter()
		initVM()
		observeResponse()
	}

	private fun initView() {
		supportActionBar?.title = getString(R.string.titleName)
	}

	private fun initAdapter() {
		binding.rvPopular.itemAnimator = DefaultItemAnimator()
		binding.rvPopular.adapter = adapter
	}

	private fun onPopularItemClicked(item: ResultsItem?) {
		startActivity<PopularDetailActivity> {
			putExtra(AppConstant.KEY_INTENT_DATA, item?.abstract)
		}
	}

	private fun setLoader(isVisible: Boolean) {
		binding.progressBar.isVisible = if (isVisible) true else false
	}

	private fun initVM() {
		lifecycleScope.launch {
			viewModel.fetchArticlesList()
		}
	}

	private fun observeResponse() {
		viewModel.articlesListLiveData.observe(this, {
			when (it) {
				is Resource.Loading -> {
					setLoader(true)
				}
				is Resource.Success -> {
					setLoader(false)
					it.data?.let { items ->
						adapter.submitList(items)
					}
				}
				is Resource.Error -> {
					setLoader(false)
					it.message?.let { msg -> showToast(msg) }
				}
			}
		})
	}

	override fun onBackPressed() {
		super.onBackPressed()
		finish()
	}
}
