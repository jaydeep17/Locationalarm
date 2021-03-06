package samsung.usid.locationalarm;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditAlarmActivity extends Activity implements OnClickListener{

	EditText title, desc, radius;
	Button saveButton;
	String UID, oTitle, oDesc, oRadius;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		
		title = (EditText) findViewById(R.id.titleField);
		desc = (EditText) findViewById(R.id.descField);
		radius = (EditText) findViewById(R.id.radiusField);
		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setText("Save");
		saveButton.setOnClickListener(this);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		UID = extras.getString(Alarms.UID);
		oTitle = extras.getString(Alarms.TITLE); 
		oDesc = extras.getString(Alarms.DESC);
		oRadius = extras.getString(Alarms.RADIUS);
		
		title.setText(oTitle);
		desc.setText(oDesc);
		radius.setText(oRadius);
	}

	public void onClick(View v) {
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String> colValues = new ArrayList<String>();
		String nTitle = title.getText().toString();
		String nDesc = desc.getText().toString();
		String nRadius = radius.getText().toString();

		if(!oTitle.equals(nTitle)){
			colNames.add(Alarms.TITLE);
			colValues.add(nTitle);
		}
		if(!oDesc.equals(nDesc)){
			colNames.add(Alarms.DESC);
			colValues.add(nDesc);
		}
		if(!oRadius.equals(nRadius)){
			colNames.add(Alarms.RADIUS);
			colValues.add(nRadius);
		}
		SQLiteHelper sqh = new SQLiteHelper(this);
		if(sqh.updateAlarm(UID, colNames, colValues)){
			Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
		}
		
		Intent intent = new Intent();
		intent.putExtra("names", colNames);
		intent.putExtra("values", colValues);
		if(colNames.size()>0){
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
	}

	
}
