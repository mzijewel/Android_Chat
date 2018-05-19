package com.example.nayan.chatappupdated.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.Configuration;
import com.example.nayan.chatappupdated.tools.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 2/3/2018.
 */

public class UserProfileFragmentNew extends Fragment {
    private static final int PICK_IMAGE = 1994;
    TextView tvUserName;
    ImageView avatar;
    private List<Configuration> listConfig = new ArrayList<>();
    private LovelyProgressDialog waitingDialog;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;

    private View mainView;

    private Uri imageUri;
    private String mUserId;
    private Context mContext;
    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(mContext)
                    .setTitle("Name")
                    .setMessage("Are you sure want to change your profile?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };

    public UserProfileFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_info, container, false);

        init();
        prepareDisplay();

        return mainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            imageUri = data.getData();
            avatar.setImageURI(imageUri);

            StorageReference userProfile = mStorage.child(mUserId + ".jpg");
            userProfile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Utils.showTost("Successfully Uploaded");
                    String url = task.getResult().getDownloadUrl().toString();
                    mFirestore.collection("Users").document(mUserId).update("avatar", url);
                }
            });

        }
    }

    private void init() {
        mContext = getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("images");

        avatar = mainView.findViewById(R.id.img_avatar);
        avatar.setOnClickListener(onAvatarClick);
        tvUserName = mainView.findViewById(R.id.tv_username);
    }

    private void prepareDisplay() {
        if (mAuth.getCurrentUser() == null) return;
        mUserId = mAuth.getCurrentUser().getUid();
        mFirestore.collection("Users").document(mUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String imgUrl = documentSnapshot.getString("avatar");
                tvUserName.setText(name);
                Picasso.with(mContext).load(imgUrl).placeholder(R.drawable.default_avata).into(avatar);
            }
        });
    }

}
