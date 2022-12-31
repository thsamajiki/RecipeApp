package com.seoultech.recipeschoolproject.view.main.recipe;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeListFragment.EXTRA_RECIPE_DATA;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.ActivityEditRecipeBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnFileUploadListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.storage.FirebaseStorageApi;
import com.seoultech.recipeschoolproject.util.LoadingProgress;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.RealPathUtil;
import com.seoultech.recipeschoolproject.vo.RecipeData;

public class EditRecipeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnFileUploadListener, OnCompleteListener<RecipeData> {

    private ActivityEditRecipeBinding binding;
    private String photoPath;
    private static final int PERMISSION_REQ_CODE = 1010;
    private static final int PHOTO_REQ_CODE = 2020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditRecipeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setOnClickListeners();
        addTextWatcher();
    }

    private void setOnClickListeners() {
        binding.btnBack.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);
        binding.ivRecipePhoto.setOnClickListener(this);
    }

    private void addTextWatcher() {
        binding.editContent.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_recipe_photo:
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
            Glide.with(this).load(photoPath).into(binding.ivRecipePhoto);

            if(binding.editContent.getText().toString().length() > 0) {
                binding.btnComplete.setEnabled(true);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0 && !TextUtils.isEmpty(photoPath)) {
            binding.btnComplete.setEnabled(true);
        } else {
            binding.btnComplete.setEnabled(false);
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
            Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();

            String nickname = MyInfoUtil.getInstance().getNickname(this);
            String profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(this);
            RecipeData recipeData = new RecipeData();
            recipeData.setPhotoUrl(downloadUrl);
            recipeData.setContent(binding.editContent.getText().toString());
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
            Toast.makeText(this, "레시피 수정에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}