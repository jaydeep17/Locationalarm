package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	public static final String URL = "http://10.0.2.2/my_gcm/register.php";
	SharedPreferences sp;

	public GCMIntentService() {
		super(Globals.SENDER_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Set<String> ks = intent.getExtras().keySet();
		JSONObject json = new JSONObject();
		try {
			for (String s : ks) {
				json.put(s, intent.getStringExtra(s));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String msg = json.getString("message");
			Log.d(TAG, "Message Recieved : " + json.toString());
			generateNotification(context, msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Log.d(TAG, "Device registered : " + regId);
		// Updates the gcm_regId on the Server database
		RemoteDB rdb = new RemoteDB(context);
		rdb.updateGCMregId(regId);
		GCMRegistrar.setRegisteredOnServer(context, true);
		generateNotification(context, "Successfully registered");
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.d(TAG, "Device Unregistered : " + regId);
		// Updates the gcm_regId on the Server database
		RemoteDB rdb = new RemoteDB(context);
		rdb.updateGCMregId("");
	}

	private void generateNotification(Context context, String text) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("GCM Message").setContentText(text)
				.setAutoCancel(true);

		builder.setContentIntent(PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(),
				PendingIntent.FLAG_UPDATE_CURRENT));
		NotificationManager nm = (NotificationManager) context
				.getSystemService(NOTIFICATION_SERVICE);
		nm.notify(0, builder.build());
	}


}
