package com.eusecom.snowsmsden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class SpravyFragment extends ListFragment {

	View ColoredView;
	private ProgressDialog pDialog;
    Button btnRefresh;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "price";
	
	public static final String SERVER_NAME = "servername";
	public static final String USER_ID = "userid";
	public static final String DRUH_ID = "druhid";
	
	// products JSONArray
	JSONArray products = null;
	
	
	protected static String[] names = new String[] { "Sámo1", "Svatopluk I.",
			"sv. Václav", "Pøemysl Otakar I.", "Pøemysl Otakar II.",
			"Karel IV.", "Václav IV.", "Jiøí z Podìbrad", "Rudolf II.",
			"Václav Klaus" };

	private OnRulerSelectedListener mOnRulerSelectedListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        new LoadObj().execute();
		
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Donutíme kontejnerovou Activitu implementovat naše rozhraní
		try {
			mOnRulerSelectedListener = (OnRulerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnRulerSelectedListener");
		}
 
	
	}
//koniec onattach
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	
        if (ColoredView != null)
            ColoredView.setBackgroundColor(Color.BLACK); //original color

        v.setBackgroundColor(Color.GRAY); //selected color
        ColoredView = v;
		
		
		// Chytøe se zbavíme zodpovìdnosti za vybrání nìjakého vládce
		mOnRulerSelectedListener.onRulerSelected(position);
	}

	public interface OnRulerSelectedListener {
		public void onRulerSelected(int index);
	}
	
	
	   /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadObj extends AsyncTask<String, String, String> {
    	
    	
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
            
            
        	
        	// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
         
            params.add(new BasicNameValuePair("serverx", "xxxxx"));
            params.add(new BasicNameValuePair("userx", "aaaa"));
            
            // getting JSON string from URL

            JSONObject json = jParser.makeHttpRequest("http://www.eshoptest.sk/androidsnow/get_moje_obj.php", "GET", params);
            
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                	                    
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String price = c.getString(TAG_PRICE);

                        
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_PRICE, price);
                        
                        // adding HashList to ArrayList
                        productsList.add(map);
                        
                    }
                } else {
                    // no products found

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            
            // updating UI from Background Thread
        	getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                    		getActivity(), productsList,
                            R.layout.list_item_mojeobj, new String[] { TAG_PID, TAG_NAME, TAG_PRICE },
                            new int[] { R.id.pid, R.id.name, R.id.price });
                    // updating listview
                    setListAdapter(adapter);
                    
                }
            });
 
        }
 
    }
//koniec LoadObj

	
	
}
//celkom koniec seznamfragment