package com.seoultech.recipeschoolproject.view.post;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.ActivityPostRecipeBinding;
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
import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeListFragment.EXTRA_RECIPE_DATA;

public class PostRecipeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnFileUploadListener {

    private ActivityPostRecipeBinding binding;
    private String photoPath;
    private static final int PERMISSION_REQ_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostRecipeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setOnClickListeners();
        addTextWatcher();
    }

    private void setOnClickListeners() {
        binding.layoutPhoto.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvComplete.setOnClickListener(this);
        binding.ivRecipePhoto.setOnClickListener(this);
    }

    private void addTextWatcher() {
        binding.editContent.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_recipe_photo:
            case R.id.layout_photo:
                if (checkStoragePermission()) {
                    openGallery();
                }
                break;
            case R.id.tv_complete:
                uploadImage();
                break;
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
                        photoPath = RealPathUtil.getRealPath(PostRecipeActivity.this, data.getData());
                        Glide.with(PostRecipeActivity.this).load(photoPath).into(binding.ivRecipePhoto);
                        binding.layoutPhoto.setVisibility(View.GONE);

                        if(binding.editContent.getText().toString().length() > 0) {
                            binding.tvComplete.setEnabled(true);
                        }
                    }
                }
            }
    );

    private void openGallery() {
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
            openGallery();
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
            binding.tvComplete.setEnabled(true);
        } else {
            binding.tvComplete.setEnabled(false);
        }
    }

    private void uploadImage() {
        LoadingProgress.initProgressDialog(this);
        FirebaseStorageApi.getInstance().setOnFileUploadListener(this);
        FirebaseStorageApi.getInstance().uploadImage(FirebaseStorageApi.DEFAULT_IMAGE_PATH, photoPath);
    }

    private void postRecipeData(String downloadUrl) {
        Toast.makeText(this, "업로드 완료", Toast.LENGTH_SHORT).show();

        String nickname = MyInfoUtil.getInstance().getNickname(this);
        String profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(this);
        String recipeContent = binding.editContent.getText().toString();

        RecipeData recipeData = new RecipeData();
        recipeData.setPhotoUrl(downloadUrl);
        recipeData.setContent(binding.editContent.getText().toString());
        recipeData.setPostDate(Timestamp.now());
        recipeData.setRate(0);
        recipeData.setUserName(nickname);
        recipeData.setProfileUrl(profileUrl);
        recipeData.setUserKey(MyInfoUtil.getInstance().getUserKey());
        FirebaseData.getInstance().uploadRecipeData(recipeData, new OnCompleteListener<RecipeData>() {
            @Override
            public void onComplete(boolean isSuccess, Response<RecipeData> response) {
                if (isSuccess) {
                    MyInfoUtil.getInstance().putRecipeContent(PostRecipeActivity.this, recipeContent);
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        MyInfoUtil.getInstance().putRecipeImage(PostRecipeActivity.this, downloadUrl);
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(PostRecipeActivity.this, "업로드에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFileUploadComplete(boolean isSuccess, String downloadUrl) {
        LoadingProgress.dismissProgressDialog();
        if (isSuccess) {
            postRecipeData(downloadUrl);
        } else {
            Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileUploadProgress(float percent) {
        LoadingProgress.setProgress((int) percent);
    }
}