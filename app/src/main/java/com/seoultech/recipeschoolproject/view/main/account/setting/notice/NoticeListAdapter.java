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
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.NoticeData;

import java.util.ArrayList;

public class NoticeListAdapter extends BaseAdapter<NoticeListAdapter.NoticeViewHolder, NoticeData> implements View.OnClickListener {

    private Context context;
    private ArrayList<NoticeData> noticeDataList;
    private RequestManager requestManager;


    public NoticeListAdapter(Context context, ArrayList<NoticeData> noticeDataList) {
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

        holder.tvDateNoticeItem.setText(noticeData.getNoticeDate());
        holder.tvTitleNoticeItem.setText(noticeData.getNoticeTitle());
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

        private TextView tvDateNoticeItem, tvTitleNoticeItem, tvContentNoticeItem;
        private MaterialCardView mcvNoticeItem;
        private LinearLayout llContentNoticeItem;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            tvDateNoticeItem = itemView.findViewById(R.id.tv_date_notice_item);
            tvTitleNoticeItem = itemView.findViewById(R.id.tv_title_notice_item);
            llContentNoticeItem = itemView.findViewById(R.id.ll_content_notice_item);
            mcvNoticeItem = itemView.findViewById(R.id.mcv_notice_item);
            mcvNoticeItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(position, view, noticeDataList.get(position));

            view = itemView.findViewById(R.id.iv_toggle_arrow);
            if (llContentNoticeItem.getVisibility() == View.GONE) {
                rotateView(view);
                llContentNoticeItem.setVisibility(View.VISIBLE);
            } else {
                rotateView(view);
                llContentNoticeItem.setVisibility(View.GONE);
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