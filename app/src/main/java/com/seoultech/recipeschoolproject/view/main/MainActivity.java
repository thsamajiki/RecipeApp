package com.seoultech.recipeschoolproject.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.seoultech.recipeschoolproject.FragmentAdapter;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.databinding.ActivityMainBinding;
import com.seoultech.recipeschoolproject.view.main.account.AboutUsDialog;
import com.seoultech.recipeschoolproject.view.main.account.AccountFragment;
import com.seoultech.recipeschoolproject.view.main.account.setting.SettingActivity;
import com.seoultech.recipeschoolproject.view.main.chat.ChatListFragment;
import com.seoultech.recipeschoolproject.view.main.recipe.RecipeListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setFragmentAdapter();
        setOnClickListener();
        setBottomNavClickListener();
    }

    private void setFragmentAdapter() {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        fragmentAdapter.addFragment(new RecipeListFragment());
        fragmentAdapter.addFragment(new ChatListFragment());
        fragmentAdapter.addFragment(new AccountFragment());
        binding.viewPager.setAdapter(fragmentAdapter);
        final String[] titleArr = getResources().getStringArray(R.array.title_array);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNav.getMenu().getItem(position).setChecked(true);
                binding.tvTitle.setText(titleArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void setOnClickListener() {
        binding.ivAccountOptionMenu.setOnClickListener(this);
    }

    private void setBottomNavClickListener() {
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_recipe:
                        binding.viewPager.setCurrentItem(0, true);
                        binding.ivAccountOptionMenu.setVisibility(View.GONE);
                        binding.ivAccountOptionMenu.setClickable(false);
                        break;
                    case R.id.menu_chat:
                        binding.viewPager.setCurrentItem(1, true);
                        binding.ivAccountOptionMenu.setVisibility(View.GONE);
                        break;
                    case R.id.menu_user:
                        binding.viewPager.setCurrentItem(2, true);
                        binding.ivAccountOptionMenu.setVisibility(View.VISIBLE);
                        binding.ivAccountOptionMenu.setClickable(true);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_account_option_menu:
                showAccountOptionMenu();
                break;
        }
    }

    private void showAccountOptionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.ivAccountOptionMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_account_actionbar_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_about_us:
                        showAboutUsDialog();
                        break;
                    case R.id.menu_setting:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void showAboutUsDialog() {
        AboutUsDialog aboutUsDialog = new AboutUsDialog(MainActivity.this);
        aboutUsDialog.getAboutUsDialog();
    }
}