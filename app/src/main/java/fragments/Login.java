package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import Boomerang.R;
import activities.DashboardActivity;
import commonutils.UIutill;

/**
 * Created by rahul on 3/4/2015.
 */
public class Login extends android.app.Fragment {
    View v=null;
    EditText et_email,et_password;
    TextView tv_rememberme,tv_forgot;
    Button btn_login;
    ImageView iv_logo;
    LinearLayout login_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_login,null);
            et_email=(EditText)v.findViewById(R.id.et_email);
            et_password=(EditText)v.findViewById(R.id.et_password);
            tv_rememberme=(TextView)v.findViewById(R.id.tv_rememberme);
            tv_forgot=(TextView)v.findViewById(R.id.tv_forgot);
            btn_login=(Button)v.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getActivity(), DashboardActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            iv_logo=(ImageView)v.findViewById(R.id.iv_logo);
            iv_logo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(Techniques.Pulse).duration(2000).playOn(iv_logo);
                    iv_logo.setVisibility(View.VISIBLE);
                }
            },1000);

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
}
