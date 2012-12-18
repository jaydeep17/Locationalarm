package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewFriend extends Activity implements OnClickListener {

	EditText nameField, emailField;
	Button addButton;
	JSONParser jParser;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_friend);

		nameField = (EditText) findViewById(R.id.nameField);
		emailField = (EditText) findViewById(R.id.emailField);
		addButton = (Button) findViewById(R.id.addFriendButton);
		addButton.setOnClickListener(this);
	}

	// this method will create the friend
	public void onClick(View v) {
		String name = nameField.getText().toString();
		String email = emailField.getText().toString();
		SQLiteHelper sqh = new SQLiteHelper(this);
		sqh.addFriend(name, email);
		new AddMeToHisPF().execute(email);
		finish();
	}

	// As I'm adding someone to my friendlist I need to register myself in his
	// permittedFriends list
	// Params : .execute(email);
	class AddMeToHisPF extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			sp = NewFriend.this.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
			String myName = sp.getString(Globals.NAME, null);
			String myEmail = sp.getString(Globals.PREFS_EMAIL, null);
			String tableName = Globals.fixEmail(params[0]) + "_pf";
			String query = "INSERT INTO " + tableName + "("
					+ PermittedFriends.NAME + ", " + PermittedFriends.EMAIL
					+ ") VALUES('" + myName + "', '" + myEmail + "');";
			JSONParser jParser = new JSONParser();
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("tag", Globals.hisPFTag));
			par.add(new BasicNameValuePair(Globals.hisPFTag, query));
			jParser.makeHttpRequest(Globals.URL, "POST", par);
			return null;
		}

	}
}
