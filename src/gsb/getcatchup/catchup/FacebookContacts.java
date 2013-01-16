package gsb.getcatchup.catchup;

import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.GraphUser;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookContacts {

	public static final String APP_ID = "487384781296097";
	
	public static Session fbSession;

	String[] permissions = { };
	SessionStatusCallback ssCallback;
	FriendsDownloadedCallback friendsCallback;
	Context context;
	ImportDoneFB importFBDone;
	
	PrefHelper pHelper;
	
	public FacebookContacts(Context context, Bundle savedInstanceState) {
		this.context = context;
		
		ssCallback = new SessionStatusCallback();
		friendsCallback = new FriendsDownloadedCallback();
		importFBDone = new ImportDoneFB();
		pHelper = new PrefHelper(context);
		
		fbSession = Session.getActiveSession();
		
		fbSession = Session.getActiveSession();
        if (fbSession == null) {
            if (savedInstanceState != null) {
            	fbSession = Session.restoreSession((Activity) context, null, ssCallback, savedInstanceState);
            }
            if (fbSession == null) {
            	fbSession = new Session((Activity) context);
            }
            Session.setActiveSession(fbSession);
            if (fbSession.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	fbSession.openForRead(new Session.OpenRequest((Activity) context).setCallback(ssCallback));
            }
        }
		
		fbSession.addCallback(ssCallback);		
	}
	
	
	
	public void login_and_download_facebook() {		
        if (!fbSession.isOpened() && !fbSession.isClosed()) {
        	fbSession.openForRead(new Session.OpenRequest((Activity) context).setCallback(ssCallback));
        } else {
            Session.openActiveSession((Activity) context, true, ssCallback);
        }
	}
	
	public void download_friends() {		
		//Log.i(this.getClass().getName(),"LAUNCHED ASYNC TASK TO DOWNLOAD");
		Request.executeMyFriendsRequestAsync(Session.getActiveSession(), friendsCallback);				
	}
	
	
	public class FriendsDownloadedCallback implements Request.GraphUserListCallback {
		public void onCompleted(final List<GraphUser> users, Response response) {
			//if (response.getError() != null)
				//Log.i(this.getClass().getName(),"ERROR: " + response.getError().toString());
			
			//Log.i(this.getClass().getName(),"FINISHED ASYNC TASK");
			if (users != null) {
				Runnable runnable2 = new Runnable() {
		            public void run() {   		
		            	ParseContacts pContacts = new ParseContacts(context, null, importFBDone);
						pContacts.download_facebook_contacts(users);
		            }
				};
				new Thread(runnable2).start();	
			}
		}			
	}

	
	
	public class SessionStatusCallback implements Session.StatusCallback {
		public void call(Session session, SessionState state,
				Exception exception) {
			//Log.i(this.getClass().getName(),"SESSION STATE CHANGED");
			if (state == SessionState.OPENED) {
				//Log.i(this.getClass().getName(),"LOGIN SUCCEEDED");
				pHelper.set_facebook_enable(true);
				download_friends();
			} else if (state == SessionState.CLOSED_LOGIN_FAILED) {
				Log.i(this.getClass().getName(),"LOGIN FAILED");
				Log.i(this.getClass().getName(), exception.toString());
				pHelper.set_facebook_failed(true);
			} else if (state == SessionState.OPENING) {
				//Log.i(this.getClass().getName(),"OPENING UP");
			}
			
		}
	}
	
	// interface implementation login callback
  	public class ImportDoneFB implements CallBack {
  		public void callback(int threadID) {
  			if (threadID == 1) {
  				//Log.i(getClass().getSimpleName(), "Import done!");	
  				Date time = new Date();
  				pHelper.set_facebook_contacts_service_status(false);
  				pHelper.set_contacts_time(time.getTime());
  				// phone contacts parse
  			}
  		}
  	}		
}
