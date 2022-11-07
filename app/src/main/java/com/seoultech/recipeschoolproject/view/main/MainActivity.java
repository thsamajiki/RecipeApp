package com.seoultech.recipeschoolproject.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoultech.recipeschoolproject.FragmentAdaptor;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.view.main.account.AboutUsDialog;
import com.seoultech.recipeschoolproject.view.main.account.AccountFragment;
import com.seoultech.recipeschoolproject.view.main.account.setting.SettingActivity;
import com.seoultech.recipeschoolproject.view.main.chat.ChatListFragment;
import com.seoultech.recipeschoolproject.view.main.recipe.RecipeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private TextView tvTitle;
    private ImageView ivAccountOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setFragmentAdaptor();
        setOnClickListener();
        setBottomNavClickListener();
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);
        tvTitle = findViewById(R.id.tv_title);
        ivAccountOptionMenu = findViewById(R.id.iv_account_option_menu);
    }

    private void setFragmentAdaptor() {
        FragmentAdaptor fragmentAdaptor = new FragmentAdaptor(getSupportFragmentManager(), getLifecycle());
        fragmentAdaptor.addFragment(new RecipeFragment());
        fragmentAdaptor.addFragment(new ChatListFragment());
        fragmentAdaptor.addFragment(new AccountFragment());
        viewPager.setAdapter(fragmentAdaptor);
        final String[] titleArr = getResources().getStringArray(R.array.title_array);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNav.getMenu().getItem(position).setChecked(true);
                tvTitle.setText(titleArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void setOnClickListener() {
        ivAccountOptionMenu.setOnClickListener(this);
    }

    private void setBottomNavClickListener() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_recipe:
                        viewPager.setCurrentItem(0, true);
                        ivAccountOptionMenu.setVisibility(View.GONE);
                        ivAccountOptionMenu.setClickable(false);
                        break;
                    case R.id.menu_chat:
                        viewPager.setCurrentItem(1, true);
                        ivAccountOptionMenu.setVisibility(View.GONE);
                        break;
                    case R.id.menu_user:
                        viewPager.setCurrentItem(2, true);
                        ivAccountOptionMenu.setVisibility(View.VISIBLE);
                        ivAccountOptionMenu.setClickable(true);
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
        PopupMenu popupMenu = new PopupMenu(this, ivAccountOptionMenu);
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