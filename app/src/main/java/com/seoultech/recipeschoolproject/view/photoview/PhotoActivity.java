package com.seoultech.recipeschoolproject.view.photoview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.seoultech.recipeschoolproject.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.seoultech.recipeschoolproject.databinding.ActivityPhotoBinding;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPhotoBinding binding;
    public static final String EXTRA_PHOTO_URL = "photoUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        loadImage();
    }

    private String getPhotoUrl() {
        return getIntent().getStringExtra(EXTRA_PHOTO_URL);
    }

    private void loadImage() {
        Glide.with(this).load(getPhotoUrl()).into(binding.photoView);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}