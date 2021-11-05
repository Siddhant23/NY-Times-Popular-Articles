package com.test.android.siddhant.utils

import androidx.recyclerview.widget.DiffUtil
import com.test.android.siddhant.model.data.ResultsItem

class DiffUtilRecyclerView: DiffUtil.ItemCallback<ResultsItem>() {
    override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
        return oldItem == newItem
    }
}