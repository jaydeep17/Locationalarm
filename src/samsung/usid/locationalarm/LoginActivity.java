package samsung.usid.locationalarm;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		lb = (LoginButton) findViewById(R.id.login_button);
		lb.setReadPermissions(Arrays.asList("email"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		// TODO Auto-generated method stub
		super.onSessionStateChange(state, exception);
		if (state.isOpened()) {
			Request request = Request.newMeRequest(getSession(),
					new GraphUserCallback() {

						public void onCompleted(GraphUser user,Response response) {
							// TODO Auto-generated method stub
							// Toast.makeText(getApplicationContext(),
							// user.getFirstName(), Toast.LENGTH_SHORT).show();
							Log.d("Skeleton", response.toString());
							JSONObject json = response.getGraphObject()
									.getInnerJSONObject();
							try {
								Toast.makeText(getApplicationContext(),
										(String) json.get("id"),
										Toast.LENGTH_SHORT).show();
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
}
