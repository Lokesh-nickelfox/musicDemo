package com.nickelfox.music.networking;


import android.util.Log;


import com.nickelfox.music.networking.presenter.BaseView;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public abstract class CallBackWrapper<T> extends DisposableObserver<T> {
    //BaseView is just a reference of a View in MVP
    private WeakReference<BaseView> weakReference;

    public CallBackWrapper(BaseView view) {
        this.weakReference = new WeakReference<>(view);
    }

    protected abstract void onSuccess(T t);

    protected abstract void onError();

    protected abstract void onfinish();

    @Override
    public void onNext(T t) {
        //You can return StatusCodes of different cases from your API and handle it here. I usually include these cases on BaseResponse and iherit it from every Response
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        BaseView view = weakReference.get();
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();
            view.onUnknownError(getErrorMessage(responseBody));
        } else if (e instanceof SocketTimeoutException) {
            view.onTimeout();
        } else if (e instanceof IOException) {
            view.onNetworkError();
        } else {
            view.onUnknownError(e.getMessage());
        }

        onError();
    }

    @Override
    public void onComplete() {
        onfinish();
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {

            Log.d("deligates",responseBody.string().toString());
            JSONObject jsonObject = new JSONObject(responseBody.string());
            String error = jsonObject.optString("error_description");
            if (error == null || error.isEmpty()) {
                error = jsonObject.optString("error");
            }

            return error;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}