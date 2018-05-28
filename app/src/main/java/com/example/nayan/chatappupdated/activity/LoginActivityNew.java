package com.example.nayan.chatappupdated.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.model.User;
import com.example.nayan.chatappupdated.tools.StaticConfig;
import com.example.nayan.chatappupdated.tools.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ASUS on 1/22/2018.
 */

public class LoginActivityNew extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    FloatingActionButton fab;
    private EditText editTextUsername, editTextPassword;
    private LovelyProgressDialog waitingDialog;


    private FirebaseAuth mAuth;

    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private boolean firstTimeAccess;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new2);
        fab = findViewById(R.id.fab);
        editTextUsername = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        firstTimeAccess = true;
        initFirebase();
    }


    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {

            callToReg(data.getStringExtra(StaticConfig.STR_EXTRA_EMAIL), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD), data.getStringExtra(StaticConfig.STR_EXTRA_NAME));
        }
    }

    private void callToReg(final String email, String pass, final String name) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();

                    User user = new User();
                    user.online = true;
                    user.name = name;
                    user.tokenId = tokenId;
                    user.email = email;

                    mFirestore.collection("Users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivityNew.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivityNew.this, TabActivity.class));
                            LoginActivityNew.this.finish();
                        }
                    });
                }
            }
        });
    }

    public void clickLogin(View view) {
        userEmail = Utils.getPref("UserEmail", "user");
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (validate(username, password)) {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    startActivity(new Intent(LoginActivityNew.this, TabActivity.class));
                    LoginActivityNew.this.finish();
                }
            });
        } else {
            Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegister(View view) {
        startActivityForResult(new Intent(this, RegistrationActvityNew.class), StaticConfig.REQUEST_CODE_REGISTER);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    public void clickResetPassword(View view) {
        String username = editTextUsername.getText().toString();
        if (validate(username, ";")) {

        } else {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
    }

}
