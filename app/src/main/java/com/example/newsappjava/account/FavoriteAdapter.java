package com.example.newsappjava.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsappjava.R;
import com.example.newsappjava.database.DatabaseNews;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class FavoriteAdapter extends ListAdapter<DatabaseNews, FavoriteAdapter.NewsViewHolder> {

    private final Context context;
    private final AccountViewModel viewModel;

    public FavoriteAdapter(Context context, AccountViewModel viewModel) {
        super(new NewsDiffCallback());
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        DatabaseNews currentNews = getItem(position);
        if (currentNews != null) {
            holder.newsTitle.setText(currentNews.getTitle());
            holder.newsDescription.setText(currentNews.getDescription());
            holder.newsDate.setText(formatTime(currentNews.getPublishedAt()));
        }
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        final TextView newsTitle;
        final TextView newsDate;
        final TextView newsDescription;
        private final CheckBox favoriteButton;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsDate = itemView.findViewById(R.id.news_date);
            newsDescription = itemView.findViewById(R.id.news_description);
            favoriteButton = itemView.findViewById(R.id.favorite_button);

            favoriteButton.setChecked(true);
            favoriteButton.setOnCheckedChangeListener((checkBox, isChecked) -> {
                if (!isChecked) {
                    DatabaseNews news = getItem(getAbsoluteAdapterPosition());
                    if (news != null) {
                        viewModel.removeFav(context, news.getTitle());
                    }
                }
            });
        }
    }

    private String formatTime(String iso8601Time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

            return outputFormat.format(inputFormat.parse(iso8601Time));
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }

    static class NewsDiffCallback extends DiffUtil.ItemCallback<DatabaseNews> {
        @Override
        public boolean areItemsTheSame(@NonNull DatabaseNews oldItem, @NonNull DatabaseNews newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DatabaseNews oldItem, @NonNull DatabaseNews newItem) {
            return oldItem.equals(newItem);
        }
    }
}