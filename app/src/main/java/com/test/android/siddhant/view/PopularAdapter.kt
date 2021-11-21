package com.test.android.siddhant.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.android.siddhant.databinding.ItemPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.DiffUtilRecyclerView
import javax.inject.Inject

class PopularAdapter @Inject constructor(diffUtilRecyclerView: DiffUtilRecyclerView): ListAdapter<ResultsItem, PopularAdapter.ViewHolder>(diffUtilRecyclerView) {

    private var items: List<ResultsItem> = emptyList()
    lateinit var itemClickListener: ((ResultsItem?) -> Unit)

    constructor(items: List<ResultsItem>, itemClickListener: (item: ResultsItem?) -> Unit) : this(
        DiffUtilRecyclerView()
    ) {
        this.items = items
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemBinding: ItemPopularBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        internal fun bind(item: ResultsItem?) {
            with(itemBinding) {
                tvTitle.text = item?.title
                tvByline.text = item?.byline
                tvDatePublished.text = item?.publishedDate
                mCardView.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }
}