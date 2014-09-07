package com.eusecom.snowsmsden;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MojKontaktDetailActivity extends FragmentActivity {
	public static final String INDEX = "index";
	
	private MenuItem menuitem0;
	Button btnSavex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kontaktdetail_activity);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent i = getIntent(); 
		int index = i.getIntExtra(INDEX, 0);

		MojKontaktDetailFragment f = MojKontaktDetailFragment.newInstance(index);
		
		// Pøidá fragment do View s id detail
		getSupportFragmentManager().beginTransaction().add(R.id.detail, f).commit();
		
	}
	//koniec oncreate
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_kontaktdetail, menu);
        
        menuitem0 = menu.getItem(0);
        menuitem0.setEnabled(true);
        
        return true;
    }
	//koniec oncreateoption
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	

    	switch (item.getItemId()) {
        
    		case R.id.action_hladajspravy:
            

            
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