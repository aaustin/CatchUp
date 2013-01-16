package gsb.getcatchup.catchup;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v4.app.Fragment;

public class SettingsFragment extends Fragment {
	private static final long UPDATE_INTERVAL = 200;
	FragmentJuggler FJ;
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	Button addFacebook;
	ImageView facebookCheck;
	
	public SettingsFragment() {
		
	}
	
	public SettingsFragment(FragmentJuggler FJ) {
		this.FJ = FJ;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.settings_view, container, false);
		
		pHelper = new PrefHelper(getActivity());
		
		final ImageView phoneToggle = (ImageView) v.findViewById(R.id.cmdPhoneToggle);
		// final ImageView emailToggle = (ImageView) v.findViewById(R.id.cmdEmailToggle);
		addFacebook = (Button) v.findViewById(R.id.cmdAddFacebook);
		facebookCheck = (ImageView) v.findViewById(R.id.cmdFacebookCheck);
		
		if (!pHelper.get_facebook_enable()) {
			addFacebook.setVisibility(Button.VISIBLE);
			facebookCheck.setVisibility(ImageView.GONE);
		} else {
			addFacebook.setVisibility(Button.GONE);
			facebookCheck.setVisibility(ImageView.VISIBLE);
		}			
		
		if (pHelper.get_phone_notice_status())
			phoneToggle.setImageResource(R.drawable.switch_on);
		else
			phoneToggle.setImageResource(R.drawable.switch_off);
		
		/*if (pHelper.get_email_notice_status())
			emailToggle.setImageResource(R.drawable.switch_on);
		else
			emailToggle.setImageResource(R.drawable.switch_off);
		*/
		addFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				progBar = new NetworkProgressBar(getActivity());
				progBar.show("updating contact list...");
				//Log.i(DatabaseHelper.class.getName(),"LAUNCHED FB DOWNLOAD");
				pHelper.set_facebook_contacts_service_status(true);
				FJ.fbContacts.login_and_download_facebook();
				wait_on_fb();
			}
		});
		
		OnClickListener toggler = new OnClickListener() {
			public void onClick(View v) {
				if (v.getId() == R.id.cmdPhoneToggle) {
					pHelper.set_phone_notice_status(!pHelper.get_phone_notice_status());
					if (pHelper.get_phone_notice_status())
						phoneToggle.setImageResource(R.drawable.switch_on);
					else
						phoneToggle.setImageResource(R.drawable.switch_off);
				}/* else {
					pHelper.set_email_notice_status(!pHelper.get_email_notice_status());
					if (pHelper.get_email_notice_status())
						emailToggle.setImageResource(R.drawable.switch_on);
					else
						emailToggle.setImageResource(R.drawable.switch_off);
				}*/
			}			
		};
		
		phoneToggle.setOnClickListener(toggler);
		//emailToggle.setOnClickListener(toggler);
		
		Button emailMsgEdit = (Button) v.findViewById(R.id.cmdEmailArrow);
		Button smsMsgEdit = (Button) v.findViewById(R.id.cmdSMSArrow);
		
		emailMsgEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.switch_fragments("email");
			}		
		});
		
		smsMsgEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.switch_fragments("sms");
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
             				progBar.hide();
             				fbtimer.cancel();
             				addFacebook.setVisibility(Button.GONE);
             				facebookCheck.setVisibility(ImageView.VISIBLE);
             			}
             		}
  				});  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}

}
