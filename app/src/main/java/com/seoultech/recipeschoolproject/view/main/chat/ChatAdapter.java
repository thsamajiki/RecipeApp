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
import com.seoultech.recipeschoolproject.databinding.ItemChatLeftBinding;
import com.seoultech.recipeschoolproject.databinding.ItemChatRightBinding;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.ChatData;
import com.seoultech.recipeschoolproject.vo.MessageData;

import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends BaseAdapter<RecyclerView.ViewHolder, MessageData> {

    private Context context;
    private final List<MessageData> messageDataList;
    private final LayoutInflater inflater;
    private final RequestManager requestManager;
    private static final int LEFT_TYPE = 0;
    private static final int RIGHT_TYPE = 1;
    private final String myUserKey;
    private final ChatData chatData;

    public ChatAdapter(Context context, List<MessageData> messageDataList, ChatData chatData) {
        this.context = context;
        this.messageDataList = messageDataList;
        inflater = LayoutInflater.from(context);
        requestManager = Glide.with(context);
        myUserKey = MyInfoUtil.getInstance().getUserKey();
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_TYPE) {
            view = inflater.inflate(R.layout.item_chat_right, parent, false);
            return new RightViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_chat_left, parent, false);
            return new LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageData messageData = messageDataList.get(position);
        if (holder instanceof LeftViewHolder) {
            ((LeftViewHolder) holder).bind(messageData);
        } else {
            ((RightViewHolder) holder).bind(messageData);
        }
    }

    private String getOtherUserNickname(HashMap<String, String> userNames, String myUserKey) {
        for (String userKey: userNames.keySet()) {
            if (!myUserKey.equals(userKey)) {
                return userNames.get(userKey);
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
        return messageDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageData messageData = messageDataList.get(position);
        if (myUserKey.equals(messageData.getUserKey())) {
            return RIGHT_TYPE;
        }
        return LEFT_TYPE;
    }

    class RightViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatRightBinding binding;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatRightBinding.bind(itemView);
        }

        public void bind(MessageData messageItem) {
            binding.tvChat.setText(messageItem.getMessage());
            binding.tvDate.setText(TimeUtils.getInstance()
                    .convertTimeFormat(messageItem.getTimestamp().toDate(), "MM.dd"));
        }
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatLeftBinding binding;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemChatLeftBinding.bind(itemView);
        }

        public void bind(MessageData messageItem) {
            String otherUserProfile = getOtherUserProfile(chatData.getUserProfiles(), myUserKey);
            String otherUserNickname = getOtherUserNickname(chatData.getUserNicknames(), myUserKey);
            if (TextUtils.isEmpty(otherUserProfile)) {
                requestManager.load(R.drawable.ic_default_user_profile).into(binding.ivUserProfileImage);
            } else {
                requestManager.load(otherUserProfile).into(binding.ivUserProfileImage);
            }
            binding.tvChat.setText(messageItem.getMessage());
            binding.tvDate.setText(TimeUtils.getInstance()
                    .convertTimeFormat(messageItem.getTimestamp().toDate(), "MM.dd"));
            binding.tvUserName.setText(otherUserNickname);
        }
    }
}