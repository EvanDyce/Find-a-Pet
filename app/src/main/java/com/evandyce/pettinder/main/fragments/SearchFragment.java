package com.evandyce.pettinder.main.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.evandyce.pettinder.cards.CardsActivity;
import com.evandyce.pettinder.api.APIConnector;
import com.evandyce.pettinder.R;

public class SearchFragment extends Fragment {

    // declaring instance variables
    protected APIConnector db;
    protected FragmentActivity mActivity;

    //Todo: add new spinner for filtering animal types. Dogs, Cats, Dogs and Cats, Other

    // called on the creation
    // makes the fragment fill to container in the activity
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // called when the fragment is attached to an activity
    // sets the instance variable to the context of the activity that it is attached to
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
            db = new APIConnector(mActivity);
        }
    }

    // called after onCreateView and passes the view as a paramter

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializes all of the instance variables with the proper thing from the view
        EditText et_cityName = view.findViewById(R.id.et_enterCityName);
        EditText et_range = view.findViewById(R.id.et_maxRange);
        Spinner spinner_provinces = view.findViewById(R.id.spinner_provinceList);
        Button search = view.findViewById(R.id.searchFragment_searchButton);

        // sets on click listener for the search button
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // does the actual query
                // uses APIConnection object and passes string from text fields and spinner
                // VolleyResponseListener is for callbacks and when response is received the methods are called with params from APIConnector implementation
                db.getDataFromAPI(et_cityName.getText().toString(), et_range.getText().toString(), spinner_provinces.getSelectedItem().toString(), new APIConnector.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse() {
                        Intent intent = new Intent(v.getContext(), CardsActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
            }

        });
    }
}
