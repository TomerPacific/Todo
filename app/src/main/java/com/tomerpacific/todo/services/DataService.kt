package com.tomerpacific.todo.services

import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.models.TodoDataFromBackend
import com.tomerpacific.todo.models.TodoDataSetResult
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @GET("getTodoData")
    fun getData(@Header("AuthToken") token: String?, @Query("uid") uid: String?) : Call<TodoDataFromBackend>

    @GET("setTodoData")
    fun setData(@Header("AuthToken") token: String?, @Query("uid") uid: String?, @Query("data") data: String) : Call<TodoDataSetResult>

    @GET("removeAllTodoData")
    fun removeAllData(@Header("AuthToken") token: String?, @Query("uid") uid: String?) : Call<TodoDataSetResult>

}