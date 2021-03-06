package commonutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;

import com.boomerang.R;

import customviews.CircularProgressDrawable;

/**
 * Created by rahul on 3/4/2015.
 */
public class ProgressDialogClass {
    static ProgressDialog dialog;

    /**
     * -this method is used to show custom progress dialog
     *
     * @param cnt -pass the context of the activity/fragment to show the progress dialog
     */
    public static void getDialog(Context cnt) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new ProgressDialog(cnt, R.style.ProgressDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            dialog.setIndeterminateDrawable(new CircularProgressDrawable.Builder(
                    cnt)
                    .color(cnt.getResources().getColor(R.color.email_password_txtclr))
                    .angleInterpolator(new AccelerateInterpolator()).build());
            dialog.show();
        }
    }

    /**
     * -this method is used to dismiss the runing progress dialog
     */
    public static void logout() {
        if (dialog != null || dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
