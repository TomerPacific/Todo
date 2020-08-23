package com.tomerpacific.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class TodoListAdapter(context: Context, clearButtonCallback : (status: Boolean) -> Unit) : BaseAdapter() {

    private var data: MutableList<String> = mutableListOf()
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val clearButtonCB : ((status: Boolean) -> Unit) = clearButtonCallback



    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        val rootView = inflater.inflate(R.layout.todo_list_item, container, false)
        val item = getItem(position) as String

        rootView.findViewById<TextView>(R.id.todo_item_name).apply {
            text = item
        }

        rootView.findViewById<CheckBox>(R.id.todo_item_done_checkbox).apply {
            setOnCheckedChangeListener { view, isChanged ->
                if (isChanged) {
                    val parentView = view.parent as ViewGroup
                    val todoToDelete = parentView.getChildAt(0) as TextView
                    data.remove(todoToDelete.text.toString())
                    DataSavingManager.updateTodoData(view.context, data)
                    notifyDataSetChanged()

                    clearButtonCB(data.size != 0)
                }
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
        clearButtonCB(false)
        notifyDataSetChanged()
    }

    fun setTodoData(dbData: List<String>) {
        data = dbData.toMutableList()
        clearButtonCB(data.count() != 0)
        notifyDataSetChanged()
    }

    fun getTodoData() : List<String> {
        return data
    }

    fun addTodoItem(todoItem : String) {
        data.add(todoItem)

        if (data.size == 1) {
            clearButtonCB(true)
        }

        notifyDataSetChanged()
    }

}