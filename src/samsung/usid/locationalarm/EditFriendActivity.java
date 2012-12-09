package samsung.usid.locationalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditFriendActivity extends Activity implements OnClickListener{
	
	EditText nameField, emailField;
	String UID, oName, oEmail;
	Button saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_friend);
		
		nameField = (EditText) findViewById(R.id.nameField);
		emailField = (EditText) findViewById(R.id.emailField);
		saveButton = (Button) findViewById(R.id.addFriendButton);
		saveButton.setOnClickListener(this);
		
		Intent intent = getIntent();
		UID = intent.getStringExtra(Friends.UID);
		oName = intent.getStringExtra(Friends.NAME);
		oEmail = intent.getStringExtra(Friends.EMAIL);
		
		nameField.setText(oName);
		emailField.setText(oEmail);
		emailField.setEnabled(false);
		saveButton.setText("Save");
	}

	public void onClick(View v) {
		String nName = nameField.getText().toString();
		Intent intent = new Intent();
		setResult(RESULT_CANCELED);
		if(!oName.equals(nName)){
			SQLiteHelper sqh = new SQLiteHelper(this);
			sqh.renameFriend(UID, nName);
			intent.putExtra(Friends.NAME, nName);
			setResult(RESULT_OK, intent);
		}
		finish();
	}
}
