package com.test.android.siddhant.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.viewmodel.PopularVM
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PopularActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPopularBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        setLoader(true)
        val (list, adapter) = initAdapter()
        initVM(list, adapter)
    }

    private fun initView() {
        supportActionBar?.title = getString(R.string.titleName)
    }

    private fun initAdapter(): Pair<ArrayList<ResultsItem>, PopularAdapter> {
        val list = ArrayList<ResultsItem>()
        val adapter = PopularAdapter(list, object : ItemClickListener {
            override fun onItemClick(obj: Any?) {
                val item = obj as ResultsItem?
                startActivity(
                    Intent(this@PopularActivity, PopularDetailActivity::class.java)
                        .apply {
                            putExtra(AppConstant.KEY_INTENT_DATA, item?.abstract)
                        }
                )
            }
        })
        binding.rvPopular.adapter = adapter
        return Pair(list, adapter)
    }

    private fun initVM(list: ArrayList<ResultsItem>, adapter: PopularAdapter) {
        val viewModel = ViewModelProvider(this).get(PopularVM::class.java)
        viewModel.fetchArticlesList()
        updateArticlesList(viewModel, list, adapter)
    }

    private fun updateArticlesList(
        viewModel: PopularVM,
        list: ArrayList<ResultsItem>,
        adapter: PopularAdapter
    ) {
        viewModel.articlesListLiveData.observe(this, {
            setLoader(false)
            it?.let {
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun setLoader(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
