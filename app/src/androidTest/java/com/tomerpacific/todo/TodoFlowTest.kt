package com.tomerpacific.todo

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
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
    fun myTest() {

        composeTestRule.setContent {
            val state by mainViewModel.state.collectAsState()
            TodoScreen(
                state = state,
                mainViewModel::onEvent)
        }

        composeTestRule.onNodeWithContentDescription("Floating action button.").assertExists()
        composeTestRule.onNodeWithContentDescription("Floating action button.").performClick()


    }

}