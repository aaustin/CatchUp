package gsb.getcatchup.catchup;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EveryoneListAdapter extends ArrayAdapter<Contact> {

	private final Activity context;
    private final ArrayList<Contact> contacts;
    private final SubscriptionScheduler subSched;
    FragmentJuggler FJ;
    EveryoneFragment everyoneFrag;
    PortraitDownloader pDownloader;
    
	public EveryoneListAdapter(Activity context, ArrayList<Contact> contacts, FragmentJuggler FJ, EveryoneFragment everyoneFrag) {
		super(context, R.layout.everyone_row, contacts);
		//Log.i(DatabaseHelper.class.getName(),"loaded list adapter with " + contacts.size() + " contacts");
		this.subSched = new SubscriptionScheduler(context);
		this.contacts = contacts;
		this.context = context;
		this.FJ = FJ;
		this.everyoneFrag = everyoneFrag;
		pDownloader = new PortraitDownloader(context, contacts);
	}
		

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View rowView;		
		if (convertView == null) {
	        LayoutInflater inflater = context.getLayoutInflater();
	        rowView = inflater.inflate(R.layout.everyone_row, null, true);
		} else {
			rowView = convertView;
			//return rowView;
		}
        
        //Log.i(DatabaseHelper.class.getName(),"showing groupPosition " + groupPosition);
        
        TextView firstname = (TextView) rowView.findViewById(R.id.txtRowFirstname);
        TextView lastname = (TextView) rowView.findViewById(R.id.txtRowLastname);
        ImageView portrait = (ImageView) rowView.findViewById(R.id.imgPortrait);
        final Button cmdSmallSub = (Button) rowView.findViewById(R.id.cmdSmallerSub);
        final Button cmdLargerSub = (Button) rowView.findViewById(R.id.cmdLargerSub);
        final Button cmdArrow = (Button) rowView.findViewById(R.id.cmdArrow);
        
        final Contact curr = contacts.get(position);
        
	            
        cmdSmallSub.setFocusable(false);
        cmdLargerSub.setFocusable(false);
        set_click_responses(position, curr, cmdSmallSub, null, cmdLargerSub, null);        
        
        cmdArrow.setFocusable(false);
        cmdArrow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.index = curr.index;
				FJ.remove = false;
				FJ.switch_fragments("expand");
			}        	
        });
        
        
        firstname.setText(curr.firstname);
        lastname.setText(curr.lastname);      

        if (curr.portrait == null) {
        	portrait.setImageResource(R.drawable.blank_portrait);
        	pDownloader.get_image(position);
        } else {
        	Bitmap bm = BitmapFactory.decodeByteArray(curr.portrait, 0, curr.portrait.length);
        	portrait.setImageBitmap(bm);
        }

        rowView.setOnClickListener(null);
        rowView.setOnLongClickListener(null);
        
        return rowView;
	}	
	
	private void set_click_responses(final int position, final Contact current, final Button smallest, final Button smaller, final Button larger, final Button largest) {
		
		//Log.i(DatabaseHelper.class.getName(),"setting up clicks for index " + current.index + ", " + current.firstname + " " + current.lastname);
		
					
		
		if (smallest != null) {
			smallest.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int index = position;
					//Log.i(DatabaseHelper.class.getName(),"contact ref " + current.index);
					Contact curr = contacts.get(index);
					//Log.i(DatabaseHelper.class.getName(),"setting index 0 for index: " + curr.index + ", " + curr.firstname + " " + curr.lastname);
					if (curr.subindex > 0)
						subSched.unschedule_contact(curr);
					subSched.schedule_contact(curr, 0, true);
					contacts.remove(index);					
					Toast.makeText(context, "Contact added!", Toast.LENGTH_LONG).show();
					notifyDataSetChanged();
				}				
			});
		}
		
		if (smaller != null) {
			smaller.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int index = position;
					//Log.i(DatabaseHelper.class.getName(),"contact ref " + current.index);
					Contact curr = contacts.get(index);
					//Log.i(DatabaseHelper.class.getName(),"setting index 1 for " + curr.firstname + " " + curr.lastname);
					if (curr.subindex > 0)
						subSched.unschedule_contact(curr);
					subSched.schedule_contact(curr, 1, true);					
					contacts.remove(index);					
					Toast.makeText(context, "Contact added!", Toast.LENGTH_LONG).show();
					notifyDataSetChanged();
				}				
			});
		}
		
		if (larger != null) {
			larger.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int index = position;
					//Log.i(DatabaseHelper.class.getName(),"contact ref " + current.index);
					Contact curr = contacts.get(index);
					//Log.i(DatabaseHelper.class.getName(),"setting index 2 for " + curr.firstname + " " + curr.lastname);
					if (curr.subindex > 0)
						subSched.unschedule_contact(curr);
					subSched.schedule_contact(curr, 2, true);					
					contacts.remove(index);					
					Toast.makeText(context, "Contact added!", Toast.LENGTH_LONG).show();
					notifyDataSetChanged();
				}				
			});
		}
		
		if (largest != null) {
			largest.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int index = position;
					//Log.i(DatabaseHelper.class.getName(),"contact ref " + current.index);
					Contact curr = contacts.get(index);
					//Log.i(DatabaseHelper.class.getName(),"setting index 3 for " + curr.firstname + " " + curr.lastname);
					if (curr.subindex > 0)
						subSched.unschedule_contact(curr);
					subSched.schedule_contact(curr, 3, true);					
					contacts.remove(index);					
					Toast.makeText(context, "Contact added!", Toast.LENGTH_LONG).show();
					notifyDataSetChanged();
				}				
			});
		}
	}

	public boolean hasStableIds() {		
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
