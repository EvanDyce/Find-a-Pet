package com.evandyce.pettinder;

import android.app.DownloadManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class APIConnector {
    // urls for api requests
    public static final String BASE_PETFINDER_URL = "https://api.petfinder.com/v2/animals";
    public static final String GET_NEW_TOKEN_URL = "https://api.petfinder.com/v2/oauth2/token";

    // api ids for new token requests
    private static final String client_id = "rvFqoGzRclPXYPIhdOUrqYiZawJgOElpUhE8Cppxn21mCXuwhW";
    private static final String client_secret = "KqMuQg2Mubg2h5BblyZeexTwsxBcyO7CVwMmLfEq";

    // final static string for the new token value
    private static String new_token;

    //context for this class
    Context context;

    public APIConnector(Context context) {
        this.context = context;
    }

    // interface for callback functions
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(Object response);
    }

    /*
    Querys the API with data passed from SearchFragment inputs
    Calls volleyResponseListener for callback functions and passes the values
     */
    public void getDataFromAPI(String city, String range, VolleyResponseListener volleyResponseListener) {
        String url;

        // checks if there is a city
        // if there is one then it is added to the url, else no city param is used
        if (city.length() != 0) {
            url = BASE_PETFINDER_URL + "?location=" + city + ", BC";
        } else {
            url = BASE_PETFINDER_URL;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // gets array of animals from api
                            JSONArray jsonArray = response.getJSONArray("animals");
                            if (jsonArray.length() == 0) {
                                volleyResponseListener.onError("There are no dogs available in this city. Please try again later.");
                                return;
                            }

                            // gets the first dog from the list of returned values
                            JSONObject dog = jsonArray.getJSONObject(0);

                            // sends the dogs name back the the callback function
                            volleyResponseListener.onResponse(dog.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // if the error is because the token is unauthorized then it passes the message back and generates a new token
                if(error.networkResponse.statusCode == 401) {
                    volleyResponseListener.onError("Your token has been updated. Please try again");
                    generateNewToken();
                }
                // city and province do not exist in db
                else if (error.networkResponse.statusCode == 400) {
                    volleyResponseListener.onError("Please enter a valid city");
                }
            }
        })
        {
            /*
            Adds headers to the request uses the token value generated from generateNewToken()
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + new_token);
                return headers;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    /*
    Makes a new token and assigns it to the static variable new_token
    makes a post request to the API and gets the new token back
     */
    private void generateNewToken() {
        String url = "https://api.petfinder.com/v2/oauth2/token";
        String client_id = "rvFqoGzRclPXYPIhdOUrqYiZawJgOElpUhE8Cppxn21mCXuwhW";
        String secret_id = "KqMuQg2Mubg2h5BblyZeexTwsxBcyO7CVwMmLfEq";

        JSONObject postData = new JSONObject();
        try {
            postData.put("grant_type", "client_credentials");
            postData.put("client_id", client_id);
            postData.put("client_secret", secret_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            new_token = response.getString("access_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
