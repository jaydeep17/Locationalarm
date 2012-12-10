package samsung.usid.locationalarm;

import com.facebook.Session;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener {

	Preference ps;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ps = findPreference("pref_ac_settings");
		ps.setOnPreferenceClickListener(this);

	}

	public boolean onPreferenceClick(Preference preference) {
		Session s = new Session(this);
		try {
			s.openForRead(getParent());
		} catch (Exception e) {
			Log.d("session fb", "caught the exception");
		}

		Intent intent = new Intent();
		if (s.isOpened()) {
			intent.setClass(this, AccountSettingsActivity.class);
		} else {
			intent.setClass(this, LoginActivity.class);
			intent.putExtra(Globals.FORWARD, AccountSettingsActivity.class);
		}
		startActivity(intent);
		return true;
	}

}
