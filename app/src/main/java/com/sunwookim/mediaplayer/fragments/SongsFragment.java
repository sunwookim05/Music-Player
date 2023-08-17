package com.sunwookim.mediaplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunwookim.mediaplayer.R;
import com.sunwookim.mediaplayer.activities.MainActivity;
import com.sunwookim.mediaplayer.activities.PlayerActivity;
import com.sunwookim.mediaplayer.adapters.SongAdapter;


import interfaces.OnClickListen;

import static com.sunwookim.mediaplayer.adapters.SongAdapter.songs;

public class SongsFragment extends Fragment implements OnClickListen {
   private View v;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mmanager;
    private static SongAdapter songAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            v = inflater.inflate(R.layout.songs_fragment, container, false);
            recyclerView = v.findViewById(R.id.recycleview);
            recyclerView.setHasFixedSize(true);
            mmanager = new LinearLayoutManager(getContext());
            songAdapter = new SongAdapter(MainActivity.getInstance(), songs, this);
            recyclerView.setLayoutManager(mmanager);
            recyclerView.setAdapter(songAdapter);
            return v;
        }catch (Exception e){
            e.printStackTrace();
            return v;
        }
    }


    @Override
    public void onClick(int position) {
        MainActivity.hideAll(true);
        Intent intent = new Intent(MainActivity.getInstance(), PlayerActivity.class).putExtra("index", position).putExtra("val", 0).putExtra("from",true);
        startActivity(intent);
    }
    public static void search(String text){
        songAdapter.getFilter().filter(text);
    }
}
