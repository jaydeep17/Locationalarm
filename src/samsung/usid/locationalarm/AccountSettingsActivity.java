package samsung.usid.locationalarm;

import com.facebook.FacebookActivity;

import android.content.Intent;
import android.os.Bundle;

public class AccountSettingsActivity extends FacebookActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!this.isSessionOpen()) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Globals.FORWARD, AccountSettingsActivity.class);
		}
		setContentView(R.layout.account_settings);
	}

}
