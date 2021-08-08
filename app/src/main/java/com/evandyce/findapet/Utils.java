package com.evandyce.findapet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.evandyce.findapet.main.fragments.FavoritesFragment;
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

    /**
     * displays failure dialog
     * @param activity activity of current fragment
     * @param message what to display
     */
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

    /**
     * display failure dialog
     * @param context context of current activity
     * @param message message to display
     */
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

    /**
     * displays success dialog
     * @param mActivity activity
     * @param message message to display
     */
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

    /**
     * displays success dialog
     * @param context context
     * @param message message to display
     */
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

    /**
     * updates the database with new updated information
     * @param tag determines where the function was called from. Main or other
     */
    public static void updateDatabaseOnStop(String tag) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (user == null || db == null) {
            Log.d("UpdateOnStop", "User or db is null");
            return;
        }

        // retrieves the current users document from firebase
        DocumentReference userReference = db.collection("users").document(user.getEmail());
        Log.d("UTILS", "Document Reference Found");

        if (tag.equals("Main")) {
            // if from main activity only updates the list
            updateList(userReference);
            return;

        }

        // gets some current information to add to the current totals locally
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

    /**
     * Updates all of the data for each field in the users document
     * Called when activities are stopped
     *
     * @param doc doc reference to the users document in firebase
     * @param user currently signed in user
     * @param swipeCount number of swipes from the firebase total
     * @param totalLiked number of total liked from firebase
     * @param name name of the user
     */
    private static void setData(DocumentReference doc, FirebaseUser user, Long swipeCount, Long totalLiked, String name) {

        // creates a hashmap for the users data
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", user.getEmail());
        data.put("swipes", swipeCount + User.getCounter());
        data.put("total_liked", totalLiked + User.likedCounter);
        data.put("liked_list", FavoritesFragment.animalList);

        // sets the data using the create hashmap
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

    /**
     * Called when updateDatabaseOnStop is called from the main activity
     * @param doc users document reference in firebase
     */
    private static void updateList(DocumentReference doc) {
        // only updates the list
        Map<String, Object> data = new HashMap<>();
        data.put("liked_list", FavoritesFragment.animalList);

        doc.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Utils", "Document updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Utils", "Error updating document", e);
                    }
                });
    }

    /**
     * When the user logs out this updates all of teh database information and then logs them out
     */
    public static void setDataLogOut() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user == null || db == null) {
            Log.d("UpdateOnStop", "User or db is null");
            return;
        }

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

        Login.logout();
    }
}
