package samsung.usid.locationalarm;

import samsung.usid.locationalarm.MainActivity.PopulateListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AlarmDetails extends Activity{

	TextView title, desc, location, radius, uid;
	String oTitle, oDesc, oLocation, oRadius, UID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_details);
		
		uid = (TextView) findViewById(R.id.a_d_UID);
		title = (TextView) findViewById(R.id.titleDetail);
		desc = (TextView) findViewById(R.id.descDetail);
		location = (TextView) findViewById(R.id.locationDetail);
		radius = (TextView) findViewById(R.id.radiusDetail);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		UID = extras.getString(Alarms.UID);
		oTitle = extras.getString(Alarms.TITLE);
		oDesc = extras.getString(Alarms.DESC);
		oRadius  = extras.getString(Alarms.RADIUS);
		uid.setText(UID);
		title.setText(oTitle);
		desc.setText(oDesc);
		radius.setText(oRadius + " km");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alarmdetails_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.ad_edit:
			editAlarm();
			return true;
		case R.id.ad_delete:
			deleteAlarm();
			return true;
		}
		return false;
	}
	
	private void editAlarm(){
		Intent intent = new Intent(getApplicationContext(), EditAlarmActivity.class);
		intent.putExtra(Alarms.UID, UID);
		intent.putExtra(Alarms.TITLE, oTitle);
		intent.putExtra(Alarms.DESC, oDesc);
		intent.putExtra(Alarms.RADIUS, oRadius);
		startActivity(intent);
	}
	
	private void deleteAlarm(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Delete?");
		alert.setMessage("Are you sure you want to delete \n'" + oTitle + "'?");
		alert.setNegativeButton("No", null);
		alert.setPositiveButton("Yes", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SQLiteHelper sqh = new SQLiteHelper(AlarmDetails.this);
				sqh.deleteAlarm(UID);
				onBackPressed();
			}
		});
		alert.show();
	}

}
