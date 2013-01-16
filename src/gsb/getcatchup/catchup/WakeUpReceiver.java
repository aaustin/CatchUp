package gsb.getcatchup.catchup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class WakeUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		Bundle extras = intent.getExtras();
		int index = extras.getInt("index");
		
		PrefHelper pHelper = new PrefHelper(context);
		
		if (!pHelper.get_phone_notice_status())
			return;
		
		ContactDataAdapter cda = new ContactDataAdapter(context);
		cda.open();
		Contact catchup = cda.get_contact(index);
		cda.close();

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.smallicon)
		        .setContentTitle("Due for CatchUp!")
		        .setAutoCancel(true)
		        .setContentText("Catch up with " + catchup.firstname + " " + catchup.lastname + "!");
		
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		//stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}
