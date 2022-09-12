package com.seoultech.recipeschoolproject.view.main.account.setting;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.seoultech.recipeschoolproject.R;

public class OpenSourceLicenseDialog {
    private Context context;

    public OpenSourceLicenseDialog(Context context) {
        this.context = context;
    }

    public void getOpenSourceLicenseDialog() {

        new MaterialAlertDialogBuilder(context)
                .setTitle("오픈소스 라이센스")
                .setView(R.layout.dialog_open_source_license)
                .setPositiveButton("닫기", null)
                .create()
                .show();
    }
}
