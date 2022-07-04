package com.seoultech.recipeschoolproject.listener;

import com.seoultech.recipeschoolproject.vo.MessageData;

public interface OnMessageListener {
    void onMessage(boolean isSuccess, MessageData messageData);
}
