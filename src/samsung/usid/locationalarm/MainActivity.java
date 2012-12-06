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

		if (!sqh.deleteAllAlarms()) {
			Toast.makeText(this, "Failed removing all alarms",
					Toast.LENGTH_LONG).show();
		}

		// Note: comment out the below line after first run, or the alarms will
		// get inserted @ every run..
		sqh.insertSomeAlarms();

	}

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
			pDialog.show();
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
							Toast.makeText(MainActivity.this, title,
									Toast.LENGTH_SHORT).show();
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

	public void addNewAlarm(View v) {
		Toast.makeText(this, "Add new Alarm CLicked", Toast.LENGTH_SHORT)
				.show();

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
			startActivity(new Intent(getApplicationContext(), NewAlarm.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
