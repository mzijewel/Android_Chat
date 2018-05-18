package com.example.nayan.chatappupdated.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nayan.chatappupdated.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendNotificationActivity extends AppCompatActivity {
    private Button btnSend;
    private EditText edtMessage;
    private String userId, currentUuer;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_not);
        userId = getIntent().getStringExtra("user_id");
        currentUuer = FirebaseAuth.getInstance().getUid();
        btnSend = (Button) findViewById(R.id.btnSend);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        firestore = FirebaseFirestore.getInstance();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("message", message);
                    notification.put("from", currentUuer);

                    firestore.collection("Users/" + userId + "/Ntifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(SendNotificationActivity.this, "sent", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SendNotificationActivity.this, "sent error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }
}
