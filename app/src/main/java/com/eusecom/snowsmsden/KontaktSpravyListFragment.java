package com.eusecom.snowsmsden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.eusecom.snowsmsden.MCrypt;
import com.eusecom.snowsmsden.MCrypt2;

public class KontaktSpravyListFragment extends ListFragment {
	public static final String INDEX = "index";

	TextView nick1;
    TextView stat1;
    ImageView flag1;
    EditText inputSend;
    Button btnSend;
    
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	
	public static final String SERVER_NAME = "servername";
	public static final String USER_ID = "userid";
	public static final String DRUH_ID = "druhid";
	
	private static final String TAG_CID = "cid";
    private static final String TAG_CMES = "cmes";
    private static final String TAG_CSEND = "csend";
    private static final String TAG_CRECE = "crece"; 
    private static final String TAG_CKTOS = "cktos"; 
    private static final String TAG_ISTAT = "istat"; 
    private static final String TAG_INICK = "inick";
    private static final String TAG_AKOOLD = "akoold";
    private static final String TAG_CISMY = "cismy";
    private static final String TAG_CREAD = "cread";
    

	
	// products JSONArray
	JSONArray products = null;
	JSONObject product;

	String dokindex;
	String zmazatsms="0";
    String zmazatallsms="0";
    String kontaktcislo;
    String mojecislo;
    String inputsms="";
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	BufferedReader in;
	private SQLiteDatabase db3=null;
	
	String encrypted;
    String decrypted;
    String encrypted2;
    
    String hladanie="0";
    String hladajtext="";
    String device_id;
    String device_td;
    String mojstat;
    String mojnick;
    String mojcode;
    

	public static KontaktSpravyListFragment newInstance(int index) {
		KontaktSpravyListFragment f = new KontaktSpravyListFragment();
		Bundle args = new Bundle();
		args.putInt(INDEX, index);
		f.setArguments(args);
		return f;
	}
	
	//broadcastreceiver na prenos z onactivityresult
	
