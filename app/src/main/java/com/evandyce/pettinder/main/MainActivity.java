package com.evandyce.pettinder.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.evandyce.pettinder.Login;
import com.evandyce.pettinder.R;
import com.evandyce.pettinder.api.APIConnector;
import com.evandyce.pettinder.cards.Animal;
import com.evandyce.pettinder.cards.Utils;
import com.evandyce.pettinder.main.fragments.FavoritesFragment;
import com.evandyce.pettinder.main.fragments.ProfileFragment;
import com.evandyce.pettinder.main.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        // makes the default fragment the home one
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SearchFragment()).commit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8000ff")));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);

        // create new apiconnector in order to generate the new tokens
        new APIConnector(this).generateNewToken();


        // set get the values from the database and set teh ones that need to be set
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // firstTime is a flag to check if it is the users first time loading the main activity this session
        // if it is the first time it clears the current list and then reloads it with the information from the database. This helps to stop the multiplying list problem.
        if (firstTime) {
            FavoritesFragment.animalList.clear();
            Log.wtf("Clear", "animallist is cleared");
            db.collection("users").document(user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()) {
                                    Log.d("MAIN", "Liked Pets retrieved successfully");

                                    List<HashMap<String, Object>> animalsHashMap = (List<HashMap<String, Object>>) document.get("liked_list");

                                    for (HashMap<String, Object> map : animalsHashMap) {
                                        String name = (String) map.get("name");
                                        String location = (String) map.get("location");
                                        String email = (String) map.get("email");
                                        String age = (String) map.get("age");
                                        String imageURL = (String) map.get("imageUrl");
                                        String petfinderURL = (String) map.get("petfinderURL");
                                        String description = (String) map.get("description");

                                        FavoritesFragment.animalList.add(new Animal(name, location, email, age, imageURL, petfinderURL, description));
                                    }

                                } else {
                                    Log.d("MAIN", "No document exists");
                                }
                            } else {
                                Log.d("MAIN", "Task failed with" + task.getException());
                            }
                        }
                    });

            firstTime = false;
        }

    }

    /*
    detects which fragment has been selected and sets it as the current fragment
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();

                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    // not called I dont think. Too scared to remove
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Login.logout();
        this.startActivity(new Intent(this, Login.class));
        return super.onOptionsItemSelected(item);
    }

    // updates the database when the main activity stops
    @Override
    protected void onStop() {
        super.onStop();
        Utils.updateDatabaseOnStop("Main");
    }
}