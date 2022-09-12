package com.seoultech.recipeschoolproject.view.main.account;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.seoultech.recipeschoolproject.BuildConfig;
import com.seoultech.recipeschoolproject.R;

public class AboutUsDialog {
    private Context context;

    public AboutUsDialog(Context context) {
        this.context = context;
    }

    public void getAboutUsDialog() {
        //TextView tvVersion = findViewById(R.id.tv_version);
        String version = BuildConfig.VERSION_NAME;
        //tvVersion.setText(version);

        new MaterialAlertDialogBuilder(context)
                .setTitle("앱의 정보")
                .setView(R.layout.dialog_about_us)
                .setPositiveButton("닫기", null)
                .create()
                .show();
    }
}
