package com.seoultech.recipeschoolproject.view.main.recipe;

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
import com.seoultech.recipeschoolproject.databinding.ItemRecipeBinding;
import com.seoultech.recipeschoolproject.util.TimeUtils;
import com.seoultech.recipeschoolproject.view.BaseAdapter;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter<RecipeListAdapter.RecipeViewHolder, RecipeData> {

    private Context context;
    private final LayoutInflater inflater;
    private final List<RecipeData> recipeDataList;
    private final RequestManager requestManager;

    public RecipeListAdapter(Context context, List<RecipeData> recipeDataList) {
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
                    .into(holder.binding.ivRecipe);
        }

        if(!TextUtils.isEmpty((recipeData.getProfileUrl()))) {
            requestManager.load(recipeData.getProfileUrl())
                    .into(holder.binding.ivUserProfileImage);
        } else {
            requestManager.load(R.drawable.ic_default_user_profile)
                    .into((holder.binding.ivUserProfileImage));
        }

        holder.binding.tvUserName.setText(recipeData.getUserName());
        holder.binding.tvContent.setText(recipeData.getContent());
        holder.binding.tvDate.setText(TimeUtils.getInstance().convertTimeFormat(recipeData.getPostDate().toDate(), "yy.MM.dd"));
        holder.binding.ratingBar.setRating(recipeData.getRate());
    }

    @Override
    public int getItemCount() {
        return recipeDataList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemRecipeBinding binding;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            binding = ItemRecipeBinding.bind(itemView);
            setupListeners();
        }

        private void setupListeners() {
            binding.mcvContainer.setOnClickListener(this);
            binding.mcvRatingContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            getOnRecyclerItemClickListener().onItemClick(getAdapterPosition(), view, recipeDataList.get(position));
        }
    }
}