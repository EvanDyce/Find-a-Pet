package com.evandyce.pettinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evandyce.pettinder.R;
import com.evandyce.pettinder.cards.CardsActivity;
import com.evandyce.pettinder.classes.APIConnector;

public class HomeFragment extends Fragment {

    private Button b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        b = (Button) view.findViewById(R.id.load_next_activity);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), CardsActivity.class);
                view.getContext().startActivity(intent);
            }
        });
        return view;
    }

}
