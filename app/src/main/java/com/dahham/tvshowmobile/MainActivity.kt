package com.dahham.tvshowmobile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.dahham.tvshowmobile.fragments.DownloadsFragment
import com.dahham.tvshowmobile.fragments.TvShows4MobileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawer.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var tvshow4mobile = TvShows4MobileFragment()
    var downloads = DownloadsFragment()

    val TVSHOWS4MOBILE_TAG = "tvshow4mobile"
    val DOWNLOADS_FRAGMENT_TAG = "downloads_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main_drawer)
        setSupportActionBar(toolbar)

        if (savedInstanceState?.containsKey(TVSHOWS4MOBILE_TAG) == true) {
            tvshow4mobile = supportFragmentManager.getFragment(savedInstanceState, TVSHOWS4MOBILE_TAG) as TvShows4MobileFragment
        }

        if (savedInstanceState?.containsKey(DOWNLOADS_FRAGMENT_TAG) == true){
            downloads = supportFragmentManager.getFragment(savedInstanceState, DOWNLOADS_FRAGMENT_TAG) as DownloadsFragment
        }

        val toggler = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggler.syncState()

        drawer_layout.addDrawerListener(toggler)
        nav_menu.setNavigationItemSelectedListener(this)

        if (supportFragmentManager.fragments.contains(tvshow4mobile).not()) {
            tvshows4mobile_fragment()
        }

        //MobileAds.initialize(this, "ca-app-pub-5849046006048060~4044363744")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_share -> share()
            R.id.nav_send -> sendEmail()
            R.id.nav_developer -> developer()
            R.id.nav_downloads -> downloads_fragment()
            R.id.nav_tvshows4mobile -> tvshows4mobile_fragment()
        }

        return true
    }
    fun tvshows4mobile_fragment(){
        switchFragments(tvshow4mobile, TVSHOWS4MOBILE_TAG)
    }

    fun downloads_fragment(){
        switchFragments(downloads, DOWNLOADS_FRAGMENT_TAG)
    }

    fun developer() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://play.google.com/store/apps/dev?id=6243316749639111813")

        launchIntent(intent)
    }

    fun share() {
        val link = "http:/play.google.com/store/apps/details?id=com.dahham.tvshowsmobile"
        val intent = getShareIntent().setChooserTitle(R.string.share_using).setHtmlText(link).setText(link).setType("text/html").intent

        launchIntent(intent)
    }

    fun sendEmail() {
        val intent = getShareIntent().setEmailTo(arrayOf("abdullahidhako@gmail.com")).setText("Hi Abdullahi").setChooserTitle("Send Email Using").setType("text/email").intent

        launchIntent(intent)
    }

    fun getShareIntent(): ShareCompat.IntentBuilder {
        val intent = ShareCompat.IntentBuilder.from(this)

        return intent
    }

    fun launchIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.can_perform_request, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (supportFragmentManager.fragments.contains(tvshow4mobile)) {
            supportFragmentManager.putFragment(outState, TVSHOWS4MOBILE_TAG, tvshow4mobile)
        }

        if (supportFragmentManager.fragments.contains(downloads)) {
            supportFragmentManager.putFragment(outState, DOWNLOADS_FRAGMENT_TAG, downloads)
        }
    }

    fun switchFragments(fragment: Fragment, tag: String) {
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
