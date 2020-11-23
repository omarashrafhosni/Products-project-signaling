package com.example.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class products extends AppCompatActivity {
   ListView listView;
   ArrayAdapter<String> adapter;
   static int ClickedItemID;
   static ArrayList<String> productNames= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        listView= (ListView) findViewById(R.id.listview);
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        new Connection().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(products.this,DetailsView.class);
                startActivity(intent);
                ClickedItemID= position+1;
            }
        });
    }
    class Connection extends AsyncTask<String,String, String>{

        @Override
        protected String doInBackground(String... params) {
            String result="";
            String host="http://10.0.2.2/android_api/products.php";
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
                if(success==1){
                    JSONArray products= jsonResult.getJSONArray("products");
                    for(int i=0;i<products.length();i++){
                        JSONObject product= products.getJSONObject(i);
                        int productID= product.getInt("id");
                        String name= product.getString("name");
                        String description= product.getString("description");
                        String line= productID +"-"+ name+"-"+ description;
                        productNames.add(i,name);
                        adapter.add(line);
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