package com.android.gubonny.simplegithub.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.android.gubonny.simplegithub.api.model.GithubRepo
import io.reactivex.Flowable

@Dao
interface SearchHistoryDao {

    // 데이터베이스에 저장소를 추가 함.
    // 이미 저장된 항목이 있을 경우 데이터를 덮어 씀.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(repo: GithubRepo)

    // 저장되어 있는 저장소 목록을 반환 함.
    // Flowable 형태의 자료를 반환하므로, 데이터베이스가 변경되면 알림을 받아 새로운 자료를 가져 옴.
    // 따라서 항상 최신 자료를 유지 함.
    @Query("SELECT * FROM repositories")
    fun getHistory(): Flowable<List<GithubRepo>>

    // repositories 테이블의 모든 데이터를 삭제 함.
    @Query("DELETE FROM repositories")
    fun clearAll()
}

// 참고>
// Flowable은 RxJava 2.0 에서 새로 추가된 기능.
// Observable 과 동일하게 구독 가능한 이벤트를 발생시킬 수 있지만,
// Backpressure 에 대처할 수 있는 기능을 추가로 가지고 있음.
//// cf) Backpressure : 이벤트를 만들어내는 속도가 이벤트를 처리하는 속도보다
//// 빠를 때 나타나는 현상. 이 현상이 지속되면 아직 처리되지 않은 이벤트가
//// 큐에 계속 쌇이게 되므로 메모리가 부족해져 애플리케이션 강제 종료 됨.
// Flowable은 아직 처리되지 않은 이벤트를 최대한 쌓아두거나,
// 일부 이벤트 혹은 아직 처리되지 않은 이벤트 전부를 버리는 방식 중
// 하나로 해결할 수 있는 기능 제공 함.
// DB 에서는 자주 일어나는 현상이라 Flowable 을 사용 합시다

