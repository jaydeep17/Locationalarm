package samsung.usid.locationalarm;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	SQLiteHelper sqh;
	SimpleCursorAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslist);

		sqh = new SQLiteHelper(this);

		// Use only for debug purpose ...

		// if (!sqh.deleteAllAlarms()) {
		// Toast.makeText(this, "Failed removing all alarms",
		// Toast.LENGTH_LONG).show();
		// }

		// Note: comment out the below line after first run, or the alarms will
		// get inserted @ every run..
		sqh.insertSomeAlarms();

	}

	private void populateListView() {
		// TODO Auto-generated method stub
		Cursor c = sqh.fetchAllAlarms();
		TextView tv = (TextView) findViewById(R.id.tap_add);
		if (c.getCount() == 0) {
			tv.setVisibility(View.VISIBLE);
			return;
		} else {
			tv.setVisibility(View.GONE);
		}
		String columns[] = new String[] { Alarms.TITLE, Alarms.DESC,
				Alarms.RADIUS };
		int[] to = new int[] { R.id.title_entry, R.id.desc_entry,
				R.id.radius_entry };

		simpleAdapter = new SimpleCursorAdapter(this,
				R.layout.alarmslist_entry, c, columns, to, 0);
		ListView listview = (ListView) findViewById(android.R.id.list);
		listview.setAdapter(simpleAdapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor csr = (Cursor) listView.getItemAtPosition(position);
				String title = csr.getString(csr.getColumnIndex(Alarms.TITLE));
				Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT)
						.show();
			}

		});
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
		populateListView();
	}

	public void addNewAlarm(View v) {
		Toast.makeText(this, "Add new Alarm CLicked", Toast.LENGTH_SHORT)
				.show();
	}

}
