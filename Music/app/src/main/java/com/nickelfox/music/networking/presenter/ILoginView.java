package com.nickelfox.music.networking.presenter;

import okhttp3.ResponseBody;

public interface ILoginView extends BaseView {

    void onSucess();

    void onError();
}
