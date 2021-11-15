package com.test.android.siddhant.di

import android.content.Context
import android.content.SharedPreferences
import com.test.android.siddhant.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

object AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object SharedPrefModule {

        @Provides
        fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(
                context.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
        }
    }
}