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
    private val retrofitService1 = RetrofitClient.getClient("172.20.10.9:8080/")

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
    fun sendCheck(name: String) {
        // Tạo đối tượng RequestBody bằng cách sử dụng RequestBody.create

        // Gọi phương thức sendMessage
        val call = retrofitService1.sendMessage(name)

        // Thực hiện yêu cầu bất đồng bộ
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Gửi dữ liệu thành công!")
                } else {
                    println("Gửi dữ liệu không thành công. Mã lỗi: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Lỗi: ${t.message}")
            }
        })
    }
}