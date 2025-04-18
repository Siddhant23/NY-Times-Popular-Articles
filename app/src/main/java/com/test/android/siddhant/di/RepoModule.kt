package com.test.android.siddhant.di

import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.repository.PopularRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Singleton
    @Provides
    fun providesPopularRepo(
        apiService: ApiService,
        @ApplicationScope ioScope: CoroutineScope,
    ) = PopularRepo(apiService, ioScope)
}
