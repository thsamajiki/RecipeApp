package com.seoultech.recipeschoolproject.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.authentication.FirebaseAuthentication;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.view.main.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void> {

    private MaterialButton btnLogin, btnSignUp;
    private TextInputEditText editEmail, editPwd;
    private FirebaseAuthentication firebaseAuthentication = FirebaseAuthentication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setClickListener();
        firebaseAuthentication.setOnCompleteListener(this);
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        editEmail = findViewById(R.id.edit_email);
        editPwd = findViewById(R.id.edit_pwd);

    }

    private void setClickListener() {
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        String email = editEmail.getText().toString();
        String pwd = editPwd.getText().toString();

        if (!checkEmailValid(email)) {
            Toast.makeText(this, "이메일 양식을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuthentication.login(this, email, pwd);
    }

    private boolean checkEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onComplete(boolean isSuccess, Response<Void> response) {
        if(isSuccess) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}