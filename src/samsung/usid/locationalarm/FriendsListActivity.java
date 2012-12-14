package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import samsung.usid.locationalarm.MainActivity.act;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsListActivity extends ListActivity implements
		OnItemClickListener, View.OnClickListener {

	SQLiteHelper sqh;
	private SharedPreferences sp;
	TextView tv;
	ListView listview;
	SimpleCursorAdapter simpleAdapter = null;
	JSONParser jParser;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslist);

		sqh = new SQLiteHelper(this);
		sp = getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
		tv = (TextView) findViewById(R.id.tap_add);
		tv.setText("No friends found\nTap to Add");
		tv.setOnClickListener(this);

		registerForContextMenu(getListView());
		jParser = new JSONParser();
	}

	@Override
	protected void onPause() {
		super.onPause();
		pDialog.dismiss();
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
			columns = new String[] { Friends.UID, Friends.NAME, Friends.EMAIL };
			to = new int[] { R.id.alarm_uid, R.id.title_entry, R.id.desc_entry };

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
		viewFriend(position, act.VIEW);
	}

	public void onClick(View v) {
		startActivity(new Intent(this, NewFriend.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friendslist_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.flm_addFriend:
			startActivity(new Intent(this, NewFriend.class));
			return true;
		case R.id.flm_sync:
			// TODO add sync code

			// Fixes the sql syntax for alarms added before logging in
			String tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}");
			String jaryString = sqh.getLog().toString()
					.replace("{...}", tableName);

			new Local2Server().execute(jaryString);

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
			viewFriend(info.position, act.VIEW);
			return true;
		case R.id.context_edit:
			viewFriend(info.position, act.EDIT);
			return true;
		case R.id.context_delete:
			deleteFriend(info.position);
			return true;
		}
		return false;
	}

	public void viewFriend(int position, act task) {
		Cursor csr = (Cursor) listview.getItemAtPosition(position);
		String UID = csr.getString(csr.getColumnIndex(Friends.UID));
		String name = csr.getString(csr.getColumnIndex(Friends.NAME));
		String email = csr.getString(csr.getColumnIndex(Friends.EMAIL));

		Intent intent;

		if (task == act.VIEW)
			intent = new Intent(getApplicationContext(), FriendDetails.class);
		else
			intent = new Intent(getApplicationContext(),
					EditFriendActivity.class);

		intent.putExtra(Friends.UID, UID);
		intent.putExtra(Friends.NAME, name);
		intent.putExtra(Friends.EMAIL, email);
		startActivity(intent);
	}

	private void deleteFriend(int position) {
		Cursor csr = (Cursor) listview.getItemAtPosition(position);
		final String UID = csr.getString(csr.getColumnIndex(Friends.UID));
		String name = csr.getString(csr.getColumnIndex(Friends.NAME));
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Delete?");
		alert.setMessage("Are you sure you want to delete \n'" + name + "'?");
		alert.setNegativeButton("No", null);
		alert.setPositiveButton("Yes", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				sqh.deleteFriend(UID);
				new PopulateListView().execute();
			}
		});
		alert.show();
	}

	class getPermittedFriends extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("tag", Globals.downloadTag));
			par.add(new BasicNameValuePair(Globals.downloadTag, sp.getString(
					Globals.PREFS_FIXED_EMAIL, null) + "_pf"));
			Log.d("Download tag", sp.getString(
					Globals.PREFS_FIXED_EMAIL, null));
			JSONObject response = jParser.makeHttpRequest(Globals.URL, "POST",
					par);
			Log.d("JSON download", response.toString());
			try {
				JSONArray jary = response.getJSONArray("pf");
				sqh.deleteAllPermittedFriends();
				for (int i = 0; i < jary.length(); i++) {
					sqh.addPermittedFriends(
							jary.getJSONObject(i).getInt(PermittedFriends.UID),
							jary.getJSONObject(i).getString(
									PermittedFriends.NAME),
							jary.getJSONObject(i).getString(
									PermittedFriends.EMAIL));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}

	}

	class Local2Server extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FriendsListActivity.this);
			pDialog.setMessage("Syncing Database with server... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				JSONArray jary = new JSONArray(params[0]);

				for (int i = 0; i < jary.length(); i++) {
					List<NameValuePair> par = new ArrayList<NameValuePair>();
					par.add(new BasicNameValuePair("tag", Globals.syncTag));
					par.add(new BasicNameValuePair("sync", jary.getString(i)));
					JSONObject jresponse = jParser.makeHttpRequest(Globals.URL,
							"POST", par);
					if (jresponse.getInt("success") == 1) {
						continue;
					} else {
						pDialog.dismiss();
						Toast.makeText(FriendsListActivity.this,
								"Error syncing Database with server",
								Toast.LENGTH_LONG).show();
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			sqh.flushLogFile();
			new getPermittedFriends().execute();
		}

	}
}
