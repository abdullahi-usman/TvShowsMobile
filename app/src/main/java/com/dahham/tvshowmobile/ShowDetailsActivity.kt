package com.dahham.tvshowmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dahham.tvshowmobile.Models.Show
import com.dahham.tvshowmobile.fragments.SeasonTabFragment
import kotlinx.android.synthetic.main.activity_show_details.*
import kotlinx.android.synthetic.main.content_show_details.*

class ShowDetailsActivity : AppCompatActivity() {

    lateinit var show: Show
    lateinit var glide: RequestManager

    companion object {
        val DETAILS = "show_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        glide = Glide.with(this)

        if (savedInstanceState != null) {
            show = savedInstanceState.getParcelable(DETAILS)
        }else {
            ViewCompat.setTransitionName(details_poster, "poster")
            show = intent.getParcelableExtra(DETAILS)
        }

        loadItem()

        setSupportActionBar(toolbar)

        if (seasons_tab.adapter == null) {
            seasons_tab.adapter = SeasonTab(supportFragmentManager)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(DETAILS, show)
    }

    fun loadItem() {
        val casts = StringBuilder()
        if (show.casts != null) {
            for (cast in show.casts!!) {
                casts.append("$cast   ")
            }
        }

        val genres = StringBuilder()

        if (show.genre != null){
            for (genre in show.genre!!){
                genres.append("$genre  ")
            }
        }

        toolbar.title = show.name

        details_description.text = getString(R.string.show_description, show.description, genres, casts)
        rating.text = show.rating.toString()
        views.text = show.views.toString()
        runtime.text = show.runtime

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
//                override fun onTransitionEnd(transition: Transition?) {
//                    loadPoster()
//                }
//
//                override fun onTransitionResume(transition: Transition?) {}
//
//                override fun onTransitionPause(transition: Transition?) {}
//
//                override fun onTransitionCancel(transition: Transition?) {}
//
//                override fun onTransitionStart(transition: Transition?) {}
//
//            })
//        } else {
            loadPoster()
//        }
    }

    fun loadPoster() {
        glide.load(show.poster).into(details_poster)
    }

    private inner class SeasonTab(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager){

        var fragments = ArrayList<SeasonTabFragment>()

        override fun getCount(): Int {
            return show.series?.size!!
        }

        override fun getItem(position: Int): Fragment {

            if (fragments.size <= position) {
                val fragment = SeasonTabFragment()
                fragment.arguments = Bundle()
                fragment.arguments?.putString(SeasonTabFragment.SHOW_NAME, show.name)
                fragment.arguments?.putParcelable(SeasonTabFragment.SEASON, show.series?.get(position))

                fragments.add(fragment)
                return fragment
            }

            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "${show.series?.get(position)?.season}"
        }

//        override fun saveState(): Parcelable? {
//            val bundle = Bundle()
//
//            fragments.forEach {
//                val _bundle = Bundle()
//                _bundle.putString(SeasonTabFragment.SHOW_NAME, it.show_name)
//                _bundle.putParcelable(SeasonTabFragment.SEASON, it.series)
//                bundle.putBundle(fragments.indexOf(it).toString(), _bundle)
//            }
//
//            bundle.putInt("fragment_count", fragments.size)
//            return bundle
//        }
//
//        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//            super.restoreState(state, loader)
//
//            val bundle = state as Bundle
//
//            for (i in 0..bundle.getInt("fragment_count")){
//                val fragment = SeasonTabFragment()
//                fragment.arguments = bundle.getBundle(i.toString())
//
//                fragments.add(fragment)
//            }
//        }
    }
}
