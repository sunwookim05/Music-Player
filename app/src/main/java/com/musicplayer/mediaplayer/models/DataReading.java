package com.musicplayer.mediaplayer.models;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataReading {

    private Context context;
    private HashMap<String, List<Song>>albums=new HashMap<>();
    private HashMap<String,List<Song>>Artists=new HashMap<>();


    public HashMap<String, List<Song>> getAlbums() {
        return albums;
    }

    public void setAlbums(HashMap<String, List<Song>> albums) {
        this.albums = albums;
    }
    public DataReading(Context context){
        this.context=context;
    }


    //음악 읽어오는부분
    public ArrayList<Song> getAllAudioFromDevice() {
        final ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,MediaStore.Audio.AudioColumns.TITLE ,MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,MediaStore.Audio.AudioColumns.ALBUM_ID};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                Song audioModel = new Song();
                String path = c.getString(0);   // Retrieve path.
                String name = c.getString(1);   // Retrieve name.
                String album = c.getString(2);  // Retrieve album name.
                String artist = c.getString(3); // Retrieve artist name.
                Long albumID=c.getLong(4);
                audioModel.setName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);
                audioModel.setAlbumID(albumID);
                songs.add(audioModel);
                if(albums.get(album)==null){
                    albums.put(album,new ArrayList<Song>());
                }
                albums.get(album).add(audioModel);
                if(Artists.get(artist)==null){
                    Artists.put(artist,new ArrayList<Song>());
                }
                Artists.get(artist).add(audioModel);
            }
            c.close();
        }
        return songs;
    }
}



