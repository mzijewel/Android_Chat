package com.example.nayan.chatappupdated.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.adapter.MessageAdapter;
import com.example.nayan.chatappupdated.emoji.Emojicon;
import com.example.nayan.chatappupdated.emoji.EmojiconEditText;
import com.example.nayan.chatappupdated.emoji.EmojiconGridView;
import com.example.nayan.chatappupdated.emoji.EmojiconsPopup;
import com.example.nayan.chatappupdated.model.MessageNew2;
import com.example.nayan.chatappupdated.tools.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dev on 1/31/2018.
 */

public class ChatActivityNew extends AppCompatActivity {
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private static final int GALLERY_PICK = 1;
    public static String userName;
    public static String mChatUser;
    public static String mCurrentUserId;
    private final List<MessageNew2> messagesList = new ArrayList<>();
    public Bitmap bitmapAvataUser;

    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private TextView mTitleView;
    //    private EditText mChatMessageView;
    private TextView mLastSeenView;
    private CircleImageView customBarImage;
    private FirebaseAuth mAuth;
    private ImageView mChatAddBtn;
    private ImageView mChatSendBtn;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private int mCurrentPage = 1;
    // Storage Firebase
    private StorageReference mImageStorage;
    //New Solution
    private int itemPos = 0;

    //Analytics
    private String mLastKey = "";
    private String mPrevKey = "";
    private String temp;
    private ImageView imgSelect;
    private String email;
    private EmojiconEditText mChatMessageView;
    private ImageView emojiButton, submitButton;
    private FirebaseFirestore mFirestore;
    private String mUserId, fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);

        mFirestore = FirebaseFirestore.getInstance();
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mChatMessageView = findViewById(R.id.chat_message_view);
        emojiButton = findViewById(R.id.emoji_btn);
        final View rootView = findViewById(R.id.root_view);

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mChatMessageView == null || emojicon == null) {
                    return;
                }

                int start = mChatMessageView.getSelectionStart();
                int end = mChatMessageView.getSelectionEnd();
                if (start < 0) {
                    mChatMessageView.append(emojicon.getEmoji());
                } else {
                    mChatMessageView.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mChatMessageView.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        mChatMessageView.setFocusableInTouchMode(true);
                        mChatMessageView.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mChatMessageView, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        imgSelect = findViewById(R.id.imgSelect);

        mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        fromUser = getIntent().getStringExtra("fromUser");
        String userName = getIntent().getStringExtra("user_name");
        email = getIntent().getStringExtra("email");

        Utils.log("CHAT:" + fromUser);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        //---- Custom Action bar Items ----

        mTitleView = findViewById(R.id.custom_bar_title);
        mLastSeenView = findViewById(R.id.custom_bar_seen);
        customBarImage = findViewById(R.id.custom_bar_image);

        customBarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(ChatActivityNew.this, ProfileActivity.class);
                profileIntent.putExtra("user_id", mChatUser);
                profileIntent.putExtra("email", email);
                startActivity(profileIntent);
            }
        });

        mChatAddBtn = (ImageView) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageView) findViewById(R.id.chat_send_btn);
//        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        if (mChatMessageView.getText().toString().length() > 0) {
            mChatSendBtn.setImageResource(R.drawable.ic_send);
        } else {
            mChatSendBtn.setImageResource(R.drawable.ic_action_send_now);
        }

        mChatMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    mChatSendBtn.setImageResource(R.drawable.ic_send);
                } else {
                    mChatSendBtn.setImageResource(R.drawable.ic_action_send_now);
                }
            }
        });

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = findViewById(R.id.messages_list);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);


        loadMessages();


        mTitleView.setText(userName);


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });


        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_PICK);
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                checkUpcoming();


            }
        });


    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri selectedImage = data.getData();
            StorageReference filepath = mImageStorage.child("message_images").child(Utils.getToday() + ".jpg");

            filepath.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();

                        temp = download_url;
                        imgSelect.setVisibility(View.VISIBLE);
                        Picasso.with(ChatActivityNew.this).load(download_url)
                                .placeholder(R.drawable.default_avata).into(imgSelect);

                    }

                }
            });

        }

    }

    private void checkUpcoming() {

        Task incoming = mFirestore.collection("messages")
                .whereEqualTo("from", fromUser)
                .whereEqualTo("to", mUserId)
                .get();
        Task outgoing = mFirestore.collection("messages")
                .whereEqualTo("to", fromUser)
                .whereEqualTo("from", mUserId)
                .get();


        Task<List<QuerySnapshot>> all = Tasks.whenAllSuccess(incoming, outgoing);
        all.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                messagesList.clear();
                Utils.log("d:" + querySnapshots.size());
//                for (QuerySnapshot querySnapshotDoc : querySnapshots)
                for (DocumentChange doc : querySnapshots.get(0).getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            MessageNew2 message = doc.getDocument().toObject(MessageNew2.class);
                            messagesList.add(message);
                            mAdapter.notifyDataSetChanged();
                        }


                    }
            }
        });


        mRefreshLayout.setRefreshing(false);
        mLinearLayout.scrollToPositionWithOffset(messagesList.size(), 0);
    }

    private void loadMessages() {
        messagesList.clear();
        fromUser = mAuth.getCurrentUser().getUid();


        mFirestore.collection("messages").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                checkUpcoming();
            }
        });


        mRefreshLayout.setRefreshing(false);
        mLinearLayout.scrollToPositionWithOffset(messagesList.size(), 0);
    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            Map<String, Object> msg = new HashMap<>();

            msg.put("from", mUserId);
            msg.put("to", fromUser);
            msg.put("message", message);


            mFirestore.collection("messages").document().set(msg);
        }
    }

    private void sendMessage1() {

        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message) || temp != null) {

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("image", temp);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");
            imgSelect.setVisibility(View.GONE);
            temp = null;

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }

    }
}
