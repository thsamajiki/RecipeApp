package com.seoultech.recipeschoolproject.view.main.recipe;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.ActivityRecipeDetailBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.main.chat.ChatActivity;
import com.seoultech.recipeschoolproject.view.photoview.PhotoActivity;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import static com.seoultech.recipeschoolproject.view.main.chat.ChatActivity.EXTRA_OTHER_USER_KEY;
import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeListFragment.EXTRA_RECIPE_DATA;
import static com.seoultech.recipeschoolproject.view.photoview.PhotoActivity.EXTRA_PHOTO_URL;

public class RecipeDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRecipeDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setOnClickListener();
        setRecipeData();
        checkUploader();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setRecipeData();
//        setModifiedRecipeData();
    }

    private void setOnClickListener() {
        binding.ivUserProfileImage.setOnClickListener(this);
        binding.btnQuestion.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivRecipe.setOnClickListener(this);
        binding.ivOptionMenu.setOnClickListener(this);
    }

    private RecipeData getRecipeData() {
        return getIntent().getParcelableExtra(EXTRA_RECIPE_DATA);
    }

    private void setRecipeData() {
        RecipeData recipeData = getRecipeData();
        RequestManager requestManager = Glide.with(this);
        if(!TextUtils.isEmpty(recipeData.getPhotoUrl())) {
            requestManager.load(recipeData.getPhotoUrl())
                    .into(binding.ivRecipe);
        }

        if(!TextUtils.isEmpty((recipeData.getProfileUrl()))) {
            requestManager.load(recipeData.getProfileUrl())
                    .into(binding.ivUserProfileImage);
        } else {
            requestManager.load(R.drawable.ic_default_user_profile)
                    .into((binding.ivUserProfileImage));
        }

        binding.tvUserName.setText(recipeData.getUserName());
        binding.tvContent.setText(recipeData.getContent());
        binding.tvDate.setText(TimeUtils.getInstance().convertTimeFormat(recipeData.getPostDate().toDate(), "yy.MM.dd"));
        binding.ratingBar.setRating(recipeData.getRate());
    }

    private void checkUploader() {
        String myUserKey = MyInfoUtil.getInstance().getUserKey();
        Log.d("onOptionMenuClick", "onOptionMenuClick - myUserKey: " + myUserKey);
        Log.d("onOptionMenuClick", "onOptionMenuClick - check_myUserKey: " + getRecipeData().getUserKey().equals(myUserKey));
        if (getRecipeData().getUserKey().equals(myUserKey)) {
            binding.ivOptionMenu.setVisibility(View.VISIBLE);
            binding.ivOptionMenu.setClickable(true);
            Log.d("onOptionMenuClick", "onOptionMenu - visibility: " + binding.ivOptionMenu.getVisibility());
            Log.d("onOptionMenuClick", "onOptionMenu - clickable: " + binding.ivOptionMenu.isClickable());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_recipe:
                onPhotoClick(getRecipeData().getPhotoUrl());
                break;
            case R.id.iv_user_profile_image:
                onPhotoClick(getRecipeData().getProfileUrl());
                break;
            case R.id.btn_question:
                onQuestionButtonClick();
                break;
            case R.id.iv_option_menu:
                showRecipeDetailOptionMenu();
                break;
        }
    }

    private void onQuestionButtonClick() {
        String myUserKey = MyInfoUtil.getInstance().getUserKey();

        if (getRecipeData().getUserKey().equals(myUserKey)) {
            Toast.makeText(this, "나와의 대화는 불가능합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_OTHER_USER_KEY, getRecipeData().getUserKey());
        startActivity(intent);
    }

    private void showRecipeDetailOptionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.ivOptionMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_recipe_detail_actionbar_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_recipe_detail_modify:
                        onModifyRecipeMenuClick();
                        break;
                    case R.id.menu_recipe_detail_delete:
                        openDeleteRecipePopUp();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void setModifiedRecipeData() {
        final RecipeData[] recipeData = {getRecipeData()};
        FirebaseData.getInstance().getRecipe(recipeData[0].getKey(), new OnCompleteListener<RecipeData>() {
            @Override
            public void onComplete(boolean isSuccess, Response<RecipeData> response) {
                if (isSuccess && response.isNotEmpty()) {
                    recipeData[0] = response.getData();
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "레시피를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String recipeImage = MyInfoUtil.getInstance().getRecipeImage(this);
        String recipeContent = MyInfoUtil.getInstance().getRecipeContent(this);

        Log.d("setModifiedRecipeData", "recipeImage: " + recipeImage + " recipeContent: " + recipeContent);

        if(TextUtils.isEmpty(recipeImage)) {
            Glide.with(this).load(R.drawable.sample_feed_image).into(binding.ivRecipe);
        } else {
            Glide.with(this).load(recipeImage).into(binding.ivRecipe);
        }

        binding.tvContent.setText(recipeContent);
    }

    private final ActivityResultLauncher<Intent>
            modifyRecipeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK && data != null) {
                        setModifiedRecipeData();
                    }
                }
            }
    );

    private void onModifyRecipeMenuClick() {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_DATA, getRecipeData());
        modifyRecipeResultLauncher.launch(intent);
    }

    private void openDeleteRecipePopUp() {
        String title = "레시피 삭제";
        String message = "레시피를 삭제하시겠습니까?";
        String positiveText = "예";
        String negativeText = "아니오";

        new MaterialAlertDialogBuilder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDeleteRecipeMenuClick();
                    }
                })
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void onDeleteRecipeMenuClick() {
        FirebaseData.getInstance().deleteRecipeData(getRecipeData().getKey(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean isSuccess, Response<Void> response) {
                if (isSuccess) {
                    Toast.makeText(RecipeDetailActivity.this, "레시피를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "레시피 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onPhotoClick(String photoUrl) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO_URL, photoUrl);
        startActivity(intent);
    }
}