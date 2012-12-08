package samsung.usid.locationalarm;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserCallback;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginActivity extends FacebookActivity {

	LoginButton lb;
	SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		lb = (LoginButton) findViewById(R.id.login_button);
		lb.setReadPermissions(Arrays.asList("email"));

		sp = getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		super.onSessionStateChange(state, exception);

		if (state.isOpened()) {
			final ProgressDialog pDialog = new ProgressDialog(this);
			pDialog.setMessage("Collecting your details... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			Request request = Request.newMeRequest(getSession(),
					new GraphUserCallback() {

						public void onCompleted(GraphUser user,
								Response response) {
							// TODO Auto-generated method stub
							// Toast.makeText(getApplicationContext(),
							// user.getFirstName(), Toast.LENGTH_SHORT).show();
							Log.d("Skeleton", response.toString());
							JSONObject json = response.getGraphObject()
									.getInnerJSONObject();
							Editor editor = sp.edit();
							try {
								String pass = json.getString("id");
								String email = json.getString("email");
								editor.putString(Globals.PREFS_EMAIL, email);
								editor.putString(Globals.PREFS_PASS, pass);
								Toast.makeText(getApplicationContext(),
										email + "\n" + pass, Toast.LENGTH_SHORT)
										.show();
								editor.commit();

								pDialog.dismiss();
								forwardAction();

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			Request.executeBatchAsync(request);
		}
	}

	public void createAccountListener(View v) {
		startActivity(new Intent(this, CreateAccountActivity.class));
	}

	private void forwardAction() {
		Class<?> fwd = (Class<?>) getIntent().getSerializableExtra(
				Globals.FORWARD);
		if (fwd != null) {
			Intent in = new Intent(this, fwd);
			startActivity(in);
		}
		finish();
	}
}
