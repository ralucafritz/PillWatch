package com.example.pillwatch.testmeds

import android.os.Build.VERSION_CODES.P
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.network.MedsDataShaProperty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestMedsViewModel : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    init {
        getMedsData()
    }

    private fun getMedsData() {
        AppApi.retrofitService.getDataset().enqueue(object: Callback<MedsDataShaProperty> {
            override fun onResponse(call: Call<MedsDataShaProperty>, response: Response<MedsDataShaProperty>) {
               _response.value = "Success: ${response.body()?.file}"
            }

            override fun onFailure(call: Call<MedsDataShaProperty>, t: Throwable) {
                _response.value = "Failure: " + t.message
            }
        })
    }
}