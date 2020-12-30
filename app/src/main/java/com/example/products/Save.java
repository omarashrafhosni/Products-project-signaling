package com.example.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Save extends AppCompatActivity {
    TextView text;
    TextView text2;
    private Button btnmap;
    private Button Save;
    String ServerURL = "http://10.0.2.2/android_api/SaveGetData.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        text = (TextView) findViewById(R.id.name);
        String s = "Product Name: " + DetailsView.CurrentProduct;
        text.setText(s);
        text2 = (TextView) findViewById(R.id.details);
        final Shop shop = DetailsView.items.get(DetailsView.clickedItem);
        String s2 = "shop name: " + shop.name + "\n" + "Price: " + shop.price + "\n" + "Special offers: " + shop.Special + "\n" + "Distance: " + shop.distance;
        text2.setText(s2);
        btnmap = (Button) findViewById(R.id.map);
        Save = (Button) findViewById(R.id.save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData(DetailsView.CurrentProduct, shop.name, shop.distance, shop.price, shop.Special);

            }
        });
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "http://maps.google.com/maps?saddr=" + MapsActivity.latitude + "," + MapsActivity.longitude + "&daddr=" + shop.lat + "," + shop.longitude;
               // String s1 = "https://www.google.com/maps/dir/30.029968,31.40869/30.004335,31.424731/@30.0205031,31.398616";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                startActivity(intent);
            }
        });
    }

    public void InsertData(final String productname, final String shopname, final double distance, final double price, final String special) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String productname1 = productname ;
                String shopname1 = shopname ;
                String distance1= Double.toString(distance);
                String price1= Double.toString(price);
                String special1=special;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("productname", productname1));
                nameValuePairs.add(new BasicNameValuePair("shopname", shopname1));
                nameValuePairs.add(new BasicNameValuePair("distance", distance1));
                nameValuePairs.add(new BasicNameValuePair("price", price1));
                nameValuePairs.add(new BasicNameValuePair("specialoffers", special1));
                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                Toast.makeText(Save.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(productname,shopname);
    }
}