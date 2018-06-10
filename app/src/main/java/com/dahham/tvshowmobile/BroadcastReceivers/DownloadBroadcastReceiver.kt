package com.dahham.tvshowmobile.BroadcastReceivers

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ShowDetailsActivity
import com.dahham.tvshowmobile.utils.DownloadStore
import java.io.File

/**
 * Created by dahham on 5/2/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class DownloadBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

        if (intent?.action?.matches(Regex(DownloadManager.ACTION_NOTIFICATION_CLICKED)) == true){
            val _intent = Intent(context, ShowDetailsActivity::class.java)
            _intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK

            context?.startActivity(intent)
        } else if (intent?.action?.matches(Regex(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) == true) return

        val downloadStore = DownloadStore.instnace(context!!)


        if (id != -1L && downloadStore.removeFromStore(id!!)){

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
            cursor.moveToFirst()

            val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

            if (downloadStatus != DownloadManager.STATUS_SUCCESSFUL){

                downloadManager.remove(id)

                if(uri != null) {

                    File(Uri.parse(uri).path).delete()
                }

                return
            }

            val notificationBuilder : NotificationCompat.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel( "com.dahham.tvshowsmobile:tvshowsdownloads", "TV Shows Downloads", NotificationManager.IMPORTANCE_DEFAULT)

                notificationBuilder = NotificationCompat.Builder(context, notificationChannel.id)
            }else {
                notificationBuilder = NotificationCompat.Builder(context)
            }

            val show_name = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
            val season_episode = description.split(Regex("-"), 2)

            notificationBuilder.setContentTitle(show_name)
            notificationBuilder.setContentText(description)

            notificationBuilder.setContentIntent(PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_VIEW, Uri.parse(uri)), PendingIntent.FLAG_ONE_SHOT))
            notificationBuilder.setSmallIcon(R.drawable.notification_icon)
            notificationBuilder.setContentTitle(show_name).setContentText(season_episode.toString())
            notificationBuilder.setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(null, "com.dahham.tvshowsmobile:$id".hashCode(), notificationBuilder.build())


            DownloadStore.writeOutDownloadedFile(context, Episode(show_name, season_episode[0].trim(), season_episode[1].trim(), uri))
        }

    }
}