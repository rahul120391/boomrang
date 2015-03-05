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
           fragmentTransaction.setCustomAnimations(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
           fragmentTransaction.remove(_frgremove);
           fragmentTransaction.add(id,_frgadd);
           fragmentTransaction.commit();
    }
}
