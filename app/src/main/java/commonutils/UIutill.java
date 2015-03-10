package commonutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import Boomerang.R;

/**
 * Created by rahul on 3/4/2015.
 */
public class UIutill {

    static Typeface font;
    static NiftyDialogBuilder dialogBuilder;



    /**
     * this method is used to set font on the views
     *
     * @param context -pass activity context as argument to apply font on the views
     */

    public static Typeface SetFont(Context context,String fontname) {
        font = Typeface.createFromAsset(context.getAssets(),fontname);
        return font;
    }

    /********************************************************************************************************************/

    /**
     * this method is used to hidekeyboard
     *
     * @param context -pass activity context as argument to hide keyboard on that activity
     */
    public static  void HideKeyboard(Context context) {
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
    public static void ShowSnackBar(Context context, String message) {
        SnackbarManager.show(Snackbar.with(context).text(message).
                color(context.getResources().getColor(R.color.login_box_bg)).
                textColor(context.getResources().getColor(R.color.email_password_txtclr)));
    }

    /***
     *
     * @param cnt -pass activity context as argument to show dialog on that activity
     * @param title -title of the Dialog
     * @param message -message of the Dialog
     */

    public static void ShowDialog(Context cnt,String title,String message){
        if(dialogBuilder==null || !dialogBuilder.isShowing()){
            dialogBuilder=NiftyDialogBuilder.getInstance(cnt);
            dialogBuilder
                    .withTitle(title)                                  //.withTitle(null)  no title
                    .withTitleColor(cnt.getResources().getColor(R.color.email_password_txtclr))                                  //def
                    .withDividerColor(cnt.getResources().getColor(R.color.transparent))                              //def
                    .withMessage(message)                     //.withMessage(null)  no Msg
                    .withMessageColor(cnt.getResources().getColor(R.color.email_password_txtclr))                              //def  | withMessageColor(int resid)
                    .withDialogColor(cnt.getResources().getColor(R.color.login_box_bg))                               //def  | withDialogColor(int resid)
                    .withIcon(cnt.getResources().getDrawable(R.drawable.ic_launcher))
                    .withDuration(700)                                          //def
                    .withEffect(Effectstype.Newspager)                                         //def Effectstype.Slidetop
                    .withButton1Text("OK")                                      //def gone
                    .withButton2Text("Cancel")                                  //def gone
                    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .show();
        }

    }
}
