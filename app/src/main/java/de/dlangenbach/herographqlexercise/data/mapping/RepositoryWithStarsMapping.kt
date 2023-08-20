package de.dlangenbach.herographqlexercise.data.mapping

import de.dlangenbach.herographqlexercise.RepositoriesWithStarsQuery
import de.dlangenbach.herographqlexercise.data.RepositoryWithStars

fun RepositoriesWithStarsQuery.Edge.toInternal() =
    RepositoryWithStars(
        this.node?.onRepository?.name ?: "",
        this.node?.onRepository?.stargazerCount ?: 0
    )

fun List<RepositoriesWithStarsQuery.Edge>.toInternal() = this.map { it.toInternal() }