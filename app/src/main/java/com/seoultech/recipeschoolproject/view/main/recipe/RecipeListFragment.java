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
    private RecipeAdapter recipeAdapter;
    private final List<RecipeData> recipeDataList = new ArrayList<>();
    private static final int POST_REQ_CODE = 333;
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

    private void setOnClickListeners() {
        binding.btnPost.setOnClickListener(this);
    }

    private void setRecipeAdapter() {
        recipeAdapter = new RecipeAdapter(requireActivity(), recipeDataList);
        recipeAdapter.setOnRecyclerItemClickListener(this);
        binding.recyclerRecipe.setAdapter(recipeAdapter);
    }

    private void downloadRecipeData() {
        FirebaseData.getInstance().downloadRecipeData(new OnCompleteListener<ArrayList<RecipeData>>() {
            @Override
            public void onComplete(boolean isSuccess, Response<ArrayList<RecipeData>> response) {
                if (isSuccess && response.isNotEmpty()) {
                    recipeDataList.clear();
                    recipeDataList.addAll(response.getData());
                    recipeAdapter.notifyDataSetChanged();
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

                    if(resultCode == RESULT_OK && data != null) {
                        RecipeData recipeData = data.getParcelableExtra(EXTRA_RECIPE_DATA);
                        if (recipeData != null) {
                            recipeDataList.add(0, recipeData);
                            recipeAdapter.notifyItemInserted(0);
                            binding.recyclerRecipe.smoothScrollToPosition(0);
                        }
                    }
                }
            }
    );

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_post:
                Intent intent = new Intent(requireActivity(), PostRecipeActivity.class);
                postRecipeResultLauncher.launch(intent);
                break;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == POST_REQ_CODE && resultCode == RESULT_OK && data != null) {
//            RecipeData recipeData = data.getParcelableExtra(EXTRA_RECIPE_DATA);
//            if (recipeData != null) {
//                recipeDataList.add(0, recipeData);
//                recipeAdapter.notifyItemInserted(0);
//                binding.recyclerRecipe.smoothScrollToPosition(0);
//            }
//        }
//    }

//    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Log.d(TAG, "MainActivity로 돌아왔다. ");
//                    }
//                }
//            });

    @Override
    public void onItemClick(int position, View view, RecipeData data) {
        if (view.getId() == R.id.cv_rating_container) {
            RatingDialog ratingDialog = new RatingDialog(requireActivity());
            ratingDialog.setOnRatingUploadListener(this);
            ratingDialog.setRecipeData(data);
            ratingDialog.show();
        } else {
            Intent intent = new Intent(requireActivity(), RecipeDetailActivity.class);
            intent.putExtra(EXTRA_RECIPE_DATA, data);
            startActivity(intent);
        }
    }

    @Override
    public void onRatingUpload(RecipeData recipeData) {
        int index = recipeDataList.indexOf(recipeData);
        recipeDataList.set(index, recipeData);
        recipeAdapter.notifyItemChanged(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}