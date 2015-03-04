package commonutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethodManager;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import Boomerang.R;

/**
 * Created by rahul on 3/4/2015.
 */
public class UIutill {

    static Typeface font;


    /**
     * this method is used to set font on the views
     *
     * @param context -pass activity context as argument to apply font on the views
     */

    public static Typeface SetFont(Context context) {
        font = Typeface.createFromAsset(context.getAssets(), "segoeui.ttf");
        return font;
    }

    /********************************************************************************************************************/

    /**
     * this method is used to hidekeyboard
     *
     * @param context -pass activity context as argument to hide keyboard on that activity
     */
    public void HideKeyboard(Context context) {
        @SuppressWarnings("static-access")
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(
                        context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (((Activity) context).getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                        .getCurrentFocus().getWindowToken(), 0);
            }

        }
    }
    /***********************************************************************************************************************/

    /**
     * this method is used snackbar given by materialDesign
     *
     * @param context -pass activity context as argument to show snackbar on that activity
     * @param message -message to show inside snackbar
     */
    public void ShowSnackBar(Context context, String message) {
        SnackbarManager.show(Snackbar.with(context).text(message).color(context.getResources().getColor(R.color.Red)).textColor(context.getResources().getColor(R.color.White)));
    }
}
