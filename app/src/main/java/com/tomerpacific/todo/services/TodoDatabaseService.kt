package com.tomerpacific.todo.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tomerpacific.todo.data.TodoData
import com.tomerpacific.todo.data.TodoDataSetResult
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

    fun updateTodoDataInDB(context: Context, todoData : List<String>) {

        val retrofit = Retrofit.Builder()
            .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.setData(user?.displayName, todoData.toTypedArray())

        call.enqueue(object: Callback<TodoDataSetResult> {
            override fun onResponse(
                call: Call<TodoDataSetResult>,
                response: Response<TodoDataSetResult>
            ) {
                if(!response.isSuccessful) {
                    Toast.makeText(context, "There was a problem saving the Todo data in the server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoDataSetResult>, t: Throwable) {
                Toast.makeText(context, "There was a problem saving the Todo data in the server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun removeAllTodos(context: Context) {
        val retrofit = Retrofit.Builder()
            .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.removeAllData(user?.displayName)

        call.enqueue(object: Callback<TodoDataSetResult> {
            override fun onResponse(
                call: Call<TodoDataSetResult>,
                response: Response<TodoDataSetResult>
            ) {
                if(!response.isSuccessful) {
                    Toast.makeText(context, "There was a problem saving the Todo data in the server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoDataSetResult>, t: Throwable) {
                Toast.makeText(context, "There was a problem saving the Todo data in the server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}