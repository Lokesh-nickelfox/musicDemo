package com.nickelfox.music.networking.presenter;

public interface BaseView {

    void showLoading(String message);

    void showLoading();

    void hideLoading();

    void onUnknownError(String error);

    void onTimeout();

    void onNetworkError();

    void onConnectionError();

}
