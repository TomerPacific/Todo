package com.tomerpacific.todo.data

import com.google.gson.annotations.SerializedName

class TodoData {

    @SerializedName("todo_list")
    var data : List<String> = listOf()

}