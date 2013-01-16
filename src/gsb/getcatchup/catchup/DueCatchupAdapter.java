package gsb.getcatchup.catchup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DueCatchupAdapter extends ArrayAdapter<Contact> {
	
	private final Activity context;
    private final ArrayList<Contact> contacts;
    private final SubscriptionScheduler subSched;  
    FragmentJuggler FJ;
    CatchupsFragment catchupsFrag;
    PrefHelper pHelper;
    PortraitDownloader pDownloader;
	
	public DueCatchupAdapter(Activity context, ArrayList<Contact> contacts, FragmentJuggler FJ, CatchupsFragment catchupsFrag) {
		super(context, R.layout.catchup_due_row, contacts);
		this.subSched = new SubscriptionScheduler(context);
		this.contacts = contacts;
		this.context = context;
		this.FJ = FJ;
		pHelper = new PrefHelper(context);
		this.catchupsFrag = catchupsFrag;
		pDownloader = new PortraitDownloader(context, contacts);
	}	
	
	@Override	
	public boolean isEnabled(int position) {
		return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;		
		
		if (convertView == null) {
	        LayoutInflater inflater = context.getLayoutInflater();
	        rowView = inflater.inflate(R.layout.catchup_due_row, null, true);
		} else { 
			rowView = convertView;			
			//return rowView;
		}
		
       
		TextView firstname = (TextView) rowView.findViewById(R.id.txtRowFirstname);
	    TextView lastname = (TextView) rowView.findViewById(R.id.txtRowLastname);
	    ImageView portrait = (ImageView) rowView.findViewById(R.id.imgPortrait);
	    Button cmdDismiss = (Button) rowView.findViewById(R.id.cmdDismiss);
	    Button cmdMail = (Button) rowView.findViewById(R.id.cmdEmail);
	    Button cmdText = (Button) rowView.findViewById(R.id.cmdChat);
	    final Button cmdArrow = (Button) rowView.findViewById(R.id.cmdArrow);
	        
	    final Contact curr = contacts.get(position);	    	       
	    
	    cmdArrow.setFocusable(false);
	    cmdArrow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.index = curr.index;
				FJ.remove = true;
				FJ.switch_fragments("expand");
			}        	
        });
	    
	    cmdMail.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent view = new Intent(Intent.ACTION_VIEW);
	    		StringBuilder uri = new StringBuilder("mailto:");
	    		uri.append("");
	    		uri.append("?subject=").append(Uri.encode(pHelper.get_email_subject()));
	    		uri.append("&body=").append(Uri.encode(pHelper.get_email_content().replace("#FirstName", curr.firstname)));
	    		view.setData(Uri.parse(uri.toString()));
	    		catchupsFrag.startActivity(view);
	    	}
	    });
	    
	    cmdText.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
	    		sendIntent.setData(Uri.parse("sms:"));
	    		sendIntent.putExtra("sms_body", pHelper.get_sms_content().replace("#FirstName", curr.firstname));
	    		catchupsFrag.startActivity(sendIntent);
	    	}
	    }); 
	    
	    cmdDismiss.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		subSched.schedule_contact(curr, curr.subindex, true);
	    		FJ.callback.callback(1, curr.index);
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
	

}
