package com.test.android.siddhant.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.Resource
import com.test.android.siddhant.utils.Util
import com.test.android.siddhant.utils.startActivity
import com.test.android.siddhant.viewmodel.PopularVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PopularActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPopularBinding.inflate(layoutInflater) }
    private  val viewModel: PopularVM by viewModels()
    @Inject lateinit var adapter : PopularAdapter
    private lateinit var list : ArrayList<ResultsItem>

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
        list = ArrayList()
        adapter = PopularAdapter(list) { item ->
            startActivity<PopularDetailActivity> {
                putExtra(AppConstant.KEY_INTENT_DATA, item?.abstract)
            }
        }
        binding.rvPopular.itemAnimator = DefaultItemAnimator()
        binding.rvPopular.adapter = adapter
    }

    private fun setLoader(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
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
                        list.addAll(items)
                        adapter.submitList(list)
                    }
                }
                is Resource.Error -> {
                    setLoader(false)
                    it.message?.let { msg -> Util(this).showToast(msg) }
                }
            }
        })
    }
}
