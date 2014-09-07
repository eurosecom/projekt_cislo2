/* Copyright (c) 2008-2009 -- CommonsWare, LLC

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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseSpravy extends SQLiteOpenHelper {
	private static final String DATABASE_NAME3="db3";
	public static final String SERVER3="server3";
	public static final String NICK3="nick3";
	public static final String MAIL3="mail3";
	public static final String UZID3="uzid3";
	public static final String NAME3="name3";
	public static final String PSWD3="pswd3";
	public static final String DRUH3="druh3";
	public static final String DATM3="datm3";
	
	
	public DatabaseSpravy(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME3, null, 3);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db3) {
		
		db3.execSQL("CREATE TABLE mojespravy (_id INTEGER PRIMARY KEY AUTOINCREMENT, server3 TEXT, " +
				"nick3 TEXT, mail3 TEXT, uzid3 TEXT, name3 TEXT, pswd3 TEXT, druh3 TEXT, datm3 TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP);");
		
		ContentValues cv3=new ContentValues();
		
		cv3.put(SERVER3, "4680");
		cv3.put(NICK3, "vzor spravy 1");
		cv3.put(MAIL3, "preklad vzor spravy 1");
		cv3.put(UZID3, "uzid xxx");
		cv3.put(NAME3, "name xxx");
		cv3.put(PSWD3, "pswd xxx");
		cv3.put(DRUH3, "druh xxx");
		db3.insert("mojespravy", SERVER3, cv3);
		
		cv3.put(SERVER3, "4680");
		cv3.put(NICK3, "vzor spravy 2");
		cv3.put(MAIL3, "preklad vzor spravy 2");
		cv3.put(UZID3, "uzid xxx3");
		cv3.put(NAME3, "name xxx3");
		cv3.put(PSWD3, "pswd xxx3");
		cv3.put(DRUH3, "druh xxx3");
		db3.insert("mojespravy", SERVER3, cv3);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db3, int oldVersion, int newVersion) {
		android.util.Log.w("mojespravy", "Upgrading database, which will destroy all old data");
		db3.execSQL("DROP TABLE IF EXISTS mojespravy");
		onCreate(db3);
	}
}