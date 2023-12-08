package service

import androidx.datastore.core.DataStore
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class TodoItemsRepository(private val todoItemsDataStore: DataStore<TodoItems>) {

    val todoItemsFlow = todoItemsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(TodoItems.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun fetchCachedTodoItems() = todoItemsFlow.first()

    suspend fun updateTodoItems(todoItems: List<TodoItem>) {
        todoItemsDataStore.updateData {
            it.toBuilder().clearItems().addAllItems(todoItems).build()
        }
    }

}