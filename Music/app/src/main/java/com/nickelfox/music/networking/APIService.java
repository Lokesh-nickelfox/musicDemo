package com.nickelfox.music.networking;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIService {

    @GET("authorize")
    Call<ResponseBody> login(@QueryMap Map<String, String> map);

    @GET("me/playlists")
    Observable<Pager<PlaylistSimple>> getMyPlaylists(@Header("Authorization") String authHeader);


    @GET()
    Observable<Pager<PlaylistTrack>> getTracks(@Url String url, @Header("Authorization") String authHeader);

}
