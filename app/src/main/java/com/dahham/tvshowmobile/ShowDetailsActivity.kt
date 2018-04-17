package com.dahham.tvshowmobile

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        loadItem()

        setSupportActionBar(toolbar)

        seasons_tab.adapter = SeasonTab(supportFragmentManager)
    }

    fun loadItem() {
        ViewCompat.setTransitionName(details_poster, "poster")

        show = intent.getParcelableExtra(DETAILS)

        toolbar.title = show.name
        details_description.text = show.description

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition?) {
                    loadPoster()
                }

                override fun onTransitionResume(transition: Transition?) {}

                override fun onTransitionPause(transition: Transition?) {}

                override fun onTransitionCancel(transition: Transition?) {}

                override fun onTransitionStart(transition: Transition?) {}

            })
        } else {
            loadPoster()
        }
    }

    fun loadPoster() {
        glide.load(show.poster).listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                Palette.from((resource as BitmapDrawable).bitmap).generate {
                    val color = it.dominantSwatch?.rgb!!
                    toolbar_layout.contentScrim = ColorDrawable(color)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = color
                    }

                    details_poster.destroyDrawingCache()
                }

                return false
            }

        }).into(details_poster)
    }

    private inner class SeasonTab(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager){

        override fun getCount(): Int {
            return show.series?.size!!
        }

        override fun getItem(position: Int): Fragment {
            val fragment = SeasonTabFragment()
            fragment.arguments = Bundle()
            fragment.arguments?.putParcelable(SeasonTabFragment.SEASON, show.series?.get(position))

            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "SEASON ${show.series?.get(position)?.season}"
        }
    }
}
