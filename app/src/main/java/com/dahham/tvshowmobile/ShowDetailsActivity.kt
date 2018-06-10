package com.dahham.tvshowmobile

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.crashlytics.android.Crashlytics
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
        } else if (intent != null && intent.extras.containsKey(DETAILS)) {
            ViewCompat.setTransitionName(details_poster, "poster")
            show = intent.getParcelableExtra(DETAILS)
        } else {
            // We should not run in this block when atleast when we do lets quit

            Crashlytics.log(Log.ERROR, "dahham", "cannot reconstruct show with intent $intent and parcelable ${intent.getParcelableExtra(DETAILS) as? Show}")

            finish()
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

        if (show.genre != null) {
            for (genre in show.genre!!) {
                genres.append("$genre  ")
            }
        }

        toolbar.title = show.name

        details_description.text = getString(R.string.show_description, show.description, genres, casts)
        rating.text = show.rating.toString()
        views.text = show.views.toString()
        runtime.text = show.runtime

        loadPoster()
    }

    fun loadPoster() {
        glide.load(show.poster).into(details_poster)
    }

    private inner class SeasonTab(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

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
            }

            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "${show.series?.get(position)?.season}"
        }

        override fun saveState(): Parcelable? {
            val bundle = Bundle()

            fragments.forEach {
                supportFragmentManager.putFragment(bundle, fragments.indexOf(it).toString(), it)
            }

            bundle.putInt("count", fragments.size)
            return bundle
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            super.restoreState(state, loader)

            val bundle = state as Bundle

            for (i in 0..bundle.getInt("count")){
                val fragment = supportFragmentManager.getFragment(bundle, i.toString()) as SeasonTabFragment

                fragments.add(fragment)
            }
        }
    }
}
