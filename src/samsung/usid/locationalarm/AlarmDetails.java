package samsung.usid.locationalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AlarmDetails extends Activity{

	TextView title, desc, location, radius;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_details);
		
		title = (TextView) findViewById(R.id.titleDetail);
		desc = (TextView) findViewById(R.id.descDetail);
		location = (TextView) findViewById(R.id.locationDetail);
		radius = (TextView) findViewById(R.id.radiusDetail);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		title.setText(extras.getString("title"));
		desc.setText(extras.getString("desc"));
		radius.setText(extras.getString("radius") + " km");
	}

}
