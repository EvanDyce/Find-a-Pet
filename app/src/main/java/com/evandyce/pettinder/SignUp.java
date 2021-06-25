package com.evandyce.pettinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evandyce.pettinder.cards.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    /**
     * creates a user with teh Firebase authentication serivce and then loads the information into the database
     *
     * @param email string that is teh email of the user
     * @param password string that represents the password for the user
     * @param name string for the name of the user
     */
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

                            loadUserIntoDB(email, name);

                            updateUI(user);
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            switch (errorCode) {

                                case "ERROR_INVALID_CREDENTIAL":
                                    Utils.popupMessageFailure(getApplicationContext(), "The authentication credential is malformed or expired.");
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    mTxtEmail.setError("The email address is badly formatted.");
                                    mTxtEmail.requestFocus();
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    Utils.popupMessageFailure(getApplicationContext(),"The password entered is incorrect.");
                                    mTxtPassword.setText("");
                                    break;

                                case "ERROR_USER_MISMATCH":
                                    Utils.popupMessageFailure(getApplicationContext(),"The supplied credentials do not correspond to the previously signed in user.");
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    Utils.popupMessageFailure(getApplicationContext(),"This operation is sensitive and requires recent authentication. Log in again before retrying this request.");
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    Utils.popupMessageFailure(getApplicationContext(),"An account already exists with the same email address but different sign-in credentials.");
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    mTxtEmail.setError("The email address is already in use by another account.");
                                    mTxtEmail.requestFocus();
                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    Utils.popupMessageFailure(getApplicationContext(),"This email is already associated with a different account.");
                                    break;

                                case "ERROR_USER_DISABLED":
                                    Utils.popupMessageFailure(getApplicationContext(),"This account has been disabled by an administrator");
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    Utils.popupMessageFailure(getApplicationContext(),"User's credentials have expired. Please sign in again");
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    Utils.popupMessageFailure(getApplicationContext(),"There is no account with this email. Please create an account.");
                                    break;

                                case "ERROR_INVALID_USER_TOKEN":
                                    Utils.popupMessageFailure(getApplicationContext(),"Please sign in again");
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    Utils.popupMessageFailure(getApplicationContext(),"This operation is not allowed.");
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

    /**
     * loads the user into database
     * used for new user information and initializes all of the values to their proper value
     *
     * @param email string email for the user
     * @param name string name for the user
     */
    private void loadUserIntoDB(String email, String name) {

        // creates hashmap for the data for the user being created
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("swipes", 0);
        userData.put("total_liked", 0);
        userData.put("liked_list", new ArrayList<Animal>());

        // creates a document in the user collection using the email as the key
        db.collection("users").document(email)
                .set(userData)
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
}