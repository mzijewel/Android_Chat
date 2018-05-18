package com.example.nayan.chatappupdated.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.Friends;
import com.example.nayan.chatappupdated.tools.StaticConfig;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dev on 2/4/2018.
 */

public class FriendRequiestActivity extends AppCompatActivity {
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendReqDatabase2;
    private FirebaseUser mCurrent_user;
    public Bitmap bitmapAvataUser;
    private RecyclerView mFriendsList;
    private DatabaseReference mUsersDatabase;
    private ArrayList<String> listFriendID = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_req);
        listFriendID = new ArrayList<>();
        mFriendsList = (RecyclerView) findViewById(R.id.recFriend);
        mFriendsList.setHasFixedSize(true);
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mFriendsList.setLayoutManager(new LinearLayoutManager(FriendRequiestActivity.this));
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid());
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mUsersDatabase.keepSynced(true);
        mFriendReqDatabase.keepSynced(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendReqDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {
                final String list_user_id = getRef(i).getKey();
                FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid()).child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String req_type = dataSnapshot.child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mFriendsList.setVisibility(View.VISIBLE);
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumb = dataSnapshot.child("avata").getValue().toString();
                                        final String email = dataSnapshot.child("email").getValue().toString();


                                        if (!userThumb.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                            byte[] decodedString = Base64.decode(userThumb, Base64.DEFAULT);
                                            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        } else {
                                            bitmapAvataUser = null;
                                        }

                                        if (bitmapAvataUser != null) {
                                            friendsViewHolder.setUserImage(bitmapAvataUser, FriendRequiestActivity.this);
                                        }

                                        friendsViewHolder.setName(userName);
                                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                profileIntent.putExtra("email", email);
                                                startActivity(profileIntent);
                                                finish();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                mFriendsList.setVisibility(View.GONE);
                            }
                            HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                            Iterator listKey = mapRecord.keySet().iterator();
                            while (listKey.hasNext()) {
                                String key = listKey.next().toString();
                                listFriendID.add(mapRecord.get(key).toString());


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                friendsViewHolder.setDate(friends.getDate());


            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(Bitmap bitmapAvataUser, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            userImageView.setImageBitmap(bitmapAvataUser);
//            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avata).into(userImageView);

        }
    }

}
