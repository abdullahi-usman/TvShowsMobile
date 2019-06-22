package com.dahham.downloadmanager.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.dahham.downloadmanager.DownloadManager

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    lateinit var downloadManager : DownloadManager

    fun init(context: Context){
        downloadManager = DownloadManager.newInstance(context)
    }


    fun download(url: String){
        downloadManager.enqueue(url)
    }

    fun clearDatabase(){
        downloadManager.clearDownloads()
    }
}
