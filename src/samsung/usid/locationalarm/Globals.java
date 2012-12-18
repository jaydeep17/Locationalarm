package samsung.usid.locationalarm;

public class Globals {
	public static final String PREFS_NAME = "Location_Alarm_prefs";
	public static final String PREFS_EMAIL = "email";
	public static final String PREFS_PASS = "password";
	public static final String PREFS_FIXED_EMAIL = "fixed_email";
	public static final String FORWARD_FRIENDS = "friends";
	public static final String FORWARD = "forward";
	public static final String FIRST_TIME = "first";
	public static final String NAME = "name";
	public static final String LOGFILE = "logfile.txt";
	public static final String URL = "http://10.0.2.2/la_php_api/";
	
	public static final String loginTag = "login";
	public static final String registerTag = "register";
	public static final String regIdTag ="regIdtag";
	public static final String syncTag = "sync";
	public static final String downloadTag = "download";
	public static final String hisAlarmTag = "hisAlarm";
	public static final String hisPFTag = "hisPF";
	
	public static final String SENDER_ID = "442032442874";
	
	public static String fixEmail(String email){
		return email.replaceAll("[^a-z0-9]", "_");
	}
}
