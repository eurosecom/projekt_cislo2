/* tato fragmentovana aktivita nacita zoznam z mysql na webe a po kliknuti otvori SpravyListActivity kde 
 	zobrazi zoznam sprav z mysql z webu ku kliknutemu indexu 
 
*/

package com.eusecom.snowsmsden;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SpravyActivity extends FragmentActivity implements
		SpravyFragment.OnRulerSelectedListener {

	private boolean mDualPane;
    Button btnRefresh;
    private MenuItem menuitem0;
    private MenuItem menuitem1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



    		setContentView(R.layout.spravy);
        	// Pokud je dostupn� View s id detail, je layout dvousloupcov�
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
                    Intent i = new Intent(getApplicationContext(), SpravyActivity.class);
                    startActivity(i);
                    finish();
     
                }
            });
    						}
        	

		
	}
//koniec oncreate
		
	@Override
	public void onRulerSelected(int index) {
		if (mDualPane) { // Dvousloupcov� layout
			SpravyListFragment f = SpravyListFragment.newInstance(index);

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.detail, f);
			// Vol�n�m FragmentTransaction.addToBackStack dos�hneme toho,
			// �e p�i stisknut� tla��tka zp�t se Fragment vym�n� s t�m,
			// co v R.id.detail bylo p�edt�m (jin� DetailFragment nebo nic).
			ft.addToBackStack(null);
			ft.commit();
		} else { // Jednosloupcov� layout
			Intent i = new Intent(this, SpravyListActivity.class);
			i.putExtra(SpravyListActivity.INDEX, index);
			startActivity(i);
		}
	}
	//koniec onrulerselected
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menuactivity_reklama_slide, menu);
        
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
        
            case R.id.action_previous:

                return true;

            case R.id.action_next:

                return true;
        }
    
        return super.onOptionsItemSelected(item);
    }
    //koniec onoptionselected
	
//koniec activity	
}
