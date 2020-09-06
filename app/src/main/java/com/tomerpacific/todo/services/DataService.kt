package com.tomerpacific.todo.services

import com.tomerpacific.todo.data.TodoData
import com.tomerpacific.todo.data.TodoDataSetResult
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @GET("getTodoData")
    fun getData(@Query("uid") uid: String?) : Call<TodoData>

    @GET("setTodoData")
    fun setData(@Query("uid") uid: String?, @Query("data") data: Array<String>) : Call<TodoDataSetResult>

    @GET("removeAllTodoData")
    fun removeAllData(@Query("uid") uid: String?) : Call<TodoDataSetResult>

}