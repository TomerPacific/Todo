package service

import androidx.datastore.core.DataStore
import com.tomerpacific.todo.TodoListPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException


class TodoListPreferencesRepository(private val todoListPreferencesDataStore: DataStore<TodoListPreferences>) {

    val todoListPreferencesFlow: Flow<TodoListPreferences> = todoListPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(TodoListPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun fetchCachedTodoListPreferences() = todoListPreferencesFlow.first()

    suspend fun updateTodoListTitle(todoListTitle: String) {
        todoListPreferencesDataStore.updateData { todoListPreferences ->
            todoListPreferences.toBuilder().setTitle(todoListTitle).build()
        }
    }

}