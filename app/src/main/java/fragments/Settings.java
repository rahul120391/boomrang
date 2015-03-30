package fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import Boomerang.R;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import commonutils.URLS;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/27/2015.
 */
public class Settings<T>  extends Fragment implements View.OnClickListener, DataTransferInterface<T>{


    View v=null;
    CheckBox ch_state;
    Spinner sp_timeinterval;
    Button btn_save;
    TextView tv_interval,tv_settings,tv_autosync;
    RelativeLayout layout_Spinner;
    MethodClass<T> methodClass;
    String deviceId;
    String timeinterval;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            methodClass=new MethodClass<>(getActivity(),this);
            v=inflater.inflate(R.layout.fragment_settings,null);
            tv_interval=(TextView)v.findViewById(R.id.tv_interval);
            tv_settings=(TextView)v.findViewById(R.id.tv_settings);
            tv_autosync=(TextView)v.findViewById(R.id.tv_autosync);
            layout_Spinner=(RelativeLayout)v.findViewById(R.id.layout_Spinner);
            deviceId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            tv_interval.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_autosync.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_settings.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            ch_state=(CheckBox)v.findViewById(R.id.ch_state);
            ch_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        layout_Spinner.setVisibility(View.VISIBLE);
                        tv_interval.setVisibility(View.VISIBLE);
                    }
                    else{
                        layout_Spinner.setVisibility(View.GONE);
                        tv_interval.setVisibility(View.GONE);
                    }
                }
            });
            ch_state.setChecked(getActivity().getSharedPreferences("Login",0).getBoolean("IsAutoSync",false));
            sp_timeinterval=(Spinner)v.findViewById(R.id.sp_timeinterval);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.time_interval)){
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
                    v.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    v.setTextColor(getResources().getColor(R.color.search_box_txtclr));
                    return v;
                }

                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
                    v.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    v.setTextColor(getResources().getColor(R.color.search_box_txtclr));
                    return v;
                }
            };
            sp_timeinterval.setAdapter(adapter);
            if(ch_state.isChecked()){
                int selectpos=0;
                layout_Spinner.setVisibility(View.VISIBLE);
                tv_interval.setVisibility(View.VISIBLE);
                int synctime=getActivity().getSharedPreferences("Login",0).getInt("SyncInterval",0);
                String timeintervals[]=getResources().getStringArray(R.array.time_interval);
                for(int i=0;i<timeintervals.length;i++){
                   String values=timeintervals[i].substring(0,2);
                   int time=Integer.parseInt(values);
                   if(time==synctime){
                       selectpos=i;
                   }
                }
                sp_timeinterval.setSelection(selectpos);
            }
            else{
                tv_interval.setVisibility(View.GONE);
                layout_Spinner.setVisibility(View.GONE);
            }
            btn_save=(Button)v.findViewById(R.id.btn_save);
            btn_save.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_save.setOnClickListener(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
              switch (v.getId()){
                  case R.id.btn_save:
                      if(methodClass.checkInternetConnection()){
                          Map<String,String> values=new HashMap<>();
                          values.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                          values.put("deviceId",deviceId);
                          values.put("IsMobile","1");
                          if(ch_state.isChecked()){
                              values.put("IsAutoSync","1");
                              String value= (String) sp_timeinterval.getSelectedItem();
                              timeinterval=value.substring(0,2);
                              System.out.println("timeinterval"+timeinterval);
                              values.put("SyncInterval",timeinterval);
                          }
                          else{
                              values.put("IsAutoSync","0");
                              values.put("SyncInterval","0");
                          }
                          System.out.println("values"+values);
                          methodClass.MakeGetRequestWithParams(values, URLS.SETTINGS);
                      }
                      else{
                          UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                      }
                      break;
                  default:
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
                 String ResponseData=jsonreturn.get("ResponseData").getAsString();
                 UIutill.ShowSnackBar(getActivity(),ResponseData.trim());
                 SharedPreferences sharedprefs=getActivity().getSharedPreferences("Login",0);
                 SharedPreferences.Editor edit=sharedprefs.edit();
                 if(ch_state.isChecked()){
                    edit.putBoolean("IsAutoSync",true);
                    edit.putInt("SyncInterval",Integer.parseInt(timeinterval));
                 }
                 else{
                    edit.putBoolean("IsAutoSync",false);
                    edit.putInt("SyncInterval",Integer.parseInt("0"));
                 }
                edit.commit();
            }
            else{
                UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("Message").getAsString());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(RetrofitError error) {
        if(error!=null){
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }
}
