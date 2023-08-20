package de.dlangenbach.herographqlexercise.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import de.dlangenbach.herographqlexercise.R
import de.dlangenbach.herographqlexercise.RepositoriesWithStarsQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(private val apolloClient: ApolloClient) :
    ViewModel() {

    data class State(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val repositories: List<RepositoriesWithStarsQuery.Edge> = emptyList()
    )

    class Error(@StringRes val stringResId: Int, vararg val params: Any)

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    val error = MutableSharedFlow<Error>(replay = 0)

    /**
     * Updates the current search query
     */
    fun updateSearchQuery(searchQuery: String) {
        _state.update { it.copy(searchQuery = searchQuery) }
    }

    /**
     * Starts a new search for repository using the latest search query
     */
    fun searchRepositories() {
        viewModelScope.launch {
            val searchQuery = _state.value.searchQuery
            if (searchQuery.isNotBlank()) {
                _state.update { it.copy(isLoading = true) }
                val response = try {
                    apolloClient.query(RepositoriesWithStarsQuery(searchQuery)).execute()
                } catch (e: Exception) {
                    null
                }
                if (response?.hasErrors() != true) {
                    error.emit(Error(R.string.error_search_repositories_no_connection))
                    _state.update { it.copy(isLoading = false, repositories = emptyList()) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            repositories = response.data?.search?.edges?.filterNotNull()
                                ?: emptyList()
                        )
                    }
                }
            } else {
                error.emit(Error(R.string.error_search_query_empty))
            }
        }
    }
}