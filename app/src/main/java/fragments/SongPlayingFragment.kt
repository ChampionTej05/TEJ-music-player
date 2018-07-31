package fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.champion.echo_12.R
import databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_song_playing.view.*
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    object Statified{
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer?=null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null

        var currentSongHelper:CurrentSongHelper?=null
        var currentPosition:Int=0
        var fetchSongs:ArrayList<Songs>?=null

        var audioVisualization:AudioVisualization?=null
        var glView:GLAudioVisualizationView?=null

        var mSensorManager:SensorManager?=null
        var mSensorListener:SensorEventListener?=null

        var MY_PREFS_NAME="ShakeFeature"

        var fab:ImageButton?=null
        var favoriteContent: EchoDatabase? = null

        var updateSongTime=object :Runnable{
            override fun run() {
                val getCurrent = mediaPlayer?.currentPosition
                startTimeText?.text = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong())))
                seekBar?.progress = getCurrent
                Handler().postDelayed(this, 1000)
            }

        }
    } //end of Statified

    var trackPosition: Int = 0
    object Staticated{
        var MY_PREFS_SHUFFLE="Shuffle Feature"
        var My_PREFS_LOOP="Loop Feature"

        //Methods
        fun playNext(check:String){
            if(check.equals("PlayNextNormal",true)){
                Statified.currentPosition+=1
            }
            else if (check.equals("PlayNextLikeNormalShuffle",true)){
                var randomObject=Random()
                var randomPosition=randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition=randomPosition

            }
            if(Statified.currentPosition==Statified.fetchSongs?.size){
                Statified.currentPosition=0
            }

            //gerneally
            Statified.currentSongHelper?.isLoop=false
            var nextSong=Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songArtist=nextSong?.artist
            Statified.currentSongHelper?.songTitle=nextSong?.songTitle
            Statified.currentSongHelper?.currentPosition=Statified.currentPosition
            Statified.currentSongHelper?.songId=nextSong?.songID as Long
            Statified.currentSongHelper?.songPath=nextSong?.songData
            UpdatetextViews(Statified.currentSongHelper?.songTitle as String,Statified.currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()
            try{

                Statified.mediaPlayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            }
            catch (e:Exception){
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                //fab?.setBackgroundResource(R.drawable.favorite_on)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
            } else {
                //fab?.setBackgroundResource(R.drawable.favorite_off)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
            }
        }

        fun playPrevious(){
            Statified.currentPosition-=1
            if(Statified.currentPosition==-1){
                Statified.currentPosition=0
            }
            if(Statified.currentSongHelper?.isPlaying as Boolean){
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
            else{
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }

            Statified.currentSongHelper?.isLoop=false
            var nextSong=Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songArtist=nextSong?.artist
            Statified.currentSongHelper?.songTitle=nextSong?.songTitle
            Statified.currentSongHelper?.currentPosition=Statified.currentPosition
            Statified.currentSongHelper?.songId=nextSong?.songID as Long
            Statified.currentSongHelper?.songPath=nextSong?.songData
            UpdatetextViews(Statified.currentSongHelper?.songTitle as String,Statified.currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()
            try{

                Statified.mediaPlayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            }
            catch (e:Exception){
                e.printStackTrace()
            }


            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                //fab?.setBackgroundResource(R.drawable.favorite_on)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
            } else {
                //fab?.setBackgroundResource(R.drawable.favorite_off)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
            }

        }

        fun onSongComplete(){
            if(Statified.currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying=true


            }
            else{
                if(Statified.currentSongHelper?.isLoop as Boolean){

                    Statified.currentSongHelper?.isPlaying=true
                    var nextSong=Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.songArtist=nextSong?.artist
                    Statified.currentSongHelper?.songTitle=nextSong?.songTitle
                    Statified.currentSongHelper?.currentPosition=Statified.currentPosition
                    Statified.currentSongHelper?.songId=nextSong?.songID as Long
                    Statified.currentSongHelper?.songPath=nextSong?.songData
                    UpdatetextViews(Statified.currentSongHelper?.songTitle as String,Statified.currentSongHelper?.songArtist as String)
                    Statified.mediaPlayer?.reset()
                    try{

                        Statified.mediaPlayer?.setDataSource(Statified.myActivity,Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }

                }
                else{
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying=true
                }
            }

            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                //fab?.setBackgroundResource(R.drawable.favorite_on)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
            } else {
                //fab?.setBackgroundResource(R.drawable.favorite_off)
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
            }

        }

        fun UpdatetextViews(songTitle:String,songArtist:String){
            if(songTitle.equals("<unknown>",true)){
                songTitle=="Unknown"
            }
            if(songArtist.equals("<unknown>",true)){
                songArtist=="Unknown"
            }
            Statified.songTitleView?.setText(songTitle)
            Statified.songArtistView?.setText(songArtist)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekBar?.max = finalTime
            Statified.startTimeText?.text = String.format("%02d: %02d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            Statified.endTimeText?.text = String.format("%02d: %02d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            Statified.seekBar?.setProgress(startTime)
           // Statified.seekBar?.setProgress(finalTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }
    } //end of Staticated
    var mAcceleration:Float=0f
    var mAcclerationCurrent:Float=0f
    var mAcclerationLast:Float=0f



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title="Now Playing"
        Statified.seekBar=view?.findViewById(R.id.seekBar)
        Statified.startTimeText=view?.findViewById(R.id.startTime)
        Statified.endTimeText=view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton=view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton=view?.findViewById(R.id.nextButton)
        Statified.previousImageButton=view?.findViewById(R.id.previousButton)
        Statified.loopImageButton=view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton=view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView=view?.findViewById(R.id.songArtist)
        //title id check and then use
        Statified.songTitleView=view?.findViewById(R.id.songTitleFavScreen)
        Statified.glView=view?.findViewById(R.id.visualizer_view)
        Statified.fab=view?.findViewById(R.id.favoriteIcon)
        Statified.fab?.alpha=0.8f

        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization=Statified.glView as AudioVisualization
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity=activity
    }


    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        Statified.audioVisualization?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Statified.audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager=Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration=0.0f
        mAcclerationCurrent=SensorManager.GRAVITY_EARTH
        mAcclerationLast=SensorManager.GRAVITY_EARTH
        bindShakeListener()


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item:MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
        val sortitem:MenuItem?=menu?.findItem(R.id.action_sort)
        sortitem?.isVisible=false


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.action_redirect ->{
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.currentSongHelper= CurrentSongHelper()
        Statified.favoriteContent=EchoDatabase(Statified.myActivity as Context)


        Statified.currentSongHelper?.isPlaying=true
        Statified.currentSongHelper?.isLoop=false
        Statified.currentSongHelper?.isShuffle=false

        var path:String?=null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        try {
           path= arguments!!.getString("path")
            _songTitle = arguments!!.getString("songTitle")
            _songArtist = arguments!!.getString("songArtist")
            songId = arguments!!.getInt("songID").toLong()
            Statified.currentPosition=arguments!!.getInt(("songPosition"))
            Statified.fetchSongs=arguments!!.getParcelableArrayList("songData")


            Statified.currentSongHelper?.songPath=path
            Statified.currentSongHelper?.songArtist=_songArtist
            Statified.currentSongHelper?.songTitle=_songTitle
            Statified.currentSongHelper?.songId=songId
            Statified.currentSongHelper?.currentPosition=Statified.currentPosition


            Staticated.UpdatetextViews(Statified.currentSongHelper?.songTitle as String,Statified.currentSongHelper?.songArtist as String)


        }
        catch (e:Exception){
            e.printStackTrace()
        }

        var fromFavBottomBar=arguments?.get("FavBottomBar") as? String

        if(fromFavBottomBar!=null){
            Statified.mediaPlayer=FavoriteFragment.Statified.mediaPlayer
        }
        else{
            Statified.mediaPlayer= MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try{

                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()

            }

            catch (e:Exception){
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
        }




        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)


        //If current song playing and we want to play the other song then current song should stop and let the other be played.
        if(Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.playPauseImageButton?.setBackgroundResource((R.drawable.pause_icon))
        }
        else{
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        //completion
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()

        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context,0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

       // Shuffle Icon
        var prefsForShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed= prefsForShuffle?.getBoolean("feature",false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else{
            Statified.currentSongHelper?.isShuffle=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        //Loop Icon
        var prefsForLoop=Statified.myActivity?.getSharedPreferences(Staticated.My_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed= prefsForLoop?.getBoolean("feature",false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=false
            Statified.currentSongHelper?.isLoop=true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }
        else{
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        //To Organize the Favorite button at the start of the App so as to maintain the Favorite Button
        //First bug is Occurring here..

        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
           //Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
        } else {
           //Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
        }

        var size=Statified.favoriteContent?.checkSize()
       // Toast.makeText(Statified.myActivity,"NO of Songs in the Favorite: "+size.toString(),Toast.LENGTH_SHORT).show()

    }



    fun clickHandler(){
        Statified.fab?.setOnClickListener {
            //Check here whether the DataBase is Requesting the Query properly or Not
            //2nd Bug is occurring here.
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                //fab?.setBackgroundResource(R.drawable.favorite_off)
                //To delete  the song from DataBase
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
                Log.d("Activity Song","Song id during Deletion: "+Statified.currentSongHelper?.songId?.toString())
                Statified.favoriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
                val sizeofList=Statified.favoriteContent?.checkSize()
                Toast.makeText(Statified.myActivity,"Removed From Favorites",Toast.LENGTH_SHORT).show()


            } else {
                //fab?.setBackgroundResource(R.drawable.favorite_on)
                //To ADD the song from DataBase
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
                Log.d("Activity Song","Song id during addtion: "+Statified.currentSongHelper?.songId?.toString())

                Statified.favoriteContent?.storeAsFavorite(Statified.currentSongHelper?.songId?.toInt(),
                        Statified.currentSongHelper?.songArtist, Statified.currentSongHelper?.songTitle,Statified.currentSongHelper?.songPath)
                val sizeofList=Statified.favoriteContent?.checkSize()

                Toast.makeText(Statified.myActivity, "Added to Favorites",
                        Toast.LENGTH_SHORT).show()

            }
        }

        Statified.playPauseImageButton?.setOnClickListener {
            if(Statified.mediaPlayer?.isPlaying as Boolean){
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying=false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying=true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

        Statified.nextImageButton?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying=true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            if(Statified.currentSongHelper?.isShuffle as Boolean){
                Staticated.playNext("PlayNextLikeNormalShuffle")
            }
            else{
                Staticated.playNext("PlayNextNormal")
            }
        }

        Statified.previousImageButton?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying=true
            if(Statified.currentSongHelper?.isLoop as Boolean){
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            Staticated.playPrevious()
        }

        Statified.loopImageButton?.setOnClickListener {

            var editorShuffle=Statified.myActivity?.getSharedPreferences(
                    Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()

            var editorLoop=Statified.myActivity?.getSharedPreferences(
                    Staticated.My_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()

            if(Statified.currentSongHelper?.isLoop as Boolean){
                Statified.currentSongHelper?.isLoop=false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
                Toast.makeText(context,"Looping is Disabled", Toast.LENGTH_SHORT).show()

            }
            else{
                Statified.currentSongHelper?.isLoop=true
                Statified.currentSongHelper?.isShuffle=false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                Toast.makeText(context,"Looping is Enabled", Toast.LENGTH_SHORT).show()
                }

        }

        Statified.shuffleImageButton?.setOnClickListener {
            var editorShuffle=Statified.myActivity?.getSharedPreferences(
                    Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()

            var editorLoop=Statified.myActivity?.getSharedPreferences(
                    Staticated.My_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()


            if(Statified.currentSongHelper?.isShuffle as Boolean){
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle=false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                Toast.makeText(context,"Shuffle is Disabled", Toast.LENGTH_SHORT).show()
            }
            else{
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.currentSongHelper?.isShuffle=true
                Statified.currentSongHelper?.isLoop=false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
                Toast.makeText(context,"Shuffle is Enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }



//    fun bindShakeListener(){
//        Statified.mSensorListener=object:SensorEventListener{
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//
//            }
//
//            override fun onSensorChanged(event: SensorEvent) {
//               val x=event.values[0]
//                val y=event.values[1]
//                val z=event.values[2]
//
//                mAcclerationLast=mAcclerationCurrent
//                mAcclerationCurrent=Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
//                val delta = mAcclerationCurrent-mAcclerationLast
//                mAcceleration=mAcceleration*0.9f +delta
//                if(mAcceleration > 12){
//                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
//                    val isAllowed = prefs?.getBoolean("feature",false)
//                    if(isAllowed as Boolean){
//                        Staticated.playNext("PlayNextNormal")
//                    }
//
//                }
//
//            }
//
//        }
//    }

    /*This function handles the shake events in order to change the songs when we shake the
phone*/
    fun bindShakeListener() {
        /*The sensor listener has two methods used for its implementation i.e.
        OnAccuracyChanged() and onSensorChanged*/
                Statified.mSensorListener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /*We do noot need to check or work with the accuracy changes for the
        sensor*/
                    }

                    override fun onSensorChanged(event: SensorEvent) {
        /*We need this onSensorChanged function
        * This function is called when there is a new sensor event*/
        /*The sensor event has 3 dimensions i.e. the x, y and z in which the
        changes can occur*/
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
        /*Now lets see how we calculate the changes in the acceleration*/
        /*Now we shook the phone so the current acceleration will be the first to
        start with*/
                        mAcclerationLast=mAcclerationCurrent
        /*Since we could have moved the phone in any direction, we calculate the
        Euclidean distance to get the normalized distance*/
                        mAcclerationCurrent=Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
        /*Delta gives the change in acceleration*/
                        val delta =mAcclerationCurrent-mAcclerationLast
        /*Here we calculate the lower filter
        * The written below is a formula to get it*/
                        mAcceleration = mAcceleration * 0.9f + delta
        /*We obtain a real number for acceleration
        * and we check if the acceleration was noticeable, considering 12 here*/
                        if (mAcceleration > 12) {
        /*If the accel was greater than 12 we change the song, given the fact
        our shake to change was active*/
                            val prefs =
                                    Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                            val isAllowed = prefs?.getBoolean("feature", false)
                            if (isAllowed as Boolean) {
                                Staticated.playNext("PlayNextNormal")
                               // Toast.makeText(Statified.myActivity,"Enabled Shake Change",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(Statified.myActivity,"Disabled Shake Change",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

    }








}
