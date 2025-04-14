package io.github.kroune.superfinancer.di

import io.github.kroune.super_financer_api.data.SuperFinancerApi
import io.github.kroune.super_financer_api.data.SuperFinancerApiI
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource.AuthRemoteDataSource
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource.AuthRemoteDataSourceI
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource.PostsFeedRemoteDataSource
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource.PostsFeedRemoteDataSourceI
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.userInfoRemoteDataSource.UserInfoRemoteDataSource
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.userInfoRemoteDataSource.UserInfoRemoteDataSourceI
import io.github.kroune.superfinancer.data.FinHubApi
import io.github.kroune.superfinancer.data.FinHubApiI
import io.github.kroune.superfinancer.data.NYTimesApi
import io.github.kroune.superfinancer.data.NYTimesApiI
import io.github.kroune.superfinancer.data.database.ApplicationDatabase
import io.github.kroune.superfinancer.data.paging.newsFeedPaging.NewsFeedPagingI
import io.github.kroune.superfinancer.data.paging.newsFeedPaging.NewsFeedPagingSource
import io.github.kroune.superfinancer.data.paging.postsFeedPaging.PostsFeedPaging
import io.github.kroune.superfinancer.data.paging.postsFeedPaging.PostsFeedPagingI
import io.github.kroune.superfinancer.data.sources.local.finance.FinanceGoalsLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.finance.FinanceOperationsLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.nyTimes.NewsCacheLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource.JwtTokenLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource.JwtTokenLocalDataSourceI
import io.github.kroune.superfinancer.data.sources.remote.finHub.stockQuoteRemoteDataSource.StockQuoteRemoteDataSource
import io.github.kroune.superfinancer.data.sources.remote.finHub.stockQuoteRemoteDataSource.StockQuoteRemoteDataSourceI
import io.github.kroune.superfinancer.data.sources.remote.finHub.stockSearchRemoteDataSource.StockSearchRemoteDataSource
import io.github.kroune.superfinancer.data.sources.remote.finHub.stockSearchRemoteDataSource.StockSearchRemoteDataSourceI
import io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource.ArticlesSearchRemoteDataSource
import io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource.ArticlesSearchRemoteDataSourceI
import io.github.kroune.superfinancer.data.sources.remote.nyTimes.newsFeedRemoteDataSource.NewFeedRemoteDataSourceI
import io.github.kroune.superfinancer.data.sources.remote.nyTimes.newsFeedRemoteDataSource.NewsFeedRemoteDataSource
import io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository.ArticlesSearchRepository
import io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository.ArticlesSearchRepositoryI
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepository
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepositoryI
import io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository.FinanceOperationsRepository
import io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository.FinanceOperationsRepositoryI
import io.github.kroune.superfinancer.domain.repositories.financerGoalsRepository.FinanceGoalsRepository
import io.github.kroune.superfinancer.domain.repositories.financerGoalsRepository.FinanceGoalsRepositoryI
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewsFeedRepository
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewsFeedRepositoryI
import io.github.kroune.superfinancer.domain.repositories.postsFeedRepository.PostsFeedRepository
import io.github.kroune.superfinancer.domain.repositories.postsFeedRepository.PostsFeedRepositoryI
import io.github.kroune.superfinancer.domain.repositories.stockQuoteRepository.StockQuoteRepository
import io.github.kroune.superfinancer.domain.repositories.stockQuoteRepository.StockQuoteRepositoryI
import io.github.kroune.superfinancer.domain.repositories.stockSearchRepository.StockSearchRepository
import io.github.kroune.superfinancer.domain.repositories.stockSearchRepository.StockSearchRepositoryI
import io.github.kroune.superfinancer.domain.repositories.userInfoRepository.UserInfoRepository
import io.github.kroune.superfinancer.domain.repositories.userInfoRepository.UserInfoRepositoryI
import io.github.kroune.superfinancer.domain.usecases.authDataValidator.AuthDataValidator
import io.github.kroune.superfinancer.domain.usecases.authDataValidator.AuthDataValidatorI
import io.github.kroune.superfinancer.domain.usecases.FinanceDataValidator
import io.github.kroune.superfinancer.domain.usecases.FinanceDataValidatorI
import io.ktor.client.HttpClient
import org.koin.dsl.module

val koinModule = module {
    single<NYTimesApiI> { NYTimesApi() }
    single<HttpClient> { HttpClient() }
    single<NewsFeedRepositoryI> { NewsFeedRepository() }
    factory<NewsFeedPagingI> { NewsFeedPagingSource() }
    single<NewFeedRemoteDataSourceI> { NewsFeedRemoteDataSource() }
    single<StockQuoteRemoteDataSourceI> { StockQuoteRemoteDataSource() }
    single<StockQuoteRepositoryI> { StockQuoteRepository() }
    single<FinHubApiI> { FinHubApi() }
    single<ArticlesSearchRemoteDataSourceI> { ArticlesSearchRemoteDataSource() }
    single<ArticlesSearchRepositoryI> { ArticlesSearchRepository() }
    single<FinanceOperationsRepositoryI> { FinanceOperationsRepository() }
    single<FinanceGoalsRepositoryI> { FinanceGoalsRepository() }
    single<SuperFinancerApiI> { SuperFinancerApi() }
    single<PostsFeedRepositoryI> { PostsFeedRepository() }
    single<PostsFeedRemoteDataSourceI> { PostsFeedRemoteDataSource() }
    factory<PostsFeedPagingI> { PostsFeedPaging() }
    single<JwtTokenLocalDataSourceI> { JwtTokenLocalDataSource() }
    single<AuthRepositoryI> { AuthRepository() }
    single<AuthDataValidatorI> { AuthDataValidator() }
    single<AuthRemoteDataSourceI> { AuthRemoteDataSource() }
    single<UserInfoRemoteDataSourceI> { UserInfoRemoteDataSource() }
    single<UserInfoRepositoryI> { UserInfoRepository() }
    single<FinanceDataValidatorI> { FinanceDataValidator() }
    single<StockSearchRemoteDataSourceI> { StockSearchRemoteDataSource() }
    single<StockSearchRepositoryI> { StockSearchRepository() }
    single<FinanceOperationsLocalDataSource> { get<ApplicationDatabase>().financeOperationsDao() }
    single<NewsCacheLocalDataSource> { get<ApplicationDatabase>().newsCacheOperationsDao() }
    single<FinanceGoalsLocalDataSource> { get<ApplicationDatabase>().financeGoalDao() }
}
