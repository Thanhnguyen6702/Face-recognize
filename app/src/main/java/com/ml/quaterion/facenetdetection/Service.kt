package com.ml.quaterion.facenetdetection

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface Service {
    @Multipart
    @POST("upload.php") // Đường dẫn API của bạn
    fun uploadImage(
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Service {
        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
        return retrofit!!.create(Service::class.java)
    }
}
