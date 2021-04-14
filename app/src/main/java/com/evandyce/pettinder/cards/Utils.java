package com.evandyce.pettinder.cards;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evandyce.pettinder.R;
import com.evandyce.pettinder.User;
import com.evandyce.pettinder.main.MainActivity;
import com.evandyce.pettinder.main.fragments.FavoritesFragment;
import com.evandyce.pettinder.main.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by janisharali on 21/08/16.
 */
public class Utils {

    String TAG = "UTILS";

    // gets the size of the screen
    public static Point getDisplaySize(WindowManager windowManager) {
        try {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void popupMessageFailure(Activity activity, String message){
        new AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.ERROR)
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

    public static void popupMessageFailure(Context context, String message){
        Activity activity = (Activity) context;
        new AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.ERROR)
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

    public static void popupMessageSuccess(Activity mActivity, String message) {
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

    public static void popupMessageSuccess(Context context, String message) {
        Activity activity = (Activity) context;
        new AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.SUCCESS)
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

    public static void updateDatabaseOnStop() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userReference = db.collection("users").document(user.getEmail());
        Log.d("UTILS", "Document Reference Found");

        userReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                Log.d("UTILS", "Document found successfully");
                                Long swipeCount = (Long) document.get("swipes");
                                Long totalLiked = (Long) document.get("total_liked");
                                String name = (String) document.get("name");
                                setData(userReference, user, swipeCount, totalLiked, name);
                            } else {
                                Log.e("UTILS", "Document does not exist");
                            }
                        } else {
                            Log.e("UTILS", "Task was not successful");
                        }
                    }
                });
    }

    private static void setData(DocumentReference doc, FirebaseUser user, Long swipeCount, Long totalLiked, String name) {

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", user.getEmail());
        data.put("swipes", swipeCount + User.getCounter());
        data.put("total_liked", totalLiked + FavoritesFragment.animalList.size());
        data.put("liked_list", FavoritesFragment.animalList);
        doc.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UTILS", "Document written successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UTILS", "Error writing to document", e);
                    }
                });
    }

}
