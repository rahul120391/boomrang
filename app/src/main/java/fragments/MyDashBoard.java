package fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boomerang.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import activities.DashboardActivity;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import commonutils.URLS;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/4/2015.
 */
public class MyDashBoard<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T> {
    ProgressBar customseekbar;
    TextView tv_allowed, tv_consumed, tv_remaining;
    //ProgressBar progressbar;
    LinearLayout layout_myfiles, layout_myprofile;
    TextView tv_myfiles, tv_myprofile, tv_spacestats;
    MethodClass<T> methodClass;
    View v = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        methodClass = new MethodClass<T>(getActivity(), this);
        Map<String, String> values = new HashMap<String, String>();
        values.put("UserId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //intialize views
        layout_myfiles = (LinearLayout) v.findViewById(R.id.layout_myfiles);
        layout_myprofile = (LinearLayout) v.findViewById(R.id.layout_myprofile);
        tv_myfiles = (TextView) v.findViewById(R.id.tv_myfiles);
        tv_myprofile = (TextView) v.findViewById(R.id.tv_myprofile);
        tv_spacestats = (TextView) v.findViewById(R.id.tv_spacestats);
        tv_allowed = (TextView) v.findViewById(R.id.tv_allowed);
        tv_consumed = (TextView) v.findViewById(R.id.tv_consumed);
        tv_remaining = (TextView) v.findViewById(R.id.tv_remaining);
        customseekbar = (ProgressBar) v.findViewById(R.id.customseekbar);
        //Set Font
        tv_myfiles.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
        tv_myprofile.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
        tv_spacestats.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
        tv_allowed.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
        tv_consumed.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
        tv_remaining.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

        //Set Listeners
        layout_myfiles.setOnClickListener(this);
        layout_myprofile.setOnClickListener(this);


        if (methodClass.checkInternetConnection()) {
            methodClass.MakePostRequest(values, URLS.GETSPACESTATS);
        } else {
            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
        }

        return v;
    }

    /**
     * **********************************************************************************************************************
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_myfiles:
                ((DashboardActivity) getActivity()).FragmentTransactions(R.id.fragment_container, new MyFiles(), "myfiles");
                break;
            case R.id.layout_myprofile:
                ((DashboardActivity) getActivity()).FragmentTransactions(R.id.fragment_container, new UserProfile(), "myprofile");
                break;
        }
    }

    /**
     * **********************************************************************************************************************
     */

    @Override
    public void onSuccess(T s) {
        try {
            String value = new Gson().toJson(s);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonreturn = (JsonObject) jsonParser.parse(value);
            boolean IsSucess = jsonreturn.get("IsSucess").getAsBoolean();
            if (IsSucess) {
                if (jsonreturn.get("ResponseData") != null && jsonreturn.get("ResponseData").isJsonArray()) {
                    JsonArray ResponseData = jsonreturn.get("ResponseData").getAsJsonArray();

                    JsonObject mainobject = ResponseData.get(0).getAsJsonObject();
                    String allotedspace = mainobject.get("AllotedSpace").getAsString().trim();
                    String spaceConsumed = mainobject.get("spaceConsumed").getAsString().trim();
                    String spaceLeft = mainobject.get("spaceLeft").getAsString().trim();
                    float progress = mainobject.get("Consumed%").getAsFloat();
                    float allotted = mainobject.get("Alloted%").getAsFloat();
                    float remain = allotted - progress;
                    // use \u0025 for %
                    String remaining = remain + "\u0025";
                    getActivity().getSharedPreferences("Login", 0).edit().putInt("progress", (int) progress).commit();
                    customseekbar.setProgress(getActivity().getSharedPreferences("Login", 0).getInt("progress", 0));
                    tv_allowed.setText(Html.fromHtml("<font  color='#ffae9b'>" + getResources().getString(R.string.allowed) + "</font>" + "  " + "<font color='#FFFFFF'>" + "(" + allotedspace + ")" + "</font>"));
                    tv_consumed.setText(Html.fromHtml("<font  color='#ffae9b'>" + getResources().getString(R.string.consumed) + "</font>" + "  " + "<font color='#FFFFFF'>" + "(" + spaceConsumed + ")" + "</font>"));
                    tv_remaining.setText(Html.fromHtml("<font  color='#ffae9b'>" + getResources().getString(R.string.remaining) + "</font>" + "  " + "<font color='#FFFFFF'>" + "(" + remaining + ")" + "</font>"));
                } else {
                    UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("Message").getAsString());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * **********************************************************************************************************************
     */

    @Override
    public void onFailure(RetrofitError error) {
        if (error != null) {
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }

    }

    /**************************************************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        customseekbar.setProgress(getActivity().getSharedPreferences("Login", 0).getInt("progress", 0));
    }

    @Override
    public void onDestroyView() {
        getActivity().getSharedPreferences("Login", 0).edit().putInt("progress", 0).commit();
        super.onDestroyView();
    }
}
