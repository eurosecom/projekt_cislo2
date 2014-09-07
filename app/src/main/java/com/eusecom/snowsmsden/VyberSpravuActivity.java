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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class VyberSpravuActivity extends ListActivity {

	private SQLiteDatabase db3=null;
	private Cursor constantsCursor3=null;
	
	private SQLiteDatabase db=null;
	private Cursor constantsCursor=null;
	EditText txtTitle;
    EditText txtValue;
    String idtitle;
    String idvalue;
	
	public static final String NAME_KEY = "vybranasprava";
	String xsprava;
	String mesx;
	String indx;
	String posx;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.xspravy);
		
		db3=(new DatabaseSpravy(this)).getWritableDatabase();
		db=(new DatabaseSpravy(this)).getWritableDatabase();

	
		vylistuj();
		
		ListView lv = getListView();
		 
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem vo value je sprava
            	String spravaxyz = ((TextView) view.findViewById(R.id.title)).getText().toString();

            	Intent i = getIntent();
            	i.putExtra(NAME_KEY, spravaxyz);
                setResult(100, i); 
                finish();
 
            }
        });
        
        registerForContextMenu(getListView());

	}
	//koniec oncreate
	
	private void vylistuj() {
		constantsCursor3=db3.rawQuery("SELECT _id, server3, pswd3, name3, nick3, mail3, uzid3 "+
				"FROM mojespravy WHERE _id > 0 ORDER BY datm3 DESC",
				null);

		//toto bezi len v api11 ak dam prec  to "}, 0" tak je to deprecated
		ListAdapter adapter=new SimpleCursorAdapter(this,
				R.layout.rowspravy, constantsCursor3,
				new String[] {"nick3", "mail3", "name3", "pswd3", "server3", "uzid3", "_id"},
				new int[] {R.id.title, R.id.value, R.id.namex2, R.id.pswdx2, R.id.mailx2, R.id.uzidx2, R.id.idx2}, 0);

		setListAdapter(adapter);
	}
	//koniec vylistuj
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor3.close();
		db3.close();

	}
	//koniec ondestroy


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_vyberspravu, menu);
        

        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	

    	switch (item.getItemId()) {
    	
        
            case R.id.action_newxsprava:
            	
            	add();

                return true;


                
        }
    
        return super.onOptionsItemSelected(item);
    }
    //koniec onoptionselected
    
  //oncontextmenu
    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v,
    ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    int position = info.position;
    posx = position + "";
	 	
    	mesx = constantsCursor3.getString( constantsCursor3.getColumnIndex("nick3") );
    	indx = constantsCursor3.getString( constantsCursor3.getColumnIndex("_id") );
    	 	

    	//menu.setHeaderTitle(indx + "/" + mesx + "/" + posx);
    	menu.setHeaderTitle(mesx);
    
    getMenuInflater().inflate(R.menu.kontext_vyberspravu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    posx = info.position + "";
    
    switch (item.getItemId()) {
        case R.id.upravitxsprava:
        	
        	update(info.id);
			return(true);

    	
        case R.id.zmazatxsprava:

        	delete(info.id);
			return(true);
    	


        }

        return super.onContextItemSelected(item);
    }
    //koniec oncontextmenu
    
    @SuppressLint("InflateParams")
	private void update(final long rowId) {
		if (rowId>0) {
		LayoutInflater inflater=LayoutInflater.from(this);
		View addView=inflater.inflate(R.layout.add_edit, null);

		constantsCursor=db.rawQuery("SELECT _ID, nick3, mail3 "+
				"FROM mojespravy WHERE _id = " + rowId + " ORDER BY _id",
				null);

		//constantsCursor.getString(0) je id, constantsCursor.getString(1) je title, constantsCursor.getString(2) je value
	      if (constantsCursor.moveToFirst()) {
	    	  idtitle=constantsCursor.getString(1);
	    	  idvalue=constantsCursor.getString(2);
	      }
		
		txtTitle = (EditText) addView.findViewById(R.id.title);
		txtTitle.setText(idtitle);
	    txtValue = (EditText) addView.findViewById(R.id.value);
		txtValue.setText(idvalue);
		final DialogWrapper wrapper=new DialogWrapper(addView);
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.update_title)
			.setView(addView)
			.setPositiveButton(R.string.ok,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					processUpdate(wrapper, rowId);
				}
			})
			.setNegativeButton(R.string.cancel,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					// ignore, just dismiss
				}
			})
			.show();
		}
		//koniec ak rowId>0
	}
	//koniec update
	
	@SuppressLint("InflateParams")
	private void add() {
		LayoutInflater inflater=LayoutInflater.from(this);
		View addView=inflater.inflate(R.layout.add_edit, null);
		final DialogWrapper wrapper=new DialogWrapper(addView);
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.add_title)
			.setView(addView)
			.setPositiveButton(R.string.ok,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					processAdd(wrapper);
				}
			})
			.setNegativeButton(R.string.cancel,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					// ignore, just dismiss
				}
			})
			.show();
	}
	
	private void delete(final long rowId) {
		if (rowId>0) {
			new AlertDialog.Builder(this)
				.setTitle(R.string.delete_title)
				.setPositiveButton(R.string.ok,
														new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
																int whichButton) {
						processDelete(rowId);
					}
				})
				.setNegativeButton(R.string.cancel,
														new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
																int whichButton) {
					// ignore, just dismiss
					}
				})
				.show();
		}
	}
	
	private void processAdd(DialogWrapper wrapper) {
		ContentValues values=new ContentValues(2);
		
		values.put("nick3", wrapper.getTitle());
		values.put("mail3", wrapper.getValue());
		
		db.insert("mojespravy", "nick3", values);
		vylistuj();
	}
	
	private void processUpdate(DialogWrapper wrapper, long rowId) {
		ContentValues values=new ContentValues(3);
		
		values.put("nick3", wrapper.getTitle());
		values.put("mail3", wrapper.getValue());
		
		java.util.Date date= new java.util.Date();
		Timestamp ts = new Timestamp(date.getTime());
		String tss = ts + "";
		values.put("datm3", tss);
		
		db.update("mojespravy", values, "_ID=" + rowId, null);
		vylistuj();
	}
	
	private void processDelete(long rowId) {
		String[] args={String.valueOf(rowId)};
		
		db.delete("mojespravy", "_ID=?", args);
		vylistuj();
	}
	
	class DialogWrapper {
		EditText titleField=null;
		EditText valueField=null;
		View base=null;
		
		DialogWrapper(View base) {
			this.base=base;
			valueField=(EditText)base.findViewById(R.id.value);
		}
		
		String getTitle() {
			return(getTitleField().getText().toString());
		}
		
		String getValue() {
			//aby vratil float musi byt Float getValue() { a potom toto
			//return(Float.valueOf(getValueField().getText().toString()));
			return(getValueField().getText().toString());
		}
		
		private EditText getTitleField() {
			if (titleField==null) {
				titleField=(EditText)base.findViewById(R.id.title);
			}
			
			return(titleField);
		}
		
		private EditText getValueField() {
			if (valueField==null) {
				valueField=(EditText)base.findViewById(R.id.value);
			}
			
			return(valueField);
		}
	}
	//koniec wrapper
    
  
    
	






//koniec activity
}