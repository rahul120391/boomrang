package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Boomerang.R;
import activities.DashboardActivity;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import commonutils.URLS;
import customviews.CustomSeekBar;
import customviews.ProgressItem;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/4/2015.
 */
public class MyDashBoard<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>{
    View v=null;
    CustomSeekBar customseekbar;
    TextView tv_allowed,tv_consumed,tv_remaining;
    //ProgressBar progressbar;
    LinearLayout layout_myfiles,layout_myprofile;
    TextView tv_myfiles,tv_myprofile,tv_spacestats;
    MethodClass<T> methodClass;
    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;
    private float totalSpan = 1500;
    private float redSpan = 200;
    private float blueSpan = 300;
    private float greenSpan = 400;
    private float yellowSpan = 150;
    private float darkGreySpan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            methodClass=new MethodClass<T>(getActivity(),this);
            Map<String,String> values=new HashMap<String,String>();
            values.put("UserId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
            methodClass.MakePostRequest(values, URLS.GETSPACESTATS);
            v=inflater.inflate(R.layout.fragment_dashboard,null);
            layout_myfiles=(LinearLayout)v.findViewById(R.id.layout_myfiles);
            layout_myfiles.setOnClickListener(this);
            layout_myprofile=(LinearLayout)v.findViewById(R.id.layout_myprofile);
            layout_myprofile.setOnClickListener(this);
            customseekbar=(CustomSeekBar)v.findViewById(R.id.customseekbar);
            initDataToSeekbar();
            tv_myfiles=(TextView)v.findViewById(R.id.tv_myfiles);
            tv_myprofile=(TextView)v.findViewById(R.id.tv_myprofile);
            tv_spacestats=(TextView)v.findViewById(R.id.tv_spacestats);
            tv_allowed=(TextView)v.findViewById(R.id.tv_allowed);
            tv_consumed=(TextView)v.findViewById(R.id.tv_consumed);
            tv_remaining=(TextView)v.findViewById(R.id.tv_remaining);
            tv_allowed.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.allowed)+"</font>"+"  "+"<font color='#FFFFFF'>(0.5 TB)</font>"));
            tv_consumed.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.consumed)+"</font>"+"  "+"<font color='#FFFFFF'>(0.5 TB)</font>"));
            tv_remaining.setText(Html.fromHtml("<font  color='#ffae9b'>"+getResources().getString(R.string.remaining)+"</font>"+"  "+"<font color='#FFFFFF'>(0.5 TB)</font>"));

            //Set Font
            tv_myfiles.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_myprofile.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_spacestats.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_allowed.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_consumed.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_remaining.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
    private void initDataToSeekbar() {
        progressItemList = new ArrayList<ProgressItem>();
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = ((redSpan / totalSpan) * 100);
        Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
        mProgressItem.color = R.color.all_indi_color;
        progressItemList.add(mProgressItem);
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (blueSpan / totalSpan) * 100;
        mProgressItem.color = R.color.consume_indi_color;
        progressItemList.add(mProgressItem);
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (greenSpan / totalSpan) * 100;
        mProgressItem.color = R.color.remaining_indi_color;
        progressItemList.add(mProgressItem);
        customseekbar.initData(progressItemList);
        customseekbar.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_myfiles:
                ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,new MyFiles(),"myfiles");
                break;
            case R.id.layout_myprofile:
                ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,new UserProfile(),"myprofile");
                break;
        }
    }

    @Override
    public void onSuccess(T s) {
            try{
                String value=new Gson().toJson(s);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
                boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
                if(IsSucess){
                    JsonArray ResponseData=jsonreturn.get("ResponseData").getAsJsonArray();
                    JsonObject mainobject=ResponseData.get(0).getAsJsonObject();
                    String allotedspace=mainobject.get("AllotedSpace").getAsString();
                    String spaceConsumed=mainobject.get("spaceConsumed").getAsString();

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public void onFailure(RetrofitError error) {
        UIutill.ShowDialog(getActivity(),getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
    }


}
