package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Boomerang.R;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import commonutils.URLS;
import retrofit.RetrofitError;

/**
 * Created by rahul on 5/12/2015.
 */
public class Forgotpassword extends Fragment implements View.OnClickListener, DataTransferInterface {
    View v=null;
    TextView tv_heading;
    EditText et_email;
    Button btn_forgot;
    MethodClass methodClass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_forgotpassword,null);
            methodClass=new MethodClass(getActivity(),this);
            //intialize views
            tv_heading=(TextView)v.findViewById(R.id.tv_heading);
            et_email=(EditText)v.findViewById(R.id.et_email);
            btn_forgot=(Button)v.findViewById(R.id.btn_forgot);

            //settTypeface
            tv_heading.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_forgot.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            //setListener
            btn_forgot.setOnClickListener(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_forgot:
                try{
                    if(et_email.getText().toString().trim().length()==0){
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.empty_email));
                    }
                    else if (!et_email.getText().toString().trim()
                            .matches(Patterns.EMAIL_ADDRESS.pattern())) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.valied_Email));
                    }
                    else{
                        if (methodClass.checkInternetConnection()) {
                            methodClass.MakeGetRequest(URLS.FORGOTPASS,et_email.getText().toString().trim());
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
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
    public void onSuccess(Object s) {
         try{
             String value = new Gson().toJson(s);
             System.out.println("value" + value);
             JsonParser jsonParser = new JsonParser();
             JsonObject jsonreturn = (JsonObject) jsonParser.parse(value);
             boolean IsSucess = jsonreturn.get("IsSucess").getAsBoolean();
             if(IsSucess){
                  String ResponseData=jsonreturn.get("ResponseData").getAsString();
                  if(!ResponseData.equalsIgnoreCase("")){
                      UIutill.ShowSnackBar(getActivity(),ResponseData);
                      getActivity().onBackPressed();
                  }
                 else{
                     String Message=jsonreturn.get("Message").getAsString();UIutill.ShowSnackBar(getActivity(),Message);
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
        if (error != null) {
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }
}
