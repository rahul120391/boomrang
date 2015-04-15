package reciever;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import activities.DashboardActivity;
import de.greenrobot.event.EventBus;
import fragments.MyFiles;

/**
 * Created by rahul on 4/2/2015.
 */
public class CheckFragmentVisibilityBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            System.out.println("broadcast is fired");
            if (DashboardActivity.fragmentManager != null) {
                Fragment fragment = DashboardActivity.fragmentManager.findFragmentByTag("myfiles");
                if (fragment instanceof MyFiles) {
                    EventBus.getDefault().post("fromcheck");
                } else {
                    System.out.println("inside else");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
