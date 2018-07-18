package com.nickelfox.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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


        recyclerview_tacks.setLayoutManager(new LinearLayoutManager(this));
        trackResultsAdapter = new TrackResultsAdapter(this, this);
        recyclerview_tacks.setAdapter(trackResultsAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onSucess(Pager<PlaylistSimple> responseBody) {

        playlist.addAll(responseBody.items);
        pLayListViewAdapter.notifyDataSetChanged();


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
        trackResultsAdapter.clearData();
        List<Track> trackList = new ArrayList<Track>();
        for (int i = 0; i < responseBody.items.size(); i++) {
            Track track = responseBody.items.get(i).track;
            trackList.add(track);

        }
        trackResultsAdapter.addData(trackList);
        trackResultsAdapter.notifyDataSetChanged();

        Toast.makeText(this, " " + responseBody.items.size(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onItemSelected(View itemView, Track item) {


        Toast.makeText(this, " " + item.duration_ms, Toast.LENGTH_SHORT).show();
    }
}
