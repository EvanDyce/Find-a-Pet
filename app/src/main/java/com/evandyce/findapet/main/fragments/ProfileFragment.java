package com.evandyce.findapet.main.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.evandyce.findapet.Login;
import com.evandyce.findapet.Utils;
import com.evandyce.findapet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import static com.evandyce.findapet.Utils.popupMessageSuccess;
import static com.evandyce.findapet.Utils.popupMessageFailure;

public class ProfileFragment extends Fragment {

    String TAG = "ProfileFrag";

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

        Button logOut = view.findViewById(R.id.profile_signout_button);
        Button changePassword = view.findViewById(R.id.profile_resetpassword_button);


        /**
         *  sets the onclicklistener for the reset password button on fragment
         * opens a dialog that accepts and email and sends the firebase password reset email to that email.
         * Todo: Update this function to another dialog that just sends it to the users registered email for convenience and security
         **/
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyAlertDialog.Builder((Activity) v.getContext())
                        .setTitle("Send Reset Password Email")
                        .setBackgroundColor(Color.parseColor("#8000ff"))  //Don't pass R.color.colorvalue
                        .setMessage("The email used to create account will receive email")
                        .setNegativeBtnText("Cancel")
                        .setPositiveBtnBackground(Color.parseColor("#8000ff"))  //Don't pass R.color.colorvalue
                        .setPositiveBtnText("Send")
                        .setNegativeBtnBackground(Color.parseColor("#8000ff"))  //Don't pass R.color.colorvalue
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                // get email and sent reset through firebase
                                String email = mAuth.getCurrentUser().getEmail();
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Utils.popupMessageSuccess(mActivity, "The reset link has been sent.");
                                                Log.d(TAG, "The email was sent.");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String errorMessage = e.getMessage();
                                        Log.w(TAG, "Password Reset Failed: " + errorMessage);
                                        switch (errorMessage){
                                            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                                Utils.popupMessageFailure(mActivity, "There is no account with this email. Please make an account.");
                                                break;

                                            case "The email address is badly formatted.":
                                                Utils.popupMessageFailure(mActivity, "Please enter a valid email address.");
                                                break;
                                        }
                                    }
                                });
                                    }
                                })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(v.getContext(),"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Login.logout();
                Utils.setDataLogOut();
                mActivity.startActivity(new Intent(mActivity, Login.class));
            }
        });

        setProfileValues(view);
    }

    /**
     * update the values on the screen
     * retrieves the values from the database and updates when the view is created
     * @param view fragment is passed as view
     */
    private void setProfileValues(View view) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");
        mDatabase = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView name = view.findViewById(R.id.profile_name_tv);
        TextView email = view.findViewById(R.id.profile_email_tv);

        TextView swipeCount = view.findViewById(R.id.profile_swipes_tv);
        TextView currentLiked = view.findViewById(R.id.profile_currentliked_tv);
        TextView totalLiked = view.findViewById(R.id.profile_totalliked_tv);


        mDatabase.collection("users").document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Log.d(TAG, "DocumentSnapshot Retrieved Successfully: "+ document.getData());
                                email.setText(document.get("email").toString());
                                name.setText(document.get("name").toString());
                                swipeCount.setText(String.valueOf((Long) document.get("swipes")));
                                currentLiked.setText(String.valueOf(FavoritesFragment.animalList.size()));
                                totalLiked.setText(String.valueOf((Long) document.get("total_liked")));

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Get Document Failed: " + task.getException());
                        }
                    }
                });
    }
}
