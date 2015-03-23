package fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import Boomerang.R;
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
public class Login<T> extends android.app.Fragment implements View.OnClickListener, DataTransferInterface<T> {
    View v=null;
    EditText et_email,et_password;
    TextView tv_rememberme,tv_forgot;
    Button btn_login;
    ImageView iv_logo;
    MethodClass<T> methodClass;
    LinearLayout login_layout;
    SharedPreferences sharedprefs,checkremstate;
    ToggleButton tb_rememb;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            sharedprefs=getActivity().getSharedPreferences("Login",0);
            checkremstate=getActivity().getSharedPreferences("RemState",0);
            methodClass=new MethodClass<T>(getActivity(),this);
            v=inflater.inflate(R.layout.fragment_login,null);
            tb_rememb=(ToggleButton)v.findViewById(R.id.tb_rememb);
            et_email=(EditText)v.findViewById(R.id.et_email);
            et_password=(EditText)v.findViewById(R.id.et_password);

            if(checkremstate!=null){
                if(checkremstate.getString("username","")!=null){
                    et_email.setText(checkremstate.getString("username",""));
                }
                if(checkremstate.getString("password","")!=null){
                    et_password.setText(checkremstate.getString("password",""));
                }
                tb_rememb.setChecked(checkremstate.getBoolean("status",false));
            }
            tv_rememberme=(TextView)v.findViewById(R.id.tv_rememberme);
            tv_forgot=(TextView)v.findViewById(R.id.tv_forgot);
            btn_login=(Button)v.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(this);
            iv_logo=(ImageView)v.findViewById(R.id.iv_logo);
            iv_logo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.top_down));
                    iv_logo.setVisibility(View.VISIBLE);
                }
            },1500);

            login_layout=(LinearLayout)v.findViewById(R.id.login_layout);
            login_layout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    login_layout.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.bottom_up));
                    login_layout.setVisibility(View.VISIBLE);
                }
            },2000);
            /****************************Set Font*******************************/
            et_email.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            et_password.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_rememberme.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_forgot.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_login.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
              try{
                     if(et_email.getText().toString().length()==0){
                         UIutill.ShowSnackBar(getActivity(),getString(R.string.empty_email));
                     }
                     else if(!et_email.getText().toString()
                             .matches(Patterns.EMAIL_ADDRESS.pattern())){
                         UIutill.ShowSnackBar(getActivity(),getString(R.string.valied_Email));
                     }
                    else if(et_password.getText().toString().length()==0){
                         UIutill.ShowSnackBar(getActivity(),getString(R.string.empty_password));
                     }
                    else{
                         if(methodClass.checkInternetConnection()){
                             Map<String,String> values=new HashMap<String,String>();
                             values.put("EmailID",et_email.getText().toString());
                             values.put("Password",et_password.getText().toString());
                             System.out.println("urls"+ URLS.LOGIN);
                             System.out.println("values"+values);
                             methodClass.MakePostRequest(values, URLS.LOGIN);
                         }
                         else{
                             UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                         }

                     }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
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
                if(jsonreturn.get("ResponseData").isJsonArray()){
                    JsonArray ResponseData=jsonreturn.get("ResponseData").getAsJsonArray();
                    JsonObject mainobject=ResponseData.get(0).getAsJsonObject();
                    SharedPreferences.Editor e=sharedprefs.edit();
                    System.out.println("user id"+mainobject.get("UserID").getAsInt()+"");
                    e.putString("UserID",mainobject.get("UserID").getAsInt()+"");
                    e.putString("FirstName",mainobject.get("FirstName").getAsString());
                    e.putString("LastName",mainobject.get("LastName").getAsString());
                    e.putString("RegistrationDate",mainobject.get("RegistrationDate").getAsString());
                    e.putString("emailID",mainobject.get("emailID").getAsString());
                    e.putInt("DirectoryId",mainobject.get("DirectoryId").getAsInt());
                    if(mainobject.get("Company")!=null){
                        e.putString("Company",mainobject.get("Company").getAsString());
                    }
                    else{
                        e.putString("Company","Not specified");
                    }
                    e.putString("Password",mainobject.get("Password").getAsString());
                    if(mainobject.get("ZipCode")!=null){
                        e.putString("ZipCode",mainobject.get("ZipCode").getAsString());
                    }
                    else{
                        e.putString("ZipCode","Not specified");
                    }
                    e.commit();

                    if(tb_rememb.isChecked()){
                        SharedPreferences.Editor edit=checkremstate.edit();
                        edit.putString("username",et_email.getText().toString());
                        edit.putString("password",et_password.getText().toString());
                        edit.putBoolean("status",true);
                        edit.commit();
                    }
                    else{
                        getActivity().getSharedPreferences("RemState",0).edit().clear().commit();
                    }
                    Intent i=new Intent(getActivity(), DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                }
                else{
                    UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("ResponseData").getAsString());
                }

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