	String KeyWord;
	public static final String ACTION_INTENT = "com.eusecom.snowsmsden.action.UI_UPDATE";
    protected BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_INTENT.equals(intent.getAction())) {
                String value = intent.getStringExtra("UI_KEY");
                updateUIOnReceiverValue(value);
            }
        }
    };

    private void updateUIOnReceiverValue(String value) {
        // you probably want this:
        inputSend.setText(value);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(ACTION_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        device_id = tm.getDeviceId();
        device_td = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID); //*** use for tablets
        
        mojstat = SettingsActivity.getMojState(getActivity());
        mojnick = SettingsActivity.getNickName(getActivity());
        mojcode = SettingsActivity.getUserCode(getActivity());
        if(SettingsActivity.getOkPristup(getActivity()).equals("0"))
    	{ mojcode = "a0a0a0a0"; }
        
        
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }
    //koniec broadcastreceiver na prenos z onactivityresult

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.communic, container, false);
		
		init(v);
		return v;

	}
	//konieconcerateview
	
	void init(View view) {
        inputSend = (EditText) view.findViewById(R.id.inputSend);
        inputSend.setText(KeyWord);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//index vybraneho kontaktu
		int index = getArguments().getInt(INDEX, 0);
		
		//index je 0,1,2,3,... a ocislovanie ID v sqllite je 1,2,3,4,5.. preto k indexu + 1
		//toto som potreboval len ked zoradene podla id dal som movetofirst v cursore 
		//int indexplus=index+1;
		//kontaktindex = indexplus + "";
		//teraz zoradujem podla datm2 desc, server2 a dam movetoposition

		hladanie = SettingsActivity.getHladanie(getActivity());
		hladajtext = SettingsActivity.getHladajtext(getActivity());
		
		db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();

		if( hladanie.equals("1")) {
			constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
					"FROM mojedomeny WHERE server2 = '%" + hladajtext + "%' OR nick2 LIKE '%" + hladajtext + "%' ORDER BY datm2 DESC, server2",
					null);
		}else{
		constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
				"FROM mojedomeny WHERE _id > 0 ORDER BY datm2 DESC, server2",null);
		}

		
		  //getString(2) znamena ze beriem 3tiu 0,1,2 premennu teda pswd2
	      if (constantsCursor2.moveToPosition(index)) {
	    	  kontaktcislo=constantsCursor2.getString(1);
	      }

            String UpdateSql = "UPDATE mojedomeny SET mail2=' ' WHERE server2=" + kontaktcislo +"";
            db2.execSQL(UpdateSql);
	      db2.close();
	      
	      mojecislo = SettingsActivity.getUserId(getActivity());
	      //mojecislo="4679";
		
		productsList = new ArrayList<HashMap<String, String>>();
		new loadSMS().execute();
		
        
        registerForContextMenu(getListView());
        
        btnSend = (Button) getActivity().findViewById(R.id.btnSend);
     	btnSend.setOnClickListener(new View.OnClickListener() {
   
             @Override
             public void onClick(View arg0) {

            	 	zmazatallsms="3";
            	 	zmazatsms="0";
            	 	inputSend = (EditText) getActivity().findViewById(R.id.inputSend);
            	 	inputsms=inputSend.getText().toString();
            	 	inputSend.setText("");
            	 	
            	 	db3=(new DatabaseSpravy(getActivity())).getWritableDatabase();
            	 	
            	 	ContentValues values=new ContentValues(3);
 
            		values.put("server3", kontaktcislo);
            		values.put("nick3", inputsms);

            		//String[] argsx={kontaktcislo};
            		//db3.delete("mojspravy", "server3=?", argsx);
            		db3.insert("mojespravy", "server3", values);
            	 	
            	 	db3.close();
            	 	
            	 	db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
            	 	java.util.Date date= new java.util.Date();
            		Timestamp ts = new Timestamp(date.getTime());
            		String tss = ts + "";
                    String UpdateSql = "UPDATE mojedomeny SET datz2='" + tss + "', mail2=' ' WHERE server2=" + kontaktcislo +"";
            	 	db2.execSQL(UpdateSql);
            	 	db2.close();
            	 	
            	 	productsList = new ArrayList<HashMap<String, String>>();
         			new loadSMS().execute();

             }
         });
			
	}
	//koniec onactivitycreated
	

	
	/**
     * Background Async Task to Load all SMS by making HTTP Request
     * */
    class loadSMS extends AsyncTask<String, String, String> {
    	
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.progdata));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            

    		String hladaj1x = "";
    		String hladaj2x = "";
    		String hladaj3x = "";
    		String userx = "";
    		String hladajx = "";
    		String cismy = mojecislo;
    		String cisadv = kontaktcislo;
    		
    		
        	//to hex
        	String textsprhex="";
			try {
				textsprhex = toHex(inputsms);
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
        	String userxplus2 = textsprhex;
        	//String mjpsw = "89abcdef";
			
			MCrypt2 mcrypt2 = new MCrypt2(mojcode);
        	/* Encrypt */
        	try {
				encrypted2 = MCrypt2.bytesToHex( mcrypt2.encrypt(userxplus2) );
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
        	String prmall = "aaaaa";
        	String randomnum = String.valueOf(Math.random());
        	String userxplus = device_id + "/" + userx + "/" + randomnum + "/" + hladajx
       			 + "/" + hladaj1x + "/" + hladaj2x + "/" + hladaj3x + "/" + cismy + "/" + cisadv + 
       			 "/" + zmazatsms + "/" + zmazatallsms + "/" + encrypted2 + "/" + mojstat + "/" + mojnick + "/" + device_td;
        	
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
        	
        	// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();        
            params.add(new BasicNameValuePair("prmall", prmall));
            params.add(new BasicNameValuePair("userhash", encrypted));
            //params.add(new BasicNameValuePair("userhash2", encrypted2));
            params.add(new BasicNameValuePair("sid", randomnum));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest("http://www.eshoptest.sk/androidsnow/rsp_comunics.php", "GET", params);
            
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
                    
                    String[] notes = getResources().getStringArray(R.array.country_notes);
                    String[] titleen = getResources().getStringArray(R.array.country_titleen);
                    
                    List<String> notesList = Arrays.asList(notes);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String cidx = c.getString(TAG_CID);
                        String cmesx = c.getString(TAG_CMES);
                        String csendx = c.getString(TAG_CSEND);
                        String crecex = c.getString(TAG_CRECE);
                        String cktosx = c.getString(TAG_CKTOS);
                        String istatx = c.getString(TAG_ISTAT);
                        String cismyx = c.getString(TAG_CISMY);
                        String creadx = c.getString(TAG_CREAD);
                        
                        //uloz iid do preferences
                        if( i == 0 )
                        {
                        	//toto uklada preference
                         	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                         	Editor editor = prefs.edit();
                         	editor.putString("userid", cismyx).apply();
                         	editor.commit();
                        	
                        }
                        
                        
                        //int index_notes = Int.indexOf(notes, istatx);
                        int index_notes = notesList.indexOf(istatx);

                        String inickx = c.getString(TAG_INICK) + " / " + titleen[index_notes];
                        String akooldx = c.getString(TAG_AKOOLD);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, cidx);
                        
                        if( cidx.equals("0")) {
                        	map.put(TAG_CMES,  getResources().getString(R.string.nosms));	
                        }else{
                        
                        MCrypt2 mcryptde2 = new MCrypt2(mojcode);
                    	/* Encrypt */
                    	try {
                    		decrypted = new String( mcryptde2.decrypt( cmesx ) );
            			} catch (Exception e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
                    	
                    	//ok funguje este musim vyriesit z hex do stringu v nacitani
                        
                        //takto to mam ako class dajjakoold v tomto classe
                        String dajakooldx = dajjakoold(akooldx);
                        
                        //takto ak ulozene citatelne
                        //map.put(TAG_CMES,  "[ " + dajakooldx + " ] " + cmesx);
                        
                        //takto ak ulozene cryptovane
                        decrypted=decrypted.trim();
                        
                        //String sdecrypted = hexToAscii(decrypted);
                        String sdecrypted = hexToString(decrypted);
                        
                        map.put(TAG_CMES,  "[ " + dajakooldx + " ] " + sdecrypted);
                        
                        }

                        map.put(TAG_CSEND, csendx);
                        map.put(TAG_CRECE, crecex);
                        map.put(TAG_CKTOS, cktosx);
                        map.put(TAG_ISTAT, istatx);
                        map.put(TAG_INICK, inickx);
                        map.put(TAG_CREAD, creadx);

                        
                        // adding HashList to ArrayList
                        productsList.add(map);
                        
                        zmazatallsms="0";
                        zmazatsms="0";
                        inputsms="";
                    }
                } else {
 
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter =new CustomAdapter(
                    		getActivity(), productsList,
                        R.layout.communic_row, new String[] { TAG_CID, TAG_CMES, TAG_CSEND, TAG_CRECE, TAG_CKTOS, TAG_CREAD },
                        new int[] { R.id.cidx, R.id.cmesx, R.id.csendx, R.id.crecex, R.id.cktosx, R.id.creadx });
                    // updating listview
                    setListAdapter(adapter);
                    
                    //toto ked odremujem tak spadne app ked pretocim z LAND do PORT preto som musel zakazat zmenu orientacie v
                    //KontaktyActivity aj KontaktSpravyActivity
                    nick1 = (TextView) getActivity().findViewById(R.id.nick1);
                    String nick1s = productsList.get(0).get(TAG_INICK);
                    nick1.setText(nick1s);
                    stat1 = (TextView) getActivity().findViewById(R.id.stat1);
                    String stat1s = productsList.get(0).get(TAG_ISTAT);
                    stat1.setText(stat1s);
                    
                    flag1 = (ImageView) getActivity().findViewById(R.id.flag1);
                    String advidx = kontaktcislo;
                    String imageUrl = "http://www.eshoptest.sk/androidsnow/flags/flag_" + advidx + ".jpg";
            	   	LoadImageFromWebOperations(imageUrl);


                }
            });
 
        }
 
    }
    //koniec LoadSMS
    
