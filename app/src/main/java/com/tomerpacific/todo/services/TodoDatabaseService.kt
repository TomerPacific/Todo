package com.tomerpacific.todo.services

import android.content.Context
import kotlinx.coroutines.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomerpacific.todo.models.TodoDataFromBackend
import com.tomerpacific.todo.models.TodoDataSetResult
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.models.TodoData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object TodoDatabaseService {

    fun fetchTodoDataFromDB(success : (data: List<TodoData>) -> Unit, failure: (error:String) -> Unit) {
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
                    val call = service.getData(token, getUserUUID())

                    call.enqueue(object: Callback<TodoDataFromBackend> {
                        override fun onResponse(call: Call<TodoDataFromBackend>, response: Response<TodoDataFromBackend>) {
                            if (response.isSuccessful) {
                                val body = response.body() as TodoDataFromBackend

                                if (body.data.isNullOrEmpty()) {
                                    success.invoke(listOf())
                                    return
                                }

                                val listType = object : TypeToken<List<TodoData>>() {}.type
                                success.invoke(Gson().fromJson<List<TodoData>>(body.data, listType))
                            }
                        }

                        override fun onFailure(call: Call<TodoDataFromBackend>, t: Throwable) {
                            failure.invoke("Failure when trying to fetch todo data")
                        }
                    })
                }
            }
        }
    }

    fun updateTodoDataInDB(context: Context, todoData : List<TodoData>) {

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
                    val call = service.setData(token, getUserUUID(),  Gson().toJson(todoData))

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

    fun removeAllTodos(context: Context) = runBlocking {

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val idToken = getUserIdToken(user)

            val retrofit = Retrofit.Builder()
                .baseUrl(TodoConstants.BASE_URL_FOR_REQUEST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(DataService::class.java)
            val call = service.removeAllData(idToken, getUserUUID())

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

    private suspend fun getUserIdToken(user: FirebaseUser) = coroutineScope {

        val job = async {
            user.getIdToken(false).result?.token
        }
        job.await()
    }

    private fun getUserUUID() : String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid as String
    }

}