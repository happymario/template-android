package com.victoria.bleled.app.etc

import android.net.http.SslError
import android.os.Bundle
import android.view.MenuItem
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.victoria.bleled.R
import com.victoria.bleled.common.Constants

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val title = intent.getStringExtra(Constants.ARG_TYPE)
        val url = intent.getStringExtra(Constants.ARG_DATA)

        if (title != null) {
            setupToolbar(title)
        } else {
            setupToolbar("WebView")
        }
        initWebView(findViewById(R.id.webview), url)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupToolbar(title: String) {
        setSupportActionBar(findViewById(R.id.toolbar))

        val ab: ActionBar? = supportActionBar
        ab?.title = title
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initWebView(webview: WebView, url: String?) {
        webview.settings.builtInZoomControls = true //줌버튼 보이기
        webview.webViewClient =
            WebClient() // 응용프로그램에서 직접 url 처리 주소창 없애기
        val set = webview.settings
        set.loadWithOverviewMode = true //웹뷰에 딱 맞게 크기 조절
        set.useWideViewPort = true //웹뷰에 딱 맞게 크기 조절
        set.javaScriptEnabled = true //javascript 허용 여부
        set.setSupportZoom(true) // 확대,축소 기능을 사용할 수 있도록 설정
        set.builtInZoomControls = true //줌기능 사용 여부
        set.displayZoomControls = true //줌버튼 보여주기 여부
        set.javaScriptCanOpenWindowsAutomatically = true // javascript가 window.open()을 사용할 수 있도록 설정
        set.setSupportMultipleWindows(false) // 여러개의 윈도우를 사용할 수 있도록 설정
        set.loadsImagesAutomatically = true // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정
        set.useWideViewPort = true // wide viewport를 사용하도록 설정
        set.cacheMode = WebSettings.LOAD_NO_CACHE // 웹뷰가 캐시를 사용하지 않도록 설정

        if (url != null) {
            webview.loadUrl(url)
        }
    }

    /************************************************************
     * Sub Classes
     */
    internal class WebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            AlertDialog.Builder(view.context)
                .setTitle(R.string.app_name)
                .setMessage(R.string.notification_error_ssl_cert_invalid)
                .setPositiveButton(
                    R.string.continue_to
                ) { dialog, which -> handler.proceed() }
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, which -> }
                .show()
        }
    }
}