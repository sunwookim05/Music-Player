package com.musicplayer.mediaplayer.adapters;

import android.content.ContentUris;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.musicplayer.mediaplayer.R;
import com.musicplayer.mediaplayer.models.Song;
import com.musicplayer.mediaplayer.activities.MainActivity;

import interfaces.OnClickListen;

public class AlbumAdapter  extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements Filterable {
    private OnClickListen alclicklisten;
    private  LayoutInflater inflater;

    public AlbumAdapter(OnClickListen alclicklisten) {
        this.alclicklisten = alclicklisten;
        inflater=(LayoutInflater) MainActivity.getInstance().getSystemService(MainActivity.getInstance().LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_album_layout,viewGroup,false);
        return new ViewHolder(view,alclicklisten);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Song song=MainActivity.al.get(i).get(0);
        viewHolder.album.setText(song.getAlbum());
        viewHolder.artist.setText(song.getArtist());
        Glide
                .with(MainActivity.getInstance())
                .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),song.getAlbumID()).toString())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.xd)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .thumbnail(0.1f)
                .transition(new DrawableTransitionOptions()
                        .crossFade()
                )
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return MainActivity.al.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView album,artist;
        private   ImageView imageView;
        private OnClickListen onClickListen;
        public ViewHolder(@NonNull View itemView,OnClickListen onClickListen) {
            super(itemView);
            album=itemView.findViewById(R.id.album_name);
            artist=itemView.findViewById(R.id.album_artist);
            imageView=itemView.findViewById(R.id.album_image);
            this.onClickListen=onClickListen;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListen.onClick(getAdapterPosition());
        }
    }

}
