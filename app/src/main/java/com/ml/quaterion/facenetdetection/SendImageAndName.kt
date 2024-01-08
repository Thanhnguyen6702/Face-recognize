package com.ml.quaterion.facenetdetection

import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class SendImageAndName() {
    private val retrofitService = RetrofitClient.getClient("http://thanhhust.x10.mx/Iot/")
    fun send(bitmap: Bitmap, name: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray)
        val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)

        val call: Call<ResponseBody> = retrofitService.uploadImage(nameRequestBody, filePart)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() // Lấy nội dung từ phản hồi
                    if (responseBody != null) {
                        Log.d("SendImageAndName", responseBody)
                    }
                } else {
                    // Xử lý lỗi từ máy chủ
                    Log.e("SendImageAndName", "Lỗi khi tải ảnh lên: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                // Xử lý lỗi mạng hoặc lỗi từ Retrofit
                Log.e("SendImageAndName", "Lỗi khi tải ảnh lên: ${t.message}")
            }
        })
    }
}