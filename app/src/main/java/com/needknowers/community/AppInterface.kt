package com.needknowers.community

import com.needknowers.community.model.AppDirection
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AppInterface {
    @GET("/maps/api/directions/json")
    fun requestDirection(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("mode") mode: String,
            @Query("key") key: String
    ): Call<AppDirection>
}