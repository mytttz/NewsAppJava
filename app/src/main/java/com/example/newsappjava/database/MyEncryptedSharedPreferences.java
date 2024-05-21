package com.example.newsappjava.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class MyEncryptedSharedPreferences {

    private static EncryptedSharedPreferences sharedPreferences = null;

    public static void initialize(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    context,
                    "encrypted_prefs_file",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteEncryptedSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("encrypted_prefs_file", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static EncryptedSharedPreferences getEncryptedSharedPreferences() {
        if (sharedPreferences == null) {
            throw new IllegalStateException("SharedPreferencesManager is not initialized");
        }
        return sharedPreferences;
    }
}
