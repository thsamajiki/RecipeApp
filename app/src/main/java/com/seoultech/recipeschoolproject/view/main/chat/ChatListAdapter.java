package com.seoultech.recipeschoolproject.view.main.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.databinding.ItemChatListBinding;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.ChatData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends BaseAdapter<ChatListAdapter.ViewHolder, ChatData> {

    private Context context;
    private final List<ChatData> chatDataList;
    private final RequestManager requestManager;
    private final LayoutInflater inflater;
    private final String myUserKey;

    public ChatListAdapter(Context context, List<ChatData> chatDataList) {
        this.context = context;
        this.chatDataList = chatDataList;
        inflater = LayoutInflater.from(context);
        requestManager = Glide.with(context);
        myUserKey = MyInfoUtil.getInstance().getUserKey();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatData chatData = chatDataList.get(position);

        Collections.sort(chatDataList);

        holder.bind(chatData);
    }

    private String getOtherUserNickname(HashMap<String, String> userNicknames, String myUserKey) {
        for (String userKey: userNicknames.keySet()) {
            if (!myUserKey.equals(userKey)) {
                return userNicknames.get(userKey);
            }
        }

        return null;
    }

    private String getOtherUserProfile(HashMap<String, String> userProfiles, String myUserKey) {
        for (String userKey: userProfiles.keySet()) {
            if (!myUserKey.equals(userKey)) {
                return userProfiles.get(userKey);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return chatDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemChatListBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatListBinding.bind(itemView);
            binding.layoutChat.setOnClickListener(this);
        }

        public void bind(ChatData chatItem) {
            String otherUserNickname = getOtherUserNickname(chatItem.getUserNicknames(), myUserKey);
            String otherUserProfile = getOtherUserProfile(chatItem.getUserProfiles(), myUserKey);
            binding.tvUserName.setText(otherUserNickname);

            if (TextUtils.isEmpty(otherUserProfile)) {
                requestManager.load(R.drawable.ic_default_user_profile).into(binding.ivUserProfileImage);
            } else {
                requestManager.load(otherUserProfile).into(binding.ivUserProfileImage);
            }

            binding.tvChat.setText(chatItem.getLastMessage().getMessage());

            binding.tvDate.setText(TimeUtils.getInstance()
                    .convertTimeFormat(chatItem.getLastMessage().getTimestamp().toDate(), "YY.MM.dd"));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(position, view, chatDataList.get(position));
        }
    }
}