package com.example.newsappjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NewsDao {

    @Query("SELECT * FROM news")
    List<DatabaseNews> getAllNews();

    @Query("SELECT * FROM news WHERE title = :title")
    DatabaseNews getNewByTitle(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNew(DatabaseNews databaseNews);

    @Query("DELETE FROM news WHERE title = :title")
    void deleteNew(String title);

    @Query("DELETE FROM news")
    void deleteAllNews();

    @Query("SELECT COUNT(*) FROM news")
    int getNewsCount();

    @Update
    void updateNews(DatabaseNews databaseNews);
}