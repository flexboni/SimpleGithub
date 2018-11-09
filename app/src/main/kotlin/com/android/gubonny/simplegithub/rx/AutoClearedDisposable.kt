package com.android.gubonny.simplegithub.rx

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable(

        // 생명주기를 참조할 액티비티
        private val lifecycleOwner: AppCompatActivity,

        // onStop() 콜백 함수가 호출되었을 때,
        // 관리하고 있는 디스포저블 객체를 해제할지 여부를 지정 함.
        // 기본값은 true~!!
        private val alwaysClearOnStop: Boolean = true,
        private val compositeDisposable: CompositeDisposable
        = CompositeDisposable()) : LifecycleObserver {

    // 디스포저블 추가
    fun add(disposable: Disposable) {
        // LifeCycleOwner.lifecycle 을 사용하여
        // 참조하고 있는 컴포넌트의 LifeCycle 객체에 접근.
        // LifeCycle.State 를 사용하여 상태 정보인 LifeCycle.State 에 접근.
        // LifeCycle.State.isAtLeast() 함수를 사용하여
        // 현재 상태가 특정 상타의 이후 상태인지 여부를 반환 함.
        // 코틀린 표준 라이브러리에서 제공하는 check() 함수로
        // LifeCycle.State.isAtLeast() 함수의 반환 값이 참인지 확인하며,
        // 만약 참이 아닌 경우 IllegalStateException 예외를 발생 시킴.
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))

        // 앞선 검증 절차를 통과한 경우에만 디스포져블 추가 함.
        compositeDisposable.add(disposable)
    }

    // onStop() 콜백 함수가 호출되면 cleanUp() 함수를 호출 함.
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        // onStop() 콜백 함수가 호출되었을 때 무조건 디스포저블을 해제하지 않는 경우,
        // 액티비티의 isFinishing() 메서드를 사용하여
        // 종료되지 않는 시점(예: 다른 액티비티 호출) 에만 디스포져블을 해제하지 않도록 함.
        if (!alwaysClearOnStop && !lifecycleOwner.isFinishing) {
            return
        }

        // 관리하는 디스포져블을 헤재 함.
        compositeDisposable.clear()
    }

    // onDestroy() 콜백 함수가 호출되면 detachSelf() 함수를 호출 함.
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        // 관리하는 디스포저블을 해제 함.
        compositeDisposable.clear()

        // 더 이상 액티비티의 생명주기 이벤트를 받지 않도록
        // 액티비티 생명주기 옵서버에서 제거 함.
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}