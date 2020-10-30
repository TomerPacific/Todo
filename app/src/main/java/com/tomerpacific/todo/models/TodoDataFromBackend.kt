package com.tomerpacific.todo.models

import com.google.gson.annotations.SerializedName

class TodoDataFromBackend {

    @SerializedName("todo_list")
    var data : List<String> = listOf()

}