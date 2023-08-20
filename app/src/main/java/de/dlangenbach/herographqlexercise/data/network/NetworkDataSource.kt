package de.dlangenbach.herographqlexercise.data.network

import com.apollographql.apollo3.ApolloClient
import de.dlangenbach.herographqlexercise.RepositoriesWithStarsQuery
import de.dlangenbach.herographqlexercise.data.RepositoryWithStars
import de.dlangenbach.herographqlexercise.data.mapping.toInternal
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val apolloClient: ApolloClient) {

    sealed class Result {
        data class Success(val data: List<RepositoryWithStars>) : Result()
        object Error : Result()
    }

    /**
     * Searches for repositories using the given [searchQuery]
     */
    suspend fun searchRepositories(searchQuery: String): Result {
        val response = try {
            apolloClient.query(RepositoriesWithStarsQuery(searchQuery)).execute()
        } catch (e: Exception) {
            null
        }
        return if (response?.hasErrors() != false) {
            Result.Error
        } else {
            Result.Success(
                response.data?.search?.edges?.filterNotNull()?.toInternal() ?: emptyList()
            )
        }
    }
}