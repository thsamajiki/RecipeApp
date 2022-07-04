package com.seoultech.recipeschoolproject.view.main.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.view.login.LoginActivity;
import com.google.android.material.button.MaterialButton;

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
        String profileUrl = MyInfoUtil.getInstance().getProfileUrl(requireActivity());

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
                signOut();
                break;
        }
    }

    private void intentProfileEdit() {
        Intent intent = new Intent(requireActivity(), ProfileEditActivity.class);
        startActivityForResult(intent, PROFILE_EDIT_REQ);
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
}