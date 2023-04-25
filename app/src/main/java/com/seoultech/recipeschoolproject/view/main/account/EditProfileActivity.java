package com.seoultech.recipeschoolproject.view.main.account;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.seoultech.recipeschoolproject.databinding.ActivityEditProfileBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnFileUploadListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.storage.FirebaseStorageApi;
import com.seoultech.recipeschoolproject.util.LoadingProgress;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.RealPathUtil;

import java.util.HashMap;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, OnFileUploadListener, TextWatcher {

    private ActivityEditProfileBinding binding;
    private String photoPath;
    private String profileUrl;
    private String userNickname;
    private static final int PERMISSION_REQ_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setOnClickListeners();
        setUserData();
    }

    private void setOnClickListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.tvComplete.setOnClickListener(this);
        binding.fabProfileEdit.setOnClickListener(this);
        binding.editUserNickname.addTextChangedListener(this);
    }

    private void setUserData() {
        profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(this);

        if (TextUtils.isEmpty(profileUrl)) {
            Glide.with(this).load(R.drawable.ic_user).into(binding.ivUserProfileImage);
        } else {
            Glide.with(this).load(profileUrl).into(binding.ivUserProfileImage);
        }
        userNickname = MyInfoUtil.getInstance().getNickname(this);
        binding.editUserNickname.setText(userNickname);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

    private final ActivityResultLauncher<Intent>
        openGalleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK && data != null) {
                        photoPath = RealPathUtil.getRealPath(EditProfileActivity.this, data.getData());
                        Glide.with(EditProfileActivity.this).load(photoPath).into(binding.ivUserProfileImage);
                        if(binding.editUserNickname.getText().toString().length() > 0) {
                            binding.tvComplete.setEnabled(true);
                        }
                    }
                }
            }
    );

    private void intentGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(EXTERNAL_CONTENT_URI, "image/*");
        openGalleryResultLauncher.launch(pickIntent);
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
        final String newUserNickname = binding.editUserNickname.getText().toString();
        editData.put(MyInfoUtil.EXTRA_NICKNAME, newUserNickname);
        String userKey = MyInfoUtil.getInstance().getKey();
        FirebaseData.getInstance().updateUserData(userKey, editData, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if (isSuccess) {
                    MyInfoUtil.getInstance().putNickname(EditProfileActivity.this, newUserNickname);
                    if (!TextUtils.isEmpty(newProfileUrl)) {
                        MyInfoUtil.getInstance().putProfileImageUrl(EditProfileActivity.this, newProfileUrl);
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "유저 정보 변경에 실패했습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
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
            binding.tvComplete.setEnabled(false);
        } else {
            if (s.toString().equals(userNickname)) {
                binding.tvComplete.setEnabled(false);
            } else {
                binding.tvComplete.setEnabled(true);
            }
        }
    }
}