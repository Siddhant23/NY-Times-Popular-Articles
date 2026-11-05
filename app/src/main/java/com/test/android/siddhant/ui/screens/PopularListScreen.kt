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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.android.siddhant.R
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = stringResource(R.string.title_most_popular)) })
        },
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
                    // The cause is shown inline (previously it was duplicated in a snackbar).
                    // Polite live region so TalkBack announces the failure, as the snackbar did.
                    Column(
                        modifier =
                            Modifier
                                .padding(horizontal = 24.dp)
                                .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.error_load_articles),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (!state.message.isNullOrBlank()) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Button(onClick = onRetry) {
                            Text(text = stringResource(R.string.action_retry))
                        }
                    }
                }
            }
        }
    }
}
