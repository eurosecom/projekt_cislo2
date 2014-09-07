package com.eusecom.snowsmsden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SpravyListActivity extends FragmentActivity {
	public static final String INDEX = "index";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		Intent i = getIntent();
		int index = i.getIntExtra(INDEX, 0);

		SpravyListFragment f = SpravyListFragment.newInstance(index);
		
		// Pøidá fragment do View s id detail
		getSupportFragmentManager().beginTransaction().add(R.id.detail, f).commit();
	}
}