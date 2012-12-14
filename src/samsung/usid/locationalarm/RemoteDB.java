package samsung.usid.locationalarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class RemoteDB {

	private JSONParser jParser;
	
	
	
	
	public RemoteDB() {
		jParser = new JSONParser();
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
	
	
}
