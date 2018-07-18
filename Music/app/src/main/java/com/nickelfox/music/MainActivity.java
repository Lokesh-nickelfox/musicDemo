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
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.PlaylistTracksInformation;
import kaaes.spotify.webapi.android.models.Track;

import static com.nickelfox.music.CompareActivity.SELECTED_SONGS_LIST_KEY;


public class MainActivity extends AppCompatActivity implements IPlaylistView, PLayListViewAdapter.OnClickListener, ITrackView, TrackResultsAdapter.ItemSelectedListener {

    static final String EXTRA_TOKEN = "EXTRA_TOKEN";

    private PLayListViewAdapter pLayListViewAdapter;
    private TrackResultsAdapter trackResultsAdapter;

    PlayListPresenter playListPresenter;
    TrackListPresenter trackListPresenter;

    @BindView(R.id.progress_1)
    ProgressBar progressBar1;
    @BindView(R.id.progress_2)
    ProgressBar progressBar2;

    List<PlaylistSimple> playlist = new ArrayList<>();
    private Player mPlayer;

    private AppCompatButton selectSongsButton, doneButton, clearSelectionButton;
    private LinearLayout selectSongsPanel;

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

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView recyclerview_playlist = findViewById(R.id.recyclerview_playlist);
        RecyclerView recyclerview_tacks = findViewById(R.id.recyclerview_tacks);
        selectSongsButton = findViewById(R.id.btn_select_Songs);
        selectSongsPanel = findViewById(R.id.select_songs_panel);
        doneButton = findViewById(R.id.done);
        clearSelectionButton = findViewById(R.id.clear_selections);

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


        selectSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSongsButton.setVisibility(View.GONE);
                selectSongsPanel.setVisibility(View.VISIBLE);
                trackResultsAdapter.showSelector();
                if (mPlayer != null) {
                    mPlayer.pause();
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Track> selectedTracks = new ArrayList<>(trackResultsAdapter.getSelectedTracks());
                if (selectedTracks.size() < 2) {
                    Toast.makeText(MainActivity.this, "Need to select 2 tracks to compare", Toast.LENGTH_SHORT).show();
                } else {
                    Intent compareIntent = new Intent(MainActivity.this, CompareActivity.class);
                    compareIntent.putParcelableArrayListExtra(SELECTED_SONGS_LIST_KEY, selectedTracks);
                    startActivity(compareIntent);
                }
            }
        });

        clearSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSongsPanel.setVisibility(View.GONE);
                selectSongsButton.setVisibility(View.VISIBLE);
                trackResultsAdapter.hideSelector();
            }
        });
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

    @Override
    public void onSucess(Pager<PlaylistSimple> responseBody) {
        progressBar1.setVisibility(View.GONE);
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
        onError();
    }

    @Override
    public void onTimeout() {
        onError();
    }

    @Override
    public void onNetworkError() {
        onError();
    }

    private void onError() {
        progressBar1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionError() {
        onError();
    }

    @Override
    public void onClick(PlaylistTracksInformation playlistTracksInformation) {
        trackListPresenter = new TrackListPresenter(this, playlistTracksInformation.href);
        trackListPresenter.getTracks();
    }

    @Override
    public void onSucessTackList(Pager<PlaylistTrack> responseBody) {
        progressBar2.setVisibility(View.GONE);
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
    public void onItemSelected(Track item) {
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
        if (item.getItemId() == R.id.menu_logout) {
            CredentialsHandler.setToken(null, 0, null);
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
