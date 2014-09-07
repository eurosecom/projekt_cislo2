package com.eusecom.snowsmsden;

import java.sql.Timestamp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
 
public class NotifyClickActivity extends Activity {

 
    TextView inputEdiUser;
    private SQLiteDatabase db2=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aka_sms);
        
        //toto ked tu nebolo a spustil som odkaz na notify ked som pred tym vymazal pamat (nebol zostatok activit )tak spadla tato activity na
        //stahovani obrazka kvoli problemu s policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
   		StrictMode.setThreadPolicy(policy);
        
        //toto uklada preference
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	Editor editor = prefs.edit();

        editor.putString("okpristup", "0").apply();

    	editor.commit();
    	
    	
    	if(SettingsActivity.getOkPristup(getBaseContext()).equals("1"))
    	{

    		
    		
    	}else{	
    		update(0);

    	}
       
        
        
    }
    //koniec oncreate
    
    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("NotificationMessage"))
            {

                // extract the extra-data in the Notification
                String msg = extras.getString("NotificationMessage");
                inputEdiUser = (TextView) findViewById(R.id.inputEdiUser);
                inputEdiUser.setVisibility(View.VISIBLE);
                inputEdiUser.setText(msg);
                
                db2=(new DatabaseDomeny(this)).getWritableDatabase();
            	
           	 	java.util.Date date= new java.util.Date();
           		Timestamp ts = new Timestamp(date.getTime());
           		String tss = ts + "";
           	 	String UpdateSql = "UPDATE mojedomeny SET datz2='" + tss + "', datm2='" + tss + "'  WHERE server2=" + msg + " ";
           	 	db2.execSQL(UpdateSql);
           	 	db2.close();
                
                //toto zavolam ale musim zistit index (mozem ho dat na 0 jako ze prve v poradi a pred tym upravit datz2)
                //alebo urobim uplne novu aktivitu
           	 	int indexi=0;
                Intent i = new Intent(this, KontaktSpravyListActivity.class);
    			i.putExtra(KontaktSpravyListActivity.INDEX, indexi);
    			startActivity(i);
    			finish();
                
                
            }
        }
        
    }
    //koniec onNewIntent
    
    
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
   					finish();
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
           	
          //spustim activitu
       		onNewIntent(getIntent());
           	
   		}else{
   			
   			new AlertDialog.Builder(this)
   	        .setMessage(getString(R.string.wrongpsw))
   	        .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
   	            public void onClick(DialogInterface dialog, int which) { 
   	              
   	            	finish();
   	            }
   	         })

   	         .show();
   		}
   		

   	}
       //koniec processupdate
   	
   	

    
    

}
//koniec activity