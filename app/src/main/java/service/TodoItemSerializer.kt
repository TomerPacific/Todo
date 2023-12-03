package service

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.tomerpacific.todo.TodoItem
import java.io.InputStream
import java.io.OutputStream

object TodoItemSerializer: Serializer<TodoItem> {

    override val defaultValue: TodoItem = TodoItem.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TodoItem {
        try {
            return TodoItem.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: TodoItem,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.todoItemDatastore: DataStore<TodoItem> by dataStore(
    fileName = "todoItem.proto",
    serializer = TodoItemSerializer
    )