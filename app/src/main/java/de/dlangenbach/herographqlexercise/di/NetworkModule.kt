package de.dlangenbach.herographqlexercise.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .addHttpHeader(
            "Authorization",
            "bearer $AUTHENTICATION_TOKEN"
        )
        .build()

    private const val AUTHENTICATION_TOKEN = "PLEASE UPDATE"
}