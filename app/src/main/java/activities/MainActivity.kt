package activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.champion.echo_12.R
import com.example.champion.echo_12.adapters.NavigationDrawerAdapter
import fragments.MainScreenFragment
import fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {

    //Objects to be used

    object Statified{
        var drawerLayout:DrawerLayout?=null
        var notificationManager:NotificationManager?=null

    }

    var navigationDrawerIconslist:ArrayList<String> = arrayListOf()
    var images_for_navdrawer = intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,
            R.drawable.navigation_settings,R.drawable.navigation_aboutus)

    var trackNotificationBuilder:Notification?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statified.drawerLayout=findViewById(R.id.drawer_layout)

        navigationDrawerIconslist.add("All Songs")
        navigationDrawerIconslist.add("Favorites")
        navigationDrawerIconslist.add("Settings")
        navigationDrawerIconslist.add("About Us")



        val toggle=ActionBarDrawerToggle(this@MainActivity,MainActivity.Statified.drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment= MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment,mainScreenFragment,"MainScreenFragment")
                .commit()

        var __navigationAdapter= NavigationDrawerAdapter(navigationDrawerIconslist,images_for_navdrawer,this@MainActivity)
        __navigationAdapter.notifyDataSetChanged()

        var navigation_recycler_view=findViewById<RecyclerView>(R.id.navigation_recycler_view)
        //to position objects
        navigation_recycler_view.layoutManager=LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator=DefaultItemAnimator()
        navigation_recycler_view.adapter=__navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)

        val intent=Intent(this@MainActivity,MainActivity::class.java)

        val pIntent=PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),intent,0)
        trackNotificationBuilder=Notification.Builder(this@MainActivity)
                .setContentText("Music is Playing in the background")
                .setContentTitle("TEJ Music Player")
                .setSmallIcon(R.drawable.tej_splash_screen)
                .setContentIntent(pIntent)
                //so that user should not be able to cancel the notification by
                //swiping in and it has to click to cancel the event
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Statified.notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    override fun onStart() {
        super.onStart()
        try{

            Statified.notificationManager?.cancel(1998)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun onStop() {
        super.onStop()
        try{

            if(SongPlayingFragment.Statified.mediaPlayer != null){
                if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                    Statified.notificationManager?.notify(1998,trackNotificationBuilder)
                }
            }

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try{

            Statified.notificationManager?.cancel(1998)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}
