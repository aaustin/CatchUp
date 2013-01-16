package gsb.getcatchup.catchup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CatchupExpandedFragment extends Fragment {

	int index;
	boolean remove;
	SubscriptionScheduler subSched;
		
	View view;
	
	ArrayList<Contact> contacts;
	PrefHelper pHelper;
	
	Button cmdMail;
	Button cmdText;
	
	ImageView portraitImg;
	ImageView smallestCheck;
	ImageView smallerCheck;
	ImageView largerCheck;
	ImageView largestCheck;
	Button cmdSave;
	ImageView cmdEditDate;
	TextView txtDate;
	RelativeLayout editDate;
	ImageView heading;
		
	FragmentJuggler FJ;
	
	boolean customDate;
	Calendar catchupDate;
	int subindex;
	boolean alreadyScheduled;
	Contact currContact;
	
	PortraitDownloader pDownloader;
	
	CustomDate callback;
	
	boolean persist;
	
	public CatchupExpandedFragment() {
		
	}
	
	public CatchupExpandedFragment(FragmentJuggler FJ, Button cmdSave) {
		callback = new CustomDate();
		this.FJ = FJ;
		this.cmdSave = cmdSave;
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.catchup_expanded_view, container, false);               		
		subSched = new SubscriptionScheduler(getActivity());		 
		this.view = v;		
		
		contacts = new ArrayList<Contact>();
		pHelper = new PrefHelper(getActivity());
		
		pDownloader = new PortraitDownloader(getActivity(), contacts);
		
		cmdEditDate = (ImageView) v.findViewById(R.id.cmdEditDate);
		txtDate = (TextView) v.findViewById(R.id.txtDate);
		editDate = (RelativeLayout) v.findViewById(R.id.layoutEditDate);
		heading = (ImageView) v.findViewById(R.id.imgHeading2);
	    cmdMail = (Button) v.findViewById(R.id.cmdEmail);
	    cmdText = (Button) v.findViewById(R.id.cmdChat);
		
		
		//Log.i(DatabaseHelper.class.getName(),"on create of catchup expanded called");
		
		editDate.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
					cmdEditDate.setPressed(true);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					cmdEditDate.setPressed(false);
				return false;
			}			
		});		
		editDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.lazydate = (Calendar) catchupDate.clone();
				//Log.i(DatabaseHelper.class.getName(),"on click edit date: Year = " + FJ.lazydate.get(Calendar.YEAR));
				FJ.switch_fragments("date");
			}			
		});
		cmdEditDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.lazydate = (Calendar) catchupDate.clone();
				//Log.i(DatabaseHelper.class.getName(),"on click edit date: Year = " + FJ.lazydate.get(Calendar.YEAR));
				FJ.switch_fragments("date");
			}			
		});
				
		
		cmdSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				save_him();
			}			
		});
		
		/*
		Button cmdFake = (Button) v.findViewById(R.id.cmdTest);
		cmdFake.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currContact.catchupdate = Calendar.getInstance();
				currContact.catchupdate.add(Calendar.MINUTE, 1);
				currContact.subindex = 1;
				subSched.schedule_contact(currContact, 1, false);
				finish_him(1);
				
			}			
		});*/
		//cmdFake.setVisibility(Button.GONE);
		FJ.callbackEditDateExpand = callback;
		
		init_interface();
		
		cmdMail.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent view = new Intent(Intent.ACTION_VIEW);
	    		StringBuilder uri = new StringBuilder("mailto:");
	    		uri.append("");
	    		uri.append("?subject=").append(Uri.encode(pHelper.get_email_subject()));
	    		uri.append("&body=").append(Uri.encode(pHelper.get_email_content().replace("#FirstName", currContact.firstname)));
	    		view.setData(Uri.parse(uri.toString()));
	    		startActivity(view);
	    	}
	    });
	    
	    cmdText.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
	    		sendIntent.setData(Uri.parse("sms:"));
	    		sendIntent.putExtra("sms_body", pHelper.get_sms_content().replace("#FirstName", currContact.firstname));
	    		startActivity(sendIntent);
	    	}
	    }); 
		
		return v;
    }
    
	private void init_interface() {		
		portraitImg = (ImageView) view.findViewById(R.id.imgPortrait);
		TextView txtFirstname = (TextView) view.findViewById(R.id.txtRowFirstname);
		TextView txtLastname = (TextView) view.findViewById(R.id.txtRowLastname);
		RelativeLayout cmdSmallest = (RelativeLayout) view.findViewById(R.id.cmdSmallestSubscription);
		smallestCheck = (ImageView) view.findViewById(R.id.cmdSmallestCheck);
		RelativeLayout cmdSmaller = (RelativeLayout) view.findViewById(R.id.cmdSmallerSubscription);
		smallerCheck = (ImageView) view.findViewById(R.id.cmdSmallerCheck);
		RelativeLayout cmdLarger = (RelativeLayout) view.findViewById(R.id.cmdLargerSubscription);
		largerCheck = (ImageView) view.findViewById(R.id.cmdLargerCheck);
		RelativeLayout cmdLargest = (RelativeLayout) view.findViewById(R.id.cmdLargestSubscription);
		largestCheck = (ImageView) view.findViewById(R.id.cmdLargestCheck);
		Button cmdRemove = (Button) view.findViewById(R.id.cmdUnsubscribe);
		
		if (!persist) {
			ContactDataAdapter cda = new ContactDataAdapter(getActivity());
			cda.open();
			Contact curr = cda.get_contact(index); 
	        cda.close();
	        
	        customDate = false;
	        contacts.clear();
	        contacts.add(curr);
	        currContact = curr;
	        subindex = curr.subindex;
	        catchupDate = curr.catchupdate;
	        if (curr.subindex >= 0) {
	        	alreadyScheduled = true;
	        	set_date(4);
	        } else {
	        	if (catchupDate == null)
		        	catchupDate = Calendar.getInstance(); 

	        	alreadyScheduled = false;
	        	set_date(subindex);
	        }
	        
	        
	        if (curr.portrait == null)
	        	pDownloader.get_image(0);
	        else {
	        	Bitmap bm = BitmapFactory.decodeByteArray(curr.portrait, 0, curr.portrait.length);
	        	portraitImg.setImageBitmap(bm);
	        }
	        
	        
		} else
			set_date(4);		
		
		
		if(!remove) {
			cmdRemove.setVisibility(Button.GONE);
			cmdMail.setVisibility(Button.GONE);
			cmdText.setVisibility(Button.GONE);
		} else {
			cmdMail.setVisibility(Button.VISIBLE);
			cmdText.setVisibility(Button.VISIBLE);
		}
		
		smallestCheck.setVisibility(ImageView.INVISIBLE);
		smallerCheck.setVisibility(ImageView.INVISIBLE);
		largerCheck.setVisibility(ImageView.INVISIBLE);
		largestCheck.setVisibility(ImageView.INVISIBLE);
		
		txtFirstname.setText(currContact.firstname);
		txtLastname.setText(currContact.lastname);
		
        
        set_click_responses(currContact, cmdSmallest, cmdSmaller, cmdLarger, cmdLargest, cmdRemove);
	}
	
	public void init_new_person(int index, boolean remove) {
		this.index = index;
		this.remove = remove;
		this.persist = false;
	}
	
	public void init_old_person(int index, boolean remove) {
		this.index = index;
		this.remove = remove;
		this.persist = true;
	}
	
	private void set_date(int schedindex) {
		DateFormat df = DateFormat.getDateInstance();
		if (schedindex == -1) {	
			catchupDate = Calendar.getInstance();
        	txtDate.setText(df.format(new Date(catchupDate.getTimeInMillis())));
		} else if (schedindex > -1 && schedindex < 4) {
			catchupDate = subSched.get_future_date(schedindex);
			txtDate.setText(df.format(new Date(catchupDate.getTimeInMillis())));			
		} else
			txtDate.setText(df.format(new Date(catchupDate.getTimeInMillis())));
		
		//Log.i(DatabaseHelper.class.getName(),"set date of " + txtDate.getText().toString());
	}
   
	private void save_him() {		
		if (alreadyScheduled)
			subSched.unschedule_contact(currContact);
		if (subindex > -1) {		
			currContact.subindex = subindex;
			currContact.catchupdate = catchupDate;
			
			subSched.schedule_contact(currContact, subindex, !customDate);					
		} 		
		
		finish_him(subindex);
	}
	
    private void finish_him(int option) {
    	FJ.go_back();
    	
    	if (remove) {
    		FJ.callback.callback(option, index);
    		FJ.hide_back_button();
    	} else {
    		FJ.callbackEveryone.callback(option, index);
    	}
    }    
    
    private void set_click_responses(final Contact current, final RelativeLayout smallest, final RelativeLayout smaller, final RelativeLayout larger, final RelativeLayout largest, final Button remove) {
		
		if (subindex == 0)
			smallestCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 1 && smaller != null)
			smallerCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 2 && larger != null)
			largerCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 3 && largest != null)
			largestCheck.setVisibility(ImageView.VISIBLE);
				
		
		smallest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (subindex == 0) {
					subindex = -1;					
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(-1);
				} else {				
					subindex = 0;
					smallestCheck.setVisibility(ImageView.VISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(0);
					//finish_him(1);
				}
			}				
		});
		
		smaller.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				if (subindex == 1) {
					subindex = -1;					
					smallerCheck.setVisibility(ImageView.INVISIBLE);	
					set_date(-1);
				} else {					
					subindex = 1;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.VISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(1);
					//finish_him(2);
				}
			}				
		});
		
		larger.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				if (subindex == 2) {
					subindex = -1;					
					largerCheck.setVisibility(ImageView.INVISIBLE);
					set_date(-1);
				} else {
					subindex = 2;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.VISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(2);
					//finish_him(3);
				}
			}				
		});
		
		largest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				if (subindex == 3) {
					subindex = -1;					
					largestCheck.setVisibility(ImageView.INVISIBLE);	
					set_date(-1);
				} else {
					subindex = 3;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.VISIBLE);		
					set_date(3);
					//finish_him(4);
				}
			}				
		});	
		
		remove.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {							
				if (current.subindex >= 0) {
					subSched.unschedule_contact(current);					
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					finish_him(-1);
				}
			}			
		});
	}
    
	// interface implementation login callback
	public class CustomDate implements EditDateCallback {
		public void callback(Calendar c) {	
			//Log.i(DatabaseHelper.class.getName(),"call back from edit date fragment");
			customDate = true;
			catchupDate = c;
			set_date(4);
			
			if (subindex == -1) {
				subindex = 1;
				smallerCheck.setVisibility(ImageView.VISIBLE);
			}
		}
 		
	}
}
