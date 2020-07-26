package com.tomerpacific.todo

import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @GET("getTodoData")
    fun getData(@Query("username") username: String?) : Call<TodoData>

    @Headers("Content-type: application/json")
    @GET("setTodoData")
    fun setData(@Query("username") username: String?, @Query("data") data: List<String>) : Call<TodoDataSetResult>

    @GET("removeAllTodoData")
    fun removeAllData(@Query("username") username: String?) : Call<TodoDataSetResult>

}