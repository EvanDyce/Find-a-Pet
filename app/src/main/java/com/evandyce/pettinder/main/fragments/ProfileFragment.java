package com.evandyce.pettinder.main.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.evandyce.pettinder.Login;
import com.evandyce.pettinder.R;
import com.evandyce.pettinder.api.APIConnector;
import com.evandyce.pettinder.cards.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    protected FragmentActivity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");
        mDatabase = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        ImageView profilePicture = view.findViewById(R.id.profile_profilepic_image);
        TextView name = view.findViewById(R.id.profile_name_tv);
        TextView email = view.findViewById(R.id.profile_email_tv);

        TextView swipeCount = view.findViewById(R.id.profile_swipes_tv);
        TextView currentLiked = view.findViewById(R.id.profile_currentliked_tv);
        TextView totalLiked = view.findViewById(R.id.profile_totalliked_tv);

        Button logOut = view.findViewById(R.id.profile_signout_button);
        Button changePassword = view.findViewById(R.id.profile_resetpassword_button);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(mActivity);
                Drawable style = getResources().getDrawable(R.drawable.custom_input, v.getContext().getTheme());
                resetMail.setBackground(style);
                resetMail.setScaleX(0.9f);
                resetMail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black, 0, 0, 0);
                resetMail.setCompoundDrawablePadding(20);
                resetMail.setHint("Enter email");
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Reset Password?")
                        .setMessage("Please enter your email to receive reset link.")
                        .setView(resetMail);

                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get email and sent reset through firebase
                        String email = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        popupMessageSuccess("The reset link has been sent.");
                                        Log.d("PasswordResetSuccess", "The email was sent.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String errorMessage = e.getMessage().toString();
                                System.out.println(errorMessage);
                                switch (errorMessage){
                                    case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                        popupMessageFailure("There is no account with this email. Please make an account.");
                                        break;

                                    case "The email address is badly formatted.":
                                        popupMessageFailure("Please enter a valid email address.");
                                        break;
                                }
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passwordResetDialog.show();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.logout();
                mActivity.startActivity(new Intent(mActivity, Login.class));
            }
        });

//        name.setText(user.getDisplayName().toString());
        mDatabase.collection("users").document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("DATA", "DocumentSnapshot data: "+ document.getData());
                                email.setText(document.get("email").toString());
                                name.setText(document.get("name").toString());
                                swipeCount.setText(document.get("swipes").toString());
                                List<Animal> liked_list = (List<Animal>) document.get("liked_list");
                                currentLiked.setText(String.valueOf(liked_list.size()));
                                totalLiked.setText(document.get("liked_count").toString());

                            } else {
                                Log.d("DATA", "No such document");
                            }
                        } else {
                            Log.d("DATAO", "get failed with" + task.getException());
                        }
                    }
                });


    }


    public void popupMessageFailure(String message){
        new AestheticDialog.Builder(mActivity, DialogStyle.FLAT, DialogType.ERROR)
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

    public void popupMessageSuccess(String message) {
        new AestheticDialog.Builder(mActivity, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle("Success")
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

    public void setUserID(String id) {
        this.userID = id;
    }
}
