package samsung.usid.locationalarm;

import samsung.usid.locationalarm.MainActivity.act;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsListActivity extends ListActivity implements
		OnItemClickListener, OnClickListener {

	SQLiteHelper sqh;
	TextView tv;
	ListView listview;
	SimpleCursorAdapter simpleAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslist);

		sqh = new SQLiteHelper(this);
		tv = (TextView) findViewById(R.id.tap_add);
		tv.setText("No friends found\nTap to Add");
		tv.setOnClickListener(this);
		
		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();
		new PopulateListView().execute();
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
			pDialog = new ProgressDialog(FriendsListActivity.this);
			pDialog.setMessage("Loading friends... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			c = sqh.fetchAllFriends();

			if (c.getCount() == 0) {
				return null;
			}
			columns = new String[] { Friends.UID, Friends.NAME, Friends.EMAIL};
			to = new int[] { R.id.alarm_uid, R.id.title_entry, R.id.desc_entry};

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
						FriendsListActivity.this.getListView().setVisibility(
								View.GONE);
					}
				});
				return;
			}
			runOnUiThread(new Runnable() {

				public void run() {
					tv.setVisibility(View.GONE);
					FriendsListActivity.this.getListView().setVisibility(
							View.VISIBLE);
					simpleAdapter = new SimpleCursorAdapter(
							FriendsListActivity.this,
							R.layout.alarmslist_entry, c, columns, to, 0);
					listview = (ListView) findViewById(android.R.id.list);
					listview.setAdapter(simpleAdapter);

					listview.setOnItemClickListener(FriendsListActivity.this);
				}
			});
		}

	}

	public void onItemClick(AdapterView<?> listView, View view, int position,
			long id) {
		
	}

	public void onClick(View v) {
		startActivity(new Intent(this,NewFriend.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friendslist_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.flm_addFriend:
			startActivity(new Intent(this,NewFriend.class));
			return true;
		case R.id.flm_sync:
			//TODO add sync code
			return true;
		}
		return false;
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
			//TODO add View code
			return true;
		case R.id.context_edit:
			//TODO add Edit code
			return true;
		case R.id.context_delete:
			//TODO add Delete code
			return true;
		}
		return false;
	}
	
}
