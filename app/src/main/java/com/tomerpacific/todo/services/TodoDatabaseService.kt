package com.tomerpacific.todo.services

import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tomerpacific.todo.Data.TodoData
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.TodoListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TodoDatabaseService private constructor() {

    private var user : FirebaseUser? = null

    private object HOLDER {
        val INSTANCE = TodoDatabaseService()
    }

    companion object {
        val instance: TodoDatabaseService by lazy { HOLDER.INSTANCE }
    }

    fun fetchTodoDataFromDB(todoAdapter : TodoListAdapter) {
        user = FirebaseAuth.getInstance().currentUser

        val retrofit = Retrofit.Builder()
            .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.getData(user?.displayName)

        call.enqueue(object: Callback<TodoData> {
            override fun onResponse(call: Call<TodoData>, response: Response<TodoData>) {
                if (response.isSuccessful) {
                    val body = response.body() as TodoData
                    todoAdapter.setTodoData(body.data)
                }
            }

            override fun onFailure(call: Call<TodoData>, t: Throwable) {

            }
        })
    }

    fun removeAllTodos() {

    }
}