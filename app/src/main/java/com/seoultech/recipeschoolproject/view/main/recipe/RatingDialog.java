package com.seoultech.recipeschoolproject.view.main.recipe;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.DialogRatingBarBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnRatingUploadListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.util.BaseDialog;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.vo.RateData;
import com.seoultech.recipeschoolproject.vo.RecipeData;
import com.google.firebase.Timestamp;

public class RatingDialog extends BaseDialog implements View.OnClickListener {

    private DialogRatingBarBinding binding;
    private RecipeData recipeData;

    private OnRatingUploadListener onRatingUploadListener;

    public void setOnRatingUploadListener(OnRatingUploadListener onRatingUploadListener) {
        this.onRatingUploadListener = onRatingUploadListener;
    }

    public RatingDialog(Context context) {
        super(context);
    }

    public void setRecipeData(RecipeData recipeData) {
        this.recipeData = recipeData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogRatingBarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.tvCancel.setOnClickListener(this);
        binding.tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                uploadRating();
                break;
        }
    }

    private void uploadRating() {
        float rating = binding.ratingBar.getRating();

        if (rating == 0) {
            Toast.makeText(getContext(), "평점을 매겨주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        RateData rateData = makeRateData(rating);
        FirebaseData.getInstance().uploadRating(recipeData, rateData, new OnCompleteListener<RecipeData>() {
            @Override
            public void onComplete(boolean isSuccess, Response<RecipeData> response) {
                if (isSuccess) {
                    Toast.makeText(getContext(), "평가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    onRatingUploadListener.onRatingUpload(response.getData());
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "평가가 실패하였습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private RateData makeRateData(float rate) {
        String userKey = MyInfoUtil.getInstance().getUserKey();
        String userNickname = MyInfoUtil.getInstance().getNickname(getContext());
        String profileUrl = MyInfoUtil.getInstance().getProfileImageUrl(getContext());
        RateData rateData = new RateData();
        rateData.setDate(Timestamp.now());
        rateData.setProfileUrl(profileUrl);
        rateData.setUserKey(userKey);
        rateData.setKey(userKey);
        rateData.setUserNickname(userNickname);
        rateData.setRate(rate);

        return rateData;
    }
}
