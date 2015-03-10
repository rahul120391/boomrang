package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Boomerang.R;
import activities.MainActivity;
/**
 * Created by rahul on 3/4/2015.
 */
public class Splash extends android.app.Fragment {

    View v=null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
         v=inflater.inflate(R.layout.fragment_splash,null);
            Thread thread=new Thread(){
                @Override
                public void run() {
                    super.run();
                    try{
                        Thread.sleep(3000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    ((MainActivity)getActivity()).FragmentTransactions(R.id.fragment_place,new Splash(),new Login());
                }
            };
            thread.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
