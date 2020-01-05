package com.test.android.siddhant.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.android.siddhant.R
import com.test.android.siddhant.utils.AppConstant
import kotlinx.android.synthetic.main.activity_popular_detail.*

class PopularDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_detail)

        supportActionBar?.title = getString(R.string.titleName)
        if (intent.hasExtra(AppConstant.KEY_INTENT_DATA)) {
            tvDetailTxt.text = intent.getStringExtra(AppConstant.KEY_INTENT_DATA)
        }
    }
}
