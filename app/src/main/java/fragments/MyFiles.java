package fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import commonutils.UIutill;
import commonutils.URLS;
import customviews.SwipeMenu;
import customviews.SwipeMenuCreator;
import customviews.SwipeMenuItem;
import customviews.SwipeMenuListView;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>,AdapterView.OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener{
    View v=null;
    RelativeLayout layout_myfiles,layout_search,layout_refresh,layout_upload;
    TextView tv_foldername,tv_back;
    customviews.SwipeMenuListView lv_myfiles;
    ArrayList<MyFilesDataModel> myfileslist=new ArrayList<>();
    SwipeMenuCreator creator;
    AlertDialog dialog;
    MethodClass<T> methodClass;
    RelativeLayout layout_foldernames;
    Stack<Integer> stack=new Stack<Integer>();
    Stack<String> foldernames=new Stack<>();
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
            foldernames.clear();
            stack.clear();
            layout_foldernames.setVisibility(View.GONE);
            layout_myfiles=(RelativeLayout)v.findViewById(R.id.layout_myfiles);
            layout_search=(RelativeLayout)v.findViewById(R.id.layout_search);
            layout_refresh=(RelativeLayout)v.findViewById(R.id.layout_refresh);
            layout_upload=(RelativeLayout)v.findViewById(R.id.layout_upload);
            layout_myfiles.setOnClickListener(this);
            layout_search.setOnClickListener(this);
            layout_refresh.setOnClickListener(this);
            layout_upload.setOnClickListener(this);
            lv_myfiles=(customviews.SwipeMenuListView)v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnItemClickListener(this);

            lv_myfiles.setOnMenuItemClickListener(this);
            tv_back=(TextView)v.findViewById(R.id.tv_back);
            tv_foldername=(TextView)v.findViewById(R.id.tv_foldername);
            tv_back.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_back.setOnClickListener(this);
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));


            creator =new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    int viewtype = menu.getViewType();
                    SwipeMenuItem item1 = new SwipeMenuItem(
                            getActivity());
                    item1.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item1.setWidth(dp2px(60));
                    item1.setIcon(R.drawable.iv_delete);
                    menu.addMenuItem(item1);
                    SwipeMenuItem item2 = new SwipeMenuItem(
                            getActivity());

                    item2.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item2.setWidth(dp2px(60));
                    item2.setIcon(R.drawable.iv_download);
                    menu.addMenuItem(item2);
                    SwipeMenuItem item3 = new SwipeMenuItem(
                            getActivity());
                    item3.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item3.setWidth(dp2px(60));
                    item3.setIcon(R.drawable.iv_share);
                    menu.addMenuItem(item3);
                    if (myfileslist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
                        SwipeMenuItem item4 = new SwipeMenuItem(
                                getActivity());
                        item4.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                        item4.setWidth(dp2px(60));
                        item4.setIcon(R.drawable.iv_requestfile);
                        menu.addMenuItem(item4);
                    }
                }
            };

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
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
                UploadFiles files=new UploadFiles();
                Bundle b=new Bundle();
                b.putInt("folderid",stack.lastElement());
                files.setArguments(b);
                ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,files,"uploadfiles");
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
                        dialog.dismiss();
                        SearchResult result=new SearchResult();
                        Bundle b=new Bundle();
                        b.putString("searctext",et_search.getText().toString());
                        result.setArguments(b);
                        ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,result,"searchresult");
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
                    if(jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size()>=0){
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


                        layout_foldernames.setVisibility(View.VISIBLE);

                                if (position == 1) {
                                    stack.push(folderid);
                                    foldernames.push(foldername);
                                } else if (position == 2) {
                                    stack.pop();
                                    foldernames.pop();
                                }
                                if (stack.size() > 1) {
                                    tv_back.setText(getString(R.string.back));
                                    tv_back.setVisibility(View.VISIBLE);
                                } else {
                                    tv_back.setVisibility(View.GONE);
                                }
                                tv_foldername.setText(foldernames.lastElement());

                                adapter = new MyFilesAdapter(getActivity(), myfileslist);
                                lv_myfiles.setAdapter(adapter);
                        lv_myfiles.setMenuCreator(creator);

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

        System.out.println("item click"+positionn);
        if(DashboardActivity.slidingpane.isOpen()){
            DashboardActivity.slidingpane.closePane();
        }
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

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        System.out.println("position"+index);
        return false;
    }
}
