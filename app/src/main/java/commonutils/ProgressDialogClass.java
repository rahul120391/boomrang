package commonutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;

import Boomerang.R;
import customviews.CircularProgressDrawable;

/**
 * Created by rahul on 3/4/2015.
 */
public class ProgressDialogClass {
    static ProgressDialog dialog;

    public static void getDialog(Context cnt) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new ProgressDialog(cnt, R.style.ProgressDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setIndeterminate(true);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            dialog.setIndeterminateDrawable(new CircularProgressDrawable.Builder(
                    cnt)
                    .color(cnt.getResources().getColor(R.color.Red))
                    .angleInterpolator(new AccelerateInterpolator()).build());
            dialog.show();
        }
    }

    public static void logout() {
        if (dialog != null || dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
