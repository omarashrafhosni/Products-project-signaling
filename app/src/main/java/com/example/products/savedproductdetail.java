package com.example.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.HttpCookie;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class savedproductdetail extends AppCompatActivity {
    TextView text2;
    private Button delete;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedproductdetail);
        text2 = (TextView) findViewById(R.id.details1);
        t= (TextView) findViewById(R.id.t);
        final productshop ps = savedproductslist.items.get(savedproductslist.clickedItem);
        String s2 = "Product name: " + ps.productname + "\n" +"Shop name: "+ ps.shopname+ "\n"+ "Price: " + ps.price + "\n" + "Special offers: " + ps.Special + "\n" + "Distance: " + ps.distance;
        text2.setText(s2);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Carry(ps.productname, ps.shopname,ps.Special);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, savedproductslist.class));
        finish();
    }
    private void Carry(final String productname, final String shopname, final String special) {
        class Connection extends AsyncTask<String,String, String> {
            @Override
            protected String doInBackground(String... params) {

                String productname1= productname;
                String shopname1= shopname;
                String specialoffers= special;
                try{
                    HttpClient httpClient= new DefaultHttpClient();
                    HttpPost httpPost= new HttpPost("http://10.0.2.2/android_api/delete.php?productname=" + URLEncoder.encode(productname1,  "UTF-8")+"&shopname="+ URLEncoder.encode(shopname1,  "UTF-8")+"&specialoffers="+URLEncoder.encode(specialoffers,  "UTF-8"));
                    HttpResponse response= httpClient.execute(httpPost);
                    Toast.makeText(getApplicationContext(),"Delete Data", Toast.LENGTH_SHORT).show();
                    Log.e("pass 1", "connection success");
                    t.setText("Remove Successfull");
                }
                catch (Exception e){
                    Log.e("Fail 1", e.toString());

                }
                return "Data removed Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                Toast.makeText(savedproductdetail.this, "Data Removed Successfully", Toast.LENGTH_LONG).show();

            }
        }
        Connection connection= new Connection();
        connection.execute(productname,shopname);

        }

    }


