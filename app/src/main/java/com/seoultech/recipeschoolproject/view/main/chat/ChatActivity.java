package com.seoultech.recipeschoolproject.view.main.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.ActivityChatBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnMessageListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.vo.ChatData;
import com.seoultech.recipeschoolproject.vo.MessageData;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerItemClickListener<MessageData>, OnMessageListener {

    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private final List<MessageData> messageDataList = new ArrayList<>();
    private ChatData chatData;

    public static final String EXTRA_OTHER_USER_KEY = "otherUserKey";
    public static final String EXTRA_CHAT_DATA = "chatData";
    private ListenerRegistration messageRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setOnClickListeners();

        chatData = getChatData();
        if (chatData != null) {
            initAdapter();
            initMessageRegistration();
        } else {
            checkExistChatData();
        }
    }

    private void checkExistChatData() {
        if (TextUtils.isEmpty(getOtherUserKey())) {
            return;
        }
        FirebaseData firebaseData = FirebaseData.getInstance();
        firebaseData.checkExistChatData(getOtherUserKey(), new OnCompleteListener<ChatData>() {
            @Override
            public void onComplete(boolean isSuccess, Response<ChatData> response) {
                if (isSuccess && response.isNotEmpty()) {
                    chatData = response.getData();
                    initAdapter();
                    initMessageRegistration();
                }
            }
        });
    }

    private void initMessageRegistration() {
        messageRegistration = FirebaseData.getInstance().getMessageList(chatData.getKey(), this);
    }

    private void setOnClickListeners() {
        binding.btnBack.setOnClickListener(this);
        binding.cvSend.setOnClickListener(this);
    }

    private void initAdapter() {
        chatAdapter = new ChatAdapter(this, messageDataList, chatData);
        chatAdapter.setOnRecyclerItemClickListener(this);
        binding.recyclerChat.setAdapter(chatAdapter);
    }

    private ChatData getChatData() {
        return getIntent().getParcelableExtra(EXTRA_CHAT_DATA);
    }

    private String getOtherUserKey() {
        return getIntent().getStringExtra(EXTRA_OTHER_USER_KEY);
    }

    @Override
    public void onItemClick(int position, View view, MessageData data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.cv_send:
                if (chatData == null) {
                    createChatRoom();
                } else {
                    sendMessage();
                }
                break;
        }
    }

    // 이미 메시지가 있느 상태에서 메시지를 보낼 때
    private void sendMessage() {
        String message = binding.editMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.editMessage.setText("");
        FirebaseData firebaseData = FirebaseData.getInstance();
        firebaseData.sendMessage(message, chatData);
    }

    // 첫 메시지를 보낼 때
    private void createChatRoom() {
        FirebaseData firebaseData = FirebaseData.getInstance();
        String message = binding.editMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.editMessage.setText("");
        firebaseData.createChatRoom(this, getOtherUserKey(), message, new OnCompleteListener<ChatData>() {
            @Override
            public void onComplete(boolean isSuccess, Response<ChatData> response) {
                if (isSuccess && response.isNotEmpty()) {
                    chatData = response.getData();
                    initAdapter();
                    initMessageRegistration();
                }
            }
        });
    }

    @Override
    public void onMessage(boolean isSuccess, MessageData messageData) {
        if (isSuccess && messageData != null) {
            messageDataList.add(messageData);
            chatAdapter.notifyItemInserted(messageDataList.size() - 1);
            binding.recyclerChat.smoothScrollToPosition(messageDataList.size() - 1);
        }
    }
}