package samsung.usid.locationalarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewAlarm extends Activity implements OnClickListener {

	Button saveButton;
	EditText titleField, descField, locationField, radiusField;
	SQLiteHelper sqh;

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
		finish();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		sqh = new SQLiteHelper(getApplicationContext());
		String title = titleField.getText().toString();
		String desc = descField.getText().toString();
		String radius = radiusField.getText().toString();
		sqh.addAlarm(title, desc, radius, "1234567", "1234567", "me");
		Toast.makeText(getApplicationContext(), "Alarm added",
				Toast.LENGTH_SHORT).show();
	}
}
