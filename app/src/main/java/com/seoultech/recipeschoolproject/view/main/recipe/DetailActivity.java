package com.seoultech.recipeschoolproject.view.main.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.login.SignUpActivity;
import com.seoultech.recipeschoolproject.view.main.account.ProfileEditActivity;
import com.seoultech.recipeschoolproject.view.main.chat.ChatActivity;
import com.seoultech.recipeschoolproject.view.photoview.PhotoActivity;
import com.seoultech.recipeschoolproject.vo.RecipeData;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.seoultech.recipeschoolproject.view.main.chat.ChatActivity.EXTRA_OTHER_USER_KEY;
import static com.seoultech.recipeschoolproject.view.main.recipe.RecipeFragment.EXTRA_RECIPE_DATA;
import static com.seoultech.recipeschoolproject.view.photoview.PhotoActivity.EXTRA_PHOTO_URL;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDate, tvContent, tvUserNickname;
    private ImageView ivRecipe, btnBack, ivOptionMenu;
    private CircleImageView ivProfile;
    private RatingBar ratingBar;
    private MaterialButton btnQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        setData();
    }

    private void initView() {
        tvDate = findViewById(R.id.tv_date);
        tvContent = findViewById(R.id.tv_content);
        tvUserNickname = findViewById(R.id.tv_user_nickname);
        ivRecipe = findViewById(R.id.iv_recipe);
        ivOptionMenu = findViewById(R.id.iv_option_menu);
        ivProfile = findViewById(R.id.iv_profile);
        ratingBar = findViewById(R.id.rating_bar);
        btnBack = findViewById(R.id.btn_back);
        btnQuestion = findViewById(R.id.btn_question);
        ivProfile.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivRecipe.setOnClickListener(this);
        ivOptionMenu.setOnClickListener(this);
    }

    private RecipeData getRecipeData() {
        return getIntent().getParcelableExtra(EXTRA_RECIPE_DATA);
    }

    private void setData() {
        RecipeData recipeData = getRecipeData();
        RequestManager requestManager = Glide.with(this);
        if(!TextUtils.isEmpty(recipeData.getPhotoUrl())) {
            requestManager.load(recipeData.getPhotoUrl())
                    .into(ivRecipe);
        }

        if(!TextUtils.isEmpty((recipeData.getProfileUrl()))) {
            requestManager.load(recipeData.getProfileUrl())
                    .into(ivProfile);
        } else {
            requestManager.load(R.drawable.ic_default_user_profile)
                    .into((ivProfile));
        }

        tvUserNickname.setText(recipeData.getUserNickname());
        tvContent.setText(recipeData.getContent());
        tvDate.setText(TimeUtils.getInstance().convertTimeFormat(recipeData.getPostDate().toDate(), "yy.MM.dd"));
        ratingBar.setRating(recipeData.getRate());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.iv_recipe:
                intentPhoto(getRecipeData().getPhotoUrl());
                break;
            case R.id.iv_profile:
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
                    ivOptionMenu.setVisibility(View.VISIBLE);
                    showRecipeDetailOptionMenu();
                }
                break;
        }
        finish();
    }

    private void showRecipeDetailOptionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, ivOptionMenu);
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
        Intent intent = new Intent(this, SignUpActivity.class);
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