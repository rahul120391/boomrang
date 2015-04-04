package fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Boomerang.R;
import commonutils.UIutill;

/**
 * Created by rahul on 3/4/2015.
 */
public class UserProfile extends Fragment {
    View v=null;
    TextView tv_userinfo,tv_email,tv_emailvalue,tv_password,tv_passwordvalue,
            tv_company,tv_companyvalue,tv_otherinfo,tv_regdate,tv_regvalue,tv_zipcode,tv_zipcodevalue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_userprofile,null);
            tv_userinfo=(TextView)v.findViewById(R.id.tv_userinfo);
            tv_email=(TextView)v.findViewById(R.id.tv_email);
            tv_emailvalue=(TextView)v.findViewById(R.id.tv_emailvalue);
            tv_password=(TextView)v.findViewById(R.id.tv_password);
            tv_passwordvalue=(TextView)v.findViewById(R.id.tv_passwordvalue);
            tv_company=(TextView)v.findViewById(R.id.tv_company);
            tv_companyvalue=(TextView)v.findViewById(R.id.tv_companyvalue);
            tv_otherinfo=(TextView)v.findViewById(R.id.tv_otherinfo);
            tv_regdate=(TextView)v.findViewById(R.id.tv_regdate);
            tv_regvalue=(TextView)v.findViewById(R.id.tv_regvalue);
            tv_zipcode=(TextView)v.findViewById(R.id.tv_zipcode);
            tv_zipcodevalue=(TextView)v.findViewById(R.id.tv_zipcodevalue);

            //Set Font
            tv_userinfo.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_emailvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_password.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_passwordvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_company.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_companyvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_passwordvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_otherinfo.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_regdate.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_regvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_zipcode.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_zipcodevalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            SharedPreferences prefs=getActivity().getSharedPreferences("Login",0);
            tv_emailvalue.setText(prefs.getString("emailID",""));
            String newpass=allStar(prefs.getString("Password",""));
            tv_passwordvalue.setText(newpass);
            tv_companyvalue.setText(prefs.getString("Company",""));
            tv_regvalue.setText(prefs.getString("RegistrationDate",""));
            tv_zipcodevalue.setText(prefs.getString("ZipCode",""));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }
    public String allStar(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }
}
