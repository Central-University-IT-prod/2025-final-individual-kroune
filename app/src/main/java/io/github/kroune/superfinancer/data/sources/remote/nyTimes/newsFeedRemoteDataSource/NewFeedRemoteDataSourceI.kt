package io.github.kroune.superfinancer.data.sources.remote.nyTimes.newsFeedRemoteDataSource

import io.github.kroune.superfinancer.domain.models.NewsFeedModel

interface NewFeedRemoteDataSourceI {
    suspend fun getNewsFeed(offset: Int, amount: Int): NewsFeedModel
}
