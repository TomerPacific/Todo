package com.tomerpacific.todo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.tomerpacific.todo.R
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.viewmodels.MainActivityViewModel

class TodoListAdapter(context: Context,
                      clearButtonCallback : (status: Boolean) -> Unit,
                      private var mainActivityViewModel: MainActivityViewModel) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val clearButtonCB : ((status: Boolean) -> Unit) = clearButtonCallback
    private var todoItemsList : List<TodoData> = listOf()

    init {
        mainActivityViewModel.getTodoData().value?.let {
            todoItemsList = it
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        val rootView = inflater.inflate(R.layout.todo_list_item, container, false)
        val item = getItem(position) as TodoData

        rootView.findViewById<TextView>(R.id.todo_item_name).apply {
            text = item.todoItem
        }

        rootView.findViewById<CheckBox>(R.id.todo_item_done_checkbox).apply {
            setOnCheckedChangeListener { view, isChanged ->
                if (isChanged) {
                    val parentView = view.parent as ViewGroup
                    val todoToDeleteTextView = parentView.getChildAt(0) as TextView
                    val todoToDelete : String = todoToDeleteTextView.text.toString()

                    val mutableTodoList : MutableList<TodoData> = todoItemsList.toMutableList()
                    with(mutableTodoList.iterator()) {
                        forEach {
                            if (it.todoItem == todoToDelete) {
                                remove()
                                return@forEach
                            }
                        }
                    }
                    mainActivityViewModel.removeTodoItem(context, TodoData(todoToDelete))
                    todoItemsList = mutableTodoList.toList()
                    notifyDataSetChanged()

                    clearButtonCB(todoItemsList.isNotEmpty())
                }
            }
        }

        return rootView
    }

    override fun getItem(position: Int): Any {
        return todoItemsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return todoItemsList.size
    }

    fun submitList(data: List<TodoData>) {
        todoItemsList = data
        notifyDataSetChanged()
    }

}