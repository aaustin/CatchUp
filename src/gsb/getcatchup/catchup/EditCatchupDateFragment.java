package gsb.getcatchup.catchup;

import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class EditCatchupDateFragment extends Fragment {
	
	Button cmdSave;
	FragmentJuggler FJ;
	
	DatePicker dp;
	
	Calendar c;
	
	boolean falseDateSet;
	
	public EditCatchupDateFragment() {
		
	}
	
	public EditCatchupDateFragment(FragmentJuggler FJ, Button cmdSave) {
		this.FJ = FJ;
		this.cmdSave = cmdSave;
		falseDateSet = false;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_catchup_date_view, container, false);
		
		this.c = FJ.lazydate;
		
		
		FJ.cmdBack.setOnClickListener(FJ.persistBack);

		//Log.i(DatabaseHelper.class.getName(),"on create of date edit called");
		//Log.i(DatabaseHelper.class.getName(),"on create lazy date: Year = " + FJ.lazydate.get(Calendar.YEAR));
		//Log.i(DatabaseHelper.class.getName(),"on create c date: Year = " + c.get(Calendar.YEAR));
		
		
		
		dp = (DatePicker) v.findViewById(R.id.dtCatchupPicker);
		dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				//Log.i(DatabaseHelper.class.getName(),"called onDateChange");
				if (falseDateSet) {
					//Log.i(DatabaseHelper.class.getName(),"date changed: Year = " + year);
					falseDateSet = false;
					dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));					
				}					
			}			
		});
		
		//Log.i(DatabaseHelper.class.getName(),"*************");
		//Log.i(DatabaseHelper.class.getName(),"on create lazy date: Year = " + FJ.lazydate.get(Calendar.YEAR));
		//Log.i(DatabaseHelper.class.getName(),"on create c date: Year = " + c.get(Calendar.YEAR));

		
		cmdSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				c.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 10, 0, 0);
				
				if (FJ.depthTracker.get(FJ.depthTracker.size()-2).equals("expand"))
					FJ.callbackEditDateExpand.callback((Calendar)c.clone());		
				else
					FJ.callBackEditDateAdd.callback((Calendar)c.clone());
				FJ.go_back_persist();
				FJ.cmdBack.setOnClickListener(FJ.regBack);
			}			
		});		
		
		dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		
		return v;
	}
	
	public void set_false_date() {
		falseDateSet = true;
	}
		
}
