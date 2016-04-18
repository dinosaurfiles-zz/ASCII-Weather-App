package co.dinosaurfiles.asciiweatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView asciiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asciiText = (TextView) findViewById(R.id.asciiartText);
        fetchConnect(null);
    }

    public void fetchConnect(View view){
        String stringUrl = "http://api.openweathermap.org/data/2.5/weather?lat=10&lon=122&appid=de96a1cfb5bb79880ebc64418d76eaac";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            asciiText.setText("No network connection available.");
        }

    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject  jsonRootObject = new JSONObject(result);
                JSONArray jsonArray = jsonRootObject.optJSONArray("weather");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String mainWeather = jsonObject.optString("main").toString();

                //String name = jsonObject.optString("name").toString();

                asciiText.setText(mainWeather);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(result);
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 762;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }


    /*
        String text = "<font  color=#cc0029>Erste Farbe</font> <font color=#ffcc00>zweite Farbe</font>";
        yourtextview.setText(Html.fromHtml(text));

        String cloudy = "       .  ---  .    " +System.lineSeparator()+
                        " . -(            ).  "+System.lineSeparator()+
                        " (____.___)___) "+System.lineSeparator();

        asciiText.setText(cloudy);

    String cloudy = System.lineSeparator() +
            "       .  ---  .    " + System.lineSeparator() +
            " . -(           ).  " + System.lineSeparator() +
            " (____.___)___) " + System.lineSeparator() +
            System.lineSeparator();


    private void getDetailsThread(View view) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    int number;

                    @Override
                    public void run() {

                        TextView asciiText = (TextView) findViewById(
                                R.id.asciiartText
                        );

                        asciiText.setText(cloudy);

                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();
    }*/
}
