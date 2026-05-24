package com.test.android.siddhant

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.test.android.siddhant.ui.navigation.PopularDetailRoute
import com.test.android.siddhant.ui.navigation.PopularListRoute
import com.test.android.siddhant.ui.screens.PopularDetailScreen
import com.test.android.siddhant.ui.screens.PopularListScreen
import com.test.android.siddhant.ui.theme.NYTimesTheme
import com.test.android.siddhant.viewmodel.PopularVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NYTimesTheme {
                PopularApp()
            }
        }
    }
}

@Composable
private fun PopularApp() {
    val backStack = rememberNavBackStack(PopularListRoute)
    val vm: PopularVM = hiltViewModel()

    NavDisplay(
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<PopularListRoute> {
                    PopularListScreen(
                        viewModel = vm,
                        onArticleClick = { item ->
                            backStack.add(PopularDetailRoute(item.abstract.orEmpty()))
                        },
                    )
                }
                entry<PopularDetailRoute> { route ->
                    PopularDetailScreen(
                        articleAbstract = route.articleAbstract,
                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    )
                }
            },
    )
}
