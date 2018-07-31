package databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import fragments.SongPlayingFragment
import fragments.Songs


class EchoDatabase: SQLiteOpenHelper{

    var _songList = ArrayList<Songs>()


    object Staticated{
        var DB_VERSION = 1
        val DB_NAME = "FavoriteDatabase"
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "( " +Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING," + Staticated.COLUMN_SONG_PATH + " STRING);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?){
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(Staticated.COLUMN_ID, id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentValues)

        db.close()
    }

    fun queryDBList():ArrayList<Songs>?{
        try {

            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)

            if (cSor.moveToFirst()){
                do {

                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                }while (cSor.moveToNext())
            }else{

                return null
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        toPrintSongsInDatabase()
        return _songList
    }

    fun checkifIdExists(_id: Int): Boolean{
        var storeId = -1090
        var songName:String?=null
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor  = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()){
            do{
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                songName=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                //android.widget.Toast.makeText(this@EchoDatabase,"Song ID: "+storeId.toString(),Toast.LENGTH_SHORT).show()
                //Log.d("DataBase Activity: ","Song id: "+storeId.toString()+" Song Name: "+songName)


            }while (cSor.moveToNext())
        }else{
            return false
        }
        return storeId != -1090
    }

    fun deleteFavourite(_id: Int){
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int{
        var counter = 0
        val db = this.readableDatabase
        var query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()){
            do{
                counter = counter + 1
            }while (cSor.moveToNext())
        }else{
            return 0
        }
        return counter
    }

    fun toPrintSongsInDatabase(){

        for(i in 0.._songList.size-1){
            var obj1:Songs=_songList[i]

            Log.d("DATABASE: ","NAME: "+obj1.songTitle)
        }


    }

}
