package gsb.getcatchup.catchup;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AddFragment extends Fragment {
	private static final long UPDATE_INTERVAL = 200;
	FragmentJuggler FJ;
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	Context context;
	
	public AddFragment() {
	
	}
	
	public AddFragment(FragmentJuggler fj) {
		this.FJ = fj;
		context = FJ.context;
		pHelper = new PrefHelper(context);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.add_view, container, false);
		Button addContacts = (Button) v.findViewById(R.id.cmdAddContacts);
		Button addCustom = (Button) v.findViewById(R.id.cmdAddCustom);
		Button addFacebook = (Button) v.findViewById(R.id.cmdAddFacebook);
		
		addContacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				FJ.switch_fragments("everyone");
				FJ.show_back_button();
			}			
		});
		
		addCustom.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				FJ.switch_fragments("custom");
				FJ.show_back_button();
			}			
		});
		
		addFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				if (pHelper.get_facebook_enable())
					load_fb_fragment();
				else {
					progBar = new NetworkProgressBar(context);
					progBar.show("updating contact list...");
					//Log.i(DatabaseHelper.class.getName(),"LAUNCHED FB DOWNLOAD");
					pHelper.set_facebook_contacts_service_status(true);
					pHelper.set_facebook_failed(false);
					FJ.fbContacts.login_and_download_facebook();
					wait_on_fb();
				}
				
				
			}			
		});
	
        return v;
    }
	
	
	private void wait_on_fb() {
		fbtimer = new Timer();
		fbtimer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {				
  				getActivity().runOnUiThread(new Runnable() {
             		public void run() { 		 			
             			if (!pHelper.get_facebook_contact_service_status() && pHelper.get_facebook_enable()) {	
             				progBar.show("finished!");
             				//Log.i(DatabaseHelper.class.getName(),"FINISHED FB DOWNLOAD");
             				load_fb_fragment();
             				progBar.hide();
             				fbtimer.cancel();
             			} else if (pHelper.get_facebook_failed()) {
             				progBar.show("failed to connect!");
             				//Log.i(DatabaseHelper.class.getName(),"FINISHED FB DOWNLOAD");             				
             				progBar.hide();
             				fbtimer.cancel();
             			}
             		}
  				});  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}
	
	private void load_fb_fragment() {
		FJ.switch_fragments("facebook");
		FJ.show_back_button();
	}
	
}
