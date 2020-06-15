package com.tomerpacific.todo


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class TodoListAdapter : BaseAdapter {

    private var data: List<String>
    private var inflater: LayoutInflater

    constructor(context: Context) {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val sharedPref = context.getSharedPreferences("todo_list_pref", Context.MODE_PRIVATE)
        val saved = sharedPref.getStringSet("todo", listOf("").toSet())

        data = when(saved) {
            null -> listOf("")
            else -> saved.toList()
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        val rootView = inflater.inflate(R.layout.todo_list_item, container, false)
        val todoName : TextView = rootView.findViewById(R.id.todo_item_name)

        val item = getItem(position) as String
        todoName.text = item

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



}