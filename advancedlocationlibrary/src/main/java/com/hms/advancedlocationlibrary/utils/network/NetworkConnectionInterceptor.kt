package com.hms.advancedlocationlibrary.utils.network

import android.content.Context
import android.net.ConnectivityManager
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException.Companion.NO_INTERNET_CONNECTION
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


internal class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw AdvancedLocationException(NO_INTERNET_CONNECTION)
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}