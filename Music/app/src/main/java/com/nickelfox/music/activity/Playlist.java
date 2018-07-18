package com.nickelfox.music.activity;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class Playlist {

    public interface View {
        void reset();

        void addData(List<PlaylistSimple> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void getList();

        void loadMoreResults();

        void selectPlaylist(PlaylistSimple item);

        void resume();

        void pause();

        void destroy();

    }
}
