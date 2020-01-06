package com.test.android.siddhant.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.android.siddhant.databinding.ItemPopularBinding
import com.test.android.siddhant.model.data.ResultsItem

class PopularAdapter(
    private val items: List<ResultsItem>,
    private val clickListener: ItemClickListener?
) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    class ViewHolder(private val binding: ItemPopularBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(item: ResultsItem?, clickListener: ItemClickListener?) {
            binding.result = item
            binding.executePendingBindings()
            binding.mCardView.setOnClickListener {
                clickListener?.onItemClick(item)
            }
        }
    }
}