package com.evandyce.findapet.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evandyce.findapet.cards.Animal;
import com.evandyce.findapet.R;
import com.evandyce.findapet.cards.RVAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    // good for deletion of liked items?
    //https://github.com/pedant/sweet-alert-dialog

    protected FragmentActivity mActivity;

    public static List<Animal> animalList = new ArrayList<>();
    private static RecyclerView rv;
    private static TextView emptyView;
    private static ImageView fullScreenImage;
    private static RelativeLayout fullScreenDim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Context) {
            this.mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // gets the two possible views. One is recycler view and the other is get swiping message
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_rv);
        fullScreenImage = (ImageView) view.findViewById(R.id.full_screen_imageview);
        fullScreenDim = (RelativeLayout) view.findViewById(R.id.full_screen_dim);

        /**
         * Setting on click listener for the full screen image view
         * If the imageview is empty, do nothing
         * Else change the image to nothing and hide it
         */
        fullScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullScreenImage.getVisibility() == View.GONE) {
                    return;
                }

                fullScreenImage.setImageDrawable(null);
                fullScreenImage.setVisibility(View.GONE);
                fullScreenDim.setVisibility(View.GONE);
            }
        });

//        makes layout manager and sets the recycler view layout manager
        LinearLayoutManager llm = new LinearLayoutManager(mActivity);
        rv.setLayoutManager(llm);

        // sets teh adapter
        RVAdapter adapter = new RVAdapter(mActivity, animalList);
        rv.setAdapter(adapter);

        emptyList(animalList);
    }

    /**
     * determines which fragment to show based on the size of the animallist
     * if it is empty then it shows the get swiping text, else it just shows the list
     * @param animalList current animalList
     */
    public static void emptyList(List<Animal> animalList) {
        if (animalList.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Show the image clicked on in full screen
     * @param imageURL image url to load with glide to show in teh image view
     * @param context context used to use glide
     */
    public static void setFullScreenImage(String imageURL, Context context) {
        Glide.with(context).load(imageURL).into(fullScreenImage);
        fullScreenImage.setVisibility(View.VISIBLE);
        fullScreenDim.setVisibility(View.VISIBLE);
    }
}
