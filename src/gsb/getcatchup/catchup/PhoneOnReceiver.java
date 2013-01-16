package gsb.getcatchup.catchup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class PhoneOnReceiver extends BroadcastReceiver {	
	@Override
	public void onReceive(Context context, Intent intent) {
		SubscriptionScheduler subScheduler = new SubscriptionScheduler(context);
		subScheduler.phone_restart();				
	}
}
