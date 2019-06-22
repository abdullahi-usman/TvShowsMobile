package com.dahham.downloadmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.room.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by dahham on 6/19/18.
 * This file is part of DownloadManager licensed under GNU Public License
 *
 */
class DownloadManager private constructor(context: Context) {

    var all_downloads = ArrayList<Download>()
    var notificationManager: NotificationManager;
    var preferenceManager: SharedPreferences;
    private var database: Database

    val ALL_DOWNLOADS = 8

    init {

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        database = Room.databaseBuilder(context, DatabaseBuilder::class.java, "Downloads.db").allowMainThreadQueries().build().DownloadStore()
        Thread(Runnable { all_downloads.addAll(database.query()) }).start()

    }
    companion object {
        fun newInstance(context: Context) = DownloadManager(context);
    }

    fun setDownloadLocation(location: String){
        preferenceManager.edit().putString("__dahham__download_location", location).apply()
    }

    fun enqueue(url: String, display_name: String? = null, summary: String? = null): Download{
        val storageLocation = preferenceManager.getString("__dahham__download_location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath)

        var nameFromUrl = url

        if (url.contains("/") && url.contains(".")){
            nameFromUrl = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."))
        }


        val download = PrivateDownload(name = display_name ?: nameFromUrl, url = url, uuid = UUID.randomUUID(), status = Download.WAITING, storageLocation = storageLocation, summary = summary);

        synchronized(database) {
            database.insert(download)
        }

        synchronized(all_downloads) {
            all_downloads.add(download)
        }

        startNextDownload()

        return download
    }

    fun removeDownload(download: Download){

        if (download.status == Download.IN_PROGRESS){
            download.stop()
        }

        synchronized(all_downloads) {
            if (all_downloads.contains(download)) {
                all_downloads.remove(download)
            }
        }

        synchronized(database) {
            database.delete(download)
        }
    }

    fun clearDownloads(){
        all_downloads.forEach { if (it.status == Download.IN_PROGRESS) (it as PrivateDownload).__stop() }

        database.delete(*all_downloads.toTypedArray())

        synchronized(all_downloads) {
            all_downloads.clear()
        }
    }

    fun downloads(which: Int): ArrayList<Download>{
        if (which == ALL_DOWNLOADS) return all_downloads

        val downloads = ArrayList<Download>()

        all_downloads.forEach { if (it.status == which) downloads.add(it) }

        return downloads
    }

    private fun startNextDownload(){

        all_downloads.forEach { if (it.status == Download.IN_PROGRESS) return }

        all_downloads.forEach {

            if (it.status == Download.WAITING) {

                it.downloadProgressListener = downloadListener

                (it as PrivateDownload).__start()

                return
            }

        }

    }

    val downloadListener = fun (download: Download, error: Throwable?) {
        val notification : Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(context.packageName + "__downloads__", "Downloads", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "Notifications about ongoing downloads"
            notificationManager.createNotificationChannel(notificationChannel)
            notification = Notification.Builder(context, notificationChannel.id)

        }else {
            notification = Notification.Builder(context)
        }

        notification.setContentTitle(download.name)
        notification.setContentText(download.summary)
        notification.setSmallIcon(R.drawable.ic_download_icon)
        notification.setAutoCancel(true)
        notification.setOnlyAlertOnce(true)
        notification.setOngoing(true)

        if (download.status == Download.COMPLETED || download.status == Download.CANCEL){

            notification.setOngoing(false)
            notification.setContentText("Download Completed")

            Toast.makeText(context, "Download completed for ${download.name}", Toast.LENGTH_LONG).show()

            startNextDownload()
        } else {

            notification.setProgress(100, (download.progress * download.size).toInt() / 100, download.size <= 0)

        }

        notificationManager.notify(download.url.hashCode(), notification.build())

        Thread(Runnable {
            synchronized(database) {
                database.update(download)
            }
        }).start()

    }

}

class PrivateDownload(name: String, summary: String? = null, url: String, storageLocation: String, size: Long = 0, status: Int = STOPPED, progress: Long = 0, uuid: UUID):
        Download(name, summary, url, storageLocation, size, status, progress, uuid){

    override fun start() {
        throw UnsupportedOperationException("Downloads registered with the download manager cannot be start explicitly")
    }

    override fun stop() {
        throw UnsupportedOperationException("Downloads registered with the download manager cannot be stop explicitly")
    }

    fun __start(){
        super.start()
    }
    fun __stop(){
        super.stop()
    }
}

@Dao
private interface Database {
    @Query("SELECT * from Downloads ORDER BY database_index DESC")
    fun query(): List<Download>

    @Insert
    fun insert(vararg download: Download)

    @Delete
    fun delete(vararg download: Download)

    @Update
    fun update(vararg download: Download)
}


@androidx.room.Database(entities = arrayOf(Download::class), version = 1)
@TypeConverters(UUIDConverter::class)
private abstract class DatabaseBuilder: RoomDatabase(){
    abstract fun DownloadStore(): Database;
}