package com.example.infinity.pixie.service;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by infinity on 4/21/17.
 */

public class HttpClientUtil {
    private final String baseUrl = "http://52.53.93.85/";
    final String extractUrl = baseUrl + "api/extract";
    AsyncHttpClient httpClient = new AsyncHttpClient();



    public void extractData(JsonHttpResponseHandler listener){
        try
        {
            RequestParams params = new RequestParams();

            httpClient.post(extractUrl, params, listener);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
