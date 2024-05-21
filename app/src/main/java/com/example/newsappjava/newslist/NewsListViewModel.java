package com.example.newsappjava.newslist;


import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newsappjava.database.AppDatabase;
import com.example.newsappjava.database.DatabaseNews;
import com.example.newsappjava.network.NetworkNews;
import com.example.newsappjava.network.NewsRepository;
import com.example.newsappjava.network.NewsResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListViewModel extends ViewModel {

    private final NewsRepository repository;
    private final MutableLiveData<List<NetworkNews>> _networkNews = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public NewsListViewModel(NewsRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<NetworkNews>> getNetworkNews() {
        return _networkNews;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void fetchNews() {
        repository.getNews().enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    NewsResponse newsResponse = response.body();
                    if (newsResponse != null) {
                        List<NetworkNews> newsList = newsResponse.getArticles();
                        _networkNews.postValue(newsList); // Отправляем новый список новостей через LiveData
                    } else {
                        _error.postValue("Пустой ответ");
                    }
                } else {
                    _error.postValue("Ошибка: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                _error.postValue("Ошибка в процессе выполнения запроса: " + t.getMessage());
            }
        });
    }

    public void addFav(@NotNull final Context context, @NotNull final NetworkNews networkNews) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(networkNews, "networkNews");

        Observable.fromCallable(() -> {
                    AppDatabase.getDatabase(context).newsDao().insertNew(toDatabaseNews(networkNews));
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void removeFav(@NotNull final Context context, @NotNull final String title) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(title, "title");

        Observable.fromCallable(() -> {
                    AppDatabase.getDatabase(context).newsDao().deleteNew(title);
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private DatabaseNews toDatabaseNews(NetworkNews networkNews) {
        return new DatabaseNews(0L, networkNews.getName(), networkNews.getTitle(),
                networkNews.getDescription(), networkNews.getPublishedAt());
    }
}