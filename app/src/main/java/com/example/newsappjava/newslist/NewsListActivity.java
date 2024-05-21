package com.example.newsappjava.newslist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsappjava.R;
import com.example.newsappjava.account.AccountActivity;
import com.example.newsappjava.network.ApiService;
import com.example.newsappjava.network.NewsRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewsListActivity extends AppCompatActivity {

    private RecyclerView newsRecycler;
    private NewsListViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_movie_list);

        newsRecycler = findViewById(R.id.news_list);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        viewModel = new NewsListViewModel(new NewsRepository(ApiService.create()));
        viewModel.fetchNews();

        NewsAdapter adapter = new NewsAdapter(this, viewModel);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.feed) {

            } else if (itemId == R.id.account) {
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        newsRecycler.setAdapter(adapter);
        newsRecycler.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getNetworkNews().observe(this, adapter::submitList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.feed);
    }
}