package com.test.android.siddhant.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.repository.PopularRepo
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.Resource
import com.test.android.siddhant.utils.Util
import com.test.android.siddhant.utils.Util.Companion.showToast
import com.test.android.siddhant.viewmodel.PopularVM
import com.test.android.siddhant.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class PopularActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPopularBinding.inflate(layoutInflater) }
    private lateinit var adapter : PopularAdapter
    private lateinit var list : ArrayList<ResultsItem>
    private lateinit var viewModel: PopularVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initAdapter()
        initVM()
    }

    private fun initView() {
        supportActionBar?.title = getString(R.string.titleName)
    }

    private fun initAdapter() {
        list = ArrayList()
        adapter = PopularAdapter(list) { item ->
            startActivity(
                Intent(this@PopularActivity, PopularDetailActivity::class.java)
                    .apply {
                        putExtra(AppConstant.KEY_INTENT_DATA, item?.abstract)
                    }
            )
        }
        binding.rvPopular.adapter = adapter
    }

    private fun setLoader(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun initVM() {
        viewModel = ViewModelProvider(this,ViewModelFactory(PopularRepo())).get(PopularVM::class.java)

        if (Util.isNetworkAvailable(this)){
            lifecycleScope.launch {
                viewModel.fetchArticlesList()
            }
        }else{
            showToast(this, getString(R.string.error_internet))
        }

        observeResponse()
    }

    private fun observeResponse() {
        viewModel.articlesListLiveData.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> {
                    setLoader(true)
                }
                Resource.Status.SUCCESS -> {
                    setLoader(false)
                    it.data?.let { items ->
                        list.addAll(items)
                    }
                    adapter.notifyDataSetChanged()
                }
                Resource.Status.ERROR -> {
                    setLoader(false)
                    it.message?.let { msg -> showToast(this, msg) }
                }
            }
        })
    }
}