public void LoadImageFromWebOperations(String url) {
    	
    	try {
   	   	  //ImageView i = (ImageView) getActivity().findViewById(R.id.adflag1);
   	   	  //String imageUrl = "http://www.eshoptest.sk/androidrsp/flag_cz.jpg";
   	      String imageUrl = url;
   	   	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
   	   	  flag1.setImageBitmap(bitmap); 
   	   	} catch (MalformedURLException e) {
   	   	  e.printStackTrace();
   	   	} catch (IOException e) {
   	   	  e.printStackTrace();
   	   	}
    	
    }
    //koniecloadimage
    
public class CustomAdapter extends SimpleAdapter {
    	
    	private List<Map<String, ?>> itemList;
    	
        @SuppressWarnings("unchecked")
		public CustomAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			
			this.itemList = (List<Map<String, ?>>) data;

		}
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {            

        	   // A ViewHolder keeps references to children views to avoid unneccessary calls            
        	   // to findViewById() on each row.            
        	   ViewHolder holder;            
        	   // When convertView is not null, we can reuse it directly, there is no need            
        	   // to reinflate it. We only inflate a new View when the convertView supplied            
        	   // by ListView is null.            

        	   if (convertView == null) { 

				convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.communic_row, parent, false);   
        	    // Creates a ViewHolder and store references to the two children views                
        	    // we want to bind data to.               
        	    holder = new ViewHolder();                
        	    holder.smstext = (TextView) convertView.findViewById(R.id.cmesx);
        	    holder.meidtext = (TextView) convertView.findViewById(R.id.cmex);
        	    holder.advidtext = (TextView) convertView.findViewById(R.id.csendx);
        	    //holder.icon = (ImageView) convertView.findViewById(R.id.icon);                
        	    convertView.setTag(holder);            
        	   } else {                
        	    // Get the ViewHolder back to get fast access to the TextView                
        	    // and the ImageView.
        	    holder = (ViewHolder) convertView.getTag();

        	   }            

        	   //nastavi data do holderu z itemlistu
        	   //holder.name.setText("xkxkxkxkx");
        	   //holder.icon.setImageBitmap( mIcon1 );

        	   holder.smstext.setText((CharSequence) itemList.get(position).get(TAG_CMES));
        	   holder.advidtext.setText((CharSequence) itemList.get(position).get(TAG_CSEND));
        	   holder.advidtext.setBackgroundResource(R.drawable.backtext_yellow);
        	   holder.advidtext.setTextColor(Color.BLACK);
        	   holder.meidtext.setText(getText(R.string.textme));
        	   holder.meidtext.setBackgroundResource(R.drawable.backtext_blue);
        	   holder.meidtext.setTextColor(Color.BLACK);
        	   
        	   //toto mi nechodilo to lp
        	   //LayoutParams lp = (LayoutParams) holder.smstext.getLayoutParams();
        	   
        	   holder.meidtext.setVisibility(View.VISIBLE);
        	   holder.advidtext.setVisibility(View.VISIBLE);
        	   
        	   String mojasms = itemList.get(position).get(TAG_CKTOS).toString();
        	   if( mojasms.equals("1")) {
        		   holder.smstext.setTextColor(Color.BLACK);
        		   //holder.smstext.setBackground(getResources().getDrawable(R.drawable.LightBlue));
        		   holder.smstext.setBackgroundResource(R.drawable.LightBlue);
        		   holder.advidtext.setVisibility(View.GONE);

        	   }else{
        		   holder.smstext.setTextColor(Color.BLACK);
        		   //len pre API > 16 holder.smstext.setBackground(getResources().getDrawable(R.drawable.LightYellow));
        		   //aj pre api 14 holder.smstext.setBackgroundResource(R.drawable.LightYellow);
        		   holder.smstext.setBackgroundResource(R.drawable.LightYellow);
        		   holder.meidtext.setVisibility(View.GONE);

        	   }
        		   


        	   return convertView;
        	  }  

        	class ViewHolder {            
        	    TextView smstext;
        	    TextView meidtext;
        	    TextView advidtext;
        	    //ImageView icon;        
        	} 
        

        
    }
    //koniec  customadapter
    
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
        	
        	String strx = String.valueOf(hexData[count]);
        	if( strx.equals("c")) {
        		
        		int firstDigit = Character.digit(hexData[count], 16);
                int secDigit = Character.digit(hexData[count + 1], 16);
                int treDigit = Character.digit(hexData[count + 2], 16);
                int lastDigit = Character.digit(hexData[count + 3], 16);
                int decimal = firstDigit * 4096 + secDigit * 256 + treDigit * 16 + lastDigit;
                if( decimal >= 49792 && decimal <= 49855 ) { decimal = decimal - 49664; } //c280-c2bf
                if( decimal >= 50048 && decimal <= 50111 ) { decimal = decimal - 49856; } //c3
                if( decimal >= 50304 && decimal <= 50367 ) { decimal = decimal - 50048; } //c4
                if( decimal >= 50560 && decimal <= 50623 ) { decimal = decimal - 50240; } //c580-c5bf
                if( decimal >= 50816 && decimal <= 50879 ) { decimal = decimal - 50432; } //c6
                if( decimal >= 51072 && decimal <= 51135 ) { decimal = decimal - 50624; } //c7
                if( decimal >= 51328 && decimal <= 51391 ) { decimal = decimal - 50816; } //c880-c8bf
                if( decimal >= 51584 && decimal <= 51647 ) { decimal = decimal - 51008; } //c9
                if( decimal >= 51840 && decimal <= 51903 ) { decimal = decimal - 51200; } //ca
                if( decimal >= 52096 && decimal <= 52159 ) { decimal = decimal - 51392; } //cb
                if( decimal >= 52352 && decimal <= 52415 ) { decimal = decimal - 51584; } //cc
                if( decimal >= 52608 && decimal <= 52671 ) { decimal = decimal - 51776; } //cd
                if( decimal >= 52864 && decimal <= 52927 ) { decimal = decimal - 51968; } //ce
                if( decimal >= 53120 && decimal <= 53183 ) { decimal = decimal - 52160; } //cf80-cfbf
                count += 2;
                sb.append((char)decimal);
        		
        	}else{
        	
        		int firstDigit = Character.digit(hexData[count], 16);
        		int lastDigit = Character.digit(hexData[count + 1], 16);
        		int decimal = firstDigit * 16 + lastDigit;
        		sb.append((char)decimal);
        	}
        }
        return sb.toString();
    }
    

    
 
    
    //dni, hodiny, minuty zo sekund
    public String dajjakoold(String sekundy) {

    	String sekx=sekundy;
    	Float dni;
    	Float hodiny;
    	Float minuty;
    	
    	Float f =Float.parseFloat(sekx);
    	minuty=f/60 - 0.49f;
    	hodiny=f/3600 - 0.4999f;
    	dni=f/86400 - 0.499999f;
    	double dminuty=(double)Math.round(minuty);
    	double dhodiny=(double)Math.round(hodiny);
    	double ddni=(double)Math.round(dni);

    	
    	DecimalFormat sf = new DecimalFormat("0");
    	
    	
    	String minx = sf.format(dminuty);
    	String hodx = sf.format(dhodiny);
    	String dnix = sf.format(ddni);
    	
    	if( ddni == 1 ){ dnix = dnix + getString(R.string.den1); }
    	if( ddni > 1 ){ dnix = dnix + getString(R.string.den2); }
    	if( dhodiny == 1 ){ hodx = hodx + getString(R.string.hodina1); }
    	if( dhodiny > 1 ){ hodx = hodx + getString(R.string.hodina2); }
    	if( dminuty == 1 ){ minx = minx + getString(R.string.minuta1); }
    	if( dminuty > 1 ){ minx = minx + getString(R.string.minuta2); }
    	
    	
    	if( ddni == 0 ){ dnix = ""; }
    	if( dhodiny == 0 ){ hodx = ""; }
    	
    	
    	if( ddni > 0 ){ hodx = ""; minx = ""; }
    	if( dhodiny > 0 && ddni == 0 ){ dnix = ""; minx = ""; }
    	
    	String jakooldx = getString(R.string.akodlhopred) + " " + dnix + hodx + minx + " " + getString(R.string.akodlhoza);
    

    	
        return jakooldx;
    }
    //koniec dni, hodiny, minuty zo sekund
    

	
	//oncontextmenu
    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v,
    ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    int position = info.position;
	 	
	 	String mesx = productsList.get(position).get(TAG_CMES);
	 	//String cidx = productsList.get(position).get(TAG_CID);
 	
    	//menu.setHeaderTitle(cidx + "/" + mesx);
    	menu.setHeaderTitle(mesx);
    
    getActivity().getMenuInflater().inflate(R.menu.kontext_sms, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    switch (item.getItemId()) {
        case R.id.smszmazat:
        	
        	db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
    	 	java.util.Date date= new java.util.Date();
    		Timestamp ts = new Timestamp(date.getTime());
    		String tss = ts + "";
    	 	String UpdateSql = "UPDATE mojedomeny SET datz2='" + tss + "' WHERE server2=" + kontaktcislo +"";
    	 	db2.execSQL(UpdateSql);
    	 	db2.close();
        	
        	int position = info.position;
            zmazatallsms="1";
            zmazatsms = productsList.get(position).get(TAG_CID);
            productsList = new ArrayList<HashMap<String, String>>();
    		new loadSMS().execute();

    	break;
    	
        case R.id.allsmszmazat:
        	
        	zmazatallsms="2";
            zmazatsms="0";
            productsList = new ArrayList<HashMap<String, String>>();
    		new loadSMS().execute();

    	break;
    	


        }

        return super.onContextItemSelected(item);
    }
    //koniec oncontextmenu

    
    public String toHex(String arg) throws UnsupportedEncodingException {
  	  return String.format("%x", new BigInteger(1, arg.getBytes("utf-8")));
  	}
	
	
	
}
//koniec detailfragment