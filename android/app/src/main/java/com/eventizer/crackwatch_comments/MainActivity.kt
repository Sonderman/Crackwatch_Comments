package com.eventizer.crackwatch_comments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val myWebView: WebView = findViewById(R.id.webview)

        myWebView.webViewClient = object :WebViewClient(){

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                Log.d("Url:",url)
                if(!url?.startsWith("https://crackwatch.com/")!!&&!url?.startsWith("https://b2.crackwatch.com/file/")!!){
                    myWebView.loadUrl("https://crackwatch.com/best-comments?period=day")
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            fun getFileNameJpg(url: String?): String? {
                var filenameWithoutExtension = ""
                filenameWithoutExtension = System.currentTimeMillis()
                    .toString() + ".jpg"
                return filenameWithoutExtension
            }

            fun getFileNameGif(url: String?): String? {
                var filenameWithoutExtension = ""
                filenameWithoutExtension = System.currentTimeMillis()
                    .toString() + ".gif"
                return filenameWithoutExtension
            }



            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if (url!!.contains(".jpg") || url.contains(".gif")) {
                    val PERMISSIONS = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if(ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            PERMISSIONS,
                            112
                        )

                    }else{
                        Toast.makeText(this@MainActivity,"Download Started",Toast.LENGTH_SHORT).show();
                        val request = DownloadManager.Request(
                            Uri.parse(url)
                        )
                        val destinationFile = File(
                            Environment.getExternalStorageDirectory(),
                            if (url!!.contains(".jpg")){
                                getFileNameJpg(url)
                            }else{
                                getFileNameGif(url)
                            }

                        )
                        request.setDescription("Downloading ...")
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationUri(Uri.fromFile(destinationFile))
                        val downloadManager =
                            this@MainActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val downloadID = downloadManager.enqueue(request)
                        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(
                                context: Context,
                                intent: Intent
                            ) {
                                //Fetching the download id received with the broadcast
                                val id =
                                    intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                //Checking if the received broadcast is for our enqueued download by matching download id
                                if (downloadID == id) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Download Completed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }
                        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        return true
                    }




                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }

        myWebView.settings.javaScriptEnabled=true
        myWebView.settings.allowContentAccess=true
        myWebView.settings.domStorageEnabled=true
        myWebView.settings.useWideViewPort=true
        myWebView.settings.setAppCacheEnabled(true)
        myWebView.loadUrl("https://crackwatch.com/best-comments?period=day")

    }

    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        }else {
            super.onBackPressed()
        }
    }
}