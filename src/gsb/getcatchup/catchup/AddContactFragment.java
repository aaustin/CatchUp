package gsb.getcatchup.catchup;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddContactFragment extends Fragment {

	private static final int PICK_IMAGE = 1;
	
	Context context;
	Fragment curr;
	
	ImageView portrait;
	EditText txtFirstname;
	EditText txtLastname;
	RelativeLayout cmdSmallest;
	RelativeLayout cmdSmaller;
	RelativeLayout cmdLarger;
	RelativeLayout cmdLargest;
    Button cmdSave;
    
	ImageView cmdEditDate;
	TextView txtDate;
	RelativeLayout editDate;
        
    ImageView smallestCheck;
	ImageView smallerCheck;
	ImageView largerCheck;
	ImageView largestCheck;
	
    boolean customDate;
	Calendar catchupDate;
    byte[] portraitArr;
    int subindex;
    boolean persist;
    
    CustomDateAdd callback;
    
    SubscriptionScheduler subSched;
    
    FragmentJuggler FJ;
    String firstName;
    String lastName;
	
    
    public AddContactFragment() {
    	
    }
    
	public AddContactFragment(FragmentJuggler fj, Button save) {
		this.FJ = fj;
		this.cmdSave = save;		
		callback = new CustomDateAdd();
		FJ.callBackEditDateAdd = callback;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.add_contact_view, container, false);

        context = getActivity();
        curr = this;
        
        cmdEditDate = (ImageView) v.findViewById(R.id.cmdEditDate);
		txtDate = (TextView) v.findViewById(R.id.txtDate);
		editDate = (RelativeLayout) v.findViewById(R.id.layoutEditDate);
        
        portrait = (ImageView) v.findViewById(R.id.imgPortraitAdd);
        txtFirstname = (EditText) v.findViewById(R.id.txtFirstname);
        txtLastname = (EditText) v.findViewById(R.id.txtLastname);
        cmdSmallest = (RelativeLayout) v.findViewById(R.id.cmdSmallestSubscription);
		smallestCheck = (ImageView) v.findViewById(R.id.cmdSmallestCheck);
		cmdSmaller = (RelativeLayout) v.findViewById(R.id.cmdSmallerSubscription);
		smallerCheck = (ImageView) v.findViewById(R.id.cmdSmallerCheck);
		cmdLarger = (RelativeLayout) v.findViewById(R.id.cmdLargerSubscription);
		largerCheck = (ImageView) v.findViewById(R.id.cmdLargerCheck);
		cmdLargest = (RelativeLayout) v.findViewById(R.id.cmdLargestSubscription);
		largestCheck = (ImageView) v.findViewById(R.id.cmdLargestCheck);
         
        smallestCheck.setVisibility(ImageView.INVISIBLE);
		smallerCheck.setVisibility(ImageView.INVISIBLE);
		largerCheck.setVisibility(ImageView.INVISIBLE);
		largestCheck.setVisibility(ImageView.INVISIBLE);
        
		FJ.show_save_button();		
        subSched = new SubscriptionScheduler(context);
        
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
				firstName = txtFirstname.getText().toString();
				lastName = txtLastname.getText().toString();
				FJ.lazydate = catchupDate;
				FJ.switch_fragments("date");
			}			
		});
		cmdEditDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				firstName = txtFirstname.getText().toString();
				lastName = txtLastname.getText().toString();
				FJ.lazydate = catchupDate;
				FJ.switch_fragments("date");
			}			
		});
        
        portrait.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				curr.startActivityForResult(Intent.createChooser(intent, "Select portrait"), PICK_IMAGE);				
			}
        	
        });
        
        init_interface();
        
        set_click_responses(cmdSmallest, cmdSmaller, cmdLarger, cmdLargest, cmdSave);
        
        return v;
    }
    
    private void init_interface() {
    
    	if (subindex == 0)
			smallestCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 1)
			smallerCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 2)
			largerCheck.setVisibility(ImageView.VISIBLE);
		else if (subindex == 3)
			largestCheck.setVisibility(ImageView.VISIBLE);
    	
    	if(persist) {
    		txtFirstname.setText(firstName);
    		txtLastname.setText(lastName);
    		
    		set_date(4);
    		
    		if (portraitArr != null) {
    	        Bitmap image = BitmapFactory.decodeByteArray(portraitArr, 0, portraitArr.length);
    	        portrait.setImageBitmap(image);
            }
    	} else
    		set_date(-1);
    }
    
    public void init_new_person() {
    	firstName = "";
    	lastName = "";
    	portraitArr = null;
    	this.subindex = -1;
		this.persist = false;
	}
	
	public void init_old_person() {
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
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            Uri _uri = data.getData();

            if (_uri != null) {
                //User had pick an image.
                Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                cursor.moveToFirst();

                String imageFilePath = cursor.getString(0);
                
                int sampleSize = 4;
                BitmapFactory.Options opts = new BitmapFactory.Options(); 
                opts.inSampleSize = sampleSize; 
	            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, opts);
	            int width = 100;
	            int height = (int) (bmp.getHeight()*((double)width/bmp.getWidth()));
	            bmp = Bitmap.createScaledBitmap(bmp, width, height, false);         
	    	    portrait.setImageBitmap(bmp);
                
	    	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    	    if (imageFilePath.contains("png") || imageFilePath.contains("PNG"))
	    	    	bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    	    else
	    	    	bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	    	    
	    	    portraitArr = stream.toByteArray();
                
                cursor.close();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
	private void set_click_responses(final RelativeLayout smallest, final RelativeLayout smaller, final RelativeLayout larger, final RelativeLayout largest, final Button save) {
				
		
		smallest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				if (subindex == 0) {					
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					subindex = -1;
				} else {
					subindex = 0;
					smallestCheck.setVisibility(ImageView.VISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(0);
				}
			}				
		});
		
		smaller.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (subindex == 1) {					
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					subindex = -1;
				} else {
					subindex = 1;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.VISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(1);
				}
			}				
		});
		
		larger.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (subindex == 2) {
					subindex = -1;
					largerCheck.setVisibility(ImageView.INVISIBLE);
				} else {
					subindex = 2;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.VISIBLE);
					largestCheck.setVisibility(ImageView.INVISIBLE);
					set_date(2);
				}
			}				
		});
		
		largest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				if (subindex == 3) {
					subindex = -1;
					largestCheck.setVisibility(ImageView.INVISIBLE);	
				} else {
					subindex = 3;
					smallestCheck.setVisibility(ImageView.INVISIBLE);
					smallerCheck.setVisibility(ImageView.INVISIBLE);
					largerCheck.setVisibility(ImageView.INVISIBLE);
					largestCheck.setVisibility(ImageView.VISIBLE);
					set_date(3);
				}
			}				
		});	
		
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
											
				if (txtFirstname.length() == 0 && txtLastname.length() == 0) {
					Toast.makeText(context, "please enter a name", Toast.LENGTH_LONG).show();
					return;
				} else if (txtFirstname.length() == 0) {
					Toast.makeText(context, "please enter a firstname", Toast.LENGTH_LONG).show();
					return;
				} else if (txtLastname.length() == 0) {
					Toast.makeText(context, "please enter a lastname", Toast.LENGTH_LONG).show();
					return;
				}
				
				Contact newCont = new Contact();
				
				newCont.subindex = subindex;
				newCont.firstname = txtFirstname.getText().toString();
				newCont.lastname = txtLastname.getText().toString();
				if (portraitArr != null)
					newCont.portrait = portraitArr;
				
				newCont.datasource = "custom";
				
				ContactDataAdapter cda = new ContactDataAdapter(context);
				cda.open();
				newCont.index = cda.add_new_contact(newCont);
				cda.close();
				
				if ((subindex > -1)) {		
					newCont.catchupdate = catchupDate;					
					subSched.schedule_contact(newCont, subindex, !customDate);					
				} 	
				
				FJ.hide_save_button();
				FJ.hide_back_button();
				FJ.tHost.setCurrentTab(0);
					
			}			
		});
	}
    
	// interface implementation //Login callback
	public class CustomDateAdd implements EditDateCallback {
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
