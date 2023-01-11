package com.seoultech.recipeschoolproject.view.main.account.setting.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.material.card.MaterialCardView;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.databinding.ItemNoticeListBinding;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.NoticeData;

import java.util.ArrayList;
import java.util.List;

public class NoticeListAdapter extends BaseAdapter<NoticeListAdapter.NoticeViewHolder, NoticeData> implements View.OnClickListener {

    private final Context context;
    private final List<NoticeData> noticeDataList;
    private RequestManager requestManager;


    public NoticeListAdapter(Context context, List<NoticeData> noticeDataList) {
        this.context = context;
        this.noticeDataList = noticeDataList;
    }

    @NonNull
    @Override
    public NoticeListAdapter.NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_notice_list, parent,false);
        return new NoticeListAdapter.NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeData noticeData = noticeDataList.get(position);

        holder.binding.tvDateNoticeItem.setText(noticeData.getNoticeDate());
        holder.binding.tvTitleNoticeItem.setText(noticeData.getNoticeTitle());
//        holder.tvContentNoticeItem.setText(noticeData.getNoticeDesc());
    }

    @Override
    public int getItemCount() {
        return noticeDataList.size();
    }

    @Override
    public void onClick(View view) {

    }

    class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemNoticeListBinding binding;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNoticeListBinding.bind(itemView);

            binding.mcvNoticeItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(position, view, noticeDataList.get(position));

            view = itemView.findViewById(R.id.iv_toggle_arrow);
            if (binding.llContentNoticeItem.getVisibility() == View.GONE) {
                rotateView(view);
                binding.llContentNoticeItem.setVisibility(View.VISIBLE);
            } else {
                rotateView(view);
                binding.llContentNoticeItem.setVisibility(View.GONE);
            }
        }

        private void rotateView(View view) {
            int angle = 0;
            if (view.getRotation() != -180) {
                angle = -180;
            }
            view.animate().rotation(angle).setDuration(200).start();
        }
    }
}