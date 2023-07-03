package com.example.pillwatch.network

import com.example.pillwatch.utils.InteractionTestProperty
import com.example.pillwatch.utils.MedsDataShaProperty
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://pillwatch-sv-06da76c7968b.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("getDataset")
    suspend fun getDataset():
            MedsDataShaProperty

    @GET("getInteractionList")
    suspend fun getInteractionData(
        @Query("stringParam") rxCui: String,
        @Query("listParam") rxCuiList: List<String>
    ): InteractionTestProperty

}

object AppApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}