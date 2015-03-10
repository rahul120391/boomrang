package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Boomerang.R;
import customviews.CustomSeekBar;
import customviews.ProgressItem;

/**
 * Created by rahul on 3/4/2015.
 */
public class MyDashBoard extends Fragment {
    View v=null;
    CustomSeekBar customseekbar;
    TextView tv_allowed,tv_consumed,tv_remaining;
    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_dashboard,null);
            customseekbar=(CustomSeekBar)v.findViewById(R.id.customseekbar);
            progressItemList=new ArrayList<>();
            mProgressItem=new ProgressItem();
            mProgressItem.progressItemPercentage=40;
            mProgressItem.color=getResources().getColor(R.color.progress_filled_color);
            progressItemList.add(mProgressItem);
            mProgressItem.progressItemPercentage=60;
            mProgressItem.color=getResources().getColor(R.color.progress_unfilled_color);
            progressItemList.add(mProgressItem);
            customseekbar.initData(progressItemList);
            customseekbar.invalidate();
            tv_allowed=(TextView)v.findViewById(R.id.tv_allowed);
            tv_consumed=(TextView)v.findViewById(R.id.tv_consumed);
            tv_remaining=(TextView)v.findViewById(R.id.tv_remaining);
            tv_allowed.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.allowed)+"</font>"+"  "+"<font colo='#FFFFFF'>(0.5 TB)</font>"));
            tv_allowed.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.consumed)+"</font>"+"  "+"<font colo='#FFFFFF'>(0.5 TB)</font>"));
            tv_allowed.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.remaining)+"</font>"+"  "+"<font colo='#FFFFFF'>(0.5 TB)</font>"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
}
