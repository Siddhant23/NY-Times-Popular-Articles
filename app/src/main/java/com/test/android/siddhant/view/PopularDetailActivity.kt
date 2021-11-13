package com.test.android.siddhant.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularDetailBinding
import com.test.android.siddhant.utils.AppConstant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularDetailActivity : AppCompatActivity() {
   private val binding by lazy { ActivityPopularDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.titleName)
        intent.hasExtra(AppConstant.KEY_INTENT_DATA).apply {
            binding.tvDetailTxt.text = intent.getStringExtra(AppConstant.KEY_INTENT_DATA)
        }
    }
}
