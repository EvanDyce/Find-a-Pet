package com.evandyce.findapet.main.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.evandyce.findapet.CardsActivity;
import com.evandyce.findapet.Utils;
import com.evandyce.findapet.api.APIConnector;
import com.evandyce.findapet.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragement";
    // declaring instance variables
    protected APIConnector db;
    protected FragmentActivity mActivity;

    // instance variables for the different components in the fragment

    // values for the google autocomplete activity
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Place place = null;
    private int sliderValue = 100;

    private SeekBar rangeSlider;
    private EditText cityNameEditText;
    private TextView rangeSliderValueTextView;
    private NiceSpinner animalTypeSpinner;
    private NiceSpinner animalAgeSpinner;
    private Button searchButton;

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

    // called after onCreateView and passes the view as a parameter

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // google maps autocomplete init
        String apiKey = getString(R.string.GOOGLEAPIKEY);
        Places.initialize(getContext(), apiKey);
        PlacesClient placesClient = Places.createClient(mActivity);

        // initializes all of the instance variables with the proper thing from the view
        cityNameEditText = view.findViewById(R.id.et_enterCityName);
        rangeSlider = view.findViewById(R.id.rangeSlider);
        rangeSliderValueTextView = view.findViewById(R.id.rangeSliderText);
        animalTypeSpinner = view.findViewById(R.id.animal_type_spinner);
        animalAgeSpinner = view.findViewById(R.id.animal_age_spinner);
        searchButton = view.findViewById(R.id.searchFragment_searchButton);

        // animal type spinner
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(view.getContext(), R.array.animal_type, R.layout.spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalTypeSpinner.setAdapter(adapterType);

        // animal age spinner
        ArrayAdapter<CharSequence> adapterAge = ArrayAdapter.createFromResource(view.getContext(), R.array.animal_ages, R.layout.spinner_item);
        adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalAgeSpinner.setAdapter(adapterAge);

        rangeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangeSliderValueTextView.setText(String.valueOf(progress) + " km");
                sliderValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /*
            Sets on click listener for the edit text at the top of the fragment
            Starts and intent for the google autocomplete search when it is clicked.
         */
        cityNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() != MotionEvent.ACTION_DOWN) { return false; }

                List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.CITIES)
                        .setLocationRestriction(RectangularBounds.newInstance(
                                new LatLng(30.260786, -172.190389),
                                new LatLng(73.142387, -49.819723)
                        ))
                        .build(mActivity);

                Log.wtf(TAG, "THIS IS THE ONTOUCH");
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                return true;
            }
        });

        /*
            Adds on onClickListener to the Search button on the bottom of fragment.
         */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // does the actual query
                // uses APIConnection object and passes string from text fields and spinner
                // VolleyResponseListener is for callbacks and when response is received the methods are called with params from APIConnector implementation
                db.getDataFromApi2(place, String.valueOf(sliderValue), animalTypeSpinner.getSelectedItem().toString(), animalAgeSpinner.getSelectedItem().toString(), new APIConnector.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Utils.popupMessageFailure(mActivity, message);
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

    /**
     * callback function for startActivityForResult that is used to interpret the data.
     * Used for retrieving the information from the google autocomplete activity.
     *
     * @param requestCode request code sent by the startActivityForResult call. Used to identify the activity started.
     * @param resultCode return value from the activity. Indicates success, fail, or other.
     * @param data contains the information returned from the intent in the form of an intent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                place = Autocomplete.getPlaceFromIntent(data);
                cityNameEditText.setText(place.getName());
                Log.i(TAG, "Place: " + place.getName() + " " + place.getLatLng().longitude + ", " + place.getLatLng().latitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Utils.popupMessageFailure(mActivity, "Sorry, we could not make your request at this time. Please try again later.");
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Log.i(TAG, "Search cancelled");

            }
        }
    }
}
