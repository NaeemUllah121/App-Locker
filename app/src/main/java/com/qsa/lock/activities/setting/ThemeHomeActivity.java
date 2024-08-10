package com.qsa.lock.activities.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qsa.lock.R;
import com.qsa.lock.activities.lock.GestureCreateActivity;
import com.qsa.lock.activities.lock.GestureSelfUnlockActivity;

import com.qsa.lock.databinding.ActivityThemeHomeBinding;
import com.qsa.lock.model.Theme;
import com.qsa.lock.utils.PrefsManager;
import com.squareup.picasso.Picasso;

public class ThemeHomeActivity extends AppCompatActivity {
    ActivityThemeHomeBinding binding;
    Theme theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String themeJson = getIntent().getStringExtra("theme");
        theme = new Gson().fromJson(themeJson, Theme.class);
        Picasso.get().load(theme.getBackGround()).into(binding.setImage);

        binding.btnSetTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefsManager.setBackGroundImage(theme);
                Toast.makeText(ThemeHomeActivity.this, "Theme Applied", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(ThemeHomeActivity.this,GestureSelfUnlockActivity.class));
                finish();
            }
        });
    }
}