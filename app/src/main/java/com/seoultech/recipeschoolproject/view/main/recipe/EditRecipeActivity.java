package com.seoultech.recipeschoolproject.view.main.recipe;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeListFragment.EXTRA_RECIPE_DATA;

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
import com.bumptech.glide.RequestManager;
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
import com.seoultech.recipeschoolproject.view.main.account.EditProfileActivity;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import java.util.HashMap;

public class EditRecipeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnFileUploadListener {

    private ActivityEditRecipeBinding binding;
    private String photoPath;
    private static final int PERMISSION_REQ_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditRecipeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setRecipeData();
        setOnClickListeners();
        addTextWatcher();
    }

    private void setOnClickListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.tvComplete.setOnClickListener(this);
        binding.ivRecipePhoto.setOnClickListener(this);
    }

    private RecipeData getRecipeData() {
        return getIntent().getParcelableExtra(EXTRA_RECIPE_DATA);
    }

    private void setRecipeData() {
        RecipeData recipeData = getRecipeData();
        RequestManager requestManager = Glide.with(this);
        if(!TextUtils.isEmpty(recipeData.getPhotoUrl())) {
            requestManager.load(recipeData.getPhotoUrl())
                    .into(binding.ivRecipePhoto);
            binding.btnPhoto.setVisibility(View.INVISIBLE);
        }

        binding.editContent.setText(recipeData.getContent());
    }

    private void addTextWatcher() {
        binding.editContent.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_recipe_photo:
                if (checkStoragePermission()) {
                    openGallery();
                }
                break;
            case R.id.iv_back:
                finish();
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
                        photoPath = RealPathUtil.getRealPath(EditRecipeActivity.this, data.getData());
                        Glide.with(EditRecipeActivity.this).load(photoPath).into(binding.ivRecipePhoto);

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

    private void uploadImage() {
        LoadingProgress.initProgressDialog(this);
        FirebaseStorageApi.getInstance().setOnFileUploadListener(this);
        FirebaseStorageApi.getInstance().uploadImage(FirebaseStorageApi.DEFAULT_IMAGE_PATH, photoPath);
    }

    private void modifyRecipeData(String downloadUrl) {
        Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();

        HashMap<String, Object> editData = new HashMap<>();
        if (!TextUtils.isEmpty(downloadUrl)) {
            editData.put(MyInfoUtil.EXTRA_RECIPE_IMAGE, downloadUrl);
        }

        String newRecipeContent = binding.editContent.getText().toString();
        editData.put(MyInfoUtil.EXTRA_RECIPE_CONTENT, newRecipeContent);
        String recipeKey = getRecipeData().getKey();
        FirebaseData.getInstance().modifyRecipeData(recipeKey, editData, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if (isSuccess) {
                    MyInfoUtil.getInstance().putRecipeContent(EditRecipeActivity.this, newRecipeContent);
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        MyInfoUtil.getInstance().putRecipeImage(EditRecipeActivity.this, downloadUrl);
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditRecipeActivity.this, "레시피 수정에 실패했습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFileUploadComplete(boolean isSuccess, String downloadUrl) {
        LoadingProgress.dismissProgressDialog();
        if (isSuccess) {
            modifyRecipeData(downloadUrl);
        } else {
            Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileUploadProgress(float percent) {
        LoadingProgress.setProgress((int) percent);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        binding.tvComplete.setEnabled(s.length() > 0 && !TextUtils.isEmpty(photoPath));
    }
}