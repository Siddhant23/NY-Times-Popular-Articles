package com.test.android.siddhant.view

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.test.android.siddhant.R
import com.test.android.siddhant.databinding.ActivityPopularDetailBinding
import com.test.android.siddhant.utils.AppConstant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularDetailActivity : BaseActivity() {
    private val binding by lazy { ActivityPopularDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.titleName)
        intent.hasExtra(AppConstant.KEY_INTENT_DATA).apply {
            binding.tvDetailTxt.text = intent.getStringExtra(AppConstant.KEY_INTENT_DATA)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.parentPopularDetail) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right,
            )
            insets
        }
    }
}
