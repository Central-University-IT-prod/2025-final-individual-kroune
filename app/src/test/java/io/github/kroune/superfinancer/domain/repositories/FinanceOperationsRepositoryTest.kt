package io.github.kroune.superfinancer.domain.repositories

import io.github.kroune.superfinancer.data.sources.local.finance.FinanceOperationsLocalDataSource
import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity
import io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository.FinanceOperationsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.test.assertEquals

class FinanceOperationsRepositoryTest {
    private lateinit var module: Module
    private lateinit var repository: FinanceOperationsRepository
    private lateinit var mockDataSource: MockFinanceOperationsDataSource

    @Before
    fun setUp() {
        mockDataSource = MockFinanceOperationsDataSource()
        module = module {
            single<FinanceOperationsLocalDataSource> { mockDataSource }
        }
        startKoin {
            modules(module)
        }
        repository = FinanceOperationsRepository()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should add operation`() = runBlocking {
        val operation = FinanceOperationEntity(1, 100.0f, 1, "Test Operation")

        val result = repository.addOperation(operation)

        assertEquals(1, result)
        assertEquals(operation, mockDataSource.lastInsertedOperation)
    }

    @Test
    fun `should get all operations`() = runBlocking {
        val operations = listOf(
            FinanceOperationEntity(1, 100.0f, 1, "Operation 1"),
            FinanceOperationEntity(2, 200.0f, 2, "Operation 2")
        )
        mockDataSource.operationsList = operations

        val result = repository.getAllOperations()

        assertEquals(operations, result)
    }

    @Test
    fun `should get total money amount`() = runBlocking {
        mockDataSource.totalAmount = 300.0f

        val result = repository.getTotalMoneyAmount()

        assertEquals(300.0f, result)
    }

    @Test
    fun `should sum by goal id`() = runBlocking {
        mockDataSource.goalSum = 150.0f

        val result = repository.sumByGoalId(1)

        assertEquals(150.0f, result)
    }

    @Test
    fun `should delete operation`() = runBlocking {
        repository.deleteOperation(1)

        assertEquals(1, mockDataSource.lastDeletedOperationId)
    }

    @Test
    fun `should delete by goal id`() = runBlocking {
        repository.deleteByGoalId(1)

        assertEquals(1, mockDataSource.lastDeletedGoalId)
    }
}

private class MockFinanceOperationsDataSource : FinanceOperationsLocalDataSource {
    var lastInsertedOperation: FinanceOperationEntity? = null
    var operationsList: List<FinanceOperationEntity> = emptyList()
    var totalAmount: Float = 0f
    var goalSum: Float = 0f
    var lastDeletedOperationId: Int = 0
    var lastDeletedGoalId: Int = 0

    override suspend fun insert(entity: FinanceOperationEntity): Long {
        lastInsertedOperation = entity
        return 1L
    }

    override suspend fun getAll(): List<FinanceOperationEntity> = operationsList

    override suspend fun getTotalMonetAmount(): Float = totalAmount

    override suspend fun sumByGoalId(goalId: Int): Float = goalSum

    override suspend fun deleteByOperationId(operationId: Int) {
        lastDeletedOperationId = operationId
    }

    override suspend fun deleteByGoalId(goalId: Int) {
        lastDeletedGoalId = goalId
    }
}
