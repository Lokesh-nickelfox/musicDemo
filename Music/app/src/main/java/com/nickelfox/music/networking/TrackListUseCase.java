package com.nickelfox.music.networking;

import com.nickelfox.music.UseCases.UseCase;
import com.nickelfox.music.networking.repository.RetrofitClient;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import okhttp3.ResponseBody;

public class TrackListUseCase extends UseCase<Pager<PlaylistTrack>> {
    private RetrofitClient mDatasource = new RetrofitClient();

    private String trackURL;

    public TrackListUseCase(String trackURL) {
        this.trackURL = trackURL;
    }

    @Override
    public Observable<Pager<PlaylistTrack>> buildObservable() {
        return mDatasource.getTracks(trackURL).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
