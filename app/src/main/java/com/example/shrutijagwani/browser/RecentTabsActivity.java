package com.example.shrutijagwani.browser;

import static com.example.shrutijagwani.browser.MainActivity.CURRENT;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.shrutijagwani.browser.adapters.RecentTabAdapter;

public class RecentTabsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecentTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_tabs);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecentTabAdapter(this, getIntent().getIntExtra(CURRENT, -1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}