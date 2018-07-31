package fragments


import adapters.FavoriteAdapter
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.champion.echo_12.R
import databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_main_screen.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {

    var myActivity:Activity?=null
   // var getSongsList:ArrayList<Songs>?=null

    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null

    var refreshList: ArrayList<Songs>?= null
    var getListFromDatabase: ArrayList<Songs>?= null

    object Statified{
        var mediaPlayer:MediaPlayer?=null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater!!.inflate(R.layout.fragment_favorite, container, false)
        activity?.title="Favorites"
        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)
        setHasOptionsMenu(true)


        return view
    }


    fun getSongsFromPhone(): ArrayList<Songs>{

        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arrayList


    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent= EchoDatabase(context)
        display_favorites_by_searching()
        bottomBarSetup()



    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
        val sortItem=menu?.findItem(R.id.action_redirect)
        item?.isVisible=false


    }


    fun bottomBarSetup(){
        try{
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()

            }
            if(SongPlayingFragment.Statified.mediaPlayer !=null){
                if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                    nowPlayingBottomBar?.visibility=View.VISIBLE
                }
                else{
                    nowPlayingBottomBar?.visibility=View.INVISIBLE
                }
            }



        }
        catch (e:Exception){
        e.printStackTrace()}
    }

    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener {
            Statified.mediaPlayer=SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songID", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "Success")
            songPlayingFragment.arguments = args
            fragmentManager!!.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        }

        playPauseButton?.setOnClickListener {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

    fun display_favorites_by_searching(){
        if(favoriteContent?.checkSize() as Int > 0){
            //Refresh list to maintain updated list
            refreshList=ArrayList<Songs>()
            getListFromDatabase=favoriteContent?.queryDBList()
            if(getListFromDatabase == null){
                Toast.makeText(myActivity,"NULL QUERY", Toast.LENGTH_SHORT).show()
            }

            var fetchListFromDevice=getSongsFromPhone()
//            if(fetchListFromDevice != null){
//                for(i in 0..fetchListFromDevice?.size as Int -1){
//                    for(j in 0..getListFromDatabase?.size as Int-1){
//                        if((getListFromDatabase?.get(j)?.songID ==(fetchListFromDevice?.get(i)?.songID))){
//                            refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
//                        }
//                    }
//                }
//            }
            if(fetchListFromDevice != null){
                for(i in 0 until fetchListFromDevice?.size){
                    for(j in 0 until getListFromDatabase?.size as Int){
                        if((getListFromDatabase?.get(j)?.songID === (fetchListFromDevice?.get(i)?.songID))){
                            refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }
            }
            else{
               Toast.makeText(myActivity,"LIST IS NULL",Toast.LENGTH_SHORT).show()
            }

            if(refreshList == null){
                recyclerView?.visibility=View.INVISIBLE
                noFavorites?.visibility=View.VISIBLE
            }
            else{
                var favoriteAdapter=FavoriteAdapter(refreshList as ArrayList<Songs>,myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                //recyclerView?.visibility=View.VISIBLE
               // noFavorites?.visibility=View.INVISIBLE
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)


            }

        }
        else{
            //no Songs in the database
            recyclerView?.visibility=View.INVISIBLE
            noFavorites?.visibility=View.VISIBLE
        }



    }


}
