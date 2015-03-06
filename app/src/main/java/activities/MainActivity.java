package activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import Boomerang.R;


public class MainActivity extends FragmentActivity {
    FragmentManager fragmentManager;
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
     * @param _frgremove
     * -fragment to remove
      * @param _frgadd
      * -fragment to add
     */
     public void FragmentTransactions(int id, Fragment _frgremove,Fragment  _frgadd){
           fragmentManager = getSupportFragmentManager();
           FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
           fragmentTransaction.setCustomAnimations(R.anim.accordion_right_in,R.anim.accordion_left_out,R.anim.accordion_left_in,R.anim.accordion_right_out);
           fragmentTransaction.remove(_frgremove);
           fragmentTransaction.add(id,_frgadd);
           fragmentTransaction.commit();
    }
}
