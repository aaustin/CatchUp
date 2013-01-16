package gsb.getcatchup.catchup;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PrefHelper {
	private static final String APP_SHARED_PREFS = "com.sympostia.mainpref";
	private static final long TIME_EXPIRATION = 7*24*60*60*1000; // 7 days until checks contacts again

	private static final String FIRST_BOOTUP = "first_bootup";
	private static final String CONFIG_STATUS = "config_status";
	private static final String EMAIL = "email";
	private static final String EMAIL_NOTIFICATIONS_STATUS = "email_notification_status";
	private static final String PHONE_NOTIFICATIONS_STATUS = "phone_notification_status";
	private static final String EMAIL_SUBJECT_SAVED = "email_subject";
	private static final String EMAIL_CONTENT_SAVED = "email_content";
	private static final String SMS_CONTENT_SAVED = "sms_content";
		
    private static final String FACEBOOK_ENABLE = "facebook_enable";
    private static final String FACEBOOK_FAILED = "facebook_failed";
    private static final String FACEBOOK_CONTACTS_SERVICE = "facebook_contacts_service";

	private static final String PHONE_CONTACTS_TIME = "phone_contacts_time";
	private static final String PHONE_CONTACTS_SERVICE = "phone_contacts_service";
	
	
	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;	
	
	Context ctx;
	
	public PrefHelper(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
		
		ctx = context;
		this.prefsEditor = appSharedPrefs.edit();
	}
		
	// check if internet is enabled
 	public boolean check_enable_online() {
 	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
 	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
 	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
 	        return true;
 	    }
 	    return false;
 	}
 	
 	// FIRST BOOT UP
 	public boolean get_first_bootup_status() {
 		return appSharedPrefs.getBoolean(FIRST_BOOTUP, true);
 	}
 		
 	public void set_first_bootup_status(boolean check) {
 		prefsEditor.putBoolean(FIRST_BOOTUP, check);
 		prefsEditor.commit();
 	}

	// CONFIG STATUS
	public boolean get_config_status() {
		return appSharedPrefs.getBoolean(CONFIG_STATUS, false);
	}
		
	public void set_config_status(boolean check) {
		prefsEditor.putBoolean(CONFIG_STATUS, check);
		prefsEditor.commit();
	}
	
	public String get_email() {
		return appSharedPrefs.getString(EMAIL, "none");
	}	

	public void set_email(String email) {		
		prefsEditor.putString(EMAIL, email);
		prefsEditor.commit();
	}
	
	public String get_email_subject() {
		return appSharedPrefs.getString(EMAIL_SUBJECT_SAVED, "Let's catch up");
	}	

	public void set_email_subject(String email) {		
		prefsEditor.putString(EMAIL_SUBJECT_SAVED, email);
		prefsEditor.commit();
	}
	
	public String get_email_content() {
		return appSharedPrefs.getString(EMAIL_CONTENT_SAVED, "Hey #FirstName,\nHaven't seen you in a while. Let's catch up sometime soon?\n");
	}	

	public void set_email_content(String email) {		
		
		//Log.i(DatabaseHelper.class.getName(), email);
		prefsEditor.putString(EMAIL_CONTENT_SAVED, email);
		prefsEditor.commit();
	}
	

	public String get_sms_content() {
		
		return appSharedPrefs.getString(SMS_CONTENT_SAVED, "Hey #FirstName, haven't seen you in a while. Let's catch up sometime soon?");
	}	

	public void set_sms_content(String email) {		
		//Log.i(DatabaseHelper.class.getName(), email);
		prefsEditor.putString(SMS_CONTENT_SAVED, email);
		prefsEditor.commit();
	}
	
	public boolean get_email_notice_status() {
		return appSharedPrefs.getBoolean(EMAIL_NOTIFICATIONS_STATUS, false);
	}
		
	public void set_email_notice_status(boolean check) {
		prefsEditor.putBoolean(EMAIL_NOTIFICATIONS_STATUS, check);
		prefsEditor.commit();
	}
	
	public boolean get_phone_notice_status() {
		return appSharedPrefs.getBoolean(PHONE_NOTIFICATIONS_STATUS, true);
	}
		
	public void set_phone_notice_status(boolean check) {
		prefsEditor.putBoolean(PHONE_NOTIFICATIONS_STATUS, check);
		prefsEditor.commit();
	}
		
	
	// FACEBOOK SESSION
	
	public boolean get_facebook_enable() {
		return appSharedPrefs.getBoolean(FACEBOOK_ENABLE, false);
	}
		
	public void set_facebook_enable(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_ENABLE, check);
		prefsEditor.commit();
	}
	
	public boolean get_facebook_failed() {
		return appSharedPrefs.getBoolean(FACEBOOK_FAILED, false);
	}
		
	public void set_facebook_failed(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_FAILED, check);
		prefsEditor.commit();
	}
		
	public boolean get_facebook_contact_service_status() {
		return appSharedPrefs.getBoolean(FACEBOOK_CONTACTS_SERVICE, false);
	}
		
	public void set_facebook_contacts_service_status(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_CONTACTS_SERVICE, check);
		prefsEditor.commit();
	}	
		
	
	// CONTACTS STATUS
	public boolean phone_contacts_stale() {
		Date time = new Date();
		
		if ((time.getTime() - get_contacts_time()) > TIME_EXPIRATION)
			return true;
		else
			return false;
	}
	
	public long get_contacts_time() {
		return appSharedPrefs.getLong(PHONE_CONTACTS_TIME, 0);
	}
		
	public void set_contacts_time(long time) {
		prefsEditor.putLong(PHONE_CONTACTS_TIME, time);
		prefsEditor.commit();
	}
	
	public boolean get_phone_contact_service_status() {
		return appSharedPrefs.getBoolean(PHONE_CONTACTS_SERVICE, false);
	}
		
	public void set_phone_contacts_service_status(boolean check) {
		prefsEditor.putBoolean(PHONE_CONTACTS_SERVICE, check);
		prefsEditor.commit();
	}	
}
