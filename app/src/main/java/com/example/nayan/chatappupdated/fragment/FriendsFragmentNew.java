package com.example.nayan.chatappupdated.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.activity.ChatActivityNew;
import com.example.nayan.chatappupdated.model.Friends;
import com.example.nayan.chatappupdated.tools.StaticConfig;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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
 * Created by Dev on 1/31/2018.
 */

public class FriendsFragmentNew extends Fragment {
    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public Bitmap bitmapAvataUser;


    private ArrayList<String> listFriendID = null;

    private int c;

    public FriendsFragmentNew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        listFriendID = new ArrayList<>();
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

//        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(StaticConfig.UID);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, final int k) {
                FirebaseDatabase.getInstance().getReference().child("Friends/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                            Iterator listKey = mapRecord.keySet().iterator();
                            while (listKey.hasNext()) {
                                String key = listKey.next().toString();
                                c++;
                                if (!listFriendID.contains(mapRecord.get(key).toString()))
                                    listFriendID.add(mapRecord.get(key).toString());
                                Log.e("frnd", " size int " + c);
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends_size").child(StaticConfig.UID);
                                HashMap<String, Integer> userMap = new HashMap<>();
                                userMap.put("name", listFriendID.size());

                                mDatabase.setValue(userMap);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(k).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            //email not found

                        } else {
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userThumb = dataSnapshot.child("avatar").getValue().toString();
                            final String email = dataSnapshot.child("email").getValue().toString();

                            if (dataSnapshot.hasChild("online")) {

                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                friendsViewHolder.setUserOnline(userOnline);

                            }

                            if (!userThumb.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                byte[] decodedString = Base64.decode(userThumb, Base64.DEFAULT);
                                bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            } else {
                                bitmapAvataUser = null;
                            }

                            if (bitmapAvataUser != null) {
                                friendsViewHolder.setUserImage(bitmapAvataUser, getContext());
                            }

                            friendsViewHolder.setName(userName);


                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent chatIntent = new Intent(getContext(), ChatActivityNew.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", userName);
                                    chatIntent.putExtra("email", email);
                                    ChatActivityNew.userName = userName;
                                    startActivity(chatIntent);

//                                    CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
//
//                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                                    builder.setTitle("Select Options");
//                                    builder.setItems(options, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            //Click Event for each item.
////                                            if (i == 0) {
////
////                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
////                                                profileIntent.putExtra("user_id", list_user_id);
////                                                profileIntent.putExtra("email", email);
////                                                startActivity(profileIntent);
////
////                                            }
//
////                                            if (i == 1) {
////                                            Intent chatIntent = new Intent(getContext(), ChatActivityNew.class);
////                                            chatIntent.putExtra("user_id", list_user_id);
////                                            chatIntent.putExtra("user_name", userName);
////                                            chatIntent.putExtra("email", email);
////                                            ChatActivityNew.userName = userName;
////                                            startActivity(chatIntent);
//
//
////                                            }
//
//                                        }
//                                    });

//                                    builder.show();
//
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }
}
