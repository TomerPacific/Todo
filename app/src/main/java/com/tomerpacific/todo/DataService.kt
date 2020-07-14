package com.tomerpacific.todo

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {
    @GET("getTodoData?")
    fun getData(@Query("username") username: String) : Call<JSONObject>
}