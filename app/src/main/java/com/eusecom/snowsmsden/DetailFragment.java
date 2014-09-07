package com.eusecom.snowsmsden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	public static final String INDEX = "index";

    Button btnPrepare;
    
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_NAME = "name";
	
	public static final String SERVER_NAME = "servername";
	public static final String USER_ID = "userid";
	public static final String DRUH_ID = "druhid";
	
	// products JSONArray
	JSONArray products = null;
	JSONObject product;



	TextView txtDokindex;
	String dokindex;
	
	public static DetailFragment newInstance(int index) {
		DetailFragment f = new DetailFragment();
		Bundle args = new Bundle();
		args.putInt(INDEX, index);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.detail, container, false);
		
		int index = getArguments().getInt(INDEX, 0);
		final String indexs = index + "";
		
		//pozor indexs je poradie dokladu vo vypise ( 0,1,2... ) nie cislo dokladu !!!!!! musim osetrit v php
		TextView txtDokindex = (TextView) v.findViewById(R.id.txtDokindex);
		txtDokindex.setText(indexs);
		
        productsList = new ArrayList<HashMap<String, String>>();
		new LoadObjDetail().execute();

		
		return v;

	}
	//konieconcerateview
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


    	View vxx = getView();

     	
	     // save button
			btnPrepare = (Button) vxx.findViewById(R.id.btnPrepare);
	 
	        // save button click event
			btnPrepare.setOnClickListener(new View.OnClickListener() {
	 
	            @Override
	            public void onClick(View view) {

	            	//View vxx2 = getView();
	        		//TextView txtDokindex = (TextView) vxx2.findViewById(R.id.txtDokindex);
	             	//String dokindexx = txtDokindex.getText().toString();
	    			//Intent i = new Intent(getActivity(), ObsluzitActivity.class);
	    			//i.putExtra(ObsluzitActivity.INDEX, dokindexx);
	    			//startActivity(i);
	                getActivity().finish();
	 
	            }
	        });
			
	        Context ctx = getActivity();
	    	String druhidx = PreferenceManager.getDefaultSharedPreferences(ctx).getString(DRUH_ID, "0");
            if( druhidx.equals("99")) {
            	
            }else
            {
    			View vxx4 = getView();
    			View c4 = vxx4.findViewById(R.id.btnPrepare);
                c4.setVisibility(View.GONE);           	
            	
            }
            
			View vxx3 = getView();
			View c = vxx3.findViewById(R.id.btnFinish);
            c.setVisibility(View.GONE);
			
	}
	//koniec onactivitycreated

	   /**
  * Background Async Task to Load all product by making HTTP Request
  * */
 class LoadObjDetail extends AsyncTask<String, String, String> {
 	
     /**
      * Before starting background thread Show Progress Dialog
      * */
     @Override
     protected void onPreExecute() {
         super.onPreExecute();
         super.onPreExecute();
         pDialog = new ProgressDialog(getActivity());
         pDialog.setMessage(getString(R.string.progdata));
         pDialog.setIndeterminate(false);
         pDialog.setCancelable(false);
         pDialog.show();
     }

     /**
      * getting All obj from url
      * */
     protected String doInBackground(String... args) {
         
     	String serverx = "aaaa";
    	String userx = 	"bbbb";
     	
        View vxx = getView();
		TextView txtDokindex = (TextView) vxx.findViewById(R.id.txtDokindex);
     	String dokindex = txtDokindex.getText().toString();
     	
     	// Building Parameters
         List<NameValuePair> params = new ArrayList<NameValuePair>();
      
         params.add(new BasicNameValuePair("serverx", serverx));
         params.add(new BasicNameValuePair("userx", userx));
         params.add(new BasicNameValuePair("dokindex", dokindex));         
         // getting JSON string from URL

         JSONObject json = jParser.makeHttpRequest("http://www.eshoptest.sk/androidsnow/get_detail_obj.php", "GET", params);
         
         // Check your log cat for JSON reponse
         Log.d("All Products: ", json.toString());

         try {
             // Checking for SUCCESS TAG
             int success = json.getInt(TAG_SUCCESS);

             	                    
                 // products found
                 // Getting Array of Products
                 products = json.getJSONArray(TAG_PRODUCTS);

                 // looping through All Products

                     JSONObject c = products.getJSONObject(0);

                     // Storing each json item in variable
                     String name = c.getString(TAG_NAME);
                     
                     // creating new HashMap
                     HashMap<String, String> map = new HashMap<String, String>();

                     // adding each child node to HashMap key => value
                     map.put(TAG_NAME, name);
                     
                     // adding HashList to ArrayList
                     productsList.add(map);
                 
                     if (success == 1) {
                     	
                     } else {
                         // no products found
                         // Launch Add New product Activity
                         //Intent i = new Intent(getActivity(),NodokladActivity.class);
                         // Closing all previous activities
                         //startActivity(i);
                         getActivity().finish();
                     }
         } catch (JSONException e) {
             e.printStackTrace();
         }

         return null;
     }

     protected void onPostExecute(String file_url) {
         // dismiss the dialog after getting all products
         pDialog.dismiss();
         
         View vx = getView();

		 TextView tv = (TextView) vx.findViewById(R.id.details);
		 String prodx = productsList.get(0).toString();
		 
	     	String delims = "[{]+";
	     	String[] prodxxx = prodx.split(delims);
	     	
	     	String proda1 = prodxxx[1]; 
	     	
	     	String delims2 = "[}]+";
	     	String[] prodb = proda1.split(delims2);
	     	
	     	String prodb0 = prodb[0]; 
	     	
	     	String[] separated = prodb0.split("name=");
	     	String prod1 = separated[1];

		 tv.setText(prod1);
		 


     }
     //koniec onpostexecute
 }
//koniec LoadObj
	
	
	
}
//koniec detailfragment