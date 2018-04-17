package com.dahham.tvshowmobile

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.dahham.tvshowmobile.fragments.TvShows4MobileFragment
import com.dahham.tvshowmobile.fragments.TvmdbFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawer.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val tvmdb = TvmdbFragment()
    val tvshow4mobile = TvShows4MobileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)
        setSupportActionBar(toolbar)

        val toggler = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggler.syncState()

        drawer_layout.addDrawerListener(toggler)
        nav_menu.setNavigationItemSelectedListener(this)

        switchFragments(tvshow4mobile, "tvshow4mobile")
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        when(item.itemId){
//            R.id.nav_source_tmdb -> switchFragments(tvmdb, "tvmdb")
//            R.id.nav_source_tvshows4mobile -> switchFragments(tvshow4mobile, "tvshow4mobile")
        }


        return true
    }

    fun switchFragments(fragment: Fragment, tag: String){
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, tag).commit()
    }


//    external fun stringFromJNI(): String
//
//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("downloader")
//        }
//    }
}
