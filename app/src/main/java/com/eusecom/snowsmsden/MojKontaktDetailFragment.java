package com.eusecom.snowsmsden;

import java.io.BufferedReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MojKontaktDetailFragment extends Fragment {
	public static final String INDEX = "index";

    EditText inputCislo;
    EditText inputNick;
    EditText inputCode;
    Button btnSavex;
    Button btnDelx;
    

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;
    
	
	// products JSONArray
	JSONArray products = null;
	JSONObject product;

	String dokindex;
	String zmazatsms="0";
    String zmazatallsms="0";
    String kontaktcislo;
    String mojecislo;
    String inputsms="";
    String kontaktnick;
    String kontaktcode;
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	BufferedReader in;

    
    String hladanie="0";
    String hladajtext="";
    

	public static MojKontaktDetailFragment newInstance(int index) {
		MojKontaktDetailFragment f = new MojKontaktDetailFragment();
		Bundle args = new Bundle();
		args.putInt(INDEX, index);
		f.setArguments(args);
		return f;
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.kontaktdetail_fragment, container, false);
		
		return v;

	}
	//konieconcerateview
	

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//index vybraneho kontaktu
		int index = getArguments().getInt(INDEX, 0);
		

		hladanie = SettingsActivity.getHladanie(getActivity());
		hladajtext = SettingsActivity.getHladajtext(getActivity());
		
		db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();

		if( hladanie.equals("1")) {
			constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
					"FROM mojedomeny WHERE server2 = '%" + hladajtext + "%' OR nick2 LIKE '%" + hladajtext + "%' ORDER BY datm2 DESC, server2",
					null);
		}else{
		constantsCursor2=db2.rawQuery("SELECT _ID, server2, pswd2, name2, nick2, mail2, uzid2 "+
				"FROM mojedomeny WHERE _id > 0 ORDER BY datm2 DESC, server2",null);
		}

		
		  //getString(2) znamena ze beriem 3tiu 0,1,2 premennu teda pswd2
	      if (constantsCursor2.moveToPosition(index)) {
	    	  kontaktcislo=constantsCursor2.getString(1);
	    	  kontaktnick=constantsCursor2.getString(4);
	    	  kontaktcode=constantsCursor2.getString(2);
	      }
	      
	      db2.close();
	      
	      mojecislo = SettingsActivity.getUserId(getActivity());
	      
	      inputCislo = (EditText) getActivity().findViewById(R.id.inputCislo);
          inputCislo.setText(kontaktcislo);
          inputNick = (EditText) getActivity().findViewById(R.id.inputNick);
          inputNick.setText(kontaktnick);
          inputCode = (EditText) getActivity().findViewById(R.id.inputCode);
          inputCode.setText(kontaktcode);
          
          inputCislo.setEnabled(false);
          inputCislo.setFocusable(false);
          
          btnSavex = (Button) getActivity().findViewById(R.id.btnSavex);
       	  btnSavex.setOnClickListener(new View.OnClickListener() {
     
               @Override
               public void onClick(View arg0) {
            	   
            	   inputNick = (EditText) getActivity().findViewById(R.id.inputNick);
            	   String nicks = inputNick.getText().toString();
            	   inputCode = (EditText) getActivity().findViewById(R.id.inputCode);
            	   String codes = inputCode.getText().toString();

            	db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
            	
            	String UpdateSql1 = "UPDATE mojedomeny SET nick2='" + nicks + "', pswd2='" + codes + "' WHERE server2=" + kontaktcislo + " ";
           	 	db2.execSQL(UpdateSql1);
            	
           	 	java.util.Date date= new java.util.Date();
           		Timestamp ts = new Timestamp(date.getTime());
           		String tss = ts + "";
           	 	String UpdateSql = "UPDATE mojedomeny SET datz2='" + tss + "' WHERE server2=" + kontaktcislo + " ";
           	 	db2.execSQL(UpdateSql);
           	 	db2.close();
           	 	
           	 	String valueThatYouWantToSend = "znovulistujpoupdate";
        	 	sendValueToFragments(valueThatYouWantToSend);
        	 	
        	 	String xxx1 = String.format(getString(R.string.contactsaved), kontaktcislo);
           	 	
           	 	Toast.makeText(getActivity(), xxx1,Toast.LENGTH_LONG).show();

           	 	 
               }
           });
       	  
       	  btnDelx = (Button) getActivity().findViewById(R.id.btnDelx);
     	  btnDelx.setOnClickListener(new View.OnClickListener() {
   
             @Override
             public void onClick(View arg0) {
            	 
            	 new AlertDialog.Builder(getActivity())
                 .setTitle(getString(R.string.delete_contact))
                 .setMessage(kontaktcislo)
                 .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) { 

                    	 
                    	 db2=(new DatabaseDomeny(getActivity())).getWritableDatabase();
                      	
                  		String UpdateSql1 = "DELETE FROM mojedomeny WHERE server2=" + kontaktcislo + " ";
                 	 	db2.execSQL(UpdateSql1);
                 	 	db2.close();
                 	 	
                 	 	String valueThatYouWantToSend = "znovulistujpodelete";
                 	 	sendValueToFragments(valueThatYouWantToSend);
                 	 	
                 	 	String xxx1 = String.format(getString(R.string.contactdeleted), kontaktcislo);
                    	 	
                    	 	Toast.makeText(getActivity(), xxx1,Toast.LENGTH_LONG).show();
                 	 	
                 	 	int indexx=0;
                 	 	MojKontaktDetailFragment f = MojKontaktDetailFragment.newInstance(indexx);

             			FragmentTransaction ft = getFragmentManager().beginTransaction();
             			ft.replace(R.id.detail, f);
             			//ft.addToBackStack(null);
             			getFragmentManager().popBackStack();
             			ft.commit(); 

                     }
                 })

                 .setNegativeButton(getString(R.string.textno), new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) { 

                     }
                 })
                 .show();

            	 	

          	   
             }
         });


	}
	//koniec onactivitycreated
	
	protected void sendValueToFragments(String value) {
        // it has to be the same name as in the fragment
        Intent intent = new Intent("com.eusecom.snowsmsden.action.VYPIS_UPDATE");
        intent.putExtra("VYPIS_KEY", value);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

	

	
}
//koniec detailfragment