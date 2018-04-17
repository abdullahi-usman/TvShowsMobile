package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ViewModels.AbstractShowViewModel
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.show_contents_container.*


/**
 * Created by dahham on 4/4/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class AbstractShowsFragment<T>  : Fragment(), AbstractShowViewModel.ShowsViewModelListener<T>  {

    override fun onStarted() {
        if (show_content_viewswitcher.currentView != error_container) {
            show_content_viewswitcher.showPrevious()
        }

        empty_text?.text = getString(R.string.loading)
    }

    override fun onNext(item: T, msg: String) {

        if (show_content_viewswitcher != null && show_content_viewswitcher.currentView == error_container){
            show_content_viewswitcher.showNext()
            recycler_shows_container.invalidate()
        }

    }

    override fun onError(throwable: Throwable) {

        if (show_content_viewswitcher != null && show_content_viewswitcher.currentView == error_container){
            empty_text?.text = getString(R.string.error_message)

            error_button_switcher.showNext()
        }

        throwable.printStackTrace()
    }

    override fun onCompleted() {}

    open fun onFragmentSelected(){

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.show_contents_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState?.containsKey("empty_msg") == true) {
            empty_text?.text = savedInstanceState.getString("empty_msg")
        }

        error_reload_button.setOnClickListener{
            error_button_switcher.showNext()
            load()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("empty_msg", empty_text?.text.toString())
    }

    open fun onItemClick(show: T, view: View){}

    open fun load(){
        (recycler_shows_container.adapter as TvAdapter<*>).load()
    }
}