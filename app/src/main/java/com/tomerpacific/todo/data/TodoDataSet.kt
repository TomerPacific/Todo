package com.tomerpacific.todo.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TodoDataSet(_username: String?, _data : List<String>) {

    @SerializedName("username")
    @Expose
    var username: String? = _username
    @SerializedName("data")
    @Expose
    var data: List<String>? = _data

}