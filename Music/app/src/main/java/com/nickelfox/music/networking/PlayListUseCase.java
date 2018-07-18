package com.nickelfox.music.networking;




import com.nickelfox.music.UseCases.UseCase;
import com.nickelfox.music.networking.repository.RetrofitClient;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import okhttp3.ResponseBody;

public class PlayListUseCase extends UseCase<Pager<PlaylistSimple>> {
    private RetrofitClient mDatasource = new RetrofitClient();

    private String eventId;

    public PlayListUseCase(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public Observable<Pager<PlaylistSimple>> buildObservable() {
        return mDatasource.getMyPlaylist().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
