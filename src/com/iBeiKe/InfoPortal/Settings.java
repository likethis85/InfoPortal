package com.iBeiKe.InfoPortal;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Settings extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference.getKey().equals("db_initialize")) {
			Intent intent = new Intent(this,Initialize.class);
			startActivity(intent);
		} else if(preference.getKey().equals("feed_back")) {
			Intent intent = new Intent(this,FeedBack.class);
			startActivity(intent);
		} else if(preference.getKey().equals("about")) {
			Intent intent = new Intent(this,About.class);
			startActivity(intent);
		}
	return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}