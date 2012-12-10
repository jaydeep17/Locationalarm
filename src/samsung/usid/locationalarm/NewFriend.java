package samsung.usid.locationalarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewFriend extends Activity implements OnClickListener{
	
	EditText nameField, emailField;
	Button addButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_friend);
		
		nameField = (EditText) findViewById(R.id.nameField);
		emailField = (EditText) findViewById(R.id.emailField);
		addButton = (Button) findViewById(R.id.addFriendButton);
		addButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		String name = nameField.getText().toString();
		String email = emailField.getText().toString();
		SQLiteHelper sqh = new SQLiteHelper(this);
		sqh.addFriend(name, email);
		finish();
	}
}
