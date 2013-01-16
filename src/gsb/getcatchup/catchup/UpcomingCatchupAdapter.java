package gsb.getcatchup.catchup;

import java.util.Date;
import java.text.DateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UpcomingCatchupAdapter extends ArrayAdapter<Contact> {
	
	private final Activity context;
    private final ArrayList<Contact> contacts;
    FragmentJuggler FJ;
    CatchupsFragment catchupsFrag;
    PortraitDownloader pDownloader;
	
	public UpcomingCatchupAdapter(Activity context, ArrayList<Contact> contacts, FragmentJuggler FJ, CatchupsFragment catchupsFrag) {
		super(context, R.layout.catchup_upcoming_row, contacts);
		this.contacts = contacts;
		this.context = context;
		this.FJ = FJ;
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
	        rowView = inflater.inflate(R.layout.catchup_upcoming_row, null, true);
		} else {
			rowView = convertView;
			//return rowView;
		}
		
		RelativeLayout clickableArea = (RelativeLayout) rowView.findViewById(R.id.relativeLayout1);
		//RelativeLayout borderArea = (RelativeLayout) rowView.findViewById(R.id.relativeLayout3);
		TextView firstname = (TextView) rowView.findViewById(R.id.txtRowFirstname);
	    TextView lastname = (TextView) rowView.findViewById(R.id.txtRowLastname);
	    TextView date = (TextView) rowView.findViewById(R.id.txtDueDate);
	    final ImageView portrait = (ImageView) rowView.findViewById(R.id.imgPortrait);
		final Button cmdArrow = (Button) rowView.findViewById(R.id.cmdArrow);
		
	    final Contact curr = contacts.get(position);	
	    
	    //borderArea.setFocusable(true);
	    //clickableArea.setFocusable(false);
	    //cmdArrow.setFocusable(false);	   
	    cmdArrow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Log.i(DatabaseHelper.class.getName(),"Clicked: " + curr.firstname + " " + curr.lastname);
				FJ.index = curr.index;
				FJ.remove = true;
				FJ.switch_fragments("expand");	
			}        	
        });
	    clickableArea.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Log.i(DatabaseHelper.class.getName(),"Clicked: " + curr.firstname + " " + curr.lastname);
				FJ.index = curr.index;
				FJ.remove = true;
				FJ.switch_fragments("expand");	
			}        	
        });	    
	   
	    
	    firstname.setText(curr.firstname);
        lastname.setText(curr.lastname);
        
        DateFormat df = DateFormat.getDateInstance();
        date.setText(df.format(new Date(curr.catchupdate.getTimeInMillis())));
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
