package recievers;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.boomerang.R;

import java.io.File;

/**
 * Created by rahul on 3/31/2015.
 */
public class DownloadCompleteReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(action)) {

            long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = manager.query(query);

            if (c.moveToFirst()) {
                int status = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        break;
                }
                if (DownloadManager.STATUS_SUCCESSFUL == c
                        .getInt(status)) {
                    int title = c
                            .getColumnIndex(DownloadManager.COLUMN_TITLE);
                    int mediatypecoulmn = c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE);
                    String mediatype = c.getString(mediatypecoulmn);
                    Toast.makeText(context, context.getString(R.string.download_cmp), Toast.LENGTH_SHORT).show();

                    String name = c.getString(title);

                    int ur = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    String uri = c.getString(ur);
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    Intent filelocation = new Intent();
                    filelocation.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(uri);
                    filelocation.setData(Uri.fromFile(file));
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, filelocation, 0);
                    Notification mNotification = new Notification.Builder(context)
                            .setContentTitle(name)
                            .setContentText("Downloaded")
                            .setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.appicon)
                            .setSound(soundUri)
                            .build();

                    mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0, mNotification);
                } else if (DownloadManager.STATUS_FAILED == c.getInt(status)) {
                    Toast.makeText(context, context.getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    /**************************************************************************************************************************/
}
