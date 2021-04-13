package com.evandyce.pettinder.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evandyce.pettinder.R;
import com.evandyce.pettinder.RVAdapter;
import com.evandyce.pettinder.cards.Animal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    // good for deletion of liked items?
    //https://github.com/pedant/sweet-alert-dialog

    protected FragmentActivity mActivity;

    public static List<Animal> animalList = new ArrayList<>();
    private static RecyclerView rv;
    private static TextView emptyView;

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

        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_rv);

        LinearLayoutManager llm = new LinearLayoutManager(mActivity);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(mActivity, animalList);
        rv.setAdapter(adapter);

        emptyList(animalList);
    }

    public static void emptyList(List<Animal> animalList) {
        if (animalList.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
