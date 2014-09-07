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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class KontaktyFragment extends ListFragment {

	View ColoredView;
	private ProgressDialog pDialog;
    Button btnRefresh;
    EditText inputFind;
    TextView txtTitle;
    TextView txtValue;
    
    String hladanie="0";
    String hladajtext="";

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
	
	private OnRulerSelectedListener mOnRulerSelectedListener;
	
	private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        //toto nacita udaje z mysql z webu
        productsList = new ArrayList<HashMap<String, String>>();
        //new LoadKontakty().execute();
        
        inputFind = (EditText) getActivity().findViewById(R.id.inputFind);
        inputFind.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {

            	hladanie="0";
            	hladajtext= inputFind.getText().toString();
            	if(hladajtext.length() > 2 ) { hladanie="1"; }
            	db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
                vylistuj();
                db2.close();
            	
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
	 	String UpdateSql = "UPDATE mojedomeny SET datm2=datz2 WHERE _id > 0 ";
	 	db2.execSQL(UpdateSql);
	 	db2.close();
        
        //toto nacita udaje z sqllite zo smartfonu
        db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
        vylistuj();
        db2.close();
		
		
	}
	//koniec onactivitycreted
	
	private void vylistuj() {
		
		if( hladanie.equals("1")) {
			constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
					"FROM mojedomeny WHERE server2 = '%" + hladajtext + "%' OR nick2 LIKE '%" + hladajtext + "%' ORDER BY datm2 DESC, server2",
					null);
		}else{
		constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
				"FROM mojedomeny WHERE _id > 0 ORDER BY datm2 DESC, server2",null);
		}
		

		//toto uklada preference
     	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
     	Editor editor = prefs.edit();

     	editor.putString("hladanie", hladanie).apply();
     	editor.putString("hladajtext", hladajtext).apply();
     	
     	editor.commit();

		//toto bezi len v api11 ak dam prec  to "}, 0" tak je to deprecated
		ListAdapter adapter=new SimpleCursorAdapter(getActivity(),
				R.layout.rowdomeny, constantsCursor2,
				new String[] {"server2", "nick2", "name2", "pswd2", "mail2", "uzid2"},
				new int[] {R.id.title, R.id.value, R.id.namex2, R.id.pswdx2, R.id.mailx2, R.id.uzidx2}, 0);

		setListAdapter(adapter);
	}
	//koniec vylistuj

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
	
        if (ColoredView != null){
            ColoredView.setBackgroundColor(Color.BLACK); //original color
            txtTitle = (TextView) ColoredView.findViewById(R.id.title);
            txtTitle.setTextColor(Color.WHITE);
            txtValue = (TextView) ColoredView.findViewById(R.id.value);
            txtValue.setTextColor(Color.WHITE);
        }


        //kliknute color
        v.setBackgroundResource(R.drawable.backtext_yellow);
        txtTitle = (TextView) v.findViewById(R.id.title);
        txtTitle.setTextColor(Color.BLACK);
        txtValue = (TextView) v.findViewById(R.id.value);
        txtValue.setTextColor(Color.BLACK);

        
        ColoredView = v;
		
		
		// Chytøe se zbavíme zodpovìdnosti za vybrání nìjakého...
		mOnRulerSelectedListener.onRulerSelected(position);
	}

	public interface OnRulerSelectedListener {
		public void onRulerSelected(int index);
	}
	
	
	   /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadKontakty extends AsyncTask<String, String, String> {
    	
    	
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
                            R.layout.list_item_kontakty, new String[] { TAG_PID, TAG_NAME, TAG_PRICE },
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