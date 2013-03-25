package gsb.getcatchup.catchup;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SubscriptionScheduler {
	private static final int REQUEST_CODE = 19841201;
	
	// intervals for reminders (in days)
	private static int INTERVAL_ONE = 14;
	private static int INTERVAL_ONE_SPACING = 1;
	private static int INTERVAL_TWO = 30;
	private static int INTERVAL_TWO_SPACING = 2;
	private static int INTERVAL_THREE = 60;
	private static int INTERVAL_THREE_SPACING = 4;
	private static int INTERVAL_FOUR = 180;
	private static int INTERVAL_FOUR_SPACING = 7;
	
	private static int MAX_SCHEDULED_DAYS = 500;
	
	private class DayMap {
		int daysFromNow;
		Contact scheduledGuy;
		public DayMap(int daysFromNow, Contact scheduled) {
			this.daysFromNow = daysFromNow;
			this.scheduledGuy = scheduled;
		}		
		
	}
	
	ContactDataAdapter cda;
	
	Context context;
	
	private ArrayList<DayMap> dayArray;
	private Calendar now;
	int scheduledDay;
	
	public SubscriptionScheduler(Context context) {
		this.context = context;
		
		cda = new ContactDataAdapter(context);
		now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 10);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		build_day_database();
	}
	
	private void build_day_database() {
		dayArray = new ArrayList<DayMap>();
		
		cda.open();		
		ArrayList<Contact> subs = cda.get_all_subscriptions();		
		cda.close();
		
		for(int i = 0; i < MAX_SCHEDULED_DAYS; i++)
			dayArray.add(new DayMap(i, null));
		
		for (int i = 0; i < subs.size(); i++) {
			int dayIndex = days_from_now(subs.get(i).catchupdate);
			if (dayIndex > dayArray.size()) {
				for (int j = dayArray.size(); j <= dayIndex; j++)
					dayArray.add(new DayMap(i, null));
			}
			if (dayIndex >= 0) { 
				dayArray.get(dayIndex).scheduledGuy = subs.get(i);
				dayArray.get(dayIndex).daysFromNow = dayIndex;
			}
		}
	}
	
	private int days_from_now(Calendar date) {			
		int days = 0;
		 		
		if(date.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			days = date.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
		} else {
			days = now.getActualMaximum(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
			int deltaYear = now.get(Calendar.YEAR) - date.get(Calendar.YEAR);
			for (int i = 0; i < deltaYear; i++) {
				now.add(Calendar.YEAR, 1);
				days = days + now.getActualMaximum(Calendar.DAY_OF_YEAR);				
			}
			days = days + date.get(Calendar.DAY_OF_YEAR);
		}
		
		return days;		
	}

	public void schedule_contact(Contact contact, int schedIndex, boolean needDate) {
				
		int scheduleInterval;
		if (schedIndex == 0) {
			scheduledDay = INTERVAL_ONE;
			scheduleInterval = INTERVAL_ONE_SPACING;
		} else if (schedIndex == 1) {
			scheduledDay = INTERVAL_TWO;
			scheduleInterval = INTERVAL_TWO_SPACING;
		} else if (schedIndex == 2) {
			scheduledDay = INTERVAL_THREE;
			scheduleInterval = INTERVAL_THREE_SPACING;
		} else {
			scheduledDay = INTERVAL_FOUR;
			scheduleInterval = INTERVAL_FOUR_SPACING;
		}
		
		if (needDate) {
			contact.catchupdate = get_future_date(schedIndex);
		}
		contact.subindex = schedIndex;
		contact.catchupinterval = scheduleInterval;
		
		cda.open();
		cda.subscribe(contact);
		cda.close();
		schedule_catchup(contact);
		dayArray.get(scheduledDay).scheduledGuy = contact;
				
	}
	
	public Calendar get_future_date(int schedIndex) {		
		int scheduleInterval;
		if (schedIndex == 0) {
			scheduledDay = INTERVAL_ONE;
			scheduleInterval = INTERVAL_ONE_SPACING;
		} else if (schedIndex == 1) {
			scheduledDay = INTERVAL_TWO;
			scheduleInterval = INTERVAL_TWO_SPACING;
		} else if (schedIndex == 2) {
			scheduledDay = INTERVAL_THREE;
			scheduleInterval = INTERVAL_THREE_SPACING;
		} else {
			scheduledDay = INTERVAL_FOUR;
			scheduleInterval = INTERVAL_FOUR_SPACING;
		}
		
		Calendar scheduledDate = (Calendar) now.clone();			
		
		scheduledDay = find_date_around(scheduledDay, scheduleInterval);
		
		scheduledDate.add(Calendar.DAY_OF_YEAR, scheduledDay);
		
		return scheduledDate;
	}
	
	private int find_date_around(int scheduledDay, int scheduleInterval) {
		int scheduleHim = scheduledDay;
				
		boolean readyToSchedule = true;
		for (int i = scheduleHim-scheduleInterval; i < (scheduleHim+scheduleInterval); i++) {
			if (dayArray.get(i).scheduledGuy != null)
				if (dayArray.get(i).scheduledGuy.catchupinterval == scheduleInterval) {
					scheduleHim = i;
					readyToSchedule = false;
					break;
				}					
		}
		
		while (!readyToSchedule) {
			scheduleHim = scheduleHim + scheduleInterval;
			if (dayArray.get(scheduleHim).scheduledGuy == null) 
				break;
		}
		
		return scheduleHim;
	}
	
	public void unschedule_contact(Contact contact) {
		int dayIndex = days_from_now(contact.catchupdate);		
		if (dayIndex >= 0) 
			dayArray.get(dayIndex).scheduledGuy = null;
		
		contact.catchupdate = null;
		contact.subindex = -1;
		
		cda.open();
		cda.unsubscribe(contact);
		cda.close();		

	}
	
	
	
	public void schedule_catchup(Contact contact) {			
		Intent i = new Intent(context, WakeUpReceiver.class);
		i.putExtra("index", contact.index);
		PendingIntent pi = PendingIntent.getBroadcast(context, REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);		
		
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, contact.catchupdate.getTimeInMillis(), pi);
	}
	
	public void phone_restart() {
		cda.open();		
		ArrayList<Contact> subs = cda.get_all_subscriptions();		
		cda.close();
		
		for(int i = 0; i < subs.size(); i++)
			schedule_catchup(subs.get(i));
	}
}
