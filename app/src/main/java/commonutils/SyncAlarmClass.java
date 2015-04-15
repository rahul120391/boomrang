package commonutils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import reciever.CheckFragmentVisibilityBroadcast;

/**
 * Created by rahul on 4/2/2015.
 */
public class SyncAlarmClass {
    static AlarmManager manager;
    static PendingIntent pintent;

    /**
     * this method is used to set repeated alarm for specified time
     *
     * @param context -pass the context of fragment/activity to set the alarm on that specific fragment/activity
     * @param time    -time for the alarm
     */
    public static void FireAlarm(Context context, int time) {
        if (manager == null) {
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, CheckFragmentVisibilityBroadcast.class);
            pintent = PendingIntent.getBroadcast(context, 0, intent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * time,
                    1000 * 60 * time, pintent);
        }
    }

    /**
     * this method is used to stop the alarm
     */
    public static void StopAlarm() {
        if (manager != null) {
            manager.cancel(pintent);
        }
    }

}
