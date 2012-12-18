package samsung.usid.locationalarm;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
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
import com.google.android.gcm.GCMRegistrar;

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
		super.onPause();
		// finish();
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
							Log.d("Skeleton", response.toString());
							JSONObject json = response.getGraphObject()
									.getInnerJSONObject();
							Editor editor = sp.edit();
							try {
								String pass = json.getString("id");
								String email = json.getString("email");
								String name = json.getString("name");
								editor.putString(Globals.NAME, name);
								editor.putString(Globals.PREFS_EMAIL, email);
								editor.putString(Globals.PREFS_PASS, pass);
								Toast.makeText(getApplicationContext(),
										email + "\n" + pass, Toast.LENGTH_SHORT)
										.show();
								editor.commit();

								pDialog.dismiss();
								boolean isAcCreated = sp.getBoolean(email,
										false);
								if (!isAcCreated) {
									createAccount(name, email, pass);
								}
								forwardAction();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});

			Request.executeBatchAsync(request);
		}
	}

	private void createAccount(String name, String email, String pass) {
		new CreateAccount().execute(name, email, pass);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		GCMRegistrar.register(getApplicationContext(), Globals.SENDER_ID);

		// Saves the first time to true for future reference
		Editor editor = sp.edit();
		editor.putBoolean(email, true);
		editor.commit();
	}

	// Traditional method to register, w/o FB
	public void createAccountListener(View v) {
		
		//startActivity(new Intent(this, CreateAccountActivity.class));
		
		Runnable runnable = new Runnable() {
			
			public void run() {
				RemoteDB rdb = new RemoteDB(LoginActivity.this);
				rdb.updateGCMregId(GCMRegistrar.getRegistrationId(getApplicationContext()));
			}
		};
		new Thread(runnable).start();
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

	class CreateAccount extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Registering user... Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			// pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// params order {name, email, pass}
			RemoteDB rdb = new RemoteDB(LoginActivity.this);
			JSONObject response = rdb.registerUser(params[0], params[1],
					params[2]);
			try {
				String fixed_email = response.getString("fixed_email");
				if (fixed_email != null && !fixed_email.equals("")) {
					Editor editor = sp.edit();
					editor.putString(Globals.PREFS_FIXED_EMAIL, fixed_email);
					editor.commit();
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
}
