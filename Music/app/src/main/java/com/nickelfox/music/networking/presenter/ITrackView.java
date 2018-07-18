package com.nickelfox.music.networking.presenter;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import okhttp3.ResponseBody;

public interface ITrackView extends BaseView {
    void onSucessTackList(Pager<PlaylistTrack> responseBody);


}
