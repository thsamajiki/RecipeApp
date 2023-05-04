package com.seoultech.recipeschoolproject.util;

import android.content.Context;

import com.seoultech.recipeschoolproject.authentication.FirebaseAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyInfoUtil {   // 나의 정보에 관한 것을 단말기에 저장하거나 단말기로 불러오는 클래스
    private static MyInfoUtil instance;
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_PWD = "pwd";
    public static final String EXTRA_NICKNAME = "nickname";
    public static final String EXTRA_PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String EXTRA_RECIPE_IMAGE = "recipeImageUrl";
    public static final String EXTRA_RECIPE_CONTENT = "recipeContent";

    private MyInfoUtil() {
    }

    public static MyInfoUtil getInstance() {
        if (instance == null) {
            instance = new MyInfoUtil();
        }
        return instance;
    }

    public String getKey() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        return firebaseUser.getUid();
    }

    public String getNickname(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_NICKNAME, "");
    }

    public String getEmail(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_EMAIL, "");
    }

    public String getPwd(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_PWD, "");
    }

    public String getProfileImageUrl(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_PROFILE_IMAGE_URL, "");
    }

    public String getRecipeImage(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_RECIPE_IMAGE, "");
    }

    public String getRecipeContent(Context context) {
        return SharedPreference.getInstance().getValue(context, EXTRA_RECIPE_CONTENT, "");
    }

    public void putNickname(Context context, String nickname) {
        SharedPreference.getInstance().put(context, EXTRA_NICKNAME, nickname);
    }

    public void putEmail(Context context, String email) {
        SharedPreference.getInstance().put(context, EXTRA_EMAIL, email);
    }

    public void putPwd(Context context, String pwd) {
        SharedPreference.getInstance().put(context, EXTRA_PWD, pwd);
    }

    public void putProfileImageUrl(Context context, String profileImageUrl) {
        SharedPreference.getInstance().put(context, EXTRA_PROFILE_IMAGE_URL, profileImageUrl);
    }

    public void putRecipeImage(Context context, String recipeImageUrl) {
        SharedPreference.getInstance().put(context, EXTRA_RECIPE_IMAGE, recipeImageUrl);
    }

    public void putRecipeContent(Context context, String recipeContent) {
        SharedPreference.getInstance().put(context, EXTRA_RECIPE_CONTENT, recipeContent);
    }

    public void signOut(Context context) {
        SharedPreference.getInstance().remove(context);
        FirebaseAuthentication.getInstance().signOut();
    }

    public void dropOut(Context context) {
        SharedPreference.getInstance().remove(context);
    }
}
