package gsb.getcatchup.catchup;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
	private static final long SPLASH_DISPLAY_TIME = 1500;
	private static final long UPDATE_INTERVAL = 200;
	private Timer disptimer;
	private Timer faketimer;
	PrefHelper pHelper;
	TextView txtStatus;
	ProgressBar progBar;
	ImageView logo;
	RelativeLayout background;
	long displayCount;
	
	ImportDone importDone;
	ParseContacts pContacts;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Log.i(DatabaseHelper.class.getName(),"LAUNCHED SPLASH");
        pHelper = new PrefHelper(this);
        
        //Log.i(DatabaseHelper.class.getName(),"SPLASH MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
        
        displayCount = 0;
        
        logo = (ImageView) findViewById(R.id.imgLogo);
        background = (RelativeLayout) findViewById(R.id.relLayout);
        
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtStatus.setText(R.string.loading);
        progBar = (ProgressBar) findViewById(R.id.progBarContacts);
        
        if (pHelper.phone_contacts_stale() && !pHelper.get_phone_contact_service_status()) {
        	//Log.i(getClass().getSimpleName(), "service started");
        	pHelper.set_phone_contacts_service_status(true);
        	download_contacts();
	        load_status();
        } else {
        	fake_display_splash();
        }
    }
    
    private void download_contacts() {
    	importDone = new ImportDone();
    	pContacts = new ParseContacts(this, importDone, null);
    	
    	Runnable runnable1 = new Runnable() {
            public void run() {   				
				pContacts.faster_download_phone_contacts();	
            }
		};
		new Thread(runnable1).start();	
    }
    
 
    @Override
    public void onResume() {    	
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
      // Stop the tracker when it is no longer needed.
      if (disptimer != null) 
    	  disptimer.cancel();
    }
    
    // handles the polling for departure, less frequent check
  	private void load_status() {
  		//Log.i(getClass().getSimpleName(), "starting timer");
  		disptimer = new Timer();
  		disptimer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {				
  				runOnUiThread(new Runnable() {
             		public void run() { 		 			
             			displayCount = displayCount + UPDATE_INTERVAL;
             			if (displayCount > SPLASH_DISPLAY_TIME) {
	 		 				if (!pHelper.get_phone_contact_service_status()) {
	 		 					//Log.i(getClass().getSimpleName(), "timer says service done");
	 		 					progBar.setVisibility(ProgressBar.INVISIBLE); 	
	 		 					txtStatus.setText(R.string.loaded); 		 						
	 		 					load_next_activity();
	 		 					disptimer.cancel();
	 		 				}
 		 				}
             		}
  				});
  					
  			}
  		}, 0, UPDATE_INTERVAL);
  		
  	}  	
  	
  	private void fake_display_splash() {
  		faketimer = new Timer();
  		faketimer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {				
  				runOnUiThread(new Runnable() {
             		public void run() { 		 			
             			displayCount = displayCount + UPDATE_INTERVAL;
             			if (displayCount > SPLASH_DISPLAY_TIME) {	
	 		 				progBar.setVisibility(ProgressBar.INVISIBLE); 	
	 		 				txtStatus.setText(R.string.loaded); 		 						
	 		 				load_next_activity();
	 		 				faketimer.cancel();	 		 				
             			}
             		}
  				});
  					
  			}
  		}, 0, UPDATE_INTERVAL);
  	}
  	
  	private void load_next_activity() {
  		 //Log.i(DatabaseHelper.class.getName(),"SPLASH ACT MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
  		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
		logo.setImageDrawable(null);
		background.setBackgroundDrawable(null);
		finish();
		 //Log.i(DatabaseHelper.class.getName(),"SPLASH ACT MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
  	}
    
  	
    // interface implementation login callback
  	public class ImportDone implements CallBack {
  		public void callback(int threadID) {
  			if (threadID == 0) {
  				
  				//Log.i(getClass().getSimpleName(), "Import done!");	
  				 //Log.i(DatabaseHelper.class.getName(),"MEMORY STATE: " + Debug.getNativeHeapSize());
  				Date time = new Date();
  				pHelper.set_phone_contacts_service_status(false);
  				pHelper.set_contacts_time(time.getTime());
  				// phone contacts parse
  			}
  		}
  	}		
}
