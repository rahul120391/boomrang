package activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import Boomerang.R;


public class MainActivity extends FragmentActivity {
    android.app.FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
         fragmentTransaction.replace(id, _newfrag,null);
         fragmentTransaction.commit();
     }
}
