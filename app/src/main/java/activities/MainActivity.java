package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import Boomerang.R;
import commonutils.UnCaughtException;
import fragments.Splash;


public class MainActivity extends FragmentActivity {
    android.app.FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));
        SharedPreferences prefs=getSharedPreferences("Login", 0);
        if (!prefs.getString("UserID","").equalsIgnoreCase("")) {
            finish();
            Intent i = new Intent(this, DashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
        else{
            FragmentTransactions(R.id.fragment_place,new Splash(),"splash");
        }
    }

     /******************************************************************************************************************************/
     /***
     * This method is used for replacing one fragment with another
     * @param id
     * -id of the container containing fragments
      * @param _newfrag
      * -fargment to replace
     */
     public void FragmentTransactions(int id, android.app.Fragment _newfrag,String tag) {
         fragmentManager = getFragmentManager();
         android.app.FragmentTransaction fragmentTransaction = fragmentManager
                 .beginTransaction();
         fragmentTransaction.setCustomAnimations(
                 R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                 R.animator.card_flip_left_in, R.animator.card_flip_left_out);
         fragmentTransaction.add(R.id.fragment_place,_newfrag);
         fragmentTransaction.addToBackStack(null);
         fragmentTransaction.commitAllowingStateLoss();
     }
    /**************************************************************************************************************************/

    @Override
    public void onBackPressed() {
            if (getFragmentManager().getBackStackEntryCount() >2) {
                getFragmentManager().popBackStack();
            } else {
                finish();
            }
    }
}
