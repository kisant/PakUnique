package com.unique.pak

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST


class FragmentCasino : Fragment(R.layout.fragment_casino) {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("CoroutineExceptionHandler got $exception in $coroutineContext")
    }

    private var scope = CoroutineScope(
        Job() + Dispatchers.IO
    )

    private lateinit var wvCasino : WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(parent = view)
        postCasino()
    }

    private fun findViews(parent: View) {
        wvCasino = parent.findViewById(R.id.wv_casino)
        wvCasino.webViewClient = WebViewClient()
        wvSetting()
        wvUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun wvSetting() {
        wvCasino.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            domStorageEnabled = true
        }
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wvCasino, true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun wvUrl() {
        if (FCM_WV_HARDURL != "") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FCM_WV_HARDURL))
            FCM_WV_HARDURL = ""
            startActivity(intent)
        }
        if (FCM_WV_URL != "") {
            wvCasino.loadUrl(FCM_WV_URL)
            wvCasino.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP && FCM_WV_URL != "") {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(FCM_WV_URL)
                    )
                    FCM_WV_URL = ""
                    wvCasino.loadUrl(DEFAULT_WV_URL)
                    startActivity(intent)
                }
                return@setOnTouchListener false
            }
        } else {
            wvCasino.loadUrl(DEFAULT_WV_URL)
        }
    }

    private fun getFCMData() {
        for (key in FCM_DATA.keySet()) {
            when (key) {
                "game" -> {

                }
                "hardurl" -> {
                    val value = FCM_DATA.get(key) as String
                    FCM_WV_HARDURL = value
                }
                "url" -> {
                    val value = FCM_DATA.get(key) as String
                    FCM_WV_URL = value
                }
            }
        }
    }

    private fun postCasino() {
        if (TOKEN == null) {
            TOKEN = getDeviceToken()
        }
        val data = Casino(
            "com.unique.pak",
            "en",
            TOKEN,
            AppsFlyerLib.getInstance().getAppsFlyerUID(context),
            "android"
        )
        scope.launch(exceptionHandler) {
            RetrofitModule.casinoApi.postLoguser(data)
        }
    }

    private fun postCasionPushClick() {
        val data = CasinoPushClick(
            "com.unique.pak",
            AppsFlyerLib.getInstance().getAppsFlyerUID(context),
            "android"
        )
        scope.launch(exceptionHandler) {
            RetrofitModule.casinoApi.postPushClick(data)
        }
    }

    @Serializable
    private data class Casino(
        val appBundle: String,
        val locale: String?,
        val deviceToken: String?,
        val afId: String?,
        val os: String?
    )

    @Serializable
    private data class CasinoPushClick(
        val appBundle: String,
        val afId: String,
        val os: String
    )

    private interface CasinoApi {
        @POST("loguser/")
        suspend fun postLoguser(@Body data: Casino)
        @POST("logPushClick/")
        suspend fun postPushClick(@Body data: CasinoPushClick)
    }

    private class CasinoApiHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url

            val request = originalRequest.newBuilder()
                .url(originalHttpUrl)
                .build()

            return chain.proceed(request)
        }
    }

    private object RetrofitModule {
        private val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(CasinoApiHeaderInterceptor())
            .build()

        private val json = Json {
            ignoreUnknownKeys = true
        }

        @Suppress("EXPERIMENTAL_API_USAGE")
        private val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val casinoApi: CasinoApi = retrofit.create(CasinoApi::class.java)
    }

    private fun getDeviceToken(): String? {
        var token: String? = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            token = task.result

            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
        })
        return token
    }

    companion object {
        private const val TAG = "Firebase"
        private const val BASE_URL = "https://pamyatki.com/"
        private const val DEFAULT_WV_URL = "http://hhalcajs.ru/qWhjp1?extra_param_1=10500"
        private var FCM_WV_HARDURL = ""
        private var FCM_WV_URL = ""
        var TOKEN: String? = null
        var FCM_DATA = bundleOf()

        fun create() = FragmentCasino()

        fun post() = FragmentCasino().postCasionPushClick()

        fun fcmData() = FragmentCasino().getFCMData()
    }
}