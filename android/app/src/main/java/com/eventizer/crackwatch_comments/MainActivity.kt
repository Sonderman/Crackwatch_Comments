package com.eventizer.crackwatch_comments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var onDownloadComplete: BroadcastReceiver? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.webViewClient = object :WebViewClient(){

            override fun doUpdateVisitedHistory(view: WebView?, url: String, isReload: Boolean) {
                Log.d("Url:",url)
                if(!url.startsWith("https://crackwatch.com")&&!url.startsWith("https://b2.crackwatch.com/file/")){
                    //myWebView.loadUrl("https://crackwatch.com/best-comments?period=day")
                    myWebView.stopLoading()
                    myWebView.goBack()
                }

                super.doUpdateVisitedHistory(view, url, isReload)
            }

            fun getFileName(url: String): String {
                return if(url.contains(".jpg")){
                    System.currentTimeMillis()
                        .toString() + ".jpg"

                }else{
                    System.currentTimeMillis()
                        .toString() + ".gif"
                }
            }





            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {


                if (url!!.contains(".jpg") || url.contains(".gif")) {
                    val permissions = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if(ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            permissions,
                            112
                        )

                    }else{
                        showDialog(url)
                        return true
                    }

                }
                return true
            }

            private fun showDialog(url:String) {
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.dialog)
                dialog.setTitle("Choose Action")
                val share =
                    dialog.findViewById<Button>(R.id.dialogShare)
                val download =
                    dialog.findViewById<Button>(R.id.dialogDownload)

                download.setOnClickListener {
                    dialog.dismiss()
                    downloadImage(url,false)
                }
                share.setOnClickListener {
                    dialog.dismiss()
                    downloadImage(url,true)

                }

                dialog.show()
            }

            private fun downloadImage(url:String,share:Boolean) {
                Toast.makeText(this@MainActivity,"Download Started Please Wait!", Toast.LENGTH_SHORT).show()
                val request = DownloadManager.Request(
                    Uri.parse(url)
                )

                request.setDescription("Downloading ...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileName(url))
                }else{
                    request.setDestinationInExternalPublicDir("Crackwatch_Comments_Downloads", getFileName(url))
                }

                val downloadManager =
                    this@MainActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadID = downloadManager.enqueue(request)

                onDownloadComplete = object : BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: Intent
                    ) {
                        //Fetching the download id received with the broadcast
                        val id =
                            intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        //Checking if the received broadcast is for our enqueued download by matching download id
                        if (downloadID == id) {

                            if(share){
                                val shareIntent = Intent()
                                shareIntent.action=Intent.ACTION_SEND
                                shareIntent.putExtra(Intent.EXTRA_TEXT,"")
                                shareIntent.putExtra(Intent.EXTRA_STREAM,downloadManager.getUriForDownloadedFile(id))
                                shareIntent.type = "image/*"
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                startActivity(Intent.createChooser(shareIntent,"Share to: "))
                            }else{
                                Toast.makeText(
                                    this@MainActivity,
                                    "Download Completed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                        }

                    }
                }
                registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            }

        }

        myWebView.settings.javaScriptEnabled=true
        myWebView.settings.allowContentAccess=true
        myWebView.settings.domStorageEnabled=true
        myWebView.settings.useWideViewPort=true
        myWebView.settings.setAppCacheEnabled(true)
        myWebView.loadUrl("https://crackwatch.com/best-comments?period=day")

    }

    override fun onPause() {
        if(onDownloadComplete!= null){
            unregisterReceiver(onDownloadComplete)
        }
        super.onPause()
    }
    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        }else {
            super.onBackPressed()
        }
    }
}