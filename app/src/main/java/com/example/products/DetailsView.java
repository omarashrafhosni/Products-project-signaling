package com.example.products;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.database.DefaultDatabaseErrorHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;

import org.w3c.dom.Text;

public class DetailsView extends AppCompatActivity {
    private TextView text;
    int ClickedProduct=products.ClickedItemID;
    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        text= (TextView) findViewById(R.id.textView2);
        String s= "Product Name: " + products.productNames.get(ClickedProduct-1);
        text.setText(s);
        listView= (ListView) findViewById(R.id.List);
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        new Connection().execute();
    }
    class Connection extends AsyncTask<String,String, String>{

        @Override
        protected String doInBackground(String... params) {
            String result="";
            String host="http://10.0.2.2/android_api/shop_product.php";
            try {
                HttpClient client= new DefaultHttpClient();
                HttpGet request= new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer= new StringBuffer("");
                String line="";
                while((line= reader.readLine())!=null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result= stringBuffer.toString();

            }
            catch (Exception e){
                return new String("there is an Exception: "+ e.getMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            try{
                JSONObject jsonResult= new JSONObject(result);
                int success= jsonResult.getInt("success");
                int k=1;
                if(success==1){
                    JSONArray shops= jsonResult.getJSONArray("shop_product");
                    for(int i=0;i<shops.length();i++){
                        JSONObject shop= shops.getJSONObject(i);
                        if(shop.getInt("id")==ClickedProduct){
                            String name= shop.getString("name");
                            double latitude= Double.parseDouble(shop.getString("latitude"));
                            double longitude= Double.parseDouble(shop.getString("longitude")) ;
                            double Currentlat=MapsActivity.latitude;
                            double Currentlong= MapsActivity.longitude;
                            double distance= distance(Currentlat,Currentlong,latitude,longitude,"K");
                            String price= shop.getString("price");
                            String special= shop.getString("special");
                            if(special.isEmpty()){
                                special="No Offers available";
                            }
                            String line= k +"-"+ name +"\n"+ "Price: " +price +"\n"+ "Special Offers: " + special +"\n" +"Distance: "+distance +" Km";
                            k++;
                            adapter.add(line);
                        }



                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"there are no products available", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
}