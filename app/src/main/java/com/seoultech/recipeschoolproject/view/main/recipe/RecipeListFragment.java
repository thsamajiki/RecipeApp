package com.seoultech.recipeschoolproject.view.main.recipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.database.FirebaseData;
import com.seoultech.recipeschoolproject.databinding.FragmentRecipeListBinding;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnRatingUploadListener;
import com.seoultech.recipeschoolproject.listener.OnRecyclerItemClickListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.view.post.PostRecipeActivity;
import com.seoultech.recipeschoolproject.vo.RecipeData;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class RecipeListFragment extends Fragment implements View.OnClickListener, OnRecyclerItemClickListener<RecipeData>, OnRatingUploadListener {

    private FragmentRecipeListBinding binding;
    private RecipeListAdapter recipeListAdapter;
    private final List<RecipeData> recipeDataList = new ArrayList<>();
    public static final String EXTRA_RECIPE_DATA = "recipeData";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater);
        View view = binding.getRoot();
        setOnClickListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecipeAdapter();
        downloadRecipeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadRecipeData();
    }

    private void setOnClickListeners() {
        binding.btnPost.setOnClickListener(this);
    }

    private void setRecipeAdapter() {
        recipeListAdapter = new RecipeListAdapter(requireActivity(), recipeDataList);
        recipeListAdapter.setOnRecyclerItemClickListener(this);
        binding.rvRecipeList.setAdapter(recipeListAdapter);
    }

    private void downloadRecipeData() {
        FirebaseData.getInstance().downloadRecipeData(new OnCompleteListener<List<RecipeData>>() {
            @Override
            public void onComplete(boolean isSuccess, Response<List<RecipeData>> response) {
                if (isSuccess && response.isNotEmpty()) {
                    recipeDataList.clear();
                    recipeDataList.addAll(response.getData());
                    recipeListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private final ActivityResultLauncher<Intent>
            postRecipeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK && data != null) {
                        RecipeData recipeData = data.getParcelableExtra(EXTRA_RECIPE_DATA);
                        if (recipeData != null) {
                            recipeDataList.add(0, recipeData);
                            recipeListAdapter.notifyItemInserted(0);
                            binding.rvRecipeList.smoothScrollToPosition(0);
                        }
                    }
                }
            }
    );

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_post:
                Intent intent = new Intent(requireActivity(), PostRecipeActivity.class);
                postRecipeResultLauncher.launch(intent);
                break;
        }
    }

    @Override
    public void onItemClick(int position, View view, RecipeData data) {
        if (view.getId() == R.id.mcv_rating_container) {
            openRatingDialog(data);
        } else {
            onRecipeItemClick(data);
        }
    }

    private void openRatingDialog(RecipeData data) {
        RatingDialog ratingDialog = new RatingDialog(requireActivity());
        ratingDialog.setOnRatingUploadListener(this);
        ratingDialog.setRecipeData(data);
        ratingDialog.show();
    }

    private void onRecipeItemClick(RecipeData data) {
        Intent intent = new Intent(requireActivity(), RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_DATA, data);
        startActivity(intent);
    }

    @Override
    public void onRatingUpload(RecipeData recipeData) {
        int index = recipeDataList.indexOf(recipeData);
        recipeDataList.set(index, recipeData);
        recipeListAdapter.notifyItemChanged(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}