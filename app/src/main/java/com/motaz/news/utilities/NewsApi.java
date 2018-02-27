package com.motaz.news.utilities;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.motaz.news.model.News;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * Created by moatz on 15/02/18.
 */

public class NewsApi {
    public ApiResponse getResponse(URL url) throws Exception {
        HttpURLConnection connection = null;
        try {
            Gson gson = new Gson();
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                ApiResponse apiResponse = gson.fromJson(scanner.next(), ApiResponse.class);
                return apiResponse;
            } else {
                throw new Exception("failedToParseJson");
            }
        } catch (Exception e) {
            //TODO failed to open connection
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

}

