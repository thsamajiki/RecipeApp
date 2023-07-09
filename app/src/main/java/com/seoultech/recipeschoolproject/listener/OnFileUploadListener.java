package com.seoultech.recipeschoolproject.listener;

public interface OnFileUploadListener {
    void onFileUploadComplete(boolean isSuccess, String downloadUrl);
    void onFileUploadProgress(float percent);
}