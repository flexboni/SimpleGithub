package com.android.gubonny.simplegithub.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

// CompositeDisposable 의 '+=' 연산자 뒤에 Disposable 타입이 오는 경우를 재 정의
operator fun CompositeDisposable.plusAssign(disposable: Disposable) {

    // CompositeDisposable.add() 함수를 호출
    this.add(disposable)
}