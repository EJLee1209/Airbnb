package com.dldmswo1209.airbnb

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/cf5878e5-19b6-4221-b374-16cbb8f2bb42")
    fun getHouseList(): Call<HouseDto>
}