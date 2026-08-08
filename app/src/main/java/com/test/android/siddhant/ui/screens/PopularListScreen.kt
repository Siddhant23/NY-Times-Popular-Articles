package com.test.android.siddhant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.android.siddhant.model.data.ResultsItem
import com.test.android.siddhant.model.data.listKey
import com.test.android.siddhant.ui.components.ArticleCard
import com.test.android.siddhant.utils.Resource
import com.test.android.siddhant.viewmodel.PopularVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularListScreen(
    viewModel: PopularVM,
    onArticleClick: (ResultsItem) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PopularListScreen(
        uiState = uiState,
        onArticleClick = onArticleClick,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularListScreen(
    uiState: Resource<List<ResultsItem>>,
    onArticleClick: (ResultsItem) -> Unit,
    onRetry: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is Resource.Error) {
            snackbarHostState.showSnackbar(state.message.orEmpty())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Most Popular") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        when (val state = uiState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val items = state.data.orEmpty()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    itemsIndexed(
                        items = items,
                        key = { index, item -> item.listKey(index) },
                    ) { _, item ->
                        ArticleCard(
                            item = item,
                            onClick = { onArticleClick(item) },
                        )
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Unable to load articles",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Button(onClick = onRetry) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
        }
    }
}
