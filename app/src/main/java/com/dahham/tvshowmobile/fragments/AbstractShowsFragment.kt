package com.dahham.tvshowmobile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dahham.tvshowmobile.LifeCycleObservers.TvShows4MobileLifeCycle
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import kotlinx.android.synthetic.main.show_contents_container.*
import java.lang.ref.WeakReference


/**
 * Created by dahham on 4/4/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class AbstractShowsFragment<T> : Fragment(), TvShows4MobileViewModel.ShowsViewModelListener<T> {

    lateinit var glide: RequestManager
    lateinit var lifecycle: TvShows4MobileLifeCycle<T>
    lateinit var data: LiveData<List<T>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glide = Glide.with(context!!)
        lifecycle = TvShows4MobileLifeCycle(this)
        data = getLiveData()
        data.observe({ this.getLifecycle() }, { recycler_shows_container.adapter.notifyDataSetChanged() })
    }

    abstract fun getLiveData(): LiveData<List<T>>;

    override fun onStarted() {
        if (show_content_viewswitcher?.currentView != error_container) {
            show_content_viewswitcher.showNext()
        }

        if (error_button_switcher?.currentView != error_reload_progressbar) {
            error_button_switcher.showNext()
        }

        empty_text?.text = getString(R.string.loading)
    }

    override fun onNext(item: T, msg: String) {
        prepareRecyclerView()
    }

    override fun onError(throwable: Throwable) {
        prepareErrorView(getString(R.string.error_message))

        throwable.printStackTrace()
    }

    fun prepareErrorView(message: String) {
        if (show_content_viewswitcher?.currentView != error_container){
            show_content_viewswitcher.showNext();
        }

        if (error_button_switcher?.currentView != error_reload_button) {
            error_button_switcher.showNext()
        }

        error_reload_button.setOnClickListener {
            load()
        }

        empty_text?.text = message
    }

    override fun onCompleted() {
        if (data.value?.size == null || data.value?.size!! <= 0) {
            prepareErrorView(getString(R.string.network_no_data))
        }

    }

    fun prepareRecyclerView() {
        if (show_content_viewswitcher != null && show_content_viewswitcher.currentView == error_container) {
            show_content_viewswitcher.showNext()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (view == null) {
            return inflater.inflate(R.layout.show_contents_container, container, false)
        } else {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState?.containsKey("empty_msg") == true) {
            empty_text?.text = savedInstanceState.getString("empty_msg")
        }

        if (data.value?.isEmpty() == false) {
            prepareRecyclerView()
        } else {

            if (error_button_switcher.currentView != error_reload_button && state() < TvShows4MobileViewModel.STATE.STARTED) {
                error_button_switcher.showNext()
            }

            error_reload_button.setOnClickListener {
                load()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("empty_msg", empty_text?.text.toString())
    }

    override fun onStart() {
        super.onStart()
        if (data.value == null || data.value?.isEmpty() == true) {
            load()
        }
    }

    abstract fun load()
    abstract fun state(): TvShows4MobileViewModel.STATE

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context!!, R.string.permission_granted, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context!!, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    abstract fun getDownloadLink(episode: Episode): List<Link>?

    fun download(episode: Episode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            return
        }

        val downloadTask = @SuppressLint("StaticFieldLeak")

        object : AsyncTask<Void, Void, List<Link>>() {
            lateinit var progressDialog: AlertDialog

            override fun onPreExecute() {
                super.onPreExecute()
                progressDialog = AlertDialog.Builder(context!!).setView(R.layout.please_wait_dialog).setCancelable(false).create()
                progressDialog.show()
            }

            override fun doInBackground(vararg params: Void?): List<Link>? {
                try {
                    return getDownloadLink(episode)
                } catch (e: Exception) {
                    return null
                }
            }

            override fun onPostExecute(links: List<Link>?) {
                super.onPostExecute(links)
                progressDialog.dismiss()

                if (links == null || links.isEmpty() == true) {
                    Toast.makeText(context, R.string.error_message, Toast.LENGTH_LONG).show()
                    return
                }

                val dialog = AppCompatDialog(context)
                dialog.setContentView(R.layout.show_link_choice_list)
                dialog.create()

                val choice_list = dialog.findViewById<ListView>(R.id.link_choice_list)
                choice_list?.setOnItemClickListener {
                    /*parent*/_, /*view*/_, position, /*id*/_ ->
                    dialog.dismiss()

                    when (position) {
                        0 -> links.forEach { if (it.type == Link.GP3) enqueue(episode, it) }
                        1 -> links.forEach { if (it.type == Link.MP4) enqueue(episode, it) }
                        2 -> links.forEach { if (it.type == Link.HD) enqueue(episode, it) }
                    }
                }

                dialog.show()
            }

            fun enqueue(episode: Episode, link: Link) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.link));
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(result: List<Link>?) {
                super.onCancelled(result)
                progressDialog.dismiss()
            }
        }

        downloadTask.execute()
    }
}