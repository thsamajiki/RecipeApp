package com.seoultech.recipeschoolproject.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.seoultech.recipeschoolproject.FragmentAdaptor;
import com.seoultech.recipeschoolproject.R;
import com.seoultech.recipeschoolproject.view.main.account.AccountFragment;
import com.seoultech.recipeschoolproject.view.main.chat.ChatListFragment;
import com.seoultech.recipeschoolproject.view.main.recipe.RecipeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setFragmentAdaptor();
        setBottomNavClickListener();
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);
        tvTitle = findViewById(R.id.tv_title);
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

    private void setBottomNavClickListener() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_recipe:
                        viewPager.setCurrentItem(0, true);
                        break;
                    case R.id.menu_chat:
                        viewPager.setCurrentItem(1, true);
                        break;
                    case R.id.menu_user:
                        viewPager.setCurrentItem(2, true);
                        break;
                }
                return false;
            }
        });
    }
}