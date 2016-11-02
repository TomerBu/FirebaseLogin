package tomerbu.edu.firebaseupdatechildrenandondisconnect.tools;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tomerbuzaglo on 26/10/2016.
 * Copyright 2016 tomerbuzaglo. All Rights Reserved
 * <p>
 * Licensed under the Apache License, Version 2.0
 * you may not use this file except
 * in compliance with the License
 */

public class PushNotifications {


    private static Call postPush(String url, String json, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                /*.addHeader("Authorization", "key=YourServerKey")*/
                .addHeader("Authorization", "key=AIzaSyBFjNL-6gaVAfO1DMikn9kX2_OT2h3V9Dw")
                /*Server key from firebase console*/
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        //async -> call.enqueue
        call.enqueue(callback);
        return call;
    }


    public static void sendThePush() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject notificationPayload = new JSONObject();

        notificationPayload.put("Codes", "are given in coding session");
        notificationPayload.put("Hours", "17:00");

        jsonObject.put("data", notificationPayload);
        String token = "dEYuela4DNY:APA91bEVdjkOUtOZTMTn2S6tfzHqmsAzkRLWNbboVc0-FfP65kfQXjxAUnkxGZmkmcpbvRyxsLXx8uvtnmrK2eEpwOPabYePwrW_o8r9ee5HBEvpXNYBKfhFwq_l_T8LUpGh9Pb0JkMg";

        jsonObject.put("to", token);
        jsonObject.put("priority", "high");
        postPush("https://fcm.googleapis.com/fcm/send", jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            Log.e("TomerBu", responseStr);
                            // Do what you want to do with the response.
                        }
                    }
                }
        );
    }
}
