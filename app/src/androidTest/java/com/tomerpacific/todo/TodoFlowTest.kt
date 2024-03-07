package com.tomerpacific.todo

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.tomerpacific.todo.view.MainViewModel
import com.tomerpacific.todo.view.TodoScreen
import org.junit.Rule
import org.junit.Test


private const val FAB_BUTTON_CONTENT_DESCRIPTION = "Add Todo Item"
private const val TODO_TEXT_FIELD_TEXT = "What do you want to do?"
private const val TODO_ADD_BUTTON_TEXT = "Add"
private const val TODO_DELETE_BUTTON_CONTENT_DESCRIPTION = "Delete Todo"
private const val TODO_TEST_ITEM_DESCRIPTION = "Something"

class TodoFlowTest {

    private val mainViewModel = MainViewModel(ApplicationProvider.getApplicationContext())


    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addTodoItemTest() {

        composeTestRule.setContent {
            val state by mainViewModel.state.collectAsState()
            TodoScreen(
                state = state,
                mainViewModel::onEvent)
        }

        composeTestRule.onNodeWithContentDescription(FAB_BUTTON_CONTENT_DESCRIPTION).assertExists()
        composeTestRule.onNodeWithContentDescription(FAB_BUTTON_CONTENT_DESCRIPTION).performClick()

        composeTestRule.onNodeWithText(TODO_TEXT_FIELD_TEXT).assertExists()
        composeTestRule.onNodeWithText(TODO_TEXT_FIELD_TEXT).performTextInput(TODO_TEST_ITEM_DESCRIPTION)

        composeTestRule.onNodeWithText(TODO_ADD_BUTTON_TEXT).assertExists()
        composeTestRule.onNodeWithText(TODO_ADD_BUTTON_TEXT).performClick()

        composeTestRule.onNodeWithText(TODO_TEST_ITEM_DESCRIPTION).assertExists()
    }

    @Test
    fun removeTodoItemTest() {
        composeTestRule.setContent {
            val state by mainViewModel.state.collectAsState()
            TodoScreen(
                state = state,
                mainViewModel::onEvent)
        }

        composeTestRule.onNodeWithText(TODO_TEST_ITEM_DESCRIPTION).assertExists()
        composeTestRule.onNodeWithContentDescription(TODO_DELETE_BUTTON_CONTENT_DESCRIPTION).assertExists()
        composeTestRule.onNodeWithContentDescription(TODO_DELETE_BUTTON_CONTENT_DESCRIPTION).performClick()
        composeTestRule.onNodeWithText(TODO_TEST_ITEM_DESCRIPTION).assertDoesNotExist()
    }

}