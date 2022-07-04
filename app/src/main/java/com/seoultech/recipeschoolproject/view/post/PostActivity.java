package com.seoultech.recipeschoolproject.view.post;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.seoultech.recipeschoolproject.vo.RecipeData;
import com.google.firebase.Timestamp;


import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeFragment.EXTRA_RECIPE_DATA;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnFileUploadListener, OnCompleteListener<RecipeData> {

    private ImageView btnBack, ivRecipePhoto;
    private EditText editContent;
    private TextView btnComplete;
    private LinearLayout btnPhoto;
    private String photoPath;
    private static final int PERMISSION_REQ_CODE = 1010;
    private static final int PHOTO_REQ_CODE = 2020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initView();
        addTextWatcher();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        ivRecipePhoto = findViewById(R.id.iv_recipe_photo);
        editContent = findViewById(R.id.edit_content);
        btnComplete = findViewById(R.id.btn_complete);
        btnPhoto = findViewById(R.id.btn_photo);

        btnPhoto.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        ivRecipePhoto.setOnClickListener(this);
    }

    private void addTextWatcher() {
        editContent.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_recipe_photo:
            case R.id.btn_photo:
                if (checkStoragePermission()) {
                    intentGallery();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_complete:
                uploadImage();
                break;
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
            Glide.with(this).load(photoPath).into(ivRecipePhoto);
            btnPhoto.setVisibility(View.GONE);

            if(editContent.getText().toString().length() > 0) {
                btnComplete.setEnabled(true);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0 && !TextUtils.isEmpty(photoPath)) {
            btnComplete.setEnabled(true);
        } else {
            btnComplete.setEnabled(false);
        }
    }

    private void uploadImage() {
        LoadingProgress.initProgressDialog(this);
        FirebaseStorageApi.getInstance().setOnFileUploadListener(this);
        FirebaseStorageApi.getInstance().uploadImage(FirebaseStorageApi.DEFAULT_IMAGE_PATH, photoPath);
    }

    @Override
    public void onFileUploadComplete(boolean isSuccess, String downloadUrl) {
        if (isSuccess) {
            Toast.makeText(this, "업로드 완료", Toast.LENGTH_SHORT).show();

            String nickname = MyInfoUtil.getInstance().getNickname(this);
            String profileUrl = MyInfoUtil.getInstance().getProfileUrl(this);
            RecipeData recipeData = new RecipeData();
            recipeData.setPhotoUrl(downloadUrl);
            recipeData.setContent(editContent.getText().toString());
            recipeData.setPostDate(Timestamp.now());
            recipeData.setRate(0);
            recipeData.setUserNickname(nickname);
            recipeData.setProfileUrl(profileUrl);
            recipeData.setUserKey(MyInfoUtil.getInstance().getKey());
            FirebaseData.getInstance().uploadRecipeData(recipeData, this);
        }
    }

    @Override
    public void onFileUploadProgress(float percent) {
        LoadingProgress.setProgress((int) percent);
    }

    @Override
    public void onComplete(boolean isSuccess, Response<RecipeData> response) {
        LoadingProgress.dismissProgressDialog();
        if (isSuccess) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RECIPE_DATA, response.getData());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "업로드에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}