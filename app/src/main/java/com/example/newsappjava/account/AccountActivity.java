package com.example.newsappjava.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.newsappjava.R;
import com.example.newsappjava.database.MyEncryptedSharedPreferences;
import com.example.newsappjava.newslist.NewsListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class AccountActivity extends AppCompatActivity {
    private RecyclerView newsFavoriteRecycler;
    private MaterialButton enterButton;
    private MaterialButton logoutButton;
    private TextInputEditText loginTextField;
    private TextInputEditText passwordTextField;
    private TextView loginTitle;
    private TextView accountName;
    private TextInputLayout loginTextLayout;
    private TextInputLayout passwordTextLayout;
    private BottomNavigationView bottomNavigation;
    private AccountViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_and_favorite_activity);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        initializeViews();
        setupBottomNavigation();

        MyEncryptedSharedPreferences.initialize(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        EncryptedSharedPreferences encryptedSharedPreferences = MyEncryptedSharedPreferences.getEncryptedSharedPreferences();
        FavoriteAdapter adapter = new FavoriteAdapter(this, viewModel);
        newsFavoriteRecycler.setAdapter(adapter);
        newsFavoriteRecycler.setLayoutManager(new LinearLayoutManager(this));
        setupAccountInfo(preferences);

        viewModel.initialize(this);

        viewModel.getDatabaseNews().observe(this, list -> {
            adapter.submitList(list);
        });

        viewModel.getLoginSuccess().observe(this, loginSuccess -> {
            if (loginSuccess) {
                setupAccountInfo(preferences);
            } else {
                Toast.makeText(this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLogoutSuccess().observe(this, logoutSuccess -> {
            if (logoutSuccess) {
                resetUI(preferences);
            }
        });


        enterButton.setOnClickListener(v -> {
            viewModel.handleEnterButtonClick(
                    preferences,
                    encryptedSharedPreferences,
                    loginTextField.getText().toString(),
                    passwordTextField.getText().toString()
            );
        });

        logoutButton.setOnClickListener(v -> viewModel.logout(preferences));
    }

    private void initializeViews() {
        newsFavoriteRecycler = findViewById(R.id.favorite_list);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        enterButton = findViewById(R.id.enter_button);
        logoutButton = findViewById(R.id.logout_button);
        loginTextField = findViewById(R.id.login_text);
        passwordTextField = findViewById(R.id.password_text);
        loginTitle = findViewById(R.id.title_login);
        accountName = findViewById(R.id.account_name);
        loginTextLayout = findViewById(R.id.login);
        passwordTextLayout = findViewById(R.id.password);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.account);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.feed) {
                Intent intent = new Intent(AccountActivity.this, NewsListActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.account) {
                return true;
            }
            return false;
        });
    }

    private void setupAccountInfo(SharedPreferences preferences) {
        if (preferences.getInt("isUserLog", 0) == 1) {
            loginTitle.setVisibility(View.GONE);
            enterButton.setVisibility(View.GONE);
            loginTextLayout.setVisibility(View.GONE);
            passwordTextLayout.setVisibility(View.GONE);
            accountName.setVisibility(View.VISIBLE);
            accountName.setText(preferences.getString("LOGIN", ""));
            newsFavoriteRecycler.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // Скрыть элементы интерфейса связанные с аккаунтом, если пользователь не залогинен
            accountName.setVisibility(View.GONE);
            newsFavoriteRecycler.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    private void resetUI(SharedPreferences preferences) {
        loginTitle.setVisibility(View.VISIBLE);
        enterButton.setVisibility(View.VISIBLE);
        loginTextField.getText().clear();
        passwordTextField.getText().clear();
        loginTextLayout.setVisibility(View.VISIBLE);
        passwordTextLayout.setVisibility(View.VISIBLE);
        accountName.setVisibility(View.GONE);
        newsFavoriteRecycler.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
        viewModel.setupAccountUI(preferences, MyEncryptedSharedPreferences.getEncryptedSharedPreferences());
    }
}
