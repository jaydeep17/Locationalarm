package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewAlarm extends Activity implements OnClickListener {

	Button saveButton;
	EditText titleField, descField, locationField, radiusField;
	Spinner alarmFor;
	SQLiteHelper sqh;
	ArrayList<String> emails;
	JSONParser jParser;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		sqh = new SQLiteHelper(getApplicationContext());
		

		titleField = (EditText) findViewById(R.id.titleField);
		descField = (EditText) findViewById(R.id.descField);
		locationField = (EditText) findViewById(R.id.locationField);
		radiusField = (EditText) findViewById(R.id.radiusField);
		alarmFor = (Spinner) findViewById(R.id.alarmFor);

		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);

		// pf = permitted_friends
		List<String> pf = new ArrayList<String>();
		// TODO fill in the permitted_friends
		pf.add("me");
		Cursor c = sqh.fetchAllPermittedFriends();
		emails = new ArrayList<String>();
		emails.add("me");
		if (c != null) {
			do {
				String name = c.getString(c
						.getColumnIndex(PermittedFriends.NAME));
				String email = c.getString(c
						.getColumnIndex(PermittedFriends.EMAIL));
				pf.add(name);
				emails.add(email);
			} while (c.moveToNext());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pf);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		alarmFor.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	public void onClick(View v) {
		String title = titleField.getText().toString();
		String desc = descField.getText().toString();
		String radius = radiusField.getText().toString();
		String alarm_for = emails.get(alarmFor.getSelectedItemPosition());
		if (alarm_for.equals("me")) {
			sqh.addAlarm(title, desc, radius, "1234567", "1234567", "me");
		} else {
			// TODO add alarm to the friend alarm table
			new AddToHisAlarmTable().execute(alarm_for,title,desc,radius);
		}
		Toast.makeText(getApplicationContext(), "Alarm added",
				Toast.LENGTH_SHORT).show();
	}
	
	class AddToHisAlarmTable extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			String fixed_email = Globals.fixEmail(params[0]);
			String tableName = fixed_email + "_alarms";
			jParser = new JSONParser();
			sp = NewAlarm.this.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
			String myEmail = sp.getString(Globals.PREFS_EMAIL, null);
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("tag", Globals.hisAlarmTag));
			String query = "INSERT INTO " + tableName + "(" + Alarms.TITLE + ", "
					+ Alarms.DESC + ", " + Alarms.RADIUS + ", " + Alarms.LOCATION
					+ ", " + Alarms.SETBY + ") VALUES('" + params[1] + "', " + "'"
					+ params[2] + "', " + "'" + params[3] + "', " + "'" + "1234567" + ","
					+ "1234567" + "', '" + myEmail + "');";
			par.add(new BasicNameValuePair(Globals.hisAlarmTag, query));
			jParser.makeHttpRequest(Globals.URL, "POST", par);
			return null;
		}
		
	}
	
}
