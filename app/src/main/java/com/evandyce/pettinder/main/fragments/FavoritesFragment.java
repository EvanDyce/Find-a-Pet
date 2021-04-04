package com.evandyce.pettinder.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

        }

        List<Animal> temp = new ArrayList<>();

        String image = "https://images.dailyhive.com/20190429122839/shutterstock_423191707.jpg";

        temp.add(new Animal("Evan", "Aldergrove", "endyce@gmail.com", "17", image, image));
        temp.add(new Animal("Josh", "Aldergrove", "endyce@gmail.com", "60", image, image));
        temp.add(new Animal("Quinn", "Aldergrove", "endyce@gmail.com", "21", image, image));

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(mActivity);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(mActivity, temp);
        rv.setAdapter(adapter);
    }
}
