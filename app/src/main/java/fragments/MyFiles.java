package fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import Boomerang.R;
import activities.DashboardActivity;
import adapters.MyFilesAdapter;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.MySingletonclass;
import commonutils.UIutill;
import commonutils.URLS;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>,AdapterView.OnItemClickListener{
    View v=null;
    RelativeLayout layout_myfiles,layout_search,layout_refresh,layout_upload;
    TextView tv_foldername,tv_back;
    ListView lv_myfiles;
    ArrayList<MyFilesDataModel> myfileslist=new ArrayList<>();
    AlertDialog dialog;
    MethodClass<T> methodClass;
    RelativeLayout layout_foldernames;
    Stack<Integer> stack=new Stack<Integer>();
    int folderid;
    int position;
    String foldername;
    MyFilesAdapter adapter;
    String searchstring;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            methodClass=new MethodClass<>(getActivity(),this);
            v=inflater.inflate(R.layout.fragment_myfiles,null);
            foldername=getString(R.string.myfiles);
            folderid=getActivity().getSharedPreferences("Login",0).getInt("DirectoryId", 0);
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
            lv_myfiles.setOnItemClickListener(this);
            tv_back=(TextView)v.findViewById(R.id.tv_back);
            tv_foldername=(TextView)v.findViewById(R.id.tv_foldername);
            tv_back.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_back.setOnClickListener(this);
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            if(methodClass.checkInternetConnection()){
                position=1;
              methodClass.MakeGetRequest(URLS.GET_ROOT_FOLDER_FILES,getActivity().getSharedPreferences("Login",0).getString("UserID",""));
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
                position=3;
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
            case R.id.tv_back:
                try{
                    position=2;
                    if(stack.size()>2){
                        Map<String,String> map=new HashMap<>();
                        map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                        int index=stack.indexOf(stack.lastElement());
                        folderid=stack.get(index-1);
                        System.out.println("folderid"+folderid);
                        map.put("folderId",folderid+"");
                        methodClass.MakeGetRequestWithParams(map, URLS.GET_ROOT_FOLDER_FILES);
                    }
                    else{
                        methodClass.MakeGetRequest(URLS.GET_ROOT_FOLDER_FILES,getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    public void ShowSearchDialog(){
        if (dialog==null || !dialog.isShowing()){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.search_dialog, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_search_for_file=(TextView)dialoglayout.findViewById(R.id.tv_search_for_file);
            final EditText et_search=(EditText)dialoglayout.findViewById(R.id.et_search);
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

            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_search.getText().toString().length()==0){
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.empty_search));
                    }
                    else{
                       if(methodClass.checkInternetConnection()){
                           searchstring=et_search.getText().toString();
                           Map<String,String> map=new HashMap<String, String>();
                           map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                           map.put("searchText",searchstring);
                           methodClass.MakeGetRequestWithParams(map,URLS.SEARCH_FILE_FOLDER);
                       }
                        else{
                           UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                       }

                    }
                }
            });
        }


    }

    @Override
    public void onSuccess(T s) {
            try{
                System.out.println("inside on success");
                String value=new Gson().toJson(s);
                System.out.println("value"+value);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
                boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
                if(IsSucess){
                    if(jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size()>0){
                        JsonArray ResponseData=jsonreturn.get("ResponseData").getAsJsonArray();
                        System.out.println("response"+ResponseData);
                        myfileslist.clear();
                        for(int i=0;i<ResponseData.size();i++){
                           JsonObject object=ResponseData.get(i).getAsJsonObject();
                            MyFilesDataModel model=new MyFilesDataModel();
                            model.setFileid(object.get("FileID").getAsInt());
                            if(object.get("Type")!=null){
                                model.setFiletype(object.get("Type").getAsString());
                            }
                            else{
                                model.setFiletype("Unknown");
                            }

                            model.setFilepath(object.get("Path").getAsString());
                            model.setFilename(object.get("FileName").getAsString());
                            myfileslist.add(model);
                        }



                        if(myfileslist.size()>0){

                            if(position==1 || position==2){
                                tv_foldername.setText(foldername);
                                if(position==1){
                                    stack.push(folderid);
                                }
                                else if(position==2){
                                    stack.pop();
                                }
                                if(stack.size()>1){
                                    tv_back.setText(getString(R.string.back));
                                    tv_back.setVisibility(View.VISIBLE);
                                }
                                else{
                                    tv_back.setVisibility(View.GONE);
                                }
                                System.out.println("stack"+stack);
                                layout_foldernames.setVisibility(View.VISIBLE);
                                tv_foldername.setText(getString(R.string.my_file));
                                lv_myfiles.setAdapter(null);
                                adapter=new MyFilesAdapter(getActivity(),myfileslist);
                                lv_myfiles.setAdapter(adapter);
                            }
                            else if(position==3){
                                MySingletonclass.getobject().setList(myfileslist);
                                MySingletonclass.getobject().setSearchstring(searchstring);
                                ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,new SearchResult(),"search");
                            }


                        }
                        else{
                            if(position==1 ||position==2){
                                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_file));
                            }
                            else if(position==3){
                                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_search_result));
                            }

                        }

                    }
                    else{
                        UIutill.ShowDialog(getActivity(),getString(R.string.error),getString(R.string.no_file));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positionn, long id) {

        String filetype=((MyFilesDataModel)parent.getItemAtPosition(positionn)).getFiletype();
        if(filetype.equalsIgnoreCase("folder")){
            position=1;
            folderid=((MyFilesDataModel) parent.getItemAtPosition(positionn))
                    .getFileid();
            foldername=((MyFilesDataModel) parent.getItemAtPosition(positionn))
                    .getFilename();
            Map<String,String> map=new HashMap<>();
            System.out.println("userid"+getActivity().getSharedPreferences("Login",0).getString("UserID","")+"\n"+folderid+"\n"+foldername);
            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
            map.put("folderId",folderid+"");
            methodClass.MakeGetRequestWithParams(map,URLS.GET_ROOT_FOLDER_FILES);
        }
        else{

        }
    }
}
