package com.eusecom.snowsmsden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DetailActivity extends FragmentActivity {
	public static final String INDEX = "index";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		Intent i = getIntent();
		int index = i.getIntExtra(INDEX, 0);

		DetailFragment f = DetailFragment.newInstance(index);

		getSupportFragmentManager().beginTransaction().add(R.id.detail, f).commit();
	}
}