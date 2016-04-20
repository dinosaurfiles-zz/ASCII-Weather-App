package co.dinosaurfiles.asciiweatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView asciiText;
    private TextView weatherMain;
    private TextView weatherDesc;
    private TextView detailsTemp;
    private TextView detailsHumidity;
    private TextView detailsCloudinnes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asciiText = (TextView) findViewById(R.id.asciiart_Text);
        weatherMain = (TextView) findViewById(R.id.weather_main);
        weatherDesc = (TextView) findViewById(R.id.weather_description);
        detailsTemp = (TextView) findViewById(R.id.details_temperature);
        detailsHumidity = (TextView) findViewById(R.id.details_humidity);
        detailsCloudinnes = (TextView) findViewById(R.id.details_cloudinnes);
        fetchConnect(null);
    }

    public void fetchConnect(View view) {
        //String stringUrl = "http://192.168.8.101/CMSC129/app/test1.json";
        String stringUrl = "http://api.openweathermap.org/data/2.5/weather?lat=10&lon=122&appid=de96a1cfb5bb79880ebc64418d76eaac";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            asciiText.setText("No Internet Connection/Error Occured. Please try again");
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

            System.out.println("postExec");

            try{

                System.out.println("JSON");

                JSONObject jsonRootObject = new JSONObject(result);

                JSONArray weatherArray = jsonRootObject.optJSONArray("weather");
                JSONObject weatherArrayObject = weatherArray.getJSONObject(0);

                String weatherMainString = weatherArrayObject.optString("main").toString();
                weatherMain.setText(weatherMainString);

                asciiText.setText(Html.fromHtml(asciiTextArt(weatherMainString)));

                String weatherDescString = weatherArrayObject.optString("description").toString();
                weatherDesc.setText(weatherDescString);

                JSONObject mainObject = (new JSONObject(result)).getJSONObject("main");

                float tempReading = (float) (Float.parseFloat(mainObject.getString("temp")) - 273.15);

                float humidityLevel = Float.parseFloat(mainObject.getString("humidity"));

                detailsTemp.setText("Temperature: "+tempReading+"ºC");

                detailsHumidity.setText("Humidity: "+humidityLevel+"%");

                JSONObject cloudsObject = (new JSONObject(result)).getJSONObject("clouds");

                float cloudinessLevel = Float.parseFloat(cloudsObject.getString("all"));

                detailsCloudinnes.setText("Cloudiness: "+cloudinessLevel+"%");

            }catch (Exception ex){
                System.out.println("Exception"+ex);
            }

        }

        private String asciiTextArt(String mainweather){
            String art;
            if (new String(mainweather).equals("Thunderstorm")) {
                art = "<font color=#03a9f4>.-.&nbsp;&nbsp;&nbsp;<br/>" +
                        "(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;<br/>" +
                        "(____(___)</font><br/>" +
                        "" +
                        "⚡️&nbsp;⚡️&nbsp;⚡️";
            }else if(new String(mainweather).equals("Drizzle")) {
                art = "☁️☁️☁️☁️☁️<br/>" +
                        "☁️☁️☁️☁️☁️<br/>" +
                        "☁️☁️☁️☁️☁️<br/>" +
                        "☁️☁️☁️☁️☁️";
            }else if (new String(mainweather).equals("Rain")) {
                art = "<font color=#ffeb3b>_`/\"\"\"\"</font><font color=#03a9f4>&nbsp;.-.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br/>" +
                        "<font color=#ffeb3b>&nbsp;,\\_</font><font color=#03a9f4>(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br/>" +
                        "<font color=#ffeb3b>/</font><font color=#03a9f4>(____(___)<br/>" +
                        "&nbsp;‘&nbsp;&nbsp;‘&nbsp;&nbsp;‘&nbsp;&nbsp;‘<br/>" +
                        "&nbsp;‘&nbsp;&nbsp;‘&nbsp;&nbsp;‘&nbsp;&nbsp;‘</font><br/>";
            }else if (new String(mainweather).equals("Snow")) {
                art = "<font color=#03a9f4>.-.&nbsp;&nbsp;&nbsp;<br/>" +
                        "(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;<br/>" +
                        "(____(___)</font><br/>" +
                        "" +
                        "❄️&nbsp;❄️️&nbsp;❄️";
            }else if (new String(mainweather).equals("Atmosphere")) {
                art = "<font color=#03a9f4>.-.&nbsp;&nbsp;&nbsp;<br/>" +
                        "(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;<br/>" +
                        "(____(___)</font><br/>" +
                        "" +
                        "♨️&nbsp;♨️️️&nbsp;♨️️";
            }else if (new String(mainweather).equals("Clear")) {
                art = "<font color=#ffeb3b>\\&nbsp;&nbsp;&nbsp;/<br/>" +
                        ".-.<br/>" +
                        "―&nbsp;(&nbsp;&nbsp;&nbsp;&nbsp;)&nbsp;―<br/>" +
                        "`-’<br/>" +
                        "/&nbsp;&nbsp;&nbsp;\\</font>" ;
            }else if (new String(mainweather).equals("Clouds")) {
                art = "<font color=#ffeb3b>_`/\"\"\"\"</font><font color=#03a9f4>&nbsp;.-.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br/>" +
                        "<font color=#ffeb3b>&nbsp;,\\_</font><font color=#03a9f4>(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br/>" +
                        "<font color=#ffeb3b>/</font><font color=#03a9f4>(____(___)</font><br/>";
            }else if (new String(mainweather).equals("Extreme")) {
                art = "<font color=#03a9f4>.-.&nbsp;&nbsp;&nbsp;<br/>" +
                        "(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;).&nbsp;&nbsp;<br/>" +
                        "(____(___)</font><br/>" +
                        "" +
                        "☠&nbsp;⚡&nbsp;&nbsp;☠&nbsp;⚡&nbsp;☠";
            }else if (new String(mainweather).equals("Additional")){
                art = "????????️<br/>" +
                        "????????️<br/>" +
                        "????????<br/>" +
                        "????????";
            }else {
                art = "Unkown";
            }
            return art;

        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;

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
                String contentAsString = readIt(is);

                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                stream.close();
                return sb.toString();
            }
            return "Error|ReadITt";
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

