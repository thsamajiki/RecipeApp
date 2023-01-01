package com.seoultech.recipeschoolproject.view.main.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.ChatData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends BaseAdapter<ChatListAdapter.ViewHolder, ChatData> {

    private Context context;
    private final List<ChatData> chatDataList;
    private RequestManager requestManager;
    private LayoutInflater inflater;
    private final String myUserKey;

    public ChatListAdapter(Context context, List<ChatData> chatDataList) {
        this.context = context;
        this.chatDataList = chatDataList;
        inflater = LayoutInflater.from(context);
        requestManager = Glide.with(context);
        myUserKey = MyInfoUtil.getInstance().getKey();
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
        String otherUserNickname = getOtherUserNickname(chatData.getUserNicknames(), myUserKey);
        String otherUserProfile = getOtherUserProfile(chatData.getUserProfiles(), myUserKey);
        holder.tvUserName.setText(otherUserNickname);

        Collections.sort(chatDataList);

        if (TextUtils.isEmpty(otherUserProfile)) {
            requestManager.load(R.drawable.ic_default_user_profile).into(holder.ivUserProfileImage);
        } else {
            requestManager.load(otherUserProfile).into(holder.ivUserProfileImage);
        }

        holder.tvChat.setText(chatData.getLastMessage().getMessage());
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

        ShapeableImageView ivUserProfileImage;
        TextView tvUserName, tvDate, tvChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserProfileImage = itemView.findViewById(R.id.iv_user_profile_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvChat = itemView.findViewById(R.id.tv_chat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(position, v, chatDataList.get(position));
        }
    }
}