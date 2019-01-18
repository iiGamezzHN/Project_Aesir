package com.uva.aesir;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExerciseImgRequest implements Response.Listener<JSONObject>, Response.ErrorListener {
    Context context;
    private ArrayList<ExerciseImg> imgUrls = new ArrayList<>();
    private Callback callback;

    @Override
    public void onErrorResponse(VolleyError error) {
        callback.gotExerciseImgError(error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        try {


            JSONArray array = response.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject specific = array.getJSONObject(i);
                String exercise = specific.getString("exercise");
                String imgUrl = specific.getString("image");

                imgUrls.add(new ExerciseImg(exercise, imgUrl));

            }

            String nextPage = response.getString("next");

            if (nextPage != "null") {
                newPage(nextPage);
            }

        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        callback.gotExerciseImg(imgUrls);
    }

    public interface Callback {
        void gotExerciseImg(ArrayList<ExerciseImg> exerciseImgs);

        void gotExerciseImgError(String message);
    }

    public ExerciseImgRequest(Context c) {
        this.context = c;
    }

    void newPage(String url){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequests = new JsonObjectRequest(url, null, this,this);
        queue.add(jsonObjectRequests);
    }

    void getExerciseImg(Callback activity) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://wger.de/api/v2/exerciseimage/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, this, this);
        queue.add(jsonObjectRequest);

        callback = activity;
    }

}
