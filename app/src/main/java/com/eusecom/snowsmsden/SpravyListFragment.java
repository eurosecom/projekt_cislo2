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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SpravyListFragment extends ListFragment {
	public static final String INDEX = "index";

    Button btnPrepare;
    
	private ProgressDialog pDialog;

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
	JSONObject product;

	protected static String[] details = new String[] {
			"Nìjaký text1...",
			"Nìjaký text2...",
			"Nìjaký text3...",
			"Nìjaký text4...",
			"Nìjaký text5...",
			"Karel IV. Narozen 1316 v Praze,\n zemøel 1378 tamtéž.",
			"Nìjaký text7...",
			"Nìjaký text8...",
			"Nìjaký text9...",
			"Nìjaký text10..."
	};

	TextView txtDokindex;
	String dokindex;
	String zmazatsms="0";
    String zmazatallsms="0";
	
	public static SpravyListFragment newInstance(int index) {
		SpravyListFragment f = new SpravyListFragment();
		Bundle args = new Bundle();
		args.putInt(INDEX, index);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.moje_spravy, container, false);

		return v;

	}
	//konieconcerateview
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//index vybraneho kontaktu
		//int index = getArguments().getInt(INDEX, 0);
		//final String indexs = index + "";
		
		productsList = new ArrayList<HashMap<String, String>>();
		new LoadObjDetail().execute();
		
        
        registerForContextMenu(getListView());
			
	}
	//koniec onactivitycreated
	
	//oncontextmenu
    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v,
    ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    int position = info.position;
	 	
	 	String mesx = productsList.get(position).get(TAG_PID);
	 	String cidx = productsList.get(position).get(TAG_NAME);
 	
	 	//String mnox3 = productsList.get(position).get(TAG_MNOX);

    	menu.setHeaderTitle(cidx + "/" + mesx);
    
    getActivity().getMenuInflater().inflate(R.menu.kontext_sms, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    switch (item.getItemId()) {
        case R.id.smszmazat:
        	
        	int position = info.position;
            zmazatallsms="0";
            zmazatsms = productsList.get(position).get(TAG_PID);
            //new SaveSMS().execute();


    	break;
    	
        case R.id.allsmszmazat:



    	break;
    	


        }

        return super.onContextItemSelected(item);
    }
    //koniec oncontextmenu

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
//koniec LoadObjDetail
	
	
	
}
//koniec detailfragment