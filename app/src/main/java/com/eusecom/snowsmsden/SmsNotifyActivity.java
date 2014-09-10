package com.eusecom.snowsmsden;

import java.sql.Timestamp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;
 
public class SmsNotifyActivity extends Activity {

 
    TextView inputPriServer;
    TextView inputPriUser;
    private SQLiteDatabase db2=null;
    
      
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aka_sms);
        
        Intent i = getIntent();
        
        Bundle extras = i.getExtras();
        String newsmsx = extras.getString("newsms");
        int newsmsi = Integer.parseInt(newsmsx);

        String newsmstext = getString(R.string.receivedsmsfrom) + " " + newsmsx;
        

        
        Intent notificationIntent = new Intent(getApplicationContext(), NotifyClickActivity.class);
        notificationIntent.putExtra("NotificationMessage", newsmsx);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi2 = PendingIntent.getActivity(getApplicationContext(),newsmsi,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationCompat.Builder mBuilder =
        	    new NotificationCompat.Builder(this)
        	    .setSmallIcon(R.drawable.eurosecom)
        	    .setContentIntent(pi2)
        	    .setContentTitle(getString(R.string.receivedsms))
        	    .setContentText(newsmstext);
                
        String names = SettingsActivity.getNewSms(this);
        int namei = Integer.parseInt(names);
        if( namei > 0 ) {
        	
        	db2=(new DatabaseDomeny(this)).getWritableDatabase();
        	
       	 	java.util.Date date= new java.util.Date();
       		Timestamp ts = new Timestamp(date.getTime());
       		String tss = ts + "";
       	 	String UpdateSql = "UPDATE mojedomeny SET datz2='" + tss + "', mail2='" + getString(R.string.newsms) + "' WHERE server2=" + names + " ";
       	 	db2.execSQL(UpdateSql);
       	 	db2.close();
        	
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(newsmsi, mBuilder.build());
            finish();
        }
        else{

        finish();
        }              

        
    }
    //koniec oncreate
     

}