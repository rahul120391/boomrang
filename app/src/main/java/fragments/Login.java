package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Boomerang.R;
import commonutils.UIutill;

/**
 * Created by rahul on 3/4/2015.
 */
public class Login extends Fragment {
    View v=null;
    EditText et_email,et_password;
    TextView tv_rememberme,tv_forgot;
    Button btn_login;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_login,null);
            et_email=(EditText)v.findViewById(R.id.et_email);
            et_password=(EditText)v.findViewById(R.id.et_password);
            tv_rememberme=(TextView)v.findViewById(R.id.tv_rememberme);
            tv_forgot=(TextView)v.findViewById(R.id.tv_forgot);
            btn_login=(Button)v.findViewById(R.id.btn_login);

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
}
