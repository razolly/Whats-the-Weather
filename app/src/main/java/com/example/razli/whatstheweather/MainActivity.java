package com.example.razli.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText cityEditText;
    TextView infoTextView;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        infoTextView = findViewById(R.id.infoTextView);
    }

    public void buttonClicked(View view) {

        String cityEntered = cityEditText.getText().toString();

        try {
            String result = new DownloadJsonTask().execute("http://openweathermap.org/data/2.5/weather?q=" + cityEntered + "&appid=b6907d289e10d714a6e88b30761fae22").get();
            infoTextView.setText("Weather in " + cityEntered + ": ");

            // Hide keypad after button is clicked
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class DownloadJsonTask extends AsyncTask<String, Void, String> {

        // Parse JSON data and store in a JSONArray
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherData = jsonObject.getString("weather");
                jsonArray = new JSONArray(weatherData);

                String main = "";
                String description = "";

                // Just to show that data was correctly stored in the JSONArray
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    main = obj.getString("main");
                    description = obj.getString("description");
                }

                infoTextView.append("\n" + main + "\n" + description);

            } catch (Exception e) {

                Toast.makeText(MainActivity.this, "Failed to find weather", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        // Return JSON data as String object
        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
