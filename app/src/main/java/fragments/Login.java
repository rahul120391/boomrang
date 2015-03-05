package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Boomerang.R;

/**
 * Created by rahul on 3/4/2015.
 */
public class Login extends Fragment {
    View v=null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_login,null);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
