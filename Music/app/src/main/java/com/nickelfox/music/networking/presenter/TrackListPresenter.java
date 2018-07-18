package com.nickelfox.music.networking.presenter;

import android.widget.Toast;

import com.nickelfox.music.networking.CallBackWrapper;
import com.nickelfox.music.networking.TrackListUseCase;

import io.reactivex.disposables.CompositeDisposable;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import okhttp3.ResponseBody;

public class TrackListPresenter implements ITrackPressenter {


    @Override
    public void getTracks() {
        iTrackView.showLoading();
        this.disposable.add(this.mUseCase.execute().subscribeWith(new TrackListObserver()));
    }


    ITrackView iTrackView;

    private CompositeDisposable disposable = new CompositeDisposable();
    private TrackListUseCase mUseCase;


    public TrackListPresenter(ITrackView iTrackView,String trackURL) {
        this.iTrackView = iTrackView;
        mUseCase = new TrackListUseCase(trackURL);

    }

    public void onStop() {
        if (disposable != null) {
            disposable.dispose();

        }
    }


    class TrackListObserver extends CallBackWrapper<Pager<PlaylistTrack>> {
        TrackListObserver() {
            super(iTrackView);
        }

        @Override
        protected void onSuccess(Pager<PlaylistTrack> responseBody) {

            iTrackView.hideLoading();
            if (responseBody != null) {

                iTrackView.onSucessTackList(responseBody);

            }

        }

        @Override
        protected void onError() {
            iTrackView.hideLoading();
            onStop();
        }

        @Override
        protected void onfinish() {
            onStop();
        }
    }


}
