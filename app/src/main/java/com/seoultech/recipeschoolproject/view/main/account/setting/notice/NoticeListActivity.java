package com.seoultech.recipeschoolproject.view.main.account.setting.notice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnNoticeListChangeListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.vo.NoticeData;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import java.util.ArrayList;

public class NoticeListActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerItemClickListener<NoticeData> {

    private ImageView btnBack, ivToggleArrow;
    private LinearLayout llContentNoticeItem;
    private ArrayList<NoticeData> noticeDataList = new ArrayList<>();
    private RecyclerView rvNoticeList;
    private NoticeListAdapter noticeListAdapter;
    private NoticeData noticeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        initView();
        initNoticeListRecyclerViewAdapter();
        downloadNoticeData();
        setOnClickListener();
    }

    private void initView() {
        btnBack = findViewById(R.id.iv_back);
        ivToggleArrow = findViewById(R.id.iv_toggle_arrow);
        llContentNoticeItem = findViewById(R.id.ll_content_notice_item);
        rvNoticeList = findViewById(R.id.rv_notice_list);
    }

    private void initNoticeListRecyclerViewAdapter() {
        noticeListAdapter = new NoticeListAdapter(this, noticeDataList);
        noticeListAdapter.setOnRecyclerItemClickListener(this);
        rvNoticeList.setAdapter(noticeListAdapter);
    }

    private void downloadNoticeData() {
        FirebaseData.getInstance().getNoticeList(new OnCompleteListener<ArrayList<NoticeData>>() {
            @Override
            public void onComplete(boolean isSuccess, Response<ArrayList<NoticeData>> response) {
                if (isSuccess && response.isNotEmpty()) {
                    noticeDataList.clear();
                    noticeDataList.addAll(response.getData());
                    noticeListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setOnClickListener() {
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(int position, View view, NoticeData data) {

    }
}