package com.seoultech.recipeschoolproject.view.main.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.listener.OnChatListChangeListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.vo.ChatData;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;

import static com.seoultech.recipeschoolproject.view.main.chat.ChatActivity.EXTRA_CHAT_DATA;

public class ChatListFragment extends Fragment implements OnChatListChangeListener, OnRecyclerItemClickListener<ChatData> {

    private ListenerRegistration chatListRegistration;
    private RecyclerView chatListRecycler;
    private String userKey;
    private ArrayList<ChatData> chatDataArrayList = new ArrayList<>();
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userKey = MyInfoUtil.getInstance().getKey();
        chatListRegistration = FirebaseData.getInstance().getChatList(userKey, this);
        initChatListAdapter();
    }

    private void initView(View view) {
        chatListRecycler = view.findViewById(R.id.recycler_chat_list);
    }

    private void initChatListAdapter() {
        chatListAdapter = new ChatListAdapter(requireActivity(), chatDataArrayList);
        chatListAdapter.setOnRecyclerItemClickListener(this);
        chatListRecycler.setAdapter(chatListAdapter);
    }

    @Override
    public void onChatListChange(DocumentChange.Type changeType, ChatData chatData) {
        switch (changeType) {
            case ADDED:
                chatDataArrayList.add(0, chatData);
                Collections.sort(chatDataArrayList);
                chatListAdapter.notifyDataSetChanged();
                break;
            case MODIFIED:
                chatDataArrayList.remove(chatData);
                chatDataArrayList.add(0, chatData);
                chatListAdapter.notifyDataSetChanged();
                break;
            case REMOVED:
                // TODO: 채팅방 삭제 혹은 차단 기능들 만들면 배열 삭제하고 갱신처리 하면 된다. 추후 업데이트 내용
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatListRegistration != null) {
            chatListRegistration.remove();
        }
    }

    @Override
    public void onItemClick(int position, View view, ChatData data) {
        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        intent.putExtra(EXTRA_CHAT_DATA, data);
        startActivity(intent);
    }
}