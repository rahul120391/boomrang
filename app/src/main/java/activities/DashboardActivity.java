package activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import Boomerang.R;

public class DashboardActivity extends ActionBarActivity {
    FragmentManager fragmentManager;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }
    /**************************************************************************************************************************/
    /***
     * This method is used for replacing one fragment with another
     * @param id
     * -id of the container containing fragments
     * @param fragment
     * -fragment to replace
     * @param tag
     * -tag associated with the fragment
     */
    public void FragmentTransactions(int id, android.app.Fragment fragment,String tag)
    {
        Fragment _fragment = getFragmentManager().findFragmentByTag(tag);
        fragmentManager = getFragmentManager();
        if (null == _fragment) {
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        } else {
            fragmentManager.popBackStack(tag, 0);
        }
    }
}
