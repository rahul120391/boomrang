package commonutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import Boomerang.R;

/**
 * Created by rahul on 3/4/2015.
 */
public class UIutill {

    static Typeface font;
    static Dialog dialog;
    static AlertDialog mydialog;
    static int notificationid = 0;

    /**
     * this method is used to set font on the views
     *
     * @param context -pass activity context as argument to apply font on the views
     */

    public static Typeface SetFont(Context context, String fontname) {
        font = Typeface.createFromAsset(context.getAssets(), fontname);
        return font;
    }

    /********************************************************************************************************************/

    /**
     * this method is used to hidekeyboard
     *
     * @param activity -pass activity context as argument to hide keyboard on that activity
     */
    public static void HideKeyboard(Activity activity) {
        @SuppressWarnings("static-access")
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null)
            System.out.println("inside input manager");
        inputManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getApplicationWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    /***********************************************************************************************************************/

    /**
     * this method is used snackbar given by materialDesign
     *
     * @param context -pass activity context as argument to show snackbar on that activity
     * @param message -message to show inside snackbar
     */
    public static void ShowSnackBar(Context context, String message) {
        SnackbarManager.show(Snackbar.with(context).text(message).textTypeface(SetFont(context,"segoeuilght.ttf")).
                color(context.getResources().getColor(R.color.login_box_bg)).
                textColor(context.getResources().getColor(R.color.email_password_txtclr)));
    }

    /***********************************************************************************************************************/
    /**
     * this method is used to show dialog
     *
     * @param cnt     -pass activity context as argument to show dialog on that activity
     * @param title   -title of the dialog
     * @param message -error message
     */
    public static void ShowDialog(Context cnt, String title, String message) {
        if (dialog == null || !dialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(cnt);
            final View dialoglayout = inflater.inflate(R.layout.messagedialog_customview, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(cnt);
            TextView tv_title = (TextView) dialoglayout.findViewById(R.id.tv_title);
            tv_title.setText(title);
            tv_title.setTypeface(UIutill.SetFont(cnt, "segoeuilght.ttf"));
            TextView tv_message = (TextView) dialoglayout.findViewById(R.id.tv_message);
            tv_message.setTypeface(UIutill.SetFont(cnt, "segoeuilght.ttf"));
            tv_message.setText(message);
            Button btn_ok = (Button) dialoglayout.findViewById(R.id.btn_ok);
            btn_ok.setTypeface(UIutill.SetFont(cnt, "segoeuilght.ttf"));
            builder.setView(dialoglayout);
            dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
            dialog.show();
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
    /***********************************************************************************************************************/
    /**
     * this method is used to convert density pixel to pixel
     *
     * @param dp  -pass dp to convert to px
     * @param cnt -context of the fragment/activity
     * @return -returns the pixel in integer form
     */
    public static int dp2px(int dp, Context cnt) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                cnt.getResources().getDisplayMetrics());
    }

    /***********************************************************************************************************************/

    /**
     * this methos is used to generate notifications
     *
     * @param context  -pass the context of fragment/activity
     * @param messages -message to be shown inside notification
     */
    public static void generateNotification(Context context, String messages) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification mNotification = null;
        notificationid = notificationid + 1;
        mNotification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.boomerang_not))
                .setContentText(messages)
                .setSmallIcon(R.drawable.appicon)
                .setSound(soundUri)
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationid, mNotification);
    }
    /***********************************************************************************************************************/
}
