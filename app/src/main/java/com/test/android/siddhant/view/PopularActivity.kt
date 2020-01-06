package com.test.android.siddhant.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.viewmodel.PopularVM

class PopularActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPopularBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular)
        initView()
        setLoader(true)
        val (list, adapter) = initAdapter()
        initVM(list, adapter)
    }

    private fun initView() {
        supportActionBar?.title = getString(R.string.titleName)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_popular)
        binding.lifecycleOwner = this
    }

    private fun initAdapter(): Pair<ArrayList<ResultsItem>, PopularAdapter> {
        val list = ArrayList<ResultsItem>()
        val adapter = PopularAdapter(list, object : ItemClickListener {
            override fun onItemClick(obj: Any?) {
                val item = obj as ResultsItem?
                startActivity(
                    Intent(this@PopularActivity, PopularDetailActivity::class.java)
                        .putExtra(AppConstant.KEY_INTENT_DATA, item?.title)
                )
            }
        })
        binding.rvPopular.adapter = adapter
        return Pair(list, adapter)
    }

    private fun initVM(list: ArrayList<ResultsItem>, adapter: PopularAdapter) {
        val viewModel = ViewModelProviders.of(this).get(PopularVM::class.java)
        viewModel.fetchArticlesList()
        updateArticlesList(viewModel, list, adapter)
    }

    private fun updateArticlesList(
        viewModel: PopularVM,
        list: ArrayList<ResultsItem>,
        adapter: PopularAdapter
    ) {
        viewModel.articlesListLiveData.observe(this, Observer {
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
