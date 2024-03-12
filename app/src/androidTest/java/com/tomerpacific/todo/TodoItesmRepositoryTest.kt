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
import org.junit.After
import org.junit.Before
import org.junit.Test
import service.TodoItemsRepository
import service.TodoItemsSerializer
import java.io.File
import java.util.UUID
import kotlin.random.Random

const val TEST_DATA_STORE_FILE_NAME = "testStore.pb"
private val testCoroutineDispatcher: TestCoroutineDispatcher =
    TestCoroutineDispatcher()
private val testCoroutineScope =
    TestCoroutineScope(testCoroutineDispatcher + Job())

private val testContext: Context = ApplicationProvider.getApplicationContext()

@RunWith(AndroidJUnit4::class)
class TodoItemsRepositoryTest {


    private lateinit var dataStore: DataStore<TodoItems>
    private lateinit var repository: TodoItemsRepository
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createDataStore() {
        val randomDataStoreIndex: Int = Random.nextInt()
        dataStore = DataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile = {
                testContext.dataStoreFile(TEST_DATA_STORE_FILE_NAME + randomDataStoreIndex)
            },
            serializer = TodoItemsSerializer
        )
        repository = TodoItemsRepository(dataStore)
    }

    @Before
    fun setup() {
        createDataStore()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testFetchInitialTodoItems() {
        runTest {
            val items = repository.todoItemsFlow.first().itemsList
            assert(items.size == 0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testAdditionOfTodoItem() {
        runTest {
            val todoItem: TodoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
            .setItemDescription("test todo item").build()
            repository.updateTodoItems(todoItem)

            val todoItems = repository.todoItemsFlow.first().itemsList
            assert(todoItems.size == 1)

            assert(todoItems[0].itemDescription.equals("test todo item"))
        }
    }

    @After
    fun cleanup() {
        File(testContext.filesDir, "datastore").deleteRecursively()
    }

}