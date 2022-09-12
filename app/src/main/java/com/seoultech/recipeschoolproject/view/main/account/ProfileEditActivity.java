package com.seoultech.recipeschoolproject.view.main.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnFileUploadListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.storage.FirebaseStorageApi;
import com.seoultech.recipeschoolproject.util.LoadingProgress;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.RealPathUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener, OnFileUploadListener, TextWatcher {

    private ImageView ivBack;
    private TextView tvComplete;
    private TextInputEditText editUserNickname;
    private CircleImageView ivProfile;
    private FloatingActionButton fabProfileEdit;
    private String photoPath;
    private String profileUrl;
    private String userNickname;
    private static final int PERMISSION_REQ_CODE = 1010;
    private static final int PHOTO_REQ_CODE = 2020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        initView();
        setUserData();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        tvComplete = findViewById(R.id.tv_complete);
        editUserNickname = findViewById(R.id.edit_user_nickname);
        ivProfile = findViewById(R.id.iv_profile);
        fabProfileEdit = findViewById(R.id.fab_profile_edit);
        ivBack.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        fabProfileEdit.setOnClickListener(this);
        editUserNickname.addTextChangedListener(this);
    }

    private void setUserData() {
        profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(this);

        if (TextUtils.isEmpty(profileUrl)) {
            Glide.with(this).load(R.drawable.ic_user).into(ivProfile);
        } else {
            Glide.with(this).load(profileUrl).into(ivProfile);
        }
        userNickname = MyInfoUtil.getInstance().getNickname(this);
        editUserNickname.setText(userNickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                if (isNewProfile()) {
                    uploadProfileImage();
                } else {
                    updateUserData(null);
                }
                break;
            case R.id.fab_profile_edit:
                if (checkStoragePermission()) {
                    intentGallery();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private boolean isNewProfile() {
        if (TextUtils.isEmpty(profileUrl)) {
            return !TextUtils.isEmpty(photoPath);
        } else {
            if (TextUtils.isEmpty(photoPath)) {
                return false;
            } else {
                return !profileUrl.equals(photoPath);
            }
        }
    }

    private void intentGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent,PHOTO_REQ_CODE);
    }

    private boolean checkStoragePermission() {
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ActivityCompat.checkSelfPermission(this, readPermission)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, writePermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{readPermission, writePermission},
                    PERMISSION_REQ_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            intentGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQ_CODE && resultCode == RESULT_OK && data != null) {
            photoPath = RealPathUtil.getRealPath(this, data.getData());
            Glide.with(this).load(photoPath).into(ivProfile);
            if(editUserNickname.getText().toString().length() > 0) {
                tvComplete.setEnabled(true);
            }
        }
    }

    private void uploadProfileImage() {
        LoadingProgress.initProgressDialog(this);
        FirebaseStorageApi.getInstance().setOnFileUploadListener(this);
        FirebaseStorageApi.getInstance().uploadImage(FirebaseStorageApi.DEFAULT_IMAGE_PATH, photoPath);
    }

    private void updateUserData(String newProfileUrl) {
        HashMap<String, Object> editData = new HashMap<>();
        if (!TextUtils.isEmpty(newProfileUrl)) {
            editData.put(MyInfoUtil.EXTRA_PROFILE_IMAGE_URL, newProfileUrl);
        }
        final String newUserNickname = editUserNickname.getText().toString();
        editData.put(MyInfoUtil.EXTRA_NICKNAME, newUserNickname);
        String userKey = MyInfoUtil.getInstance().getKey();
        FirebaseData.getInstance().updateUserData(userKey, editData, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if (isSuccess) {
                    MyInfoUtil.getInstance().putNickname(ProfileEditActivity.this, newUserNickname);
                    if (!TextUtils.isEmpty(newProfileUrl)) {
                        MyInfoUtil.getInstance().putProfileImageUrl(ProfileEditActivity.this, newProfileUrl);
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ProfileEditActivity.this, "유저 정보 변경에 실패했습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFileUploadComplete(boolean isSuccess, String downloadUrl) {
        LoadingProgress.dismissProgressDialog();
        if (isSuccess) {
            updateUserData(downloadUrl);
        } else {
            Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileUploadProgress(float percent) {
        LoadingProgress.setProgress((int) percent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            tvComplete.setEnabled(false);
        } else {
            if (s.toString().equals(userNickname)) {
                tvComplete.setEnabled(false);
            } else {
                tvComplete.setEnabled(true);
            }
        }
    }
}