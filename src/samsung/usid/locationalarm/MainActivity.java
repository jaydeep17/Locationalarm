package samsung.usid.locationalarm;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	SQLiteHelper sqh;
	SimpleCursorAdapter simpleAdapter;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslist);

		sqh = new SQLiteHelper(this);

		// Use only for debug purpose ...
/*		if (!sqh.deleteAllAlarms()) {
			Toast.makeText(this, "Failed removing all alarms",
					Toast.LENGTH_LONG).show();
		}
*/
		// Note: comment out the below line after first run, or the alarms will
		// get inserted @ every run..
		//sqh.insertSomeAlarms();

	}

	//The below class fetches the data from the local db (SQLite)
	class PopulateListView extends AsyncTask<String, String, String> {
		// TODO Auto-generated method stub

		Cursor c;
		String columns[];
		int[] to;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading alarms... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			//pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			c = sqh.fetchAllAlarms();
			TextView tv = (TextView) findViewById(R.id.tap_add);
			if (c.getCount() == 0) {
				tv.setVisibility(View.VISIBLE);
				return null;
			} else {
				tv.setVisibility(View.GONE);
			}
			columns = new String[] { Alarms.TITLE, Alarms.DESC, Alarms.RADIUS };
			to = new int[] { R.id.title_entry, R.id.desc_entry,
					R.id.radius_entry };

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(c.getCount()==0){
				return;
			}
			runOnUiThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					simpleAdapter = new SimpleCursorAdapter(MainActivity.this,
							R.layout.alarmslist_entry, c, columns, to, 0);
					ListView listview = (ListView) findViewById(android.R.id.list);
					listview.setAdapter(simpleAdapter);

					listview.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> listView,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Cursor csr = (Cursor) listView
									.getItemAtPosition(position);
							String title = csr.getString(csr
									.getColumnIndex(Alarms.TITLE));
							String desc = csr.getString(csr.getColumnIndex(Alarms.DESC));
							//float longitude = csr.getFloat(csr.getColumnIndex(Alarms.LONGITUDE));
							//float latitude = csr.getFloat(csr.getColumnIndex(Alarms.LATITUDE));
							String radius = csr.getString(csr.getColumnIndex(Alarms.RADIUS));
							Intent intent = new Intent(getApplicationContext(),AlarmDetails.class);
							intent.putExtra("title", title);
							intent.putExtra("desc", desc);
							//intent.putExtra("longitude", longitude);
							//intent.putExtra("latitude", latitude);
							intent.putExtra("radius", radius);
							startActivity(intent);
						}
					});
				}
			});
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-(generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new PopulateListView().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mif = getMenuInflater();
		mif.inflate(R.menu.activity_splash, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_add_alarm:
			createNewAlarm(null);
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	public void createNewAlarm(View v){
		startActivity(new Intent(getApplicationContext(), NewAlarm.class));
	}
}
