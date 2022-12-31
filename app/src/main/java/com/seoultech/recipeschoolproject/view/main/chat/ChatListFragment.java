package com.seoultech.recipeschoolproject.view.main.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.FragmentChatListBinding;
import com.seoultech.recipeschoolproject.listener.OnChatListChangeListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.vo.ChatData;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.seoultech.recipeschoolproject.view.main.chat.ChatActivity.EXTRA_CHAT_DATA;

public class ChatListFragment extends Fragment implements OnChatListChangeListener, OnRecyclerItemClickListener<ChatData> {

    private ListenerRegistration chatListRegistration;
    private FragmentChatListBinding binding;
    private String userKey;
    private final List<ChatData> chatDataList = new ArrayList<>();
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userKey = MyInfoUtil.getInstance().getKey();
        chatListRegistration = FirebaseData.getInstance().getChatList(userKey, this);
        initChatListAdapter();
    }

    private void initChatListAdapter() {
        chatListAdapter = new ChatListAdapter(requireActivity(), chatDataList);
        chatListAdapter.setOnRecyclerItemClickListener(this);
        binding.recyclerChatList.setAdapter(chatListAdapter);
    }

    @Override
    public void onChatListChange(DocumentChange.Type changeType, ChatData chatData) {
        switch (changeType) {
            case ADDED:
                chatDataList.add(0, chatData);
                Collections.sort(chatDataList);
                chatListAdapter.notifyDataSetChanged();
                break;
            case MODIFIED:
                chatDataList.remove(chatData);
                chatDataList.add(0, chatData);
                chatListAdapter.notifyDataSetChanged();
                break;
            case REMOVED:
                // TODO: 채팅방 삭제 혹은 차단 기능들 만들면 배열 삭제하고 갱신처리 하면 된다. 추후 업데이트 내용
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
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