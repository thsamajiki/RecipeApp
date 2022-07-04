package com.seoultech.recipeschoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.seoultech.recipeschoolproject.authentication.FirebaseAuthentication;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.view.login.LoginActivity;
import com.seoultech.recipeschoolproject.view.main.MainActivity;

public class SplashActivity extends AppCompatActivity implements Runnable, OnCompleteListener<Void> {

    private FirebaseAuthentication firebaseAuthentication = FirebaseAuthentication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
        if (isSuccess) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}