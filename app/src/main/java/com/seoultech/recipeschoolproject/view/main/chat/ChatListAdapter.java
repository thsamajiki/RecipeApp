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
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.ChatData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends BaseAdapter<ChatListAdapter.ViewHolder, ChatData> {

    private Context context;
    private ArrayList<ChatData> chatDataArrayList;
    private RequestManager requestManager;
    private LayoutInflater inflater;
    private String myUserKey;

    public ChatListAdapter(Context context, ArrayList<ChatData> chatDataArrayList) {
        this.context = context;
        this.chatDataArrayList = chatDataArrayList;
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
        ChatData chatData = chatDataArrayList.get(position);
        String otherUserNickname = getOtherUserNickname(chatData.getUserNicknames(), myUserKey);
        String otherUserProfile = getOtherUserProfile(chatData.getUserProfiles(), myUserKey);
        holder.tvUserNickname.setText(otherUserNickname);

        Collections.sort(chatDataArrayList);

        if (TextUtils.isEmpty(otherUserProfile)) {
            requestManager.load(R.drawable.ic_default_user_profile).into(holder.ivProfile);
        } else {
            requestManager.load(otherUserProfile).into(holder.ivProfile);
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
        return chatDataArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView ivProfile;
        TextView tvUserNickname, tvDate, tvChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvUserNickname = itemView.findViewById(R.id.tv_user_nickname);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvChat = itemView.findViewById(R.id.tv_chat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(position, v, chatDataArrayList.get(position));
        }
    }
}