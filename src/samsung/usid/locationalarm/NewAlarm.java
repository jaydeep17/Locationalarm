package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewAlarm extends Activity implements OnClickListener {

	Button saveButton;
	EditText titleField, descField, locationField, radiusField;
	
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static String url_create_product = "http://10.0.2.2/create_alarm.php";
	private static final String TAG_SUCCESS = "success";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		
		titleField = (EditText) findViewById(R.id.titleField);
		descField = (EditText) findViewById(R.id.descField);
		locationField = (EditText) findViewById(R.id.locationField);
		radiusField = (EditText) findViewById(R.id.radiusField);
		
		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	class CreateNewAlarm extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewAlarm.this);
			pDialog.setMessage("Creating Product..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String title = titleField.getText().toString();
			String desc = descField.getText().toString();
			String radius = radiusField.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("description", desc));
			params.add(new BasicNameValuePair("radius", radius));
			params.add(new BasicNameValuePair("longitude", "123141"));
			params.add(new BasicNameValuePair("latitude", "121231"));
			params.add(new BasicNameValuePair("set_by", "me"));

			JSONObject json = jParser.makeHttpRequest(url_create_product,
					"POST", params);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
				} else {
					Log.e("JSON", "failed to created alarm");
					Log.e("JSON",json.getString("message"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
		}

	}
}
