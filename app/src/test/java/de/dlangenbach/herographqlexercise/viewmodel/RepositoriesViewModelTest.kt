package de.dlangenbach.herographqlexercise.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.dlangenbach.herographqlexercise.data.RepositoryWithStars
import de.dlangenbach.herographqlexercise.data.network.NetworkDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoriesViewModelTest {

    @MockK
    private lateinit var networkDataSource: NetworkDataSource

    private lateinit var repositoriesViewModel: RepositoriesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repositoriesViewModel = RepositoriesViewModel(networkDataSource)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test searching repositories with an empty search query will publish a error message`() =
        runTest {
            repositoriesViewModel.error.test {
                repositoriesViewModel.searchRepositories()
                assertThat(awaitItem().stringResId).isEqualTo(de.dlangenbach.herographqlexercise.R.string.error_search_query_empty)
            }
        }

    @Test
    fun `test searching repositories with an error will publish a error message`() = runTest {
        coEvery { networkDataSource.searchRepositories(any()) } returns NetworkDataSource.Result.Error

        repositoriesViewModel.updateSearchQuery("searchQuery")

        repositoriesViewModel.error.test {
            repositoriesViewModel.searchRepositories()
            assertThat(awaitItem().stringResId).isEqualTo(de.dlangenbach.herographqlexercise.R.string.error_search_repositories_no_connection)
        }
    }

    @Test
    fun `test searching repositories successful will update the state properly`() = runTest {
        val repositories = listOf(
            RepositoryWithStars("test", 100)
        )
        coEvery { networkDataSource.searchRepositories(any()) } returns NetworkDataSource.Result.Success(
            repositories
        )

        repositoriesViewModel.updateSearchQuery("searchQuery")

        repositoriesViewModel.state.test {
            repositoriesViewModel.searchRepositories()
            assertThat(expectMostRecentItem().repositories).isEqualTo(repositories)
        }
    }
}