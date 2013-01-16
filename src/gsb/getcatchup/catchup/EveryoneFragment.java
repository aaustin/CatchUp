package gsb.getcatchup.catchup;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;


public class EveryoneFragment extends Fragment {
	private static final long UPDATE_INTERVAL = 1500;
	ArrayList<Contact> people;
	ArrayList<Contact> displaypeople;
	ListView lv;
	EveryoneListAdapter adapter;

	private Timer disptimer;
	
	EditText txtSearch;
	
	FragmentJuggler FJ;
	UpdateEveryoneListView callback;
	
	String source;
	
	boolean scrolling;
	
	public EveryoneFragment() {
		
	}
	
	public EveryoneFragment(FragmentJuggler FJ, String source) {
		callback = new UpdateEveryoneListView();
		this.FJ = FJ;
		FJ.callbackEveryone = callback;
		this.source = source;
		disptimer = new Timer();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.everyone_view, container, false);
		lv = (ListView) v.findViewById(R.id.elvEveryone);
		txtSearch = (EditText) v.findViewById(R.id.txtSearch);
		
		scrolling = false;
		lv.setOnScrollListener(new OnScrollListener(){
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		    	scrolling = false;
		      }
		      public void onScrollStateChanged(AbsListView view, int scrollState) {
		        // TODO Auto-generated method stub
		        if(scrollState == 0) scrolling = false;
		        else scrolling = true;
		        
		      }
		    });
		
		refresh_views();
		
		txtSearch.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            	filter_listview_results();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
		
		start_timer();
			
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
             			//Log.i(DatabaseHelper.class.getName(),"timer tick");
             			if (displaypeople.size() > 0 &&  !scrolling) {
             				//Log.i(DatabaseHelper.class.getName(),"data changed!");
             				adapter.notifyDataSetChanged();             				
             			}
             			
             		}
  				});
  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}
	
	
	// filter the results for the search parameters
	private void filter_listview_results() {
		String search = txtSearch.getText().toString().toLowerCase();
		if (search.length()>0) {
			displaypeople = new ArrayList<Contact>();
			
			for(int i = 0; i < people.size(); i++)
				if (people.get(i).firstname.toLowerCase().contains(search) || people.get(i).lastname.toLowerCase().contains(search))
					displaypeople.add(people.get(i));	
		} else
			displaypeople = people;
		
		display_current_array();	
	}
		 
	private void display_current_array() {
		adapter = new EveryoneListAdapter(getActivity(), displaypeople, FJ, this);		
        lv.setAdapter(adapter);
	}
	
	private void refresh_views() {
		ContactDataAdapter cda = new ContactDataAdapter(getActivity());
		cda.open();
		people = cda.get_all_contacts(source);
		cda.close();
		
		for(int i = 0; i < people.size(); i++) 
			if (people.get(i).subindex > -1) {
				people.remove(i);
				i--;
			}
		
		displaypeople = people;
		
		display_current_array();        
	}
		
	// interface implementation login callback
	public class UpdateEveryoneListView implements FragmentCallBack {
		public void callback(int code, int index) {			
			FJ.show_back_button();
			
			if (code >= 0) {
				for(int i = 0; i < people.size(); i++)
					if (people.get(i).index == index) {
						people.remove(i);
		    			break;
		    		}
		    	  
		    	adapter.notifyDataSetChanged();
			}
		} 		
	}	
	
}
