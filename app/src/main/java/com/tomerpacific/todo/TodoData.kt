package com.tomerpacific.todo

import com.google.gson.annotations.SerializedName

class TodoData {
    fun TodoData() {}

    @SerializedName("todo_list")
    var data : List<String> = listOf()

}