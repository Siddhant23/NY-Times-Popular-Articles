package com.test.android.siddhant.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.android.siddhant.databinding.ItemPopularBinding
import com.test.android.siddhant.model.data.ResultsItem

class PopularAdapter(val itemClickListener: (item: ResultsItem?) -> Unit) :
	ListAdapter<ResultsItem, PopularAdapter.ViewHolder>(DIFF_CALLBACK) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	inner class ViewHolder(private val itemBinding: ItemPopularBinding) :
		RecyclerView.ViewHolder(itemBinding.root) {
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

	companion object {
		private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultsItem>() {
			override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
				return oldItem == newItem
			}
		}
	}
}
