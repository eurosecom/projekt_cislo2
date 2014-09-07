package com.eusecom.snowsmsden;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;



@SuppressWarnings("deprecation")
public class SettingsActivity extends android.preference.PreferenceActivity {
	

	
	public static final String USER_ID = "userid";
	public static final String NICK_NAME = "nickname";
	public static final String MOJ_STATE = "mojstate";
	public static final String REPOSITORY = "repository";
	public static final String USER_PSW = "userpsw";
	public static final String USER_CODE = "usercode";
	public static final String NAME_CLOUD = "namecloud";
	public static final String NAME_DRIVE = "namedrive";
	public static final String HLADANIE = "hladanie";
	public static final String HLADAJTEXT = "hladajtext";
	public static final String NOTIFY = "notifications";
	public static final String NEWSMS = "newsms";
	public static final String OKPRISTUP = "okpristup";
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static String getRepository(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(REPOSITORY, "1");
	}
	

	public static String getNickName(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(NICK_NAME, "");
	}

	
	public static String getMojState(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(MOJ_STATE, "");
	}
	
	public static String getUserId(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_ID, "0");
	}

	
	public static String getUserPsw(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_PSW, "1111");
	}
	
	public static String getUserCode(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_CODE, "89abcdef");
	}
	
	public static String getNameCloud(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(NAME_CLOUD, "www.eshoptest.sk/androidsnow");
	}
	
	public static String getNameDrive(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(NAME_DRIVE, "mhfdgtr34gdh");
	}
	
	public static String getHladanie(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(HLADANIE, "0");
	}
	
	public static String getHladajtext(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(HLADAJTEXT, "");
	}
	
	public static String getNotify(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(NOTIFY, "");
	}
	
	public static String getNewSms(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(NEWSMS, "0");
	}
	
	public static String getOkPristup(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(OKPRISTUP, "0");
	}
	
	
} 