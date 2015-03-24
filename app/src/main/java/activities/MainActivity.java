package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import Boomerang.R;
import fragments.Splash;


public class MainActivity extends FragmentActivity {
    android.app.FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs=getSharedPreferences("Login", 0);
        if (!prefs.getString("UserID","").equalsIgnoreCase("")) {
            finish();
            Intent i = new Intent(this, DashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else{
            FragmentTransactions(R.id.fragment_place,new Splash());
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
     public void FragmentTransactions(int id, android.app.Fragment _newfrag) {
         fragmentManager = getFragmentManager();
         android.app.FragmentTransaction fragmentTransaction = fragmentManager
                 .beginTransaction();
         fragmentTransaction.setCustomAnimations(
                 R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                 R.animator.card_flip_left_in, R.animator.card_flip_left_out);
         fragmentTransaction.replace(id, _newfrag,null);
         fragmentTransaction.commit();
     }
}
