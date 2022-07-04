package com.seoultech.recipeschoolproject.listener;

public interface OnCompleteListener<T> {
    void onComplete(boolean isSuccess, Response<T> response);
}