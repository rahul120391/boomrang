package fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import Boomerang.R;
import activities.DashboardActivity;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>{
    View v=null;
    RelativeLayout layout_myfiles,layout_search,layout_refresh,layout_upload;
    TextView tv_foldername,tv_back;
    ListView lv_myfiles;
    ArrayList<MyFilesDataModel> myfileslist=new ArrayList<>();
    AlertDialog dialog;
    MethodClass<T> methodClass;
    RelativeLayout layout_foldernames;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            methodClass=new MethodClass<>(getActivity(),this);
            v=inflater.inflate(R.layout.fragment_myfiles,null);
            layout_foldernames=(RelativeLayout)v.findViewById(R.id.layout_foldernames);
            layout_myfiles=(RelativeLayout)v.findViewById(R.id.layout_myfiles);
            layout_search=(RelativeLayout)v.findViewById(R.id.layout_search);
            layout_refresh=(RelativeLayout)v.findViewById(R.id.layout_refresh);
            layout_upload=(RelativeLayout)v.findViewById(R.id.layout_upload);
            layout_myfiles.setOnClickListener(this);
            layout_search.setOnClickListener(this);
            layout_refresh.setOnClickListener(this);
            layout_upload.setOnClickListener(this);
            lv_myfiles=(ListView)v.findViewById(R.id.lv_myfiles);
            tv_back=(TextView)v.findViewById(R.id.tv_back);
            tv_foldername=(TextView)v.findViewById(R.id.tv_foldername);
            tv_back.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            if(methodClass.checkInternetConnection()){
              //  methodClass.MakeGetRequest(URLS.GET_ROOT_FOLDER_FILES,getActivity().getSharedPreferences("Login",0).getString("UserID",""));
            }
            else{
                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_myfiles:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_refresh.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                break;
            case R.id.layout_search:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_refresh.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                ShowSearchDialog();
                break;
            case R.id.layout_refresh:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_refresh.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                break;
            case R.id.layout_upload:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_refresh.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,new UploadFiles(),"uploadfiles");
                break;
        }
    }

    public void ShowSearchDialog(){
        if (dialog==null || !dialog.isShowing()){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.search_dialog, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_search_for_file=(TextView)dialoglayout.findViewById(R.id.tv_search_for_file);
            EditText et_search=(EditText)dialoglayout.findViewById(R.id.et_search);
            Button btn_search=(Button)dialoglayout.findViewById(R.id.btn_search);
            Button btn_cancel=(Button)dialoglayout.findViewById(R.id.btn_cancel);

            tv_search_for_file.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            et_search.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_search.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_cancel.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            builder.setView(dialoglayout);
            dialog=builder.create();
            dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnim_SearchWindow;
            dialog.show();

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
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
                    if(jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size()>0){
                        JsonArray ResponseData=jsonreturn.get("ResponseData").getAsJsonArray();
                        for(int i=0;i<ResponseData.size();i++){
                           JsonObject object=ResponseData.get(i).getAsJsonObject();
                            MyFilesDataModel model=new MyFilesDataModel();
                            model.setFileid(object.get("FileID").getAsInt());
                            model.setFiletype(object.get("Type").getAsString());
                            model.setFilepath(object.get("Path").getAsString());
                            model.setFilename(object.get("FileName").getAsString());
                            myfileslist.add(model);
                        }
                      if(myfileslist.size()>0){
                          layout_foldernames.setVisibility(View.VISIBLE);
                          tv_foldername.setText(getString(R.string.my_file));
                      }

                    }
                    else{
                        UIutill.ShowDialog(getActivity(),getString(R.string.error),jsonreturn.get("ResponseData").getAsString());
                    }

                }
                else{
                    UIutill.ShowDialog(getActivity(),getString(R.string.error),jsonreturn.get("Message").getAsString());
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public void onFailure(RetrofitError error) {
        if(error!=null){

            UIutill.ShowDialog(getActivity(),getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }
}
