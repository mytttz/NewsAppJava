package com.example.newsappjava.newslist;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsappjava.R;
import com.example.newsappjava.database.AppDatabase;
import com.example.newsappjava.database.DatabaseNews;
import com.example.newsappjava.network.NetworkNews;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewsAdapter extends ListAdapter<NetworkNews, NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final NewsListViewModel viewModel;
    private final SharedPreferences preferences;


    public NewsAdapter(Context context, NewsListViewModel viewModel) {
        super(new NewsDiffCallback());
        this.context = context;
        this.viewModel = viewModel;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsDate;
        TextView newsDescription;
        CheckBox favoriteButton;

        public NewsViewHolder(View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsDate = itemView.findViewById(R.id.news_date);
            newsDescription = itemView.findViewById(R.id.news_description);
            favoriteButton = itemView.findViewById(R.id.favorite_button);

            favoriteButton.setOnClickListener(v -> {
                if (preferences.getInt("isUserLog", 0) == 0) {
                    favoriteButton.setChecked(false); // Reset to unchecked state
                    Toast.makeText(context, "Войдите в учетную запись", Toast.LENGTH_SHORT).show();
                }
            });

            favoriteButton.setOnCheckedChangeListener((checkBox, isChecked) -> {
                if (preferences.getInt("isUserLog", 0) == 1) {
                    NetworkNews news = getItem(getBindingAdapterPosition());
                    if (news != null) {
                        if (isChecked) {
                            viewModel.addFav(context, news);
                        } else {
                            viewModel.removeFav(context, news.getTitle());
                        }
                    }
                } else {
                    favoriteButton.setChecked(false); // Reset to unchecked state
                    Toast.makeText(context, "Войдите в учетную запись", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NetworkNews news = getItem(position);
        if (news != null) {
            holder.newsTitle.setText(news.getTitle());
            holder.newsDescription.setText(news.getDescription());
            holder.newsDate.setText(formatTime(news.getPublishedAt()));

            // Сброс состояния кнопки "Избранное"
            holder.favoriteButton.setOnCheckedChangeListener(null);
            holder.favoriteButton.setChecked(false);
            if (preferences.getInt("isUserLog", 0) == 1) {

                // Проверка состояния "избранное" в базе данных
                Observable.fromCallable(() -> AppDatabase.getDatabase(context).newsDao().getNewByTitle(news.getTitle()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dbNews -> {
                            boolean isFavorite = dbNews != null;
                            holder.favoriteButton.setChecked(isFavorite);
                        }, throwable -> {
                            // Обработка ошибок, если это необходимо
                            holder.favoriteButton.setChecked(false);
                        });
                // Восстановление слушателя после обновления состояния
                holder.favoriteButton.setOnCheckedChangeListener((checkBox, isChecked) -> {
                    if (isChecked) {
                        viewModel.addFav(context, news);
                    } else {
                        viewModel.removeFav(context, news.getTitle());
                    }
                });

            }
        }
    }

    static class NewsDiffCallback extends DiffUtil.ItemCallback<NetworkNews> {
        @Override
        public boolean areItemsTheSame(@NonNull NetworkNews oldItem, @NonNull NetworkNews newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NetworkNews oldItem, @NonNull NetworkNews newItem) {
            return oldItem.equals(newItem);
        }
    }

    private String formatTime(String iso8601Time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            long dateMillis = inputFormat.parse(iso8601Time).getTime();

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            return outputFormat.format(dateMillis);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }


}