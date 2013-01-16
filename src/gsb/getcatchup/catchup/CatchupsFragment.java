package gsb.getcatchup.catchup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CatchupsFragment extends Fragment {
	private static final long UPDATE_INTERVAL = 1500;
	ArrayList<Contact> overduePeeps;
	ArrayList<Contact> upcomingPeeps;
	
	private Timer disptimer;
	
	ListView dueLV;
	ListView upcomingLV;
	ImageView addCatchups;
	ImageView dueBar;
	ImageView upcomingBar;
	
	UpcomingCatchupAdapter uca;
	DueCatchupAdapter dca;
	
	UpdateListView callback;
	FragmentJuggler FJ;
	
	boolean scrolling;
	
	public CatchupsFragment() {
		
	}
	
	public CatchupsFragment(FragmentJuggler FJ) {
		callback = new UpdateListView();
		this.FJ = FJ;
		FJ.callback = callback;
		disptimer = new Timer();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
		View v = inflater.inflate(R.layout.catchup_view, container, false);		
		 //Log.i(DatabaseHelper.class.getName(),"CATCHUPS MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());
		dueBar = (ImageView) v.findViewById(R.id.imgDue);
		upcomingBar = (ImageView) v.findViewById(R.id.imgUpcoming);
		addCatchups = (ImageView) v.findViewById(R.id.imgAdd);
		dueLV = (ListView) v.findViewById(R.id.elvDueCatchups);
		upcomingLV = (ListView) v.findViewById(R.id.elvUpcomingCatchups);
		
		refresh_views();
		
		if (upcomingPeeps.size() == 0 && overduePeeps.size() == 0)
			addCatchups.setVisibility(ImageView.VISIBLE);
		else
			addCatchups.setVisibility(ImageView.GONE);
		
		addCatchups.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FJ.tHost.setCurrentTab(1);
			}			
		});
		
		scrolling = false;
		dueLV.setOnScrollListener(new OnScrollListener(){
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		    	scrolling = false;
		      }
		      public void onScrollStateChanged(AbsListView view, int scrollState) {
		        // TODO Auto-generated method stub
		        if(scrollState == 0) scrolling = false;
		        else scrolling = true;
		        
		      }
		 });
		upcomingLV.setOnScrollListener(new OnScrollListener(){
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		    	scrolling = false;
		      }
		      public void onScrollStateChanged(AbsListView view, int scrollState) {
		        // TODO Auto-generated method stub
		        if(scrollState == 0) scrolling = false;
		        else scrolling = true;
		        
		      }
		 });
		
		start_timer();
		 //Log.i(DatabaseHelper.class.getName(),"CATCHUPS MEMORY STATE: " + Debug.getNativeHeapAllocatedSize());	
        return v;        
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		//Log.i(DatabaseHelper.class.getName(),"stopped timer");
		if (disptimer != null)
			disptimer.cancel();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Log.i(DatabaseHelper.class.getName(),"stopped timer");
		if (disptimer != null)
			disptimer.cancel();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.i(DatabaseHelper.class.getName(),"stopped timer");
		if (disptimer != null)
			disptimer.cancel();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		start_timer();
	}	
	
	private void start_timer() {
		if (disptimer != null)
			disptimer.cancel();
		disptimer = new Timer();
		//Log.i(DatabaseHelper.class.getName(),"started timer");
		disptimer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {				
  				getActivity().runOnUiThread(new Runnable() {
             		public void run() { 		 			
             			//Log.i(DatabaseHelper.class.getName(),"catchups timer tick");
             			if (overduePeeps.size() > 0 && !scrolling) {
             				dca.notifyDataSetChanged();
             			}
             			if (upcomingPeeps.size() > 0 && !scrolling) {
             				uca.notifyDataSetChanged();
             			}
             		}
  				});
  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}
	
	private void refresh_views() {
		ContactDataAdapter cda = new ContactDataAdapter(getActivity());
		cda.open();
		ArrayList<Contact> subscriptions = cda.get_all_subscriptions();
		cda.close();
		overduePeeps = new ArrayList<Contact>();
		upcomingPeeps = new ArrayList<Contact>();
				
		
		Calendar now = Calendar.getInstance();
		
		for(int i = 0; i < subscriptions.size(); i++) {
			if (subscriptions.get(i).catchupdate.after(now))
				upcomingPeeps.add(subscriptions.get(i));
			else
				overduePeeps.add(subscriptions.get(i));
		}
		
		Collections.sort(overduePeeps, new ContactComparator());
		Collections.sort(upcomingPeeps, new ContactComparator());
		
		if (upcomingPeeps.size() == 0) {			
			upcomingBar.setVisibility(TextView.GONE);
		} else
			upcomingBar.setVisibility(TextView.VISIBLE);
		
		if (overduePeeps.size() == 0)
			dueBar.setVisibility(TextView.GONE);
		else
			dueBar.setVisibility(TextView.VISIBLE);
		
		uca = new UpcomingCatchupAdapter(getActivity(), upcomingPeeps, FJ, this);
		upcomingLV.setAdapter(uca);
		
		dca = new DueCatchupAdapter(getActivity(), overduePeeps, FJ, this);
		dueLV.setAdapter(dca);	
	}
	
		
	// interface implementation login callback
	public class UpdateListView implements FragmentCallBack {
		public void callback(int code, int index) {			
			
			if (code < 0) {
		    	for(int i = 0; i < overduePeeps.size(); i++)
		    		if (overduePeeps.get(i).index == index) {
		    			overduePeeps.remove(i);	
			    			break;
			    	}
			    	for(int i = 0; i < upcomingPeeps.size(); i++)
			    		if (upcomingPeeps.get(i).index == index) {
			    			upcomingPeeps.remove(i); 
			    			break;
			    		}		    	  
		    } else if (code >= 0) {
		    	refresh_views();
		    }
		    	  
		    uca.notifyDataSetChanged();
		    dca.notifyDataSetChanged();	
		}
 		
	}
	
    @Override
    public void onStart() {
        super.onStart();
 
    }
}
