package service

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.tomerpacific.todo.TodoItems
import java.io.InputStream
import java.io.OutputStream

object TodoItemsSerializer: Serializer<TodoItems> {

    override val defaultValue: TodoItems = TodoItems.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TodoItems {
        try {
            return TodoItems.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: TodoItems,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.todoItemDatastore: DataStore<TodoItems> by dataStore(
    fileName = "todoItem.proto",
    serializer = TodoItemsSerializer
    )