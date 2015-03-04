package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Boomerang.R;

/**
 * Created by rahul on 3/4/2015.
 */
public class UserProfile extends Fragment {
    View v=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_userprofile,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
