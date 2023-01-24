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

        holder.bind(recipeData);
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

        public void bind(RecipeData recipeItem) {
            if(!TextUtils.isEmpty(recipeItem.getPhotoUrl())) {
                requestManager.load(recipeItem.getPhotoUrl())
                        .into(binding.ivRecipe);
            }

            if(!TextUtils.isEmpty((recipeItem.getProfileUrl()))) {
                requestManager.load(recipeItem.getProfileUrl())
                        .into(binding.ivUserProfileImage);
            } else {
                requestManager.load(R.drawable.ic_default_user_profile)
                        .into((binding.ivUserProfileImage));
            }

            binding.tvUserName.setText(recipeItem.getUserName());
            binding.tvContent.setText(recipeItem.getContent());
            binding.tvDate.setText(TimeUtils.getInstance().convertTimeFormat(recipeItem.getPostDate().toDate(), "yy.MM.dd"));
            binding.ratingBar.setRating(recipeItem.getRate());
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