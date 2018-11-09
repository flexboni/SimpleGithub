package com.android.gubonny.simplegithub.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver

// Lifecycle 클래스의 '+=' 연산자를 오버로딩 함.
operator fun Lifecycle.plusAssign(observer: LifecycleObserver) = this.addObserver(observer)