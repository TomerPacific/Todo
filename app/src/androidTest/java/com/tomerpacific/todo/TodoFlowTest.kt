package com.tomerpacific.todo

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.tomerpacific.todo.view.MainViewModel
import com.tomerpacific.todo.view.TodoScreen
import org.junit.Rule
import org.junit.Test

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

        composeTestRule.onNodeWithContentDescription("Add Todo Item").assertExists()
        composeTestRule.onNodeWithContentDescription("Add Todo Item").performClick()

        composeTestRule.onNodeWithText("What do you want to do?").assertExists()
        composeTestRule.onNodeWithText("What do you want to do?").performTextInput("Something")

        composeTestRule.onNodeWithText("Add").assertExists()
        composeTestRule.onNodeWithText("Add").performClick()

        composeTestRule.onNodeWithText("Something").assertExists()
    }

    @Test
    fun removeTodoItemTest() {
        composeTestRule.setContent {
            val state by mainViewModel.state.collectAsState()
            TodoScreen(
                state = state,
                mainViewModel::onEvent)
        }

        composeTestRule.onNodeWithText("Something").assertExists()
        composeTestRule.onNodeWithContentDescription("Delete Todo").assertExists()
        composeTestRule.onNodeWithContentDescription("Delete Todo").performClick()
        composeTestRule.onNodeWithText("Something").assertDoesNotExist()
    }

}