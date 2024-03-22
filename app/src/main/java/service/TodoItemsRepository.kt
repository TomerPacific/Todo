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

    private var _todoItemsAdded = mutableListOf<TodoItem>()

    init {
        _todoItemsAdded = TodoItems.getDefaultInstance().itemsList.toMutableList()
    }

    suspend fun updateTodoItems(todoItem: TodoItem) {
        todoItemsDataStore.updateData { items ->
            items.toBuilder().addItems(todoItem).build()
        }
        _todoItemsAdded.add(todoItem)
    }

    suspend fun removeTodoItem(todoItem: TodoItem) {
        todoItemsDataStore.updateData { items ->
            val index = items.itemsList.indexOf(todoItem)
            items.toBuilder().removeItems(index).build()
        }
        _todoItemsAdded.remove(todoItem)
    }

    suspend fun removeAllTodoItems() {
        todoItemsDataStore.updateData { items ->
            items.toBuilder().clearItems().build()
        }
        _todoItemsAdded.clear()
    }

    fun doesTodoAlreadyExist(todoItemDescription: String): Boolean {
        return _todoItemsAdded.any { todoItem ->
            todoItem.itemDescription == todoItemDescription
        }
    }

}