package com.raika.apiservicemodule.data

import android.net.Uri
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException

suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
    try {
        val response = call()
        Log.d("raika retrofit method", " \n" + response.raw().request().method() + " \n ")
        Log.d("raika retrofit url", " \n" + response.raw().request().url().toString() + " \n ")
        Log.i(
            "raika retrofit request",
            " \n" + requestBodyToString(response.raw().request().body())
        )
        Log.w(
            "raika retrofit response",
            " \n" + GsonBuilder().setPrettyPrinting().create().toJson(response.body()) + " \n "
        )
        return if (response.body() != null && response.isSuccessful) {
            val body = response.body()
            Resource.success(body)
        } else {
            error(response, "${response.code()} ${response.message()}")
        }
    } catch (e: Exception) {
        return when (e) {
            is UnknownHostException -> {
                Log.e("raika retrofit error", " \n" + "UnknownHostException")
                error(null, "اتصال اینترنت خود را بررسی کنید")
            }
            is CancellationException -> {
                Log.e("raika retrofit error", " \n" + "CancellationException")
                error(null, "عملیات متوقف شد")
            }
            is SocketTimeoutException -> {
                Log.e("raika retrofit error", " \n" + "SocketTimeoutException")
                error(null, "مهلت زمانی به اتمام رسید")
            }
            else -> {
                Log.e("raika retrofit error", " \n" + "IOException")
                error(null, e.message.toString())
            }
        }
    } catch (e: JsonSyntaxException) {
        return error(null, "${e.message}")
    } catch (e: IllegalStateException) {
        return error(null, "${e.message}")
    }

}

private fun <T> error(response: Response<T>?, message: String): Resource<T> {
    var newMessage = message
    response?.let {
        Log.d("raika retrofit method", " \n" + response.raw().request().method() + " \n ")
        Log.d("raika retrofit url", " \n" + response.raw().request().url().toString() + " \n ")
        Log.i(
            "raika retrofit request",
            " \n" + requestBodyToString(response.raw().request().body())
        )
        Log.w(
            "raika retrofit response",
            " \n" + GsonBuilder().setPrettyPrinting().create().toJson(response.body()) + " \n "
        )
        when (response.code()) {
            401 -> newMessage = " \n401 Unauthorised \n "
            403 -> newMessage = " \n401 Forbidden \n "
            404 -> newMessage = " \n404 Not found \n "
            429 -> newMessage = " \n429 Too Many Requests \n "
        }
    }

    Log.e("raika retrofit error", " \n$newMessage \n ")
    return Resource.error(message)
}


private fun requestBodyToString(requestBody: RequestBody?): String {
    return if (requestBody != null) {
        try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bufferArray = buffer.readUtf8().split("&").toTypedArray()
            val request = StringBuilder()
            for (s in bufferArray) {
                request.append(s.replace("=".toRegex(), " : ")).append("\n")
            }
            Uri.decode(request.toString())
        } catch (e: IOException) {
            GsonBuilder().setPrettyPrinting().create().toJson(requestBody)
        }
    } else {
        "no request body\n"
    }
}
