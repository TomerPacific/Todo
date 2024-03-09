package com.tomerpacific.todo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import service.TodoItemsRepository
import service.TodoItemsSerializer
import java.io.File


@RunWith(AndroidJUnit4::class)
class TodoItemsRepositoryTest {

    val TEST_DATA_STORE_FILE_NAME = "testStore.pb"
    private val testContext: Context = ApplicationProvider.getApplicationContext()

//    private val Context.todoItemsStore: DataStore<TodoItems> by dataStore(
//        fileName = TEST_DATA_STORE_FILE_NAME,
//        serializer = TodoItemsSerializer,
//    )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dataStore: DataStore<TodoItems> = DataStoreFactory.create(
        serializer = TodoItemsSerializer,
        corruptionHandler = null,
        migrations = emptyList(),
        scope = TestScope(StandardTestDispatcher())
    ) {
        File(testContext.filesDir, "datastore/$TEST_DATA_STORE_FILE_NAME")
}

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