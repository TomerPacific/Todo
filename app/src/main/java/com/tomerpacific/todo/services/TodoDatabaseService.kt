package com.tomerpacific.todo.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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

    private object HOLDER {
        val INSTANCE = TodoDatabaseService()
    }

    companion object {
        val instance: TodoDatabaseService by lazy { HOLDER.INSTANCE }
    }

    fun fetchTodoDataFromDB(todoAdapter : TodoListAdapter) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.getIdToken(false).addOnCompleteListener{
                if (it.isSuccessful) {
                    val token = it.result?.token

                    val retrofit = Retrofit.Builder()
                        .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val service = retrofit.create(DataService::class.java)
                    val call = service.getData(token, user.uid)

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
            }
        }
    }

    fun updateTodoDataInDB(context: Context, todoData : List<String>) {

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.getIdToken(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.result?.token

                    val retrofit = Retrofit.Builder()
                        .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val service = retrofit.create(DataService::class.java)
                    val call = service.setData(token, user.uid, todoData.toTypedArray())

                    call.enqueue(object : Callback<TodoDataSetResult> {
                        override fun onResponse(
                            call: Call<TodoDataSetResult>,
                            response: Response<TodoDataSetResult>
                        ) {
                            if (!response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "There was a problem saving the Todo data in the server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<TodoDataSetResult>, t: Throwable) {
                            Toast.makeText(
                                context,
                                "There was a problem saving the Todo data in the server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }

    fun removeAllTodos(context: Context, idToken: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.removeAllData(idToken, getUserUUID())

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

    fun getUserIdToken(context: Context, callback :(Context, String) -> Unit) {

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken = it.result?.token as String
                    callback(context, idToken)
                }
            }
        }

    }

    private fun getUserUUID() : String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid as String
    }
}