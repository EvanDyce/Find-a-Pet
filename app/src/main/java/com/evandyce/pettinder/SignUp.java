package com.evandyce.pettinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evandyce.pettinder.cards.Animal;
import com.evandyce.pettinder.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUp";

    // edit text inits
    private EditText mTxtName;
    private EditText mTxtEmail;
    private EditText mTxtPassword;

    // button inits
    private Button mButtLogin;
    private Button mButtJoin;

    // auth inits
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mTxtName = (EditText) findViewById(R.id.reg_name_et);
        mTxtEmail = (EditText) findViewById(R.id.reg_email_et);
        mTxtPassword = (EditText) findViewById(R.id.reg_password_et);
        mButtLogin = (Button) findViewById(R.id.button_login);
        mButtJoin = (Button) findViewById(R.id.reg_button_join_us);
        mAuth = FirebaseAuth.getInstance();

        mButtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Login.class));
            }
        });


        mButtJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mTxtName.getText().toString();
                String email = mTxtEmail.getText().toString();
                String password = mTxtPassword.getText().toString();

                createAccount(email, password, name);
            }
        });

    }

    private void createAccount(String email, String password, String name) {
        if (email == null || email.length() == 0 || password == null || password.length() == 0 || name == null || name.length() == 0) {
            Toast.makeText(this, "Please enter a valid name and/or password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            loadUserIntoDB(user, email, name);

                            updateUI(user);
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            switch (errorCode) {

                                case "ERROR_INVALID_CREDENTIAL":
                                    popupMessage("The authentication credential is malformed or expired.");
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    mTxtEmail.setError("The email address is badly formatted.");
                                    mTxtEmail.requestFocus();
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    popupMessage("The password entered is incorrect.");
                                    mTxtPassword.setText("");
                                    break;

                                case "ERROR_USER_MISMATCH":
                                    popupMessage("The supplied credentials do not correspond to the previously signed in user.");
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    popupMessage("This operation is sensitive and requires recent authentication. Log in again before retrying this request.");
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    popupMessage("An account already exists with the same email address but different sign-in credentials.");
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    mTxtEmail.setError("The email address is already in use by another account.");
                                    mTxtEmail.requestFocus();
                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    popupMessage("This email is already associated with a different account.");
                                    break;

                                case "ERROR_USER_DISABLED":
                                    popupMessage("This account has been disabled by an administrator");
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    popupMessage("User's credentials have expired. Please sign in again");
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    popupMessage("There is no account with this email. Please create an account.");
                                    break;

                                case "ERROR_INVALID_USER_TOKEN":
                                    popupMessage("Please sign in again");
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    popupMessage("This operation is not allowed.");
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    mTxtPassword.setError("The password is invalid it must 6 characters at least");
                                    mTxtPassword.requestFocus();
                                    break;

                            }
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void loadUserIntoDB(FirebaseUser user_firebase, String email, String name) {

        System.out.println("this is the start of load user into db");

        user_firebase.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String userID = task.getResult().getToken();
                            writeData(userID, email, name);
                        } else {
                            Toast.makeText(SignUp.this, "There was an error with our database. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeData(String userID, String name, String email) {
        User user = User.getInstance(this, userID, name, email);

        db.collection("users").document(userID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document written successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        if (user == null) { return;  }

        this.startActivity(new Intent(this, MainActivity.class));
    }

    public void popupMessage(String message){
        new AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.ERROR)
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setDarkMode(false)
                .setGravity(Gravity.CENTER)
                .setAnimation(DialogAnimation.SHRINK)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                })
                .show();
    }

}