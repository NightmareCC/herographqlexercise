package de.dlangenbach.herographqlexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dagger.hilt.android.AndroidEntryPoint
import de.dlangenbach.herographqlexercise.data.RepositoryWithStars
import de.dlangenbach.herographqlexercise.ui.theme.HeroGraphQLExerciseTheme
import de.dlangenbach.herographqlexercise.viewmodel.RepositoriesViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: RepositoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroGraphQLExerciseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    val error by viewModel.error.collectAsState(initial = null)
                    RepositoriesContent(
                        state = state,
                        error = error,
                        onUpdateSearchQuery = viewModel::updateSearchQuery,
                        onStartSearch = viewModel::searchRepositories
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepositoriesContent(
    state: RepositoriesViewModel.State,
    error: RepositoriesViewModel.Error?,
    onUpdateSearchQuery: (String) -> Unit,
    onStartSearch: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            focusManager.clearFocus(true)
            snackbarHostState.showSnackbar(context.getString(it.stringResId, it.params))
        }
    }
    if (state.isLoading) {
        LoadingDialog()
    }
    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = state.searchQuery,
                        onValueChange = onUpdateSearchQuery
                    )
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(30.dp)
                            .clickable(onClick = onStartSearch),
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                LazyColumn {
                    items(items = state.repositories) {
                        RepositoryElement(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun RepositoryElement(repositoryData: RepositoryWithStars) {
    Row {
        Text(modifier = Modifier.weight(1f), text = repositoryData.name)
        Icon(Icons.Default.Star, contentDescription = null)
        Text(repositoryData.stars.toString())
    }
}

@Composable
private fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        CircularProgressIndicator()
    }
}