package com.prady.codeheist;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getWindow().setStatusBarColor(Color.BLACK);

        CircularImageView img = findViewById(R.id.profile_img);
        Glide.with(this)
                .load("https://static2.cbrimages.com/wordpress/wp-content/uploads/2019/07/Naruto-meme-header.jpg")
                .into(img);
    }
}
