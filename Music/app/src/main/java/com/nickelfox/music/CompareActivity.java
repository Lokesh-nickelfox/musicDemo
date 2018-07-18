package com.nickelfox.music;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nickelfox.music.activity.Player;
import com.nickelfox.music.utils.OnDragTouchListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;

public class CompareActivity extends AppCompatActivity {

    @BindView(R.id.song_1)
    ImageView song1Image;
    @BindView(R.id.song_2)
    ImageView song2Image;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.play_song_1)
    ImageView playSong1Button;
    @BindView(R.id.play_song_2)
    ImageView playSong2Button;
    @BindView(R.id.like_button)
    ImageView likeButton;

    public static final String SELECTED_SONGS_LIST_KEY = "selectedSongs";

    private ArrayList<Track> selectedTracks = new ArrayList<>();
    private Player mPlayer;
    private float dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        ButterKnife.bind(this);

        initToolbar();

        selectedTracks.addAll(getIntent().<Track>getParcelableArrayListExtra(SELECTED_SONGS_LIST_KEY));

        Picasso.with(this).load(selectedTracks.get(0).album.images.get(0).url).into(song1Image);
        Picasso.with(this).load(selectedTracks.get(1).album.images.get(1).url).into(song2Image);

        playSong1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(selectedTracks.get(0));
            }
        });

        playSong2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(selectedTracks.get(1));
            }
        });

        likeButton.setOnTouchListener(new OnDragTouchListener(likeButton));
        bindService(PlayerService.getIntent(this), mServiceConnection, Activity.BIND_AUTO_CREATE);

    }

    private void playSong(Track item) {
        String previewUrl = item.preview_url;

        if (previewUrl == null) {
            return;
        }
        if (mPlayer == null) return;

        String currentTrackUrl = mPlayer.getCurrentTrack();

        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) {
            mPlayer.play(previewUrl);
        } else if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.resume();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService(PlayerService.getIntent(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(PlayerService.getIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayer = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer = null;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
