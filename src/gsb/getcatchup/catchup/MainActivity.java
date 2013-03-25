package gsb.getcatchup.catchup;

import com.facebook.Session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
	TabHost tHost;
	FragmentJuggler FJ;
	int navHeight;
	
	FacebookContacts fbContacts;
    
	PrefHelper pHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Log.i(DatabaseHelper.class.getName(),"PRE LOAD MAIN ACTIVITY MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);      
        
        fbContacts = new FacebookContacts(this, savedInstanceState);
        pHelper = new PrefHelper(this);
        
        //Log.i(DatabaseHelper.class.getName(),"LAUNCHED MAIN ACTIVITY");
        //Log.i(DatabaseHelper.class.getName(),"MAIN ACTIVITY MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
        
        final ImageView imgLogo = (ImageView) findViewById(R.id.imgIntroTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgIntroInstructions);        
        final RelativeLayout blurInstructions = (RelativeLayout) findViewById(R.id.introInstructions);
        Button cmdGetStarted = (Button) findViewById(R.id.cmdGetStarted);
        final ImageView imgTitle = (ImageView) findViewById(R.id.imgTitle);
        Button cmdBack = (Button) findViewById(R.id.cmdBack);
        Button cmdSave = (Button) findViewById(R.id.cmdSave);
        cmdBack.setVisibility(Button.GONE);
        cmdSave.setVisibility(Button.GONE);
        
        cmdGetStarted.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				blurInstructions.setVisibility(RelativeLayout.GONE);
				imgLogo.setImageDrawable(null);
				imgHeader.setImageDrawable(null);
				blurInstructions.setBackgroundDrawable(null);
			}        	
        });
        
        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();        
        
        FJ = new FragmentJuggler(this, this, getSupportFragmentManager(), tHost, cmdBack, cmdSave);
        FJ.fbContacts = fbContacts;
       
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() { 
            public void onTabChanged(String tabId) {
            	//Log.i(DatabaseHelper.class.getName(),"TAB CHANGED");
            	
            	FJ.hide_back_button();
            	FJ.hide_save_button();
                FJ.depthTracker.clear();
            	
                FragmentManager fm =   getSupportFragmentManager();
                EveryoneFragment everyoneFragment = (EveryoneFragment) fm.findFragmentByTag("everyone");
                CatchupsFragment catchupsFragment = (CatchupsFragment) fm.findFragmentByTag("catchups");
                AddFragment addFragment = (AddFragment) fm.findFragmentByTag("add");
                SettingsFragment settingsFragment = (SettingsFragment) fm.findFragmentByTag("settings");
                ShareFragment shareFragment = (ShareFragment) fm.findFragmentByTag("share");
                AddContactFragment addCustomFragment = (AddContactFragment) fm.findFragmentByTag("custom");
                CatchupExpandedFragment expandFragment = (CatchupExpandedFragment) fm.findFragmentByTag("expand");
                EditEmailFragment emailFragment = (EditEmailFragment) fm.findFragmentByTag("email");
                EditSMSFragment smsFragment = (EditSMSFragment) fm.findFragmentByTag("sms");
                EditCatchupDateFragment editDateFragment = (EditCatchupDateFragment) fm.findFragmentByTag("date");
                FragmentTransaction ft = fm.beginTransaction(); 
                
                if (editDateFragment != null)
                	ft.detach(editDateFragment);
                                
                if (smsFragment != null)
                	ft.detach(smsFragment);
                
                if (emailFragment != null)
                	ft.detach(emailFragment);
                
                if (expandFragment != null)
                	ft.detach(expandFragment);
                
                if (addCustomFragment != null)
                    ft.detach(addCustomFragment);
                
        		if (everyoneFragment != null)
                    ft.detach(everyoneFragment);
                
                if (catchupsFragment != null)                 	
                    ft.detach(catchupsFragment);                

                if (addFragment !=null)
                    ft.detach(addFragment);
                
                if (settingsFragment != null)
                	ft.detach(settingsFragment);
                
                if (shareFragment != null)
                	ft.detach(shareFragment);
                
                if(tabId.equalsIgnoreCase("add")){                	
                	//Log.i(DatabaseHelper.class.getName(),"CLICKED ADD");
                	imgTitle.setImageResource(R.drawable.add_catchups_title);
                	FJ.depthTracker.add("add");
                    if (addFragment == null)
                        ft.add(R.id.realtabcontent, new AddFragment(FJ), "add");
                    else
                    	ft.attach(addFragment);
                } else if (tabId.equalsIgnoreCase("catchups")) {    
                	//Log.i(DatabaseHelper.class.getName(),"CLICKED CATCHUPS");
                	imgTitle.setImageResource(R.drawable.catchups_title);
                	FJ.depthTracker.add("catchups");
                	if (catchupsFragment == null)
                        ft.add(R.id.realtabcontent, new CatchupsFragment(FJ), "catchups");
                    else {
                        ft.attach(catchupsFragment);
                    }                 
                } else if (tabId.equalsIgnoreCase("settings")) {    
                	//Log.i(DatabaseHelper.class.getName(),"CLICKED SETTINGS");
                	imgTitle.setImageResource(R.drawable.settings_title);
                	FJ.depthTracker.add("settings");
                	if (settingsFragment == null)
                        ft.add(R.id.realtabcontent, new SettingsFragment(FJ), "settings");
                    else
                        ft.attach(settingsFragment);                    
                } else {    
                	//Log.i(DatabaseHelper.class.getName(),"CLICKED SHARE");
                	imgTitle.setImageResource(R.drawable.share_catchup_title);
                	FJ.depthTracker.add("share");
                	if (shareFragment == null)
                        ft.add(R.id.realtabcontent, new ShareFragment(), "share");
                    else
                        ft.attach(shareFragment);                    
                }
                
                ft.commit();
            }
        };
 
       

        tHost.setOnTabChangedListener(tabChangeListener);
        
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
        ImageView img = (ImageView) tabIndicator.findViewById(R.id.imgTab);
        img.setImageResource(R.drawable.cmd_nav_catchups);
        img.setBackgroundResource(R.drawable.cmd_nav_catchups);
        //img.setMaxHeight(navHeight);
        
        TabHost.TabSpec tSpecCatchups = tHost.newTabSpec("catchups");
        tSpecCatchups.setIndicator(tabIndicator);
        tSpecCatchups.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecCatchups);
        
        tabIndicator = LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
        img = (ImageView) tabIndicator.findViewById(R.id.imgTab);
        img.setImageResource(R.drawable.cmd_nav_add);
        img.setBackgroundResource(R.drawable.cmd_nav_add);
       // img.setMaxHeight(navHeight);
        
        TabHost.TabSpec tSpecAdd = tHost.newTabSpec("add");
        tSpecAdd.setIndicator(tabIndicator);
        tSpecAdd.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecAdd);
        
        tabIndicator = LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
        img = (ImageView) tabIndicator.findViewById(R.id.imgTab);
        img.setImageResource(R.drawable.cmd_nav_settings);
        img.setBackgroundResource(R.drawable.cmd_nav_settings);
        //img.setMaxHeight(navHeight);
        
        TabHost.TabSpec tSpecSettings = tHost.newTabSpec("settings");
        tSpecSettings.setIndicator(tabIndicator);
        tSpecSettings.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecSettings);
        
        tabIndicator = LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
        img = (ImageView) tabIndicator.findViewById(R.id.imgTab);
        img.setImageResource(R.drawable.cmd_nav_share);
        img.setBackgroundResource(R.drawable.cmd_nav_share);
        //img.setMaxHeight(navHeight);
        
        TabHost.TabSpec tSpecShare = tHost.newTabSpec("share");
        tSpecShare.setIndicator(tabIndicator);
        tSpecShare.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecShare);
        
        if (pHelper.get_first_bootup_status()) {
        	
        	FragmentManager fm =   getSupportFragmentManager();
        	fm.executePendingTransactions();

        	tHost.setCurrentTab(1);
        	
			imgLogo.setImageResource(R.drawable.blur_title);
			imgHeader.setImageResource(R.drawable.blur_instructions);
			blurInstructions.setVisibility(RelativeLayout.VISIBLE);
        	pHelper.set_first_bootup_status(false);
        } else {
        	blurInstructions.setVisibility(RelativeLayout.GONE);

        }
        //Log.i(DatabaseHelper.class.getName(),"MAIN ACTIVITY MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
    }
    
    @Override
    public void onBackPressed() {    
		InputMethodManager imm = (InputMethodManager) getSystemService(
			    Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    	if(FJ.depthTracker.get(FJ.depthTracker.size()-1).equals("date"))
    		FJ.go_back_persist();
    	else
    		FJ.go_back();
    }
    
    @Override
	public void onPause() {
		super.onPause();
		if (!pHelper.get_facebook_contact_service_status())
			finish();
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    // required by the facebook SDK :(
  	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
        //Log.i(this.getClass().getName(),"ON ACTIVITY RESULT CALLED");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  	}
    
}
