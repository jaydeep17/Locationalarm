package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RemoteDB {

	private JSONParser jParser;
	private SharedPreferences sp;
	private Context context;
	
	
	public RemoteDB(Context context) {
		jParser = new JSONParser();
		this.context = context;
	}
	
	public JSONObject loginUser(String email, String password){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", Globals.loginTag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jParser.makeHttpRequest(Globals.URL,"POST", params);
        return json;
	}
	
	public JSONObject registerUser(String name, String email, String password){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", Globals.registerTag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
 
        JSONObject json = jParser.makeHttpRequest(Globals.URL,"POST", params);
        return json;
    }
	
	public JSONObject updateGCMregId(String regId){
		sp = context.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
		String myEmail = sp.getString(Globals.PREFS_EMAIL, null);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String query = "UPDATE users SET gcm_regId='"+regId+"' WHERE email='"+myEmail+"'";
		Log.d("query", query);
		params.add(new BasicNameValuePair("tag", Globals.regIdTag));
		params.add(new BasicNameValuePair(Globals.regIdTag, query));
		JSONParser jParser = new JSONParser();
		return jParser.makeHttpRequest(Globals.URL, "POST", params);
	}
	
	
}
