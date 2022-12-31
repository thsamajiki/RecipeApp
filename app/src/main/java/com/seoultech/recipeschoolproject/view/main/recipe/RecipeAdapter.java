package com.seoultech.recipeschoolproject.view.main.recipe;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.RecipeData;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeAdapter extends BaseAdapter<RecipeAdapter.RecipeViewHolder, RecipeData> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecipeData> recipeDataList;
    private RequestManager requestManager;

    public RecipeAdapter(Context context, List<RecipeData> recipeDataList) {
        this.context = context;
        this.recipeDataList = recipeDataList;
        this.requestManager = Glide.with(context);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeData recipeData = recipeDataList.get(position);
        if(!TextUtils.isEmpty(recipeData.getPhotoUrl())) {
            requestManager.load(recipeData.getPhotoUrl())
                    .into(holder.ivRecipe);
        }

        if(!TextUtils.isEmpty((recipeData.getProfileUrl()))) {
            requestManager.load(recipeData.getProfileUrl())
                    .into(holder.ivProfile);
        } else {
            requestManager.load(R.drawable.ic_default_user_profile)
                    .into((holder.ivProfile));
        }

        holder.tvUserNickname.setText(recipeData.getUserNickname());
        holder.tvContent.setText(recipeData.getContent());
        holder.tvDate.setText(TimeUtils.getInstance().convertTimeFormat(recipeData.getPostDate().toDate(), "yy.MM.dd"));
        holder.ratingBar.setRating(recipeData.getRate());
    }

    @Override
    public int getItemCount() {
        return recipeDataList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate, tvContent, tvUserNickname;
        ImageView ivRecipe;
        CircleImageView ivProfile;
        RatingBar ratingBar;
        MaterialCardView cvContainer, cvRatingContainer;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvUserNickname = itemView.findViewById(R.id.tv_user_nickname);
            ivRecipe = itemView.findViewById(R.id.iv_recipe);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            cvContainer = itemView.findViewById(R.id.cv_container);
            cvRatingContainer = itemView.findViewById(R.id.cv_rating_container);
            cvContainer.setOnClickListener(this);
            cvRatingContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(getAdapterPosition(), v, recipeDataList.get(position));
        }
    }
}