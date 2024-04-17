package service

import androidx.datastore.core.DataStore
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class TodoItemsRepository(private val todoItemsDataStore: DataStore<TodoItems>) {

    val todoItemsFlow: Flow<TodoItems> = todoItemsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(TodoItems.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateTodoItems(todoItem: TodoItem) {
        todoItemsDataStore.updateData { items ->
            items.toBuilder().addItems(todoItem).build()
        }
    }

    suspend fun removeTodoItem(todoItem: TodoItem) {
        todoItemsDataStore.updateData { items ->
            when (val index = items.itemsList.indexOf(todoItem)) {
                in 0 .. Int.MAX_VALUE -> items.toBuilder().removeItems(index).build()
                else -> items.toBuilder().build()
            }
        }
    }

    suspend fun removeAllTodoItems() {
        todoItemsDataStore.updateData { items ->
            items.toBuilder().clearItems().build()
        }
    }

}