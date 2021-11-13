package com.test.android.siddhant.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.android.siddhant.databinding.ItemPopularBinding
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.utils.DiffUtilRecyclerView

class PopularAdapter(
    private val items: List<ResultsItem>,
    private val itemClickListener: (item: ResultsItem?) -> Unit
) : ListAdapter<ResultsItem, PopularAdapter.ViewHolder>(DiffUtilRecyclerView()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[holder.bindingAdapterPosition])
    }

      inner class ViewHolder(private val itemBinding: ItemPopularBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        internal fun bind(item: ResultsItem?) {
            with(itemBinding) {
                tvTitle.text = item?.abstract
                tvByline.text = item?.byline
                tvDatePublished.text = item?.publishedDate
                mCardView.setOnClickListener {
                    itemClickListener(item)
                }
            }
        }
    }
}