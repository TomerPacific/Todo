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

class TodoListAdapter(context: Context,
                      clearButtonCallback : (status: Boolean) -> Unit,
                      private var todoItems: List<TodoData>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val clearButtonCB : ((status: Boolean) -> Unit) = clearButtonCallback

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


                    val mutableTodoList : MutableList<TodoData> = todoItems.toMutableList()
                    with(mutableTodoList.iterator()) {
                        forEach {
                            if (it.todoItem == todoToDelete) {
                                remove()
                                return@forEach
                            }
                        }
                    }

                    todoItems = mutableTodoList.toList()
                    notifyDataSetChanged()

                    clearButtonCB(todoItems.isNotEmpty())
                }
            }
        }

        return rootView
    }

    override fun getItem(position: Int): Any {
        return todoItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return todoItems.size
    }

    fun submitList(data: List<TodoData>) {
        todoItems = data
        notifyDataSetChanged()
    }

}