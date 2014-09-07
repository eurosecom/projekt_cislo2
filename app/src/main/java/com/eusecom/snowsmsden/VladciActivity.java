/* tato fragmentovana aktivita nacita zoznam z mysql na webe a po kliknuti otvori DetailActivity kde 
 	zobrazi textview a dalsie widgety sprav naplnene z mysql z webu ku kliknutemu indexu 
 
*/

package com.eusecom.snowsmsden;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

public class VladciActivity extends FragmentActivity implements
		SeznamFragment.OnRulerSelectedListener {

	private boolean mDualPane;
    Button btnRefresh;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



    		setContentView(R.layout.vladci);
        	// Pokud je dostupné View s id detail, je layout dvousloupcový
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
                    Intent i = new Intent(getApplicationContext(), VladciActivity.class);
                    startActivity(i);
                    finish();
     
                }
            });
    						}
        	

		
	}
//koniec oncreate
		
	@Override
	public void onRulerSelected(int index) {
		if (mDualPane) { // Dvousloupcový layout
			DetailFragment f = DetailFragment.newInstance(index);

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.detail, f);
			// Voláním FragmentTransaction.addToBackStack dosáhneme toho,
			// že pøi stisknutí tlaèítka zpìt se Fragment vymìní s tím,
			// co v R.id.detail bylo pøedtím (jiný DetailFragment nebo nic).
			ft.addToBackStack(null);
			ft.commit();
		} else { // Jednosloupcový layout
			Intent i = new Intent(this, DetailActivity.class);
			i.putExtra(DetailActivity.INDEX, index);
			startActivity(i);
		}
	}
	
//koniec activity	
}
