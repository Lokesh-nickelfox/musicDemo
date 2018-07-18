package com.nickelfox.music.networking.presenter;

import com.nickelfox.music.networking.CallBackWrapper;
import com.nickelfox.music.networking.PlayListUseCase;

import io.reactivex.disposables.CompositeDisposable;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import okhttp3.ResponseBody;

public class PlayListPresenter implements IPlayListPresenter {

    IPlaylistView iPlaylistView;

    private CompositeDisposable disposable = new CompositeDisposable();
    private PlayListUseCase mUseCase;


    public PlayListPresenter(IPlaylistView iPlaylistView) {
        this.iPlaylistView = iPlaylistView;
        mUseCase = new PlayListUseCase("1");

    }

    public void onStop() {
        if (disposable != null) {
            disposable.dispose();

        }
    }


    class PlayListObserver extends CallBackWrapper<Pager<PlaylistSimple>> {
        PlayListObserver() {
            super(iPlaylistView);
        }

        @Override
        protected void onSuccess(Pager<PlaylistSimple> responseBody) {

            iPlaylistView.hideLoading();
            if (responseBody != null) {
                iPlaylistView.onSucess(responseBody);

            }

        }

        @Override
        protected void onError() {
            iPlaylistView.hideLoading();
            onStop();
        }

        @Override
        protected void onfinish() {
            onStop();
        }
    }


    @Override
    public void getMyPlayList() {
        iPlaylistView.showLoading();
        this.disposable.add(this.mUseCase.execute().subscribeWith(new PlayListObserver()));
    }


}
