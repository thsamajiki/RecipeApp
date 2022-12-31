package com.seoultech.recipeschoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.seoultech.recipeschoolproject.authentication.FirebaseAuthentication;
import com.seoultech.recipeschoolproject.databinding.ActivitySplashBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.view.login.LoginActivity;
import com.seoultech.recipeschoolproject.view.main.MainActivity;

public class SplashActivity extends AppCompatActivity implements Runnable, OnCompleteListener<Void> {

    private ActivitySplashBinding binding;
    private FirebaseAuthentication firebaseAuthentication = FirebaseAuthentication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuthentication.setOnCompleteListener(this);
        Handler handler = new Handler();
        handler.postDelayed(this,1000);
    }

    @Override
    public void run() {
        firebaseAuthentication.autoLogin(this);
    }


    @Override
    public void onComplete(boolean isSuccess, Response<Void> response) {
        Intent intent;
        if (isSuccess) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}