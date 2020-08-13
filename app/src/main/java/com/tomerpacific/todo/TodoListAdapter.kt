package com.tomerpacific.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.tomerpacific.todo.services.TodoDataSharedPreferencesService


class TodoListAdapter : BaseAdapter {

    private var data: MutableList<String> = mutableListOf()
    private var inflater: LayoutInflater
    private var clearButtonCB : ((status: Boolean) -> Unit)? = null

    constructor(context: Context, clearButtonCallback : (status: Boolean) -> Unit) {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        clearButtonCB = clearButtonCallback
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        val rootView = inflater.inflate(R.layout.todo_list_item, container, false)
        val todoName : TextView = rootView.findViewById(R.id.todo_item_name)
        val todoCheckbox: CheckBox = rootView.findViewById(R.id.todo_item_done_checkbox)

        val item = getItem(position) as String
        todoName.text = item

        todoCheckbox.setOnCheckedChangeListener {view, isChanged ->
            if (isChanged) {
                val parentView = view.parent as ViewGroup
                val todoToDelete = parentView.getChildAt(0) as TextView
                data.remove(todoToDelete.text.toString())
                TodoDataSharedPreferencesService.instance.removeTodo(todoToDelete.text.toString())
                notifyDataSetChanged()

                clearButtonCB?.invoke(data.size != 0)
            }
        }

        return rootView
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    fun removeAllTodos() {
        data.clear()
        clearButtonCB?.invoke(false)
        notifyDataSetChanged()
    }

    fun setTodoData(dbData: List<String>) {
        data = dbData.toMutableList()
        clearButtonCB?.invoke(data.count() != 0)
        notifyDataSetChanged()
    }

    fun getTodoData() : List<String> {
        return data
    }

    fun addTodoItem(todoItem : String) {
        data.add(todoItem)

        if (data.size == 1) {
            clearButtonCB?.invoke(true)
        }

        notifyDataSetChanged()
    }

}