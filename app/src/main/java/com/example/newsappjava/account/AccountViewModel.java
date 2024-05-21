package com.example.newsappjava.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.newsappjava.database.AppDatabase;
import com.example.newsappjava.database.DatabaseNews;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.jvm.internal.Intrinsics;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<List<DatabaseNews>> databaseNews = new MutableLiveData<>();

    public LiveData<List<DatabaseNews>> getDatabaseNews() {
        return databaseNews;
    }

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }


    private final CompositeDisposable disposable = new CompositeDisposable();

    public void initialize(Context context) {
        disposable.add(
                Observable.fromCallable(() -> AppDatabase.getDatabase(context).newsDao().getAllNews())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                databaseNews::postValue,
                                Throwable::printStackTrace
                        )
        );
    }

    public void setupAccountUI(SharedPreferences preferences, EncryptedSharedPreferences sharedPreferences) {
        if (preferences.getInt("isUserExist", 0) == 1 && preferences.getInt("isUserLog", 0) == 1) {
            loginSuccess.postValue(true);
        }
    }

    public void handleEnterButtonClick(SharedPreferences preferences, EncryptedSharedPreferences sharedPreferences,
                                       String login, String password) {
        if (login.isEmpty() || password.isEmpty()) {
            loginSuccess.postValue(false);
            return;
        }

        if (preferences.getInt("isUserExist", 0) == 1 && preferences.getInt("isUserLog", 0) == 0) {
            if (sharedPreferences.getString(login, "").equals(password)) {
                preferences.edit().putInt("isUserLog", 1).apply();
                loginSuccess.postValue(true);
            } else {
                loginSuccess.postValue(false);
            }
        } else if (preferences.getInt("isUserExist", 0) == 0 && preferences.getInt("isUserLog", 0) == 0) {
            sharedPreferences.edit().putString(login, password).apply();
            preferences.edit().putInt("isUserExist", 1).putInt("isUserLog", 1).putString("LOGIN", login).apply();
            loginSuccess.postValue(true);
        }
    }

    public void logout(SharedPreferences preferences) {
        preferences.edit().putInt("isUserLog", 0).apply();
        Log.i("5", String.valueOf(true));
        logoutSuccess.postValue(true);
    }

    public void removeFav(@NotNull final Context context, @NotNull final String title) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(title, "title");

        Observable.fromCallable(() -> {
                    AppDatabase.getDatabase(context).newsDao().deleteNew(title);
                    return AppDatabase.getDatabase(context).newsDao().getAllNews();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseNews::setValue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}