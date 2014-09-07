/* Copyright (c) 2008-2018 -- EuroSecom

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
*/
	 
package com.eusecom.snowsmsden;

import java.sql.Timestamp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class NovyKontaktActivity extends Activity {

	Button btnSavex;
	Button btnDelx;
	EditText inputCislo;
    EditText inputNick;
    EditText inputCode;
	
	private SQLiteDatabase db2=null;

	String kontaktcislo;
    String kontaktnick;
    String kontaktcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			setContentView(R.layout.kontaktdetail_fragment);
		
			btnDelx = (Button) findViewById(R.id.btnDelx);
			btnDelx.setVisibility(View.GONE);
			
			db2=(new DatabaseDomeny(this)).getWritableDatabase();
		
			btnSavex = (Button) findViewById(R.id.btnSavex);
			btnSavex.setOnClickListener(new View.OnClickListener() {
   
             @Override
             public void onClick(View arg0) {
          	   
          	   	inputNick = (EditText) findViewById(R.id.inputNick);
          	   	String nicks = inputNick.getText().toString();
          	   	inputCode = (EditText) findViewById(R.id.inputCode);
          	   	String codes = inputCode.getText().toString();
          	   	inputCislo = (EditText) findViewById(R.id.inputCislo);
          	   	String cislos = inputCislo.getText().toString();
          	   	
          	   	int cisloi = Integer.parseInt(cislos);
          	   	
          	   	if( cisloi > 0 ){
        	   	
        	   	java.util.Date date= new java.util.Date();
          		Timestamp ts = new Timestamp(date.getTime());
          		String tss = ts + "";

          	   	String UpdateSql1 = "INSERT INTO mojedomeny ( server2, nick2, pswd2, datz2 ) VALUES " + 
          	   	" ( '" + cislos + "', '" + nicks + "', '" + codes + "', '" + tss + "' ) ";
         	 	db2.execSQL(UpdateSql1);

         	 	db2.close();
          	   	
         	 	
         	 	String ulozenecislo = kontaktcislo;

            	Intent i = getIntent();
            	i.putExtra("ulozenecislo", ulozenecislo);
                setResult(100, i); 
                finish();
          	   	}//ak cislo > 0
         	 	

         	 	 
             }
         });
		



	}
	//koniec oncreate
	

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		


	}
	//koniec ondestroy



    
  
    
	






//koniec activity
}