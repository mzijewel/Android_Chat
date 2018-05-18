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
import com.example.nayan.chatappupdated.service.ServiceUtils;
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("ChatApp");
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
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
        userMap.put("online", "true");
        mFirestore.collection("Users").document(mUserId).update(userMap);
    }

    private void offline() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("online", "false");

            mFirestore.collection("Users").document(mUserId).update(userMap);

        }
    }

    @Override
    protected void onDestroy() {
        offline();
        super.onDestroy();
    }

    private void initTab() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_person,
//                R.drawable.ic_tab_group,
                R.drawable.ic_tab_infor
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FriendsFragmentNew(), STR_FRIEND_FRAGMENT);
//        adapter.addFrag(new ChatsFragment(), "chat");
//        adapter.addFrag(new GroupFragment(), STR_GROUP_FRAGMENT);
        adapter.addFrag(new UserProfileFragmentNew(), STR_INFO_FRAGMENT);
//        floatButton.setOnClickListener(((FriendsFragment) adapter.getItem(0)).onClickFloatButton.getInstance(this));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ServiceUtils.stopServiceFriendChat(TabActivity.this.getApplicationContext(), false);
//                if (adapter.getItem(position) instanceof FriendsFragment) {
//                    floatButton.setVisibility(View.VISIBLE);
//                    floatButton.setOnClickListener(((FriendsFragment) adapter.getItem(position)).onClickFloatButton.getInstance(TabActivity.this));
//                    floatButton.setImageResource(R.drawable.plus);
//                }
////                else if (adapter.getItem(position) instanceof GroupFragment) {
////                    floatButton.setVisibility(View.VISIBLE);
////                    floatButton.setOnClickListener(((GroupFragment) adapter.getItem(position)).onClickFloatButton.getInstance(MainActivity.this));
////                    floatButton.setImageResource(R.drawable.ic_float_add_group);
////                }
//                else {
//                    floatButton.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
//            if (data.getStringExtra(STR_EXTRA_ACTION).equals(LoginActivity.STR_EXTRA_ACTION_LOGIN)) {
//                authUtils.signIn(data.getStringExtra(STR_EXTRA_USERNAME), data.getStringExtra(STR_EXTRA_PASSWORD));
//            } else if (data.getStringExtra(STR_EXTRA_ACTION).equals(RegisterActivity.STR_EXTRA_ACTION_REGISTER)) {
//                authUtils.createUser(data.getStringExtra(STR_EXTRA_USERNAME), data.getStringExtra(STR_EXTRA_PASSWORD));
//            }else if(data.getStringExtra(STR_EXTRA_ACTION).equals(LoginActivity.STR_EXTRA_ACTION_RESET)){
//                authUtils.resetPassword(data.getStringExtra(STR_EXTRA_USERNAME));
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            this.finish();
//        }
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.about) {
//            Toast.makeText(this, "ChatApp version 1.0", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


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

        }

        if (item.getItemId() == R.id.main_all_btn) {

            Intent settingsIntent = new Intent(TabActivity.this, UserActivityNew.class);
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
