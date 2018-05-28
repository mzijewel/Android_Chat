package com.example.nayan.chatappupdated.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.fragment.FriendsFragmentNew;
import com.example.nayan.chatappupdated.fragment.UserProfileFragmentNew;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 1/22/2018.
 */

public class TabActivity extends AppCompatActivity {

    public static String STR_FRIEND_FRAGMENT = "FRIEND";
    public static String STR_GROUP_FRAGMENT = "GROUP";
    public static String STR_INFO_FRAGMENT = "INFO";
    private static String TAG = "TabActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout = null;
    private ViewPagerAdapter adapter;

    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private String mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity);


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("ChatApp");
        }

        viewPager = findViewById(R.id.viewpager);
        initTab();
        initFirebase();

    }


    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivityNew.class));
            finish();
        } else {
            online();
        }


    }

    private void online() {
        mUserId = mAuth.getCurrentUser().getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("online", true);
        mFirestore.collection("Users").document(mUserId).update(userMap);
    }

    private void offline() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("online", false);

            mFirestore.collection("Users").document(mUserId).update(userMap);

        }
    }

    @Override
    protected void onDestroy() {
        offline();
        super.onDestroy();
    }

    private void initTab() {
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_person,
                R.drawable.ic_tab_infor
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FriendsFragmentNew(), STR_FRIEND_FRAGMENT);

        adapter.addFrag(new UserProfileFragmentNew(), STR_INFO_FRAGMENT);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.main_settings_btn) {

            Intent settingsIntent = new Intent(TabActivity.this, FriendRequiestActivity.class);
            startActivity(settingsIntent);

        } else if (item.getItemId() == R.id.main_all_btn) {

            Intent settingsIntent = new Intent(TabActivity.this, UserActivityNew.class);
            startActivity(settingsIntent);

        } else if (item.getItemId() == R.id.main_logout_btn) {
            mAuth.signOut();
            offline();
            Intent settingsIntent = new Intent(TabActivity.this, LoginActivityNew.class);
            startActivity(settingsIntent);
        }

        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
    }
}
