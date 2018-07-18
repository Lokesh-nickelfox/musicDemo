package com.nickelfox.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.nickelfox.music.activity.Player;
import com.nickelfox.music.adapter.PLayListViewAdapter;
import com.nickelfox.music.adapter.TrackResultsAdapter;
import com.nickelfox.music.networking.presenter.IPlaylistView;
import com.nickelfox.music.networking.presenter.ITrackView;
import com.nickelfox.music.networking.presenter.PlayListPresenter;
import com.nickelfox.music.networking.presenter.TrackListPresenter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.PlaylistTracksInformation;
import kaaes.spotify.webapi.android.models.Track;

public class MainActivity extends AppCompatActivity implements IPlaylistView, PLayListViewAdapter.OnClickListener, ITrackView, TrackResultsAdapter.ItemSelectedListener {

    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

    private PLayListViewAdapter pLayListViewAdapter;

    private TrackResultsAdapter trackResultsAdapter;

    private Toolbar toolbar;
    private AppCompatTextView toobarTitle;
    private RecyclerView recyclerview_playlist, recyclerview_tacks;

    PlayListPresenter playListPresenter;

    TrackListPresenter trackListPresenter;
    List<PlaylistSimple> playlist = new ArrayList<PlaylistSimple>();

    private Player mPlayer;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        toobarTitle = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerview_playlist = findViewById(R.id.recyclerview_playlist);
        recyclerview_tacks = findViewById(R.id.recyclerview_tacks);

        Intent intent = getIntent();
        String token = intent.getStringExtra(EXTRA_TOKEN);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        recyclerview_playlist.setLayoutManager(layoutManager);
        pLayListViewAdapter = new PLayListViewAdapter(this, playlist, this);
        recyclerview_playlist.setAdapter(pLayListViewAdapter);
        playListPresenter = new PlayListPresenter(this);
        playListPresenter.getMyPlayList();


        int[] ATTRS = new int[]{android.R.attr.listDivider};

        TypedArray a = obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);
        int insetLeft = getResources().getDimensionPixelSize(R.dimen.left_divider_margin);
        InsetDrawable insetDivider = new InsetDrawable(divider, insetLeft, 0, 0, 0);
        a.recycle();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(insetDivider);
        recyclerview_tacks.addItemDecoration(itemDecoration);


        recyclerview_tacks.setLayoutManager(new LinearLayoutManager(this));
        trackResultsAdapter = new TrackResultsAdapter(this, this);
        recyclerview_tacks.setAdapter(trackResultsAdapter);

        bindService(PlayerService.getIntent(this), mServiceConnection, Activity.BIND_AUTO_CREATE);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        this.unbindService(mServiceConnection);
    }

    @Override
    public void onSucess(Pager<PlaylistSimple> responseBody) {

        playlist.addAll(responseBody.items);
        pLayListViewAdapter.notifyDataSetChanged();

        trackListPresenter = new TrackListPresenter(this, responseBody.items.get(0).tracks.href);
        trackListPresenter.getTracks();


    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onUnknownError(String error) {

    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onConnectionError() {

    }

    @Override
    public void onClick(PlaylistTracksInformation playlistTracksInformation) {
        trackListPresenter = new TrackListPresenter(this, playlistTracksInformation.href);
        trackListPresenter.getTracks();


    }

    @Override
    public void onSucessTackList(Pager<PlaylistTrack> responseBody) {

        if (responseBody.items != null && responseBody.items.size() > 0) {
            trackResultsAdapter.clearData();
            List<Track> trackList = new ArrayList<Track>();
            for (int i = 0; i < responseBody.items.size(); i++) {
                Track track = responseBody.items.get(i).track;
                trackList.add(track);

            }
            trackResultsAdapter.addData(trackList);
            trackResultsAdapter.notifyDataSetChanged();
        }



    }




    @Override
    public void onItemSelected(View itemView, Track item) {
        String previewUrl = item.href;

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


}
