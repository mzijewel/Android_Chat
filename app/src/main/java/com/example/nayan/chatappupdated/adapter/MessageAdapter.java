package com.example.nayan.chatappupdated.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.activity.ChatActivityNew;
import com.example.nayan.chatappupdated.model.MessageNew2;
import com.example.nayan.chatappupdated.tools.MainApplication;
import com.example.nayan.chatappupdated.tools.StaticConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dev on 1/31/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<MessageNew2> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    public Bitmap bitmapAvataUser;

    public MessageAdapter(List<MessageNew2> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, messageText2, time, time2;
        public CircleImageView profileImage, profileImage2;
        public TextView displayName, displayName2;
        public ImageView messageImage, messageImage2;
        public RelativeLayout relFriend, relUser;

        public MessageViewHolder(View view) {
            super(view);
            relFriend = (RelativeLayout) view.findViewById(R.id.relFriend);
            relUser = (RelativeLayout) view.findViewById(R.id.relUser);
            time = (TextView) view.findViewById(R.id.time_text_layout);
            time2 = (TextView) view.findViewById(R.id.time_text_layout2);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageText2 = (TextView) view.findViewById(R.id.message_text_layout2);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            profileImage2 = (CircleImageView) view.findViewById(R.id.message_profile_layout2);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            displayName2 = (TextView) view.findViewById(R.id.name_text_layout2);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            messageImage2 = (ImageView) view.findViewById(R.id.message_image_layout2);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        final MessageNew2 c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(MainApplication.getInstance().getContext());
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("avatar").getValue().toString();
                Log.e("cun " + name, " fun " + ChatActivityNew.userName);

                if (name.equals(ChatActivityNew.userName)) {

                    if (!image.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    } else {
                        bitmapAvataUser = null;
                    }

                    if (bitmapAvataUser != null) {
                        viewHolder.profileImage.setImageBitmap(bitmapAvataUser);
                    } else {
                        viewHolder.profileImage.setImageResource(R.drawable.default_avatar);
                    }
                    if (c.getImage() != null) {
//                        Bitmap bitmapAvataUser;
//                        byte[] decodedString = Base64.decode(c.getImage(), Base64.DEFAULT);
//                        bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        viewHolder.messageImage.setImageBitmap(bitmapAvataUser);
                        Picasso.with(viewHolder.profileImage.getContext()).load(c.getImage())
                                .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);
                        viewHolder.messageImage.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.messageImage.setVisibility(View.GONE);
                    }
                    viewHolder.displayName.setText(name);
                    viewHolder.time.setText(dateFormat.format(c.getTime()));
                    viewHolder.messageText.setText(c.getMessage());
                    viewHolder.relUser.setVisibility(View.GONE);
                    viewHolder.relFriend.setVisibility(View.VISIBLE);
                    viewHolder.messageText.setTextColor(Color.RED);
                } else {

                    if (!image.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    } else {
                        bitmapAvataUser = null;
                    }

                    if (bitmapAvataUser != null) {
                        viewHolder.profileImage2.setImageBitmap(bitmapAvataUser);
                    } else {
                        viewHolder.profileImage.setImageResource(R.drawable.default_avatar);
                    }
                    if (c.getImage() != null) {
//                        Bitmap bitmapAvataUser;
//                        byte[] decodedString = Base64.decode(c.getImage(), Base64.DEFAULT);
//                        bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        viewHolder.messageImage2.setImageBitmap(bitmapAvataUser);
                        Picasso.with(viewHolder.profileImage.getContext()).load(c.getImage())
                                .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage2);
                        viewHolder.messageImage2.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.messageImage2.setVisibility(View.GONE);
                    }
                    viewHolder.time2.setText(dateFormat.format(c.getTime()));
                    viewHolder.displayName2.setText(name);
                    viewHolder.messageText2.setText(c.getMessage());
                    viewHolder.relUser.setVisibility(View.VISIBLE);
                    viewHolder.relFriend.setVisibility(View.GONE);
                    viewHolder.messageText2.setTextColor(Color.YELLOW);
                }

//                if (!image.equals(StaticConfig.STR_DEFAULT_BASE64)) {
//                    byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
//                    bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                } else {
//                    bitmapAvataUser = null;
//                }
//
//                if (bitmapAvataUser != null) {
//                    viewHolder.profileImage.setImageBitmap(bitmapAvataUser);
//                }
//                Picasso.with(viewHolder.profileImage.getContext()).load(image)
//                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        if (message_type.equals("text")) {
//
//            viewHolder.messageText.setText(c.getMessage());
//            viewHolder.messageImage.setVisibility(View.INVISIBLE);
//
//
//        } else {
//
//            viewHolder.messageText.setVisibility(View.INVISIBLE);
//            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
//                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);
//
//        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
