package com.example.nayan.chatappupdated.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context mContext;
    private List<User> users;

    public UserAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.name);

        if (user.online.equals("true"))
            holder.imgStatus.setImageResource(R.drawable.online_icon);
        else
            holder.imgStatus.setVisibility(View.GONE);
        Picasso.with(mContext).load(user.avatar).placeholder(R.drawable.default_avata).into(holder.imgProfile);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imgStatus;
        public CircleImageView imgProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.user_single_name);
            imgProfile = itemView.findViewById(R.id.user_single_image);
            imgStatus = itemView.findViewById(R.id.user_single_online_icon);
        }
    }
}
