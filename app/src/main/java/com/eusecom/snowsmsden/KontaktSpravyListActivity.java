package com.eusecom.snowsmsden;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

public class KontaktSpravyListActivity extends FragmentActivity {
	public static final String INDEX = "index";
	
	private MenuItem menuitem0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent i = getIntent(); 
		int index = i.getIntExtra(INDEX, 0);

		KontaktSpravyListFragment f = KontaktSpravyListFragment.newInstance(index);
		
		// Pøidá fragment do View s id detail
		getSupportFragmentManager().beginTransaction().add(R.id.detail, f).commit();
		

	}
	//koniec oncreate
	
	// Response from VyberSpravu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {

        	//String valueThatYouWantToSend = "abrakadabraKontaktSpravyActivity"; /// just the value
        	String valueThatYouWantToSend = data.getExtras().getString(VyberSpravuActivity.NAME_KEY);
            sendValueToFragments(valueThatYouWantToSend);
        }

 
    }
    //koniec onacivityresult
    
    protected void sendValueToFragments(String value) {
        // it has to be the same name as in the fragment
        Intent intent = new Intent("com.eusecom.snowsmsden.action.UI_UPDATE");
        intent.putExtra("UI_KEY", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_kontaktspravy, menu);
        
        menuitem0 = menu.getItem(0);
        menuitem0.setEnabled(true);
        
        return true;
    }
	//koniec oncreateoption
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	

    	switch (item.getItemId()) {
        
    		case R.id.action_hladajspravy:
            
            
    			Intent iz = new Intent(getApplicationContext(), VyberSpravuActivity.class);
    			startActivityForResult(iz, 100);
            
            	return true;

            case R.id.action_exitspravy:
            	finish();

                return true;
        }
    
        return super.onOptionsItemSelected(item);
    }
    //koniec onoptionselected
	

	//koniec activity
}