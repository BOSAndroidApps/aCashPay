package com.bos.payment.appName.network


import okhttp3.Interceptor
import okhttp3.Response


class RetryOn204Interceptor(
    private val maxRetry: Int = 2
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response = chain.proceed(request)

        var tryCount = 0

        while (response.code == 204 && tryCount < maxRetry) {
            response.close() // important
            tryCount++
            response = chain.proceed(request)
        }

        return response
    }
}