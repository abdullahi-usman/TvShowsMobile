package com.dahham.tvshowmobile.views

import android.content.Context
import android.widget.FrameLayout

/**
 * Created by dahham on 4/4/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvmdbView(context: Context): FrameLayout(context) {

//    private val emptyView : AppCompatTextView
//    val recyclerView: RecyclerView = TvmdbRecyclerView(context)
//
//    init {
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        //addView(recyclerView)
//        //recyclerView.visibility = View.GONE
//        emptyView = LayoutInflater.from(context).inflate(R.layout.show_contents_container, this, false) as AppCompatTextView
//    }
//
//    override fun onViewAdded(child: View?) {
//        if (child == recyclerView && wasAdded(child)){
//            removeView(emptyView)
//        }
//
//        super.onViewAdded(child)
//    }
//
//    fun setEmptyView(text: String?){
//        if (recyclerView.isShown || wasAdded(recyclerView)) {
//            //recyclerView.visibility = View.GONE
//            removeView(recyclerView)
//        }
//
//        emptyView.text = text
//
//        if (!emptyView.isShown || !wasAdded(emptyView)) {
//            addView(emptyView)
//        }
//    }
//
//    fun wasAdded(view: View): Boolean{
//        for (i in 0..childCount){
//            if (getChildAt(i) == view) return true
//        }
//
//        return false
//    }
//
//    private inner class TvmdbRecyclerView(context: Context): RecyclerView(context){
//
//        override fun onViewAdded(child: View?) {
//            super.onViewAdded(child)
//
//            if (!isShown || !wasAdded(this)) {
//                this@TvmdbView.addView(this)
//            }
//        }
//
//    }

}
