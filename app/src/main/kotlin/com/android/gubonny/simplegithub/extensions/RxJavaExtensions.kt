package com.android.gubonny.simplegithub.extensions

import com.android.gubonny.simplegithub.rx.AutoClearedDisposable
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// CompositeDisposable 의 '+=' 연산자 뒤에 Disposable 타입이 오는 경우를 재 정의
//operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
operator fun AutoClearedDisposable.plusAssign(disposable: Disposable)

// CompositeDisposable.add() 함수를 호출
        = this.add(disposable)

fun runOnIoScheduler(func: () -> Unit): Disposable = Completable
        .fromCallable(func)
        .subscribeOn(Schedulers.io())
        .subscribe()

// }