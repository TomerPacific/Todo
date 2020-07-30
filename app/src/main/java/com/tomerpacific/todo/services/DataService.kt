package com.tomerpacific.todo.services

import com.tomerpacific.todo.Data.TodoData
import com.tomerpacific.todo.Data.TodoDataSetResult
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @GET("getTodoDataInSession")
    fun getData(@Query("username") username: String?) : Call<TodoData>

    @GET("setTodoData")
    fun setData(@Query("username") username: String?, @Query("data") data: List<String>) : Call<TodoDataSetResult>

    @GET("removeAllTodoData")
    fun removeAllData(@Query("username") username: String?) : Call<TodoDataSetResult>

}