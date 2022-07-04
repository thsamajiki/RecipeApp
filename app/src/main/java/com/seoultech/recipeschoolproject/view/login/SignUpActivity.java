package com.seoultech.recipeschoolproject.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.authentication.FirebaseAuthentication;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.view.main.MainActivity;
import com.seoultech.recipeschoolproject.vo.UserData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton btnSignUp;
    private TextInputEditText editEmail, editPwd, editNickname;
    private FirebaseAuthentication firebaseAuthentication = FirebaseAuthentication.getInstance();
    private FirebaseData firebaseData = FirebaseData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        btnSignUp = findViewById(R.id.btn_sign_up);
        editEmail = findViewById(R.id.edit_email);
        editPwd = findViewById(R.id.edit_pwd);
        editNickname = findViewById(R.id.edit_nickname);

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_sign_up:
                signUp();
                break;
        }
    }

    private void signUp() {
        String email = editEmail.getText().toString();
        String pwd = editPwd.getText().toString();
        String nickname = editNickname.getText().toString();

        if (!checkEmailValid(email)) {
            Toast.makeText(this, "이메일 양식을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuthentication.setOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if(!isSuccess) {
                    Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadUserData();
            }
        });
        firebaseAuthentication.signUpEmail(this, email, pwd);

    }

    private boolean checkEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void uploadUserData() {
        String nickName = editNickname.getText().toString();
        FirebaseUser firebaseUser = firebaseAuthentication.getCurrentUser();
        UserData userData = new UserData();
        userData.setNickname(nickName);
        userData.setUserKey(firebaseUser.getUid());
        firebaseData.uploadUserData(this, userData, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if(!isSuccess) {
                    Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                intentMain();
            }
        });
    }

    private void intentMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }


}