package com.example.nayan.chatappupdated.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 1/31/2018.
 */

public class UserActivityNew extends AppCompatActivity {
    public Bitmap bitmapAvataUser;
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private UserAdapter adapter;
    private List<User> users;
    private FirebaseFirestore mFirestore;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mFirestore = FirebaseFirestore.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
//
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mUsersDatabase.keepSynced(true);
        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        users = new ArrayList<>();
        adapter = new UserAdapter(this, users);
        mUsersList.setAdapter(adapter);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        prepareDis();

    }

    private void prepareDis() {

        mFirestore.collection("Users").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    User user = doc.getDocument().toObject(User.class);
                    if (user.userId.equals(mUserId)) continue;

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        users.add(user);
                        adapter.notifyDataSetChanged();
                    } else if (doc.getType() == DocumentChange.Type.MODIFIED) {

                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).tokenId.equals(user.tokenId)) {
                                users.set(i, user);
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }

                    }
                }

            }
        });
    }


}
