package com.android.gubonny.simplegithub.ui.main;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.gubonny.simplegithub.R;
import com.android.gubonny.simplegithub.ui.search.SearchActivity;

public class MainActivity extends AppCompatActivity{

    FloatingActionButton btnSearch;

    @Override
    protected void onCreate(@Nullable android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnActivityMainSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 저장소 검색 액티비티를 호출합니다.
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

    }
}
