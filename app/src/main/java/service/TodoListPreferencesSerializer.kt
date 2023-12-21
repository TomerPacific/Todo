package service

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.tomerpacific.todo.TodoListPreferences
import java.io.InputStream
import java.io.OutputStream

object TodoListPreferencesSerializer: Serializer<TodoListPreferences> {


            override val defaultValue: TodoListPreferences = TodoListPreferences.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): TodoListPreferences {
            try {
                return TodoListPreferences.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(
            t: TodoListPreferences,
            output: OutputStream
        ) = t.writeTo(output)
    }

    val Context.todoListPreferencesDatastore: DataStore<TodoListPreferences> by dataStore(
        fileName = "todoItem.proto",
        serializer = TodoListPreferencesSerializer
    )