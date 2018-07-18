package com.nickelfox.music.networking.presenter;

import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import okhttp3.ResponseBody;

public interface IPlaylistView extends BaseView {
    void onSucess(Pager<PlaylistSimple> responseBody);

}
