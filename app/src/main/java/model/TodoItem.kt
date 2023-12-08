package model

import java.util.UUID

data class TodoItem(
    val itemId: String = generateItemId(),
    var itemDescription: String
) {
    companion object {

        private fun generateItemId(): String {
            return UUID.randomUUID().toString()
        }
    }
}
