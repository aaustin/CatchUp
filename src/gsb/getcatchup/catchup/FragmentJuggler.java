package gsb.getcatchup.catchup;

import java.util.ArrayList;
import java.util.Calendar;

import gsb.getcatchup.catchup.AddContactFragment.CustomDateAdd;
import gsb.getcatchup.catchup.CatchupExpandedFragment.CustomDate;
import gsb.getcatchup.catchup.CatchupsFragment.UpdateListView;
import gsb.getcatchup.catchup.EveryoneFragment.UpdateEveryoneListView;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class FragmentJuggler {
	
	FragmentManager fm;
	FragmentTransaction ft;
	
	Context context;
	Activity main;
	
	ArrayList<String> depthTracker;
	
	Button cmdBack;
	Button cmdSave;
	TabHost tHost;
		
	UpdateListView callback;
	UpdateEveryoneListView callbackEveryone;
	CustomDate callbackEditDateExpand;
	CustomDateAdd callBackEditDateAdd;
	
	OnClickListener regBack;
	OnClickListener persistBack;
	
	FacebookContacts fbContacts;
	
	Calendar lazydate;
	int index; 
	boolean remove;
	boolean back_allowed;
	
	public FragmentJuggler(Context context, Activity act, FragmentManager fm, TabHost tHost, Button back, Button save) {
		this.fm = fm;
		this.context = context;
		this.main = act;
		this.cmdBack = back;	
		this.cmdSave = save;
		this.tHost = tHost;
		
		index = 0;
		remove = false;
		back_allowed = false;
		
		depthTracker = new ArrayList<String>();
		
		regBack = new OnClickListener() {
			public void onClick(View v) {				
				go_back();
			}			
		};
		
		persistBack = new OnClickListener() {
			public void onClick(View v) {				
				go_back_persist();
				cmdBack.setOnClickListener(regBack);
			}			
		};
		
		cmdBack.setOnClickListener(regBack);
	}
	
	public void go_back() {
		//Log.i(DatabaseHelper.class.getName(),"REGULAR GO BACK");
		if (depthTracker.size() > 0) depthTracker.remove(depthTracker.size()-1);
		if (depthTracker.size() > 0) switch_fragments(depthTracker.remove(depthTracker.size()-1));
		else main.finish();
	}
	
	public void go_back_persist() {
		depthTracker.remove(depthTracker.size()-1);
		
		//Log.i(DatabaseHelper.class.getName(),"PERSIST GO BACK");
		
		
		EditCatchupDateFragment editDateFragment = (EditCatchupDateFragment) fm.findFragmentByTag("date");
		CatchupExpandedFragment expandFragment;
		AddContactFragment customFragment;

		ft = fm.beginTransaction();
		
		if (editDateFragment != null)
        	ft.detach(editDateFragment);
		
		if (depthTracker.get(depthTracker.size()-1).equals("expand")) {
			expandFragment = (CatchupExpandedFragment) fm.findFragmentByTag("expand");	
			ft.attach(expandFragment);
			expandFragment.init_old_person(index, remove);
		} else {
			customFragment = (AddContactFragment) fm.findFragmentByTag("custom");	
			ft.attach(customFragment);
			customFragment.init_old_person();
		}
		
		
		ft.commit();
	}
		
	public void switch_fragments(String destFragment) {		
		
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
        ft = fm.beginTransaction();
        
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
		
		 if(destFragment.equalsIgnoreCase("everyone")) { 
         	//Log.i(DatabaseHelper.class.getName(),"LOADING EVERYONE FRAGMENT");
          	show_back_button();
          	hide_save_button();
          	depthTracker.add("everyone");
             if (everyoneFragment == null)
                 ft.add(R.id.realtabcontent, new EveryoneFragment(this, "phone"), "everyone");
             else {
            	everyoneFragment.source = "phone";
             	ft.attach(everyoneFragment);
             }
         } else if(destFragment.equalsIgnoreCase("facebook")) { 
         	//Log.i(DatabaseHelper.class.getName(),"LOADING FACEBOOK FRAGMENT");
          	show_back_button();
          	hide_save_button();
          	depthTracker.add("facebook");
             if (everyoneFragment == null)
                 ft.add(R.id.realtabcontent, new EveryoneFragment(this, "fb"), "everyone");
             else {
            	everyoneFragment.source = "fb";
             	ft.attach(everyoneFragment);
             }
         } else if(destFragment.equalsIgnoreCase("expand")) { 
          	//Log.i(DatabaseHelper.class.getName(),"LOADING EXPAND FRAGMENT");
          	show_back_button();
          	show_save_button();
          	depthTracker.add("expand");
            if (expandFragment == null) {
            	//Log.i(DatabaseHelper.class.getName(),"creating new expand fragment");
            	expandFragment = new CatchupExpandedFragment(this, cmdSave);            	
                ft.add(R.id.realtabcontent, expandFragment, "expand");
                expandFragment.init_new_person(index, remove);
            } else {
            	//Log.i(DatabaseHelper.class.getName(),"using old expand fragment");
            	ft.attach(expandFragment);
            	expandFragment.init_new_person(index, remove);
            }
        } else if(destFragment.equalsIgnoreCase("custom")) { 
          	//Log.i(DatabaseHelper.class.getName(),"CLICKED CUSTOM");
          	show_back_button();
          	show_save_button();
          	depthTracker.add("custom");
            if (addCustomFragment == null) {
            	addCustomFragment = new AddContactFragment(this, cmdSave);
                ft.add(R.id.realtabcontent, addCustomFragment, "custom");
                addCustomFragment.init_new_person();
            } else {
            	ft.attach(addCustomFragment);
            	addCustomFragment.init_new_person();
            }
        } else if(destFragment.equalsIgnoreCase("add")){ 
        	//Log.i(DatabaseHelper.class.getName(),"CLICKED ADD");
        	hide_back_button();
        	hide_save_button();
        	depthTracker.add("add");
        	if (addFragment == null)
                ft.add(R.id.realtabcontent, new AddFragment(this), "add");
            else
            	ft.attach(addFragment);
        } else if (destFragment.equalsIgnoreCase("catchups")) {    
        	//Log.i(DatabaseHelper.class.getName(),"CLICKED CATCHUPS");
        	hide_back_button();
        	hide_save_button();
        	depthTracker.add("catchups");
        	if (catchupsFragment == null)
                ft.add(R.id.realtabcontent, new CatchupsFragment(this), "catchups");
            else {
                ft.attach(catchupsFragment);
            }
        } else if (destFragment.equalsIgnoreCase("email")) {    
        	//Log.i(DatabaseHelper.class.getName(),"CLICKED CATCHUPS");
        	show_back_button();
        	show_save_button();
        	depthTracker.add("email");
        	if (emailFragment == null)
                ft.add(R.id.realtabcontent, new EditEmailFragment(this, cmdSave), "email");
            else {
                ft.attach(emailFragment);
            }
        } else if (destFragment.equalsIgnoreCase("sms")) {    
        	//Log.i(DatabaseHelper.class.getName(),"CLICKED CATCHUPS");
        	show_back_button();
        	show_save_button();
        	depthTracker.add("sms");
        	if (smsFragment == null)
                ft.add(R.id.realtabcontent, new EditSMSFragment(this, cmdSave), "sms");
            else {
                ft.attach(smsFragment);              
            }
        } else if (destFragment.equalsIgnoreCase("date")) {    
        	//Log.i(DatabaseHelper.class.getName(),"LOADING DATE EDIT FRAGMENT");
        	show_back_button();
        	show_save_button();
        	depthTracker.add("date");
        	if (editDateFragment == null) {
        		//Log.i(DatabaseHelper.class.getName(),"creating new date edit fragment");
        		ft.add(R.id.realtabcontent, new EditCatchupDateFragment(this, cmdSave), "date");                
        	} else {
        		//Log.i(DatabaseHelper.class.getName(),"using old date edit fragment");
                ft.attach(editDateFragment);     
                editDateFragment.set_false_date();
            }
        } else if (destFragment.equalsIgnoreCase("settings")) {    
        	//Log.i(DatabaseHelper.class.getName(),"CLICKED SETTINGS");
        	hide_back_button();
        	hide_save_button();
        	depthTracker.add("settings");
        	if (settingsFragment == null)
                ft.add(R.id.realtabcontent, new SettingsFragment(this), "settings");
            else
                ft.attach(settingsFragment);                    
        }
		 
		 ft.commit();
	}	
	
	public void show_save_button() {
		cmdSave.setVisibility(Button.VISIBLE);
	}
	
	public void hide_save_button() {
		cmdSave.setVisibility(Button.GONE);
	}
	
	public void show_back_button() {
		cmdBack.setVisibility(Button.VISIBLE);
	}
	
	public void hide_back_button() {
		cmdBack.setVisibility(Button.GONE);
	}
}
