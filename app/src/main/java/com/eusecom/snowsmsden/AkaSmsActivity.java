package com.eusecom.snowsmsden;

import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.StrictMode;
import android.preference.PreferenceManager;
 
public class AkaSmsActivity extends Activity {
 
    TextView inputEdiServer;
    TextView inputEdiUser;
    
    String namex;
    int myNum;
    String mojecislo;
    String encrypted;

 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_CSEND = "csend";

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aka_sms);

        mojecislo = SettingsActivity.getUserId(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
        StrictMode.setThreadPolicy(policy);
               
        // Getting complete product details in background thread
        new GetAkaVyhra().execute();

    }
    //koniec oncreate
    
 
    /**
     * Background Async Task to Get complete product details
     * */
    class GetAkaVyhra extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AkaSmsActivity.this);
            pDialog.setMessage(getString(R.string.progdata));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

        	
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                    	
                       
                    	String prmall = "aaaaa";
                    	String randomnum = String.valueOf(Math.random());
                    	String userxplus = mojecislo + "/" + randomnum;
                    	
                    	MCrypt mcrypt = new MCrypt();
                    	/* Encrypt */
                    	try {
            				encrypted = MCrypt.bytesToHex( mcrypt.encrypt(userxplus) );
            			} catch (Exception e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
                    	/* Decrypt */
                    	//String decrypted = new String( mcrypt.decrypt( encrypted ) );
                    	
                    	List<NameValuePair> params = new ArrayList<NameValuePair>();
                    	params.add(new BasicNameValuePair("prmall", prmall));
                        params.add(new BasicNameValuePair("userhash", encrypted));
                        params.add(new BasicNameValuePair("sid", randomnum));
                        
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                        		"http://www.eshoptest.sk/androidsnow/get_newsms.php", "GET", params);
 
                        // check your log for json response
                        Log.d("Single Product Details", json.toString());
 
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_PRODUCT); // JSON Array
 
                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
 
                            // display product data in EditText
                            //inputAkaVyhra.setText(product.getString(TAG_NAME));
                        	String namex = product.getString(TAG_CSEND);
                        	
                        	//toto uklada preference
                        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        	Editor editor = prefs.edit();

                            editor.putString("newsms", namex).apply();

                        	editor.commit();
                        	
                            Intent i = new Intent(getApplicationContext(), SmsNotifyActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("newsms", namex);
                            i.putExtras(extras);
                            startActivity(i);
                            finish();

                           
                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();

 
            
            //finish();
        }
    }
    //koniec getakavyhra
 
}