/* tato fragmentovana aktivita nacita zoznam z sqllite v smartfone a po kliknuti otvori KontaktSpravyListActivity kde 
 	zobrazi zoznam sprav z mysql z webu ku kliknutemu indexu 
 
*/

package com.eusecom.snowsmsden;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class KontaktyActivity extends FragmentActivity implements
		KontaktyFragment.OnRulerSelectedListener {

	private boolean mDualPane;
    Button btnRefresh;
    private MenuItem menuitem0;
    private MenuItem menuitem1;
    EditText inputFind;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

    		setContentView(R.layout.kontakty);
    		
    		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); }
    		
        	// Pokud je dostupny View s id detail, je layout dvousloupcove
    		mDualPane = findViewById(R.id.detail) != null;
    		
    		if (mDualPane) {
    		} else {
            // Buttons
            btnRefresh = (Button) findViewById(R.id.btnRefresh);

      
            // new obj click event
            btnRefresh.setOnClickListener(new View.OnClickListener() {
     
                @Override
                public void onClick(View view) {
                    // Launching All products Activity
                    Intent i = new Intent(getApplicationContext(), KontaktyActivity.class);
                    startActivity(i);
                    finish();
     
                }
            });
    						}
        	

		
	}
//koniec oncreate
		
	@Override
	public void onRulerSelected(int index) {
		
	//ak je pripojenie do internetu
	if (isOnline()) 
	{
	//ak je pripojenie

		if (mDualPane) { // Dvousloupcovy layout

			KontaktSpravyListFragment f = KontaktSpravyListFragment.newInstance(index);

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.detail, f);
			// Volbou FragmentTransaction.addToBackStack dosahneme toho,
			// ze pri stisknuti tlacitka zpet se Fragment vymeni s tym,
			// co v R.id.detail bylo predtym (jiny DetailFragment nebo nic).
			ft.addToBackStack(null);
			ft.commit();
		} else { // Jednosloupcovy layout
			Intent i = new Intent(this, KontaktSpravyListActivity.class);
			i.putExtra(KontaktSpravyListActivity.INDEX, index);
			startActivity(i);
		}
		
	//ak nie je pripojenie do internetu
    }else{
    	 
        new AlertDialog.Builder(this)
        .setTitle(getString(R.string.niejeinternet))
        .setMessage(getString(R.string.potrebujeteinternet))
        .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
              
            	//finish();
            }
         })

         .show();

        

   }
   //koniec ak nie je Internet
		
		
	}
	//koniec onrulerselected
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_kontakty, menu);
        
        menuitem0 = menu.getItem(0);
        menuitem1 = menu.getItem(1);
        menuitem0.setEnabled(true);
        menuitem1.setEnabled(true);



        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	
    	//tuto nereagoval na premennu strana a nezhasinal menu item a
    	//menu.findItem(R.id.action_previous).setEnabled(true);
        //menu.findItem(R.id.action_next).setEnabled(true);
    	//menu.getItem(0).setEnabled(true);
        //menu.getItem(1).setEnabled(true);
        
        //if( strana.equals("1")) { menu.findItem(R.id.action_previous).setEnabled(false); }
        //if( strana.equals("3")) { menu.findItem(R.id.action_next).setEnabled(false); }

        //if( strana.equals("3")) { menu.getItem(1).setEnabled(false); }

        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	

    	switch (item.getItemId()) {
    	
    		case R.id.action_hladajcontact:
    			
    				inputFind = (EditText) findViewById(R.id.inputFind);
    				
    				if(inputFind.getVisibility() == View.GONE)
    				{
    				inputFind.setVisibility(View.VISIBLE);
    				inputFind.requestFocus();
    				}else{
    				inputFind.setVisibility(View.GONE);
    				String hladajtext= inputFind.getText().toString();
                	if(hladajtext.length() > 0 ) {
    				Intent iz = new Intent(getApplicationContext(), KontaktyActivity.class);
                    startActivity(iz);
                    finish();
                								}
    				}
    			
            return true;
        
            case R.id.action_newcontact:

                return true;

            case R.id.action_exit:
            	finish();

                return true;
                
                
            case R.id.action_hladajspravy:
                
                
                Intent iz = new Intent(getApplicationContext(), VyberSpravuActivity.class);
                startActivityForResult(iz, 100);
                
                return true;
                
        }
    
        return super.onOptionsItemSelected(item);
    }
    //koniec onoptionselected
    
    // Response from VyberSpravu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {

        	//String valueThatYouWantToSend = "abrakadabraKontaktyActivity"; /// just the value
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
	
//koniec activity	
}
