package com.nickelfox.music.UseCases;

import io.reactivex.Observable;

public abstract class UseCase<T> {
    public abstract Observable<T> buildObservable();
    public Observable<T> execute() {
        return buildObservable();
    }
}
