package com.tomerpacific.todo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

const val TEST_DATA_STORE_FILE_NAME = "testStore.pb"
const val TEST_TODO_ITEM_DESCRIPTION = "test todo item"
private val testContext: Context = ApplicationProvider.getApplicationContext()

@OptIn(ExperimentalCoroutinesApi::class)
private val dispatcher = TestCoroutineDispatcher()
@OptIn(ExperimentalCoroutinesApi::class)
private val testScope = TestCoroutineScope(dispatcher)

@RunWith(AndroidJUnit4::class)
class TodoItemsRepositoryTest {


    private lateinit var dataStore: DataStore<TodoItems>
    private lateinit var repository: TodoItemsRepository
    private fun createDataStore() {
        dataStore = DataStoreFactory.create(
            produceFile = {
                testContext.dataStoreFile(TEST_DATA_STORE_FILE_NAME)
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
            testScope.launch {
                val items = repository.todoItemsFlow.first().itemsList
                assert(items.size == 0)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testAdditionOfTodoItem() {
        runTest {
            testScope.launch {
                val todoItem: TodoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
                    .setItemDescription(TEST_TODO_ITEM_DESCRIPTION).build()
                repository.updateTodoItems(todoItem)

                val todoItems = repository.todoItemsFlow.first().itemsList
                assert(todoItems.size == 1)

                assert(todoItems[0].itemDescription.equals(TEST_TODO_ITEM_DESCRIPTION))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testRemovalOfTodoItem() {
        runTest {
            testScope.launch {
                val todoItem: TodoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
                    .setItemDescription(TEST_TODO_ITEM_DESCRIPTION).build()
                repository.updateTodoItems(todoItem)

                var todoItems = repository.todoItemsFlow.first().itemsList
                assert(todoItems.size == 1)

                repository.removeTodoItem(todoItem)

                todoItems = repository.todoItemsFlow.first().itemsList
                assert(todoItems.size == 0)
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repository_testRemovalOfAllTodoItems() {
        runTest {
            testScope.launch {
                val todoItem: TodoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
                    .setItemDescription(TEST_TODO_ITEM_DESCRIPTION).build()
                repository.updateTodoItems(todoItem)

                var todoItems = repository.todoItemsFlow.first().itemsList
                assert(todoItems.size == 1)

                repository.removeAllTodoItems()

                todoItems = repository.todoItemsFlow.first().itemsList
                assert(todoItems.size == 0)
            }
        }
    }

    @After
    fun cleanup() {
        File(testContext.filesDir, "datastore").deleteRecursively()
    }

}