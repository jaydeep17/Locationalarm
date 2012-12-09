package samsung.usid.locationalarm;

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

public class FriendDetails extends Activity {

	TextView nameField, emailField;
	String UID, name, email;
	final int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_details);

		nameField = (TextView) findViewById(R.id.nameValue);
		emailField = (TextView) findViewById(R.id.emailValue);

		Intent intent = getIntent();
		UID = intent.getStringExtra(Friends.UID);
		name = intent.getStringExtra(Friends.NAME);
		email = intent.getStringExtra(Friends.EMAIL);

		nameField.setText(name);
		emailField.setText(email);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alarmdetails_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ad_edit:
			editFriend();
			return true;
		case R.id.ad_delete:
			deleteFriend();
			return true;
		}
		return false;
	}

	private void editFriend() {
		Intent intent = new Intent(this, EditFriendActivity.class);
		intent.putExtra(Friends.UID, UID);
		intent.putExtra(Friends.NAME, name);
		intent.putExtra(Friends.EMAIL, email);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	private void deleteFriend(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Delete?");
		alert.setMessage("Are you sure you want to delete \n'" + name + "'?");
		alert.setNegativeButton("No", null);
		alert.setPositiveButton("Yes", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SQLiteHelper sqh = new SQLiteHelper(FriendDetails.this);
				sqh.deleteFriend(UID);
				finish();
			}
		});
		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String nName = data.getStringExtra(Friends.NAME);
				nameField.setText(nName);
			}
		}
	}
}
