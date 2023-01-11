package com.sunwookim.mediaplayer.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sunwookim.mediaplayer.R;
import com.sunwookim.mediaplayer.adapters.SongAlbumAdapter;

import interfaces.OnClickListen;
import static com.sunwookim.mediaplayer.activities.MainActivity.al;

public class AlbumActivity extends AppCompatActivity implements OnClickListen {


    protected static ImageView imageView;
    protected int position;
    private static TextView textView1;
    private static TextView textView2;
    protected static RecyclerView recyclerView;
    protected static RecyclerView.LayoutManager mmanager;
    private static LinearLayout linearLayout;
    static SongAlbumAdapter songalbumAdapter;
    private static boolean hideTitle = false;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkMutedSwatch;
    private NotificationManagerCompat notificationManager;

    //엘범
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        recyclerView = findViewById(R.id.album_recycler);
        imageView=findViewById(R.id.albumimage);
        linearLayout=findViewById(R.id.linear);
        recyclerView.setHasFixedSize(true);
        textView1=findViewById(R.id.text1);
        textView2=findViewById(R.id.text2);
        mmanager=new LinearLayoutManager(this);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        position = bundle.getInt("index");
        songalbumAdapter = new SongAlbumAdapter(al.get(position),this);
        System.out.println(al.get(position).get(0).getArtist());
        textView1.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView1.setText(al.get(position).get(0).getArtist());
        String size=(al.get(position).size())+"";
        textView2.setText(size);
        notificationManager = NotificationManagerCompat.from(this);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.hide();

        Glide.with(this)
                .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),al.get(position).get(0).getAlbumID()).toString())
                .thumbnail(0.2f)
                .centerCrop()
                .placeholder(R.drawable.track)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setBack();
                        return false;
                    }
                })
                .into(imageView);

        DividerItemDecoration verticalDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
        verticalDecoration.setDrawable(verticalDivider);
        recyclerView.addItemDecoration(verticalDecoration);

        recyclerView.setLayoutManager(mmanager);
        recyclerView.setAdapter(songalbumAdapter);

    }

    public static void hideAll(boolean hide){
        if(hide){
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            hideTitle = true;
        }else{
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            hideTitle = false;
        }
    }

    @Override
    public void onClick(int position) {
        hideAll(true);
        Intent intent=new Intent(MainActivity.getInstance(), PlayerActivity.class).putExtra("index",position).putExtra("val",1).putExtra("from",true);
        startActivity(intent);
    }

    public void setBack(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        Palette.from(bitmap).maximumColorCount(40).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                lightVibrantSwatch = palette.getLightVibrantSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
                linearLayout.setBackgroundColor(Color.TRANSPARENT);
                recyclerView.setBackgroundColor(Color.TRANSPARENT);
                textView1.setTextColor(Color.WHITE);
                textView2.setTextColor(Color.WHITE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.hideAll(false);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
