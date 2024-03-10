package com.tomerpacific.todo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import service.TodoItemsRepository
import service.TodoItemsSerializer


@RunWith(AndroidJUnit4::class)
class TodoItemsRepositoryTest {

    val TEST_DATA_STORE_FILE_NAME = "testStore.pb"
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    private val testCoroutineDispatcher: TestCoroutineDispatcher =
        TestCoroutineDispatcher()
    private val testCoroutineScope =
        TestCoroutineScope(testCoroutineDispatcher + Job())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dataStore: DataStore<TodoItems> = DataStoreFactory.create(
        scope = testCoroutineScope,
        produceFile = {
            testContext.dataStoreFile(TEST_DATA_STORE_FILE_NAME)
        },
        serializer = TodoItemsSerializer
    )

    private val repository: TodoItemsRepository =
        TodoItemsRepository(dataStore)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testFetchInitialTodoItems() {
        runTest {
            val items = repository.todoItemsFlow.first().itemsList
            assert(items.size == 0)
        }
    }

}