package com.eusecom.snowsmsden;

//zaciatok prac na projekte 9.7.2014, na GooglePlay 19.08.2014

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SnowsmsdenActivity extends Activity implements
SharedPreferences.OnSharedPreferenceChangeListener {
	

	Button btnKontakty;
	Button btnSpravylist;
	TextView mynumber;
	
	PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;
    
    EditText txtTitle;
    String idtitle;
    ImageView image1;
    


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainscreen);
		
		PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
		
		
			mynumber = (TextView) findViewById(R.id.mynumber);
            String mojecis = SettingsActivity.getUserId(this);
            mynumber.setText(mojecis);

            //toto urobi len pri prvom spusteni !!!! skusim este onSaveInstanceState ulozenie okpristup a precitanie, oblubene chrome
            if(savedInstanceState == null){
            //toto uklada preference
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	Editor editor = prefs.edit();
            editor.putString("okpristup", "0").apply();
        	editor.commit();
            }else{
            //aj toto funguje cez prenos stavu v bundle onSaveInstanceState
            //String myString = savedInstanceState.getString("MyString");
            String myOkpristup = savedInstanceState.getString("Myokpristup");
            //mynumber.setText(myOkpristup);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	Editor editor = prefs.edit();
            editor.putString("okpristup", myOkpristup).apply();
        	editor.commit();
            }
            
            setSemafor();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
   		StrictMode.setThreadPolicy(policy);
   		
   		//imge click event
   		image1 = (ImageView) findViewById(R.id.casnicka);
   		image1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	
            	if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
            	{
            		//toto uklada preference
                	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                	Editor editor = prefs.edit();

                    editor.putString("okpristup", "0").apply();

                	editor.commit();
                	setSemafor();
            		
            	}else{	
            		update(0);

            	}
            	
            		
            }
        });
   		

   		btnKontakty = (Button) findViewById(R.id.btnKontakty);
   		btnKontakty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	
            	if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
            	{
            	
            		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(SnowsmsdenActivity.this, MojeKontaktyActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent i = new Intent(getApplicationContext(), MojeKontaktyActivity.class);
                        startActivity(i);
                	}
            		
            	}else{ update(0); }
            	//ak okpristup=1
            	
            }
        });
   		
   		btnSpravylist = (Button) findViewById(R.id.btnSpravylist);
   		btnSpravylist.setOnClickListener(new View.OnClickListener() {
   			
            @Override
            public void onClick(View view) {
            	
            		if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
            		{
   			
   			if (android.os.Build.VERSION.SDK_INT>=16) {
        		
                Intent slideactivity = new Intent(SnowsmsdenActivity.this, KontaktyActivity.class);
          	   
  				Bundle bndlanimation =
  						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
  				startActivity(slideactivity, bndlanimation);
            	}else{
            	
                    Intent i = new Intent(getApplicationContext(), KontaktyActivity.class);
                    startActivity(i);
            	}
   			
            		}else{ update(0); }
            		//ak okpristup=1

            }
        });
   		

   		setupNotifySMS();
        //toto urobi kazdu 1 minutu a zacne 1 minutu po spusteni
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*60*1, 1000 * 60 * 1, pi);
        
            
		
		
	}
	//koniec oncreate
	
	private void setupNotifySMS() {
        br = new BroadcastReceiver() {
               @Override
               public void onReceive(Context c, Intent i) {

                   //ak je pripojenie do internetu
                   if (isOnline()) {
                       Intent ix = new Intent(c, AkaSmsActivity.class);
                       c.startActivity(ix);
                   }
                      
                      }
               };
        registerReceiver(br, new IntentFilter("com.eusecom.snowsmsden.SnowsmsdenActivity") );
        pi = PendingIntent.getBroadcast( this, 0, new Intent("com.eusecom.snowsmsden.SnowsmsdenActivity"), 0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
  }
  //koniec setup 
	

    // na ondestroy 
 	@Override
 	public void onDestroy() {


 	    super.onDestroy();
 	    PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
 	    am.cancel(pi); 
	    unregisterReceiver(br);


 	}
 	//koniec ondestroy
 	
 	


 	
 	
	//optionsmenu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.options_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.preferences:
			
			if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
            		{
				
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
				startActivity(new Intent(this, SettingsActivity.class));
			}
			else {
				startActivity(new Intent(this, EditPreferencesNew.class));
			}
            		}else{ update(0); }

			return(true);
			
		case R.id.xspravy:
			
			if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
    				{
				if (android.os.Build.VERSION.SDK_INT>=16) {
        		
                Intent slideactivity = new Intent(SnowsmsdenActivity.this, VyberSpravuActivity.class);
          	   
  				Bundle bndlanimation =
  						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
  				startActivity(slideactivity, bndlanimation);
            	}else{
            	
                    Intent i = new Intent(getApplicationContext(), VyberSpravuActivity.class);
                    startActivity(i);
            	}
    				}else{ update(0); }

			return(true);

	
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	//koniec optionsmenu
	
	//test ci je internet pripojeny
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    //koniec test ci je internet pripojeny
    

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(SettingsActivity.USER_ID) ) {
			//cinnost ktoru urobi ak sa zmenili preferences 
			mynumber = (TextView) findViewById(R.id.mynumber);
            String mojecis = SettingsActivity.getUserId(this);
            mynumber.setText(mojecis);
		}

	                  
	}
    //koniec onSharedPreferenceChanged
	
	


	@SuppressLint("InflateParams")
	private void update(final long rowId) {
		if (rowId>=0) {
		LayoutInflater inflater=LayoutInflater.from(this);
		View addView=inflater.inflate(R.layout.psw_log, null);
		final DialogWrapper wrapper=new DialogWrapper(addView);

		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(R.string.loginpsw)
			.setView(addView)
			.setPositiveButton(R.string.login,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					//skus ci psw OK
					processUpdate(wrapper, rowId);
				}
			})
			.setNegativeButton(R.string.cancel,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					// ignore, just dismiss
				}
			});
		
		AlertDialog dialog = builder.create();
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	    dialog.show();

		
		
		}
		//koniec ak rowId>0
	}
	//koniec update
	
	class DialogWrapper {
		EditText titleField=null;
		View base=null;
		
		DialogWrapper(View base) {
			this.base=base;
			titleField=(EditText)base.findViewById(R.id.title);
			
			
		}
		
		String getTitle() {
			return(getTitleField().getText().toString());
		}
		
		
		private EditText getTitleField() {
			if (titleField==null) {
				titleField=(EditText)base.findViewById(R.id.title);
			}
			
			return(titleField);
		}
		

	}
	//koniec wrapper
	
	private void processUpdate(DialogWrapper wrapper, long rowId) {
		
		String getpsw = wrapper.getTitle();
		if(getpsw.equals(SettingsActivity.getUserPsw(this)))
		{ 
			//toto uklada preference
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	Editor editor = prefs.edit();

            editor.putString("okpristup", "1").apply();

        	editor.commit();
		}
		setSemafor();

	}
    //koniec processupdate
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
	  //savedInstanceState.putBoolean("MyBoolean", true);
	  //savedInstanceState.putDouble("myDouble", 1.9);
	  //savedInstanceState.putInt("MyInt", 1);
	  savedInstanceState.putString("MyString", "Welcome back to Android");
	  savedInstanceState.putString("Myokpristup", SettingsActivity.getOkPristup(getBaseContext()));

	  // etc.
	}
	//koniec onSaveInstanceState
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	  //boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
	  //double myDouble = savedInstanceState.getDouble("myDouble");
	  //int myInt = savedInstanceState.getInt("MyInt");
	  //String myString = savedInstanceState.getString("MyString");
	}
	//koniec onRestoreInstanceState
	
	//nastav semafor
    public boolean setSemafor() {

    	ImageView myImgView = (ImageView) findViewById(R.id.casnicka);
    	if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
    	{
  	  	myImgView.setImageResource(R.drawable.go);
    	}else{
    		myImgView.setImageResource(R.drawable.stop);	
    	}
    		
    	
    	
        return false;
    }
    //koniec nastav semafor
	
	
	
	
   
}
//koniec activity