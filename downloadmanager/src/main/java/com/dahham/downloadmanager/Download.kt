package com.dahham.downloadmanager

import android.os.Build
import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.FileStore
import java.nio.file.Files
import java.util.*

/**
 * Created by dahham on 6/19/18.
 * This file is part of DownloadManager licensed under GNU Public License
 *
 */
@Entity(tableName = "Downloads")
open class Download(var name: String, var summary: String? = null, var url: String, var storageLocation: String, var size: Long = 0, var status: Int = STOPPED, var progress: Long = 0, var uuid: UUID = UUID.randomUUID()) {

    @PrimaryKey(autoGenerate = true) var database_index: Int = 0

    @Ignore
    var disposableDownload: Disposable? = null

    @Ignore
    var downloadProgressListener: ((Download, Throwable?) -> Unit)? = null

    companion object {

        val IN_PROGRESS = 1
        val COMPLETED = 2
        val WAITING = 3
        val CANCEL = -1
        val PAUSED = 4
        val  STOPPED = -2
    }


    open fun start() {
        if (status == IN_PROGRESS) return

        download()
    }

    open fun stop() {
        if (status != IN_PROGRESS) return

        if (disposableDownload?.isDisposed == true) disposableDownload?.dispose()

        status = PAUSED
    }

    private fun download(cont: Boolean = false) {

        disposableDownload = Flowable.create({

            e: FlowableEmitter<String> ->

            try {

                status = IN_PROGRESS
                e.onNext(progress.toString())

                val inputConn = URL(url).openConnection()
                inputConn.addRequestProperty("responseCode", "200")

                val inputStream = inputConn.getInputStream().buffered()

                if (size > 0 && size != inputConn.contentLengthLong) throw IllegalStateException("size of download differs with original")

                if (cont) {
                    inputStream.skip(progress)
                }

                val bytesArray = ByteArray(DEFAULT_BUFFER_SIZE)

                val dir = File(storageLocation)
                if (!dir.exists() && !dir.mkdirs()){
                    throw FileNotFoundException("Directory does not exist and cannot be created...!")
                }

                val outStream = File("$storageLocation/${this.name}").outputStream().buffered()

                while (true) {
                    val read = inputStream.read(bytesArray)
                    if (read == -1 /*|| inputConn.getHeaderFieldInt("responseCode", -1) != 200*/) break

                    progress += read
                    outStream.write(bytesArray)

                    e.onNext(progress.toString())
                }

                outStream.flush()
                outStream.close()
                inputStream.close()
            } catch (exception: Exception) {
                if (e.tryOnError(exception) && BuildConfig.DEBUG){
                    Log.d("dahham_download_manager", "Failed to send out error: ${exception.localizedMessage}")
                }
            }

            e.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            downloadProgressListener?.invoke(this, null)
        }, {
            status = CANCEL
            downloadProgressListener?.invoke(this, it)
        }, {
            status = COMPLETED
            downloadProgressListener?.invoke(this, null)
        })
    }


    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Download && other.uuid == this.uuid
    }

}