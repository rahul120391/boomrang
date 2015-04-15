package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by rahul on 3/4/2015.
 */
public class UserProfile<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T> {
    View v = null;
    TextView tv_userinfo, tv_email, tv_emailvalue, tv_password, tv_passwordvalue,
            tv_otherinfo, tv_regdate, tv_regvalue, tv_zipcode, tv_zipcodevalue;
    TextView tv_fname, tv_fnamevalue, tv_lname, tv_lnamevalue;
    ImageView iv_editprofile;
    Dialog editprofile;
    MethodClass methodclass;
    EditText et_fname, et_lname, et_newpassword;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            methodclass = new MethodClass(getActivity(), this);
            v = inflater.inflate(R.layout.fragment_userprofile, null);
            iv_editprofile = (ImageView) v.findViewById(R.id.iv_editprofile);
            iv_editprofile.setOnClickListener(this);
            tv_userinfo = (TextView) v.findViewById(R.id.tv_userinfo);
            tv_email = (TextView) v.findViewById(R.id.tv_email);
            tv_emailvalue = (TextView) v.findViewById(R.id.tv_emailvalue);
            tv_password = (TextView) v.findViewById(R.id.tv_password);
            tv_passwordvalue = (TextView) v.findViewById(R.id.tv_passwordvalue);

            tv_otherinfo = (TextView) v.findViewById(R.id.tv_otherinfo);
            tv_regdate = (TextView) v.findViewById(R.id.tv_regdate);
            tv_regvalue = (TextView) v.findViewById(R.id.tv_regvalue);
            tv_zipcode = (TextView) v.findViewById(R.id.tv_zipcode);
            tv_zipcodevalue = (TextView) v.findViewById(R.id.tv_zipcodevalue);
            tv_fname = (TextView) v.findViewById(R.id.tv_fname);
            tv_fnamevalue = (TextView) v.findViewById(R.id.tv_fnamevalue);
            tv_lname = (TextView) v.findViewById(R.id.tv_lname);
            tv_lnamevalue = (TextView) v.findViewById(R.id.tv_lnamevalue);
            //Set Font
            tv_userinfo.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_emailvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_password.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_passwordvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_fname.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_fnamevalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_lname.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_lnamevalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_passwordvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_otherinfo.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_regdate.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_regvalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_zipcode.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_zipcodevalue.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            prefs = getActivity().getSharedPreferences("Login", 0);
            tv_emailvalue.setText(prefs.getString("emailID", ""));
            String newpass = allStar(prefs.getString("Password", ""));
            tv_passwordvalue.setText(newpass);
            tv_fnamevalue.setText(prefs.getString("FirstName", ""));
            tv_lnamevalue.setText(prefs.getString("LastName", ""));
            tv_regvalue.setText(prefs.getString("RegistrationDate", ""));
            tv_zipcodevalue.setText(prefs.getString("ZipCode", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
    /********************************************************************************************************/

    /**
     * this methiod is used to encrypt the password inform of *
     *
     * @param s -pass the password string to convert to *
     * @return -returns the password in teh encrypted format
     */
    public String allStar(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_editprofile:
                ShowEditProfileDialog();
                break;
        }
    }

    /**
     * **********************************************************************************************************************
     */

    public void ShowEditProfileDialog() {
        if (editprofile == null || !editprofile.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.layout_editprofile, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_editprofile = (TextView) dialoglayout.findViewById(R.id.tv_editprofile);
            et_fname = (EditText) dialoglayout.findViewById(R.id.et_fname);
            et_lname = (EditText) dialoglayout.findViewById(R.id.et_lname);
            TextView tv_changepassword = (TextView) dialoglayout.findViewById(R.id.tv_changepassword);
            final EditText et_currentpassword = (EditText) dialoglayout.findViewById(R.id.et_currentpassword);
            et_newpassword = (EditText) dialoglayout.findViewById(R.id.et_newpassword);
            Button btn_edit = (Button) dialoglayout.findViewById(R.id.btn_edit);
            Button btn_cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
            tv_editprofile.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_fname.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_lname.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_changepassword.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_currentpassword.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_newpassword.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_edit.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_cancel.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_fname.setText(tv_fnamevalue.getText().toString());
            et_lname.setText(tv_lnamevalue.getText().toString());
            et_currentpassword.setText(tv_passwordvalue.getText().toString());
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editprofile.dismiss();
                }
            });
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("current password" + et_currentpassword.getText().toString());
                    System.out.println("new password" + et_newpassword.getText().toString());
                    if (et_fname.getText().toString().trim().length() == 0) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.empty_fname));
                    } else if (et_lname.getText().toString().trim().length() == 0) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.empty_lname));
                    } else if (!et_currentpassword.getText().toString().trim().equalsIgnoreCase
                            (allStar(getActivity().getSharedPreferences("Login", 0).getString("Password", "")))) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.wrong_currentpass));
                    } else if (et_newpassword.getText().toString().length() == 0) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.empty_newpass));
                    } else {
                        if (methodclass.checkInternetConnection()) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("firstName", et_fname.getText().toString().trim());
                            map.put("lastName", et_lname.getText().toString().trim());
                            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                            map.put("password", et_newpassword.getText().toString().trim());
                            methodclass.MakeGetRequestWithParams(map, URLS.UPDATEPROFILE);
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                        }
                    }
                }
            });
            builder.setView(dialoglayout);
            editprofile = builder.create();
            editprofile.getWindow().getAttributes().windowAnimations = R.style.MyAnim_SearchWindow;
            editprofile.show();
        }
    }

    @Override
    public void onSuccess(T s) {
        try {
            String value = new Gson().toJson(s);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonreturn = (JsonObject) jsonParser.parse(value);
            System.out.println("json return" + jsonreturn);
            boolean IsSucess = jsonreturn.get("IsSucess").getAsBoolean();
            if (IsSucess) {
                SharedPreferences.Editor e = prefs.edit();
                e.putString("FirstName", et_fname.getText().toString().trim());
                e.putString("LastName", et_lname.getText().toString().trim());
                e.putString("Password", et_newpassword.getText().toString().trim());
                e.commit();
                tv_fnamevalue.setText(et_fname.getText().toString().trim());
                tv_lnamevalue.setText(et_lname.getText().toString().trim());
                tv_passwordvalue.setText(allStar(et_newpassword.getText().toString().trim()));
                editprofile.dismiss();
                UIutill.ShowSnackBar(getActivity(), jsonreturn.get("ResponseData").getAsString());
            } else {
                UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("Message").getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(RetrofitError error) {
        if (error != null) {
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }


    /************************************************************************************************************************/
}
