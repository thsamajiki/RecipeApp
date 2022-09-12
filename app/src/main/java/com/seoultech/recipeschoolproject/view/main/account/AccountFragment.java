package com.seoultech.recipeschoolproject.view.main.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.view.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.seoultech.recipeschoolproject.view.main.MainActivity;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private ImageView ivProfile;
    private TextView tvUserNickname;
    private MaterialButton btnProfileEdit, btnLogout;
    private static final int PROFILE_EDIT_REQ = 1010;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUserData();
    }

    private void initView(View view) {
        ivProfile = view.findViewById(R.id.iv_profile);
        tvUserNickname = view.findViewById(R.id.tv_user_nickname);
        btnProfileEdit = view.findViewById(R.id.btn_profile_edit);
        btnLogout = view.findViewById(R.id.btn_logout);

        btnProfileEdit.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    private void setUserData() {
        String profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(requireActivity());

        if (TextUtils.isEmpty(profileUrl)) {
            Glide.with(requireActivity()).load(R.drawable.ic_user).into(ivProfile);
        } else {
            Glide.with(requireActivity()).load(profileUrl).into(ivProfile);
        }

        String userNickname = MyInfoUtil.getInstance().getNickname(requireActivity());
        tvUserNickname.setText(userNickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_profile_edit:
                intentProfileEdit();
                break;
            case R.id.btn_logout:
                showLogoutDialog();
                break;
        }
    }

    private void intentProfileEdit() {
        Intent intent = new Intent(requireActivity(), ProfileEditActivity.class);
        startActivityForResult(intent, PROFILE_EDIT_REQ);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_EDIT_REQ && resultCode == RESULT_OK) {
            setUserData();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_account_actionbar_option, menu);
    }
}