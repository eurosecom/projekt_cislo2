/* tato fragmentovana aktivita nacita zoznam z sqllite v smartfone a po kliknuti otvori MojKontaktDetailActivity kde 
 	zobrazi detailne nastavenie kontaktu z sqllite 
 
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MojeKontaktyActivity extends FragmentActivity implements
		MojeKontaktyFragment.OnRulerSelectedListener {

	private boolean mDualPane;
    Button btnRefresh;
    private MenuItem menuitem0;
    private MenuItem menuitem1;
    EditText inputFind;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

    		setContentView(R.layout.mojekontakty);
    		
    		//dynamicka definicia fragmentu MojeKontaktyFragment.java
    		MojeKontaktyFragment fk = new MojeKontaktyFragment();
			FragmentTransaction ftk = getSupportFragmentManager().beginTransaction();
			ftk.replace(R.id.seznam, fk);
			//ftk.addToBackStack(null); ak by som toto nechal ked stlacim back zostane cisty display v mojekontakty
			ftk.commit();
    		
    		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); }
    		
        	// Pokud je dostupn? View s id detail, je layout dvousloupcov?
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
                    Intent i = new Intent(getApplicationContext(), MojeKontaktyActivity.class);
                    startActivity(i);
                    finish();
     
                }
            });
    						}
        	

		
	}
//koniec oncreate
		
	@Override
	public void onRulerSelected(int index) {


		if (mDualPane) { // Dvousloupcov? layout

			MojKontaktDetailFragment f = MojKontaktDetailFragment.newInstance(index);

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.detail, f);
			// Vol?n?m FragmentTransaction.addToBackStack dos?hneme toho,
			// ?e p?i stisknut? tla??tka zp?t se Fragment vym?n? s t?m,
			// co v R.id.detail bylo p?edt?m (jin? DetailFragment nebo nic).
			ft.addToBackStack(null);
			ft.commit();
		} else { // Jednosloupcov? layout
			Intent i = new Intent(this, MojKontaktDetailActivity.class);
			i.putExtra(MojKontaktDetailActivity.INDEX, index);
			startActivity(i);
		}
		

		
		
	}
	//koniec onrulerselected
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_mojekontakty, menu);
        
        menuitem0 = menu.getItem(0);
        menuitem1 = menu.getItem(1);
        menuitem0.setEnabled(true);
        menuitem1.setEnabled(true);



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
    				Intent iz = new Intent(getApplicationContext(), MojeKontaktyActivity.class);
                    startActivity(iz);
                    finish();
                								}
    				}
    			
            return true;
        
            case R.id.action_newcontact:
            	
            	Intent iz = new Intent(getApplicationContext(), NovyKontaktActivity.class);
                startActivityForResult(iz, 100);

                return true;

            case R.id.action_exit:
            	finish();

                return true;
                
                
            case R.id.action_hladajspravy:
                
                

                
                return true;
                
        }
    
        return super.onOptionsItemSelected(item);
    }
    //koniec onoptionselected
    
    // Response from novykontaktactivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
        	


        	//toto je OK vlozi do fragmentu novy list kontaktov prepisany cez povodny lebo nemozem nahradit fragment
        	//definovany staticky v xml len dynamicky, prerobil som 8.8.2014 na dynamicky
        	int reset=1;
        	if( reset == 1 ){
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	MojeKontaktyFragment llf = new MojeKontaktyFragment();
        	ft.replace(R.id.seznam, llf);
        	ft.commit();
        	
        	//toto vlozifragment index 0 = prvy do r.id.detail ak existuje
        	if( findViewById(R.id.detail) != null )
        		{
        	int indexx=0;
     	 	MojKontaktDetailFragment fd = MojKontaktDetailFragment.newInstance(indexx);
     	 	FragmentTransaction ftd = getSupportFragmentManager().beginTransaction();
 			ftd.replace(R.id.detail, fd);
 			ftd.addToBackStack(null);
 			//getFragmentManager().popBackStack();
 			ftd.commit();
        		}
        	}


        	

        }

 
    }
    //koniec onacivityresult
    

    
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
