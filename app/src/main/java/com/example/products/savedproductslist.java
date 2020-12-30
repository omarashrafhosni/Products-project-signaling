package com.example.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.net.URI;
import java.util.ArrayList;

public class savedproductslist extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> adapter;
    static int clickedItem;
    static ArrayList<productshop> items= new ArrayList<productshop>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedproductslist);
        listView= (ListView) findViewById(R.id.listviewSaved);
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        new Connection().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(savedproductslist.this,savedproductdetail.class);
                startActivity(intent);
                clickedItem=position;
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    class Connection extends AsyncTask<String,String, String> {

        @Override
        protected String doInBackground(String... params) {
            String result="";
            String host="http://10.0.2.2/android_api/savedproducts.php";
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
            items.clear();
            try{
                JSONObject jsonResult= new JSONObject(result);
                int success= jsonResult.getInt("success");
                if(success==1){
                    JSONArray savedproducts= jsonResult.getJSONArray("savedproducts");
                    for(int i=0;i<savedproducts.length();i++){
                        JSONObject savedproduct= savedproducts.getJSONObject(i);
                        String productname= savedproduct.getString("productname");
                        String shopname= savedproduct.getString("shopname");
                        String distance= savedproduct.getString("distance");
                        String price= savedproduct.getString("price");
                        String specialoffers= savedproduct.getString("specialoffers");
                        String line= (i+1) +"- Product name: "+ productname+"\n"+ "Shop name: "+ shopname+"\n"+"Distance: "+ distance+ "\n"+ "Price: "+ price+"\n"+"Special offers: "+specialoffers;
                        adapter.add(line);
                        productshop ps= new productshop(productname,shopname,distance,price,specialoffers);
                        items.add(ps);
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
}