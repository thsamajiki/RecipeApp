package com.seoultech.recipeschoolproject.view.main.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.databinding.FragmentAccountBinding;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.view.login.LoginActivity;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private FragmentAccountBinding binding;
    private static final int PROFILE_EDIT_REQ = 1010;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater);
        View view = binding.getRoot();
        setOnClickListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUserData();
    }

    private void setOnClickListeners() {
        binding.btnEditProfile.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
    }

    private void setUserData() {
        String profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(requireActivity());

        if (TextUtils.isEmpty(profileUrl)) {
            Glide.with(requireActivity()).load(R.drawable.ic_user).into(binding.ivUserProfileImage);
        } else {
            Glide.with(requireActivity()).load(profileUrl).into(binding.ivUserProfileImage);
        }

        String userName = MyInfoUtil.getInstance().getNickname(requireActivity());
        binding.tvUserName.setText(userName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_profile:
                intentEditProfile();
                break;
            case R.id.btn_logout:
                showLogoutDialog();
                break;
        }
    }

    private final ActivityResultLauncher<Intent>
            editProfileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK) {
                        setUserData();
                    }
                }
            });

    private void intentEditProfile() {
        Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
//        startActivityForResult(intent, PROFILE_EDIT_REQ);
        editProfileResultLauncher.launch(intent);
    }

    private void showLogoutDialog() {
        String logout_message = "로그아웃하시겠습니까?";
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("로그아웃")
                .setMessage(logout_message)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton("아니오", null)
                .create()
                .show();
    }

    private void signOut() {
        MyInfoUtil.getInstance().signOut(requireActivity());
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finishAffinity();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PROFILE_EDIT_REQ && resultCode == RESULT_OK) {
//            setUserData();
//        }
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_account_actionbar_option, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}