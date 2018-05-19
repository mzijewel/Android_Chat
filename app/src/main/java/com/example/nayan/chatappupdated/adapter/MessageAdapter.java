package com.example.nayan.chatappupdated.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.MessageNew2;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dev on 1/31/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    public Bitmap bitmapAvataUser;
    private List<MessageNew2> mMessageList;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<MessageNew2> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        final MessageNew2 c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        viewHolder.messageText.setText(c.getMessage());
//        viewHolder.messageImage.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
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

}
