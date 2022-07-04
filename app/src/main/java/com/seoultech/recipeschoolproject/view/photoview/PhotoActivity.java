package com.seoultech.recipeschoolproject.view.photoview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.seoultech.recipeschoolproject.R;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private PhotoView photoView;
    public static final String EXTRA_PHOTO_URL = "photoUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();
        loadImage();
    }

    private String getPhotoUrl() {
        return getIntent().getStringExtra(EXTRA_PHOTO_URL);
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        photoView = findViewById(R.id.photo_view);
        btnBack.setOnClickListener(this);
    }

    private void loadImage() {
        Glide.with(this).load(getPhotoUrl()).into(photoView);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}