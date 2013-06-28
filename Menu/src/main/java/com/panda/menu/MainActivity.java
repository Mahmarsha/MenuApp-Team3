package com.panda.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends Activity {

    private static final String DEBUG_TAG = "restaurant";
    private static final long RESULT_RESTUARANT = 1000 ;
    private TextView restaurantView;
    private TextView dishView;
    private ImageView imageView;
    private ImageView imageView2;
    private TextView iceCreamView;
    private ImageView imageArea1;
    private ScrollView scrollView;
    private ImageView imageView3;
    private TextView beefView;



    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantView = (TextView)findViewById(R.id.restaurantName);
        dishView = (TextView)findViewById(R.id.dishName);
        imageView = (ImageView)findViewById(R.id.imageArea);
        imageView2 = (ImageView)findViewById(R.id.imageArea1);
        iceCreamView = (TextView)findViewById(R.id.iceCream);
        scrollView =(ScrollView)findViewById(R.id.scrollView);


        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                checkNetworkConnection(restaurantView);
                checkNetworkConnection(dishView);
                checkNetworkConnection(imageView);
                checkNetworkConnection(imageView2);
                checkNetworkConnection(iceCreamView);


            }
        },RESULT_RESTUARANT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    public void checkNetworkConnection(View view)
    {
        String stringUrl = "http://10.0.2.2:8080/name";
        String stringUrl1 = "http://10.0.2.2:8080/starter";
        String stringUrl2 = "http://10.0.2.2:8080/image/pandaim7";
        String stringUrl3 = "http://10.0.2.2:8080/sweets";
        String stringUrl4 = "http://10.0.2.2:8080/image/pandaim10";

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            new DownloadWebpageTask().execute(stringUrl);
            new DownloadWebpageTask().execute(stringUrl1);
            new DownloadWebpageTask().execute(stringUrl2);
            new DownloadWebpageTask().execute(stringUrl3);
            new DownloadWebpageTask().execute(stringUrl4);


        } else
        {
            restaurantView.setText("No network connection available.");
            dishView.setText("No network connection available");
            iceCreamView.setText("No network connection available");

        }
    }

    public class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        private String url;

        @Override
        protected String doInBackground(String... urls)
        {
            url = urls[0];

            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (XmlPullParserException e) {
                return "Xml parser failed";
            }

        }

        @Override
        protected void onPostExecute(String result)
        {
            if(url.contains("name")==true)
            {
            restaurantView.setText(result);
            }
            else if(url.contains("starter")==true)
            {
                dishView.setText(result);
            }
            else if(url.contains("sweets")==true){
                iceCreamView.setText(result);
            }
            else if(url.contains("image/pandaim7")==true){
                imageView.setImageBitmap(bitmap);
            }

                else

            {
                imageView2.setImageBitmap(bitmap);
            }

        }

        private String downloadUrl(String myUrl) throws IOException, XmlPullParserException
        {
            InputStream is = null;

            // Only display the first 500 characters of the retrieved
            // web page content.

            int len = 500;

            try {

                Log.d(DEBUG_TAG, "The url is: " + myUrl);
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                //return text and image
                if(url.toString().contains("name") ||
                        url.toString().contains("starter")||url.toString().contains("sweets") )
                {
                    String contentAsString =readIt(is, len);
                    return Jsoup.parse(contentAsString).text();
                } else

                    bitmap = BitmapFactory.decodeStream(is);
                    return "http://10.0.2.2:8080/image/pandaim7";
//                }
//                else if (url.toString().contains("sweets")){
//
//                    bitmap = BitmapFactory.decodeStream(is);
//                    return "http://10.0.2.2:8080/image/pandaim9";
//                }
//                else{
//                    return null;
//                }

            } finally
            {
                if (is != null) {
                    is.close();
               }
            }
        }
        private String readIt(InputStream is, int len) throws IOException
        {
            Reader reader;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

    }
    }



