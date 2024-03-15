package com.tomerpacific.todo

import androidx.test.core.app.ApplicationProvider
import com.tomerpacific.todo.view.MainViewModel
import com.tomerpacific.todo.view.TodoState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var state: StateFlow<TodoState>

    @Before
    fun setup() {
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
        state = viewModel.state
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialStateTest() = runTest {
        assert(state.value.todoItems.isEmpty())
    }
}