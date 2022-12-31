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
import com.seoultech.recipeschoolproject.databinding.ActivityNoticeListBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnNoticeListChangeListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.vo.NoticeData;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import java.util.ArrayList;
import java.util.List;

public class NoticeListActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerItemClickListener<NoticeData> {

    private ActivityNoticeListBinding binding;
    private final List<NoticeData> noticeDataList = new ArrayList<>();
    private NoticeListAdapter noticeListAdapter;
    private NoticeData noticeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initNoticeListRecyclerViewAdapter();
        downloadNoticeData();
//        getNoticeListFromDatabase();
        setOnClickListener();
    }

    private void initNoticeListRecyclerViewAdapter() {
        noticeListAdapter = new NoticeListAdapter(this, noticeDataList);
        noticeListAdapter.setOnRecyclerItemClickListener(this);
        binding.rvNoticeList.setAdapter(noticeListAdapter);
    }

//    private void getNoticeListFromDatabase() {
//        NoticeRepository noticeRepository = new NoticeRepository(this);
//        noticeRepository.getNoticeList(new OnCompleteListener<ArrayList<NoticeData>>() {
//            @Override
//            public void onComplete(boolean isSuccess, ArrayList<NoticeData> data) {
//                if (isSuccess && data != null) {
//                    noticeDataList.addAll(data);
//                    noticeListAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(NoticeListActivity.this, "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void downloadNoticeData() {
        FirebaseData.getInstance().getNoticeList(new OnCompleteListener<List<NoticeData>>() {
            @Override
            public void onComplete(boolean isSuccess, Response<List<NoticeData>> response) {
                if (isSuccess && response.isNotEmpty()) {
                    noticeDataList.clear();
                    noticeDataList.addAll(response.getData());
                    noticeListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setOnClickListener() {
        binding.ivBack.setOnClickListener(this);
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