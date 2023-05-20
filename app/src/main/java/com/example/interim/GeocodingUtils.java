package com.example.interim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class GeocodingUtils {

    public interface GeocodingListener {
        void onCoordinatesObtained(double latitude, double longitude);
        void onFailure();
    }

    public static void getCoordinates(String address, GeocodingListener listener) {
        String apiKey = "1a455409975ccb48f3648c6bfe8bd018";
        String encodedAddress = encodeUrlParameter(address);
        String urlString = "http://api.positionstack.com/v1/forward?access_key=" + apiKey + "&query=" + encodedAddress;

        new GeocodingTask(listener).execute(urlString);
    }

    private static class GeocodingTask extends AsyncTask<String, Void, double[]> {
        private GeocodingListener listener;

        public GeocodingTask(GeocodingListener listener) {
            this.listener = listener;
        }

        @Override
        protected double[] doInBackground(String... urls) {
            String urlString = urls[0];
            double[] coordinates = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                String jsonResponse = response.toString();
                coordinates = parseCoordinatesFromResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return coordinates;
        }

        @Override
        protected void onPostExecute(double[] coordinates) {
            if (coordinates != null) {
                double latitude = coordinates[0];
                double longitude = coordinates[1];
                listener.onCoordinatesObtained(latitude, longitude);
            } else {
                listener.onFailure();
            }
        }
    }

    private static double[] parseCoordinatesFromResponse(String jsonResponse) {
        double[] coordinates = null;

        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray data = response.getJSONArray("data");
            if (data.length() > 0) {
                JSONObject firstItem = data.getJSONObject(0);
                double latitude = firstItem.getDouble("latitude");
                double longitude = firstItem.getDouble("longitude");
                coordinates = new double[]{latitude, longitude};
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return coordinates;
    }

    private static String encodeUrlParameter(String parameter) {
        try {
            return java.net.URLEncoder.encode(parameter, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameter;
    }
}

