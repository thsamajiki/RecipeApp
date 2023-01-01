package com.seoultech.recipeschoolproject.view.main.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.databinding.ActivityRecipeDetailBinding;
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
        setData();
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

    private void setData() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_recipe:
                intentPhoto(getRecipeData().getPhotoUrl());
                break;
            case R.id.iv_user_profile_image:
                intentPhoto(getRecipeData().getProfileUrl());
                break;
            case R.id.btn_question:
                String myUserKey = MyInfoUtil.getInstance().getKey();

                if (getRecipeData().getUserKey().equals(myUserKey)) {
                    Toast.makeText(this, "나와의 대화는 불가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(EXTRA_OTHER_USER_KEY, getRecipeData().getUserKey());
                startActivity(intent);
                break;
            case R.id.iv_option_menu:
                myUserKey = MyInfoUtil.getInstance().getKey();
                if (getRecipeData().getUserKey().equals(myUserKey)) {
                    binding.ivOptionMenu.setVisibility(View.VISIBLE);
                    binding.ivOptionMenu.setClickable(true);
                    showRecipeDetailOptionMenu();
                }
                break;
        }
        finish();
    }

    private void showRecipeDetailOptionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.ivOptionMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_recipe_detail_actionbar_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_recipe_detail_modify:
                        intentModifyRecipe();
                        break;
                    case R.id.menu_recipe_detail_delete:
                        deleteRecipeData();
                        break;
                }
                return true;
            }
        });
    }

    private void intentModifyRecipe() {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        startActivity(intent);
    }

    private void deleteRecipeData() {
//        HashMap<String, Object> editData = new HashMap<>();
//        if (!TextUtils.isEmpty(newProfileUrl)) {
//            editData.put(MyInfoUtil.EXTRA_PROFILE_URL, newProfileUrl);
//        }
    }

    private void intentPhoto(String photoUrl) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO_URL, photoUrl);
        startActivity(intent);
    }
}