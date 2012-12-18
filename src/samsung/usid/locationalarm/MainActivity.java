package samsung.usid.locationalarm;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;

public class MainActivity extends ListActivity implements OnItemClickListener {

	// TODO Create alarm triggering mechanism which is power efficient
	// TODO Handle GCM errors like Service not available, etc.
	// TODO App reinstall => then sync all the old data back from server
	SQLiteHelper sqh;
	SimpleCursorAdapter simpleAdapter = null;
	ListView listview;
	TextView tv;
	SharedPreferences sp;

	public static enum act {
		VIEW, EDIT
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslist);

		sqh = new SQLiteHelper(this);
		tv = (TextView) findViewById(R.id.tap_add);
		sp = getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);

		// Use only for debug purpose ...
		/*
		 * if (!sqh.deleteAllAlarms()) { Toast.makeText(this,
		 * "Failed removing all alarms", Toast.LENGTH_LONG).show(); }
		 */
		// Note: comment out the below line after first run, or the alarms will
		// get inserted @ every run..
		// sqh.insertSomeAlarms();

		registerForContextMenu(getListView());
	}

	// The below class fetches the data from the local db (SQLite)
	class PopulateListView extends AsyncTask<String, String, String> {

		Cursor c;
		String columns[];
		int[] to;
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading alarms... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			c = sqh.fetchAllAlarms();

			if (c.getCount() == 0) {
				return null;
			}
			columns = new String[] { Alarms.UID, Alarms.TITLE, Alarms.DESC,
					Alarms.RADIUS };
			to = new int[] { R.id.alarm_uid, R.id.title_entry, R.id.desc_entry,
					R.id.radius_entry };

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (c.getCount() == 0) {
				runOnUiThread(new Runnable() {

					public void run() {
						tv.setVisibility(View.VISIBLE);
						MainActivity.this.getListView()
								.setVisibility(View.GONE);
					}
				});
				return;
			}
			runOnUiThread(new Runnable() {

				public void run() {
					tv.setVisibility(View.GONE);
					MainActivity.this.getListView().setVisibility(View.VISIBLE);
					simpleAdapter = new SimpleCursorAdapter(MainActivity.this,
							R.layout.alarmslist_entry, c, columns, to, 0);
					listview = (ListView) findViewById(android.R.id.list);
					listview.setAdapter(simpleAdapter);

					listview.setOnItemClickListener(MainActivity.this);
				}
			});
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		new PopulateListView().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mif = getMenuInflater();
		mif.inflate(R.menu.activity_splash, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_alarm:
			createNewAlarm(null);
			return true;
		case R.id.menu_friends:
			friendsClick();
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(getApplicationContext(),
					SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void createNewAlarm(View v) {
		startActivity(new Intent(getApplicationContext(), NewAlarm.class));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_contextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_view:
			viewAlarm(info.position, act.VIEW);
			return true;
		case R.id.context_edit:
			viewAlarm(info.position, act.EDIT);
			return true;
		case R.id.context_delete:
			deleteAlarm(info.position);
			return true;
		}
		return false;
	}

	public void onItemClick(AdapterView<?> listView, View view, int position,
			long id) {
		viewAlarm(position, act.VIEW);
	}

	private void viewAlarm(int position, act task) {
		Cursor csr = (Cursor) listview.getItemAtPosition(position);
		String UID = csr.getString(csr.getColumnIndex(Alarms.UID));
		String title = csr.getString(csr.getColumnIndex(Alarms.TITLE));
		String desc = csr.getString(csr.getColumnIndex(Alarms.DESC));
		// float longitude = csr.getFloat(csr.getColumnIndex(Alarms.LONGITUDE));
		// float latitude = csr.getFloat(csr.getColumnIndex(Alarms.LATITUDE));
		String radius = csr.getString(csr.getColumnIndex(Alarms.RADIUS));
		Intent intent;

		if (task == act.VIEW)
			intent = new Intent(getApplicationContext(), AlarmDetails.class);
		else
			intent = new Intent(getApplicationContext(),
					EditAlarmActivity.class);

		intent.putExtra(Alarms.UID, UID);
		intent.putExtra(Alarms.TITLE, title);
		intent.putExtra(Alarms.DESC, desc);
		// intent.putExtra("longitude", longitude);
		// intent.putExtra("latitude", latitude);
		intent.putExtra(Alarms.RADIUS, radius);
		startActivity(intent);
	}

	private void deleteAlarm(int position) {
		Cursor csr = (Cursor) listview.getItemAtPosition(position);
		final String UID = csr.getString(csr.getColumnIndex(Alarms.UID));
		String title = csr.getString(csr.getColumnIndex(Alarms.TITLE));
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Delete?");
		alert.setMessage("Are you sure you want to delete \n'" + title + "'?");
		alert.setNegativeButton("No", null);
		alert.setPositiveButton("Yes", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				sqh.deleteAlarm(UID);
				new PopulateListView().execute();
			}
		});
		alert.show();
	}

	private void friendsClick() {
		final ProgressDialog pDialog = new ProgressDialog(this);
		pDialog.setMessage("Checking Login details... Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
		Session s = new Session(this);
		try {
			s.openForRead(getParent());
		} catch (Exception e) {
			Log.d("session fb", "caught the exception");
		}
		if (s.isOpened()) {
			// these credentials are stored in SharedPrefs so no need to pass
			// through intent
			String email = sp.getString(Globals.PREFS_EMAIL, null);
			String pass = sp.getString(Globals.PREFS_PASS, null);
			startActivity(new Intent(this, FriendsListActivity.class));
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Globals.FORWARD, FriendsListActivity.class);
			startActivity(intent);
		}
		pDialog.dismiss();

	}
}
