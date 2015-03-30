package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
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
    RelativeLayout layout_myfiles,layout_search,layout_refresh,layout_upload,layout_createfolder;
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
    Dialog confirmdialog,requestfolder,sharedialog;
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
            layout_createfolder=(RelativeLayout)v.findViewById(R.id.layout_createfolder);
            layout_myfiles.setOnClickListener(this);
            layout_search.setOnClickListener(this);
            layout_refresh.setOnClickListener(this);
            layout_upload.setOnClickListener(this);
            layout_createfolder.setOnClickListener(this);
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


                    if (!myfileslist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
                        SwipeMenuItem item2 = new SwipeMenuItem(
                                getActivity());
                        item2.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                        item2.setWidth(dp2px(60));
                        item2.setIcon(R.drawable.iv_download);
                        menu.addMenuItem(item2);
                    }
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
                ShowSearch_CreateFolderDialog("search");
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
            case R.id.layout_createfolder:
                ShowSearch_CreateFolderDialog("createfolder");
                break;
            case R.id.tv_back:
                try{
                    if(methodClass.checkInternetConnection()){
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
                    else{
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
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
                System.out.println("inside on success");

                String value=new Gson().toJson(s);
                System.out.println("value"+value);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
                boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
                if(IsSucess){
                    if(position==3 || position==4){
                        UIutill.ShowSnackBar(getActivity(),jsonreturn.get("ResponseData").getAsString().trim());
                    }
                    else if(position==1 || position==2){
                        String message=jsonreturn.get("Message").getAsString().trim();
                        if(!message.equalsIgnoreCase("")){
                            UIutill.ShowSnackBar(getActivity(),message);
                        }
                        if(jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size()>=0){
                            JsonArray ResponseData=jsonreturn.get("ResponseData").getAsJsonArray();
                            System.out.println("response"+ResponseData);
                            myfileslist.clear();
                            for(int i=0;i<ResponseData.size();i++){
                                JsonObject object=ResponseData.get(i).getAsJsonObject();
                                MyFilesDataModel model=new MyFilesDataModel();
                                model.setFileid(object.get("FileID").getAsInt());
                                if(object.get("Type")!=null){
                                    model.setFiletype(object.get("Type").getAsString().trim());
                                }
                                else{
                                    model.setFiletype("Unknown");
                                }

                                model.setFilepath(object.get("Path").getAsString().trim());
                                model.setFilename(object.get("FileName").getAsString().trim());
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
            if(methodClass.checkInternetConnection()){
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
                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
            }

        }
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        System.out.println("listview position"+position);
        System.out.println("indexx"+index);
        String type=null;
        String filetype=myfileslist.get(position).getFiletype();
        System.out.println("filetype"+filetype);
        if(filetype.equalsIgnoreCase("folder")){
            type="0";
        }
        else{
            type="1";
        }
        String fileid=myfileslist.get(position).getFileid()+"";
        switch (index){
            case 0:
                ShowConfirmDialog(fileid,type);
                break;
            case 1:
                if(type.equalsIgnoreCase("0")){
                    String filenamee=myfileslist.get(position).getFilename();
                    ShareDialogView(filenamee,fileid,filetype);
                }
                else {
                    if (type.equalsIgnoreCase("1")) {
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                        map.put("folderFileId", fileid);
                        map.put("type", "1");
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String filename = myfileslist.get(position).getFilename();
                        File directory=new File(path+"/"+"MyData");
                        if(!directory.exists()){
                            directory.mkdir();
                        }
                        String mypath = path+"/"+"MyData"+"/"+filename;
                        System.out.println("path"+mypath);
                        File file = new File(mypath);
                        try{
                            if(!file.exists()){
                                file.createNewFile();
                            }
                         new MethodClass(map,mypath,getActivity());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 2:
               if(type.equalsIgnoreCase("0")){
                   String filename=myfileslist.get(position).getFilename();
                   RequestFolderView(fileid,filename);
               }
                else{
                   String filenamee=myfileslist.get(position).getFilename();
                   ShareDialogView(filenamee,fileid,filetype);
               }
                break;
            default:
                break;
        }
        return false;
    }



    public void ShowSearch_CreateFolderDialog(final String show){
        if (dialog==null || !dialog.isShowing()){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.search_dialog, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_search_for_file=(TextView)dialoglayout.findViewById(R.id.tv_search_for_file);
            final EditText et_search=(EditText)dialoglayout.findViewById(R.id.et_search);
            Button btn_search=(Button)dialoglayout.findViewById(R.id.btn_search);
            Button btn_cancel=(Button)dialoglayout.findViewById(R.id.btn_cancel);
            if(show.equalsIgnoreCase("createfolder")){
                tv_search_for_file.setText(getString(R.string.createfolder));
                et_search.setHint(getString(R.string.enter_folder_name));
                btn_search.setText(getString(R.string.create));
            }
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
                    if(show.equalsIgnoreCase("search")){
                        if(et_search.getText().toString().trim().length()==0){
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
                    else if(show.equalsIgnoreCase("createfolder")){
                        if(et_search.getText().toString().trim().length()==0){
                            UIutill.ShowSnackBar(getActivity(),getString(R.string.empty_foldername));
                        }
                        else{
                            dialog.dismiss();
                            if(methodClass.checkInternetConnection()){
                                Map<String,String> map=new HashMap<String, String>();
                                map.put("userid",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                                map.put("currentFolderId",stack.lastElement()+"");
                                map.put("folderName",et_search.getText().toString().trim());
                                methodClass.MakeGetRequestWithParams(map,URLS.CREATE_FOLDER);
                            }
                            else{
                                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                            }

                        }
                    }

                }
            });
        }


    }
    public void ShowConfirmDialog(final String fileid,final String type){
        if(confirmdialog==null || !confirmdialog.isShowing()){
            LayoutInflater inflater =LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.confirmation_dialogview, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_title=(TextView)dialoglayout.findViewById(R.id.tv_title);
            tv_title.setText(getString(R.string.confirmation));
            TextView tv_message=(TextView)dialoglayout.findViewById(R.id.tv_message);
            if(type.equalsIgnoreCase("0")){
                tv_message.setText(getString(R.string.delete_folder_message));
            }
            else if(type.equalsIgnoreCase("1")){
                tv_message.setText(getString(R.string.delete_file_message));
            }
            Button btn_yes=(Button)dialoglayout.findViewById(R.id.btn_yes);
            Button btn_no=(Button)dialoglayout.findViewById(R.id.btn_no);

            tv_title.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_no.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_yes.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            builder.setView(dialoglayout);
            confirmdialog=builder.create();
            confirmdialog.getWindow().getAttributes().windowAnimations=R.style.Animations_SmileWindow;
            confirmdialog.show();

            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmdialog.dismiss();
                }
            });
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmdialog.dismiss();
                    if(methodClass.checkInternetConnection()){
                        position=0;
                        Map<String,String> map=new HashMap<>();
                        map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                        map.put("folderIdFileId",fileid);
                        map.put("type",type);
                        map.put("currentFolderId",stack.lastElement()+"");
                        methodClass.MakeGetRequestWithParams(map,URLS.PERMANENT_DELETE_FILE_FOLDER);
                    }
                    else{
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                    }
                }
            });
        }
    }

    public void RequestFolderView(final String folderid,final String foldername){
        if(requestfolder==null || !requestfolder.isShowing()){
            LayoutInflater inflater =LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.requestfile_dialogview, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Button btn_cancel=(Button)dialoglayout.findViewById(R.id.btn_cancel);
            btn_cancel.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            Button btn_request=(Button)dialoglayout.findViewById(R.id.btn_request);
            btn_request.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            TextView tv_requestfolder=(TextView)dialoglayout.findViewById(R.id.tv_requestfolder);
            tv_requestfolder.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            TextView tv_email=(TextView)dialoglayout.findViewById(R.id.tv_email);
            tv_email.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            final  EditText et_email=(EditText)dialoglayout.findViewById(R.id.et_email);
            et_email.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            TextView tv_message=(TextView)dialoglayout.findViewById(R.id.tv_message);
            tv_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            TextView tv_expiry=(TextView)dialoglayout.findViewById(R.id.tv_expiry);
            tv_expiry.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            final EditText et_message=(EditText)dialoglayout.findViewById(R.id.et_message);
            et_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            final Spinner sp_select_expiry=(Spinner)dialoglayout.findViewById(R.id.sp_select_expiry);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.expiry_value_array)){
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
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
            sp_select_expiry.setAdapter(adapter);
            builder.setView(dialoglayout);
            requestfolder=builder.create();
            requestfolder.getWindow().getAttributes().windowAnimations=R.style.MyAnim_SearchWindow;
            requestfolder.show();
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestfolder.dismiss();
                }
            });
            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_email.getText().toString().trim().length()==0){
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.email_empty));
                    }
                    else if(!et_email.getText().toString()
                            .matches(Patterns.EMAIL_ADDRESS.pattern())){
                        UIutill.ShowSnackBar(getActivity(),getString(R.string.valied_Email));
                    }
                    else{
                        if(methodClass.checkInternetConnection()){
                            requestfolder.dismiss();
                            position=3;
                            Map<String,String> map=new HashMap<String, String>();
                            map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                            map.put("requestedFolderId",folderid);
                            map.put("requestedFolderName",foldername);
                            switch (sp_select_expiry.getSelectedItemPosition()){
                                case 0:
                                    map.put("expiryInDays","0");
                                    break;
                                case 1:
                                    map.put("expiryInDays","1");
                                    break;
                                case 2:
                                    map.put("expiryInDays","7");
                                    break;
                                default:
                                    break;
                            }
                            map.put("emailId",et_email.getText().toString().trim());
                            map.put("Message",et_message.getText().toString().trim());
                            methodClass.MakeGetRequestWithParams(map,URLS.REQUEST_FILE);
                        }
                        else{
                            UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                        }

                    }
                }
            });
        }
    }

   public void ShareDialogView(final String filename,final String fileid,final String type){
       if(sharedialog==null || !sharedialog.isShowing()){
           LayoutInflater inflater =LayoutInflater.from(getActivity());
           final View dialoglayout = inflater.inflate(R.layout.sharefolder_dialogview, null);
           final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
           TextView tv_share_file_folder=(TextView)dialoglayout.findViewById(R.id.tv_share_file_folder);
           tv_share_file_folder.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           TextView tv_email=(TextView)dialoglayout.findViewById(R.id.tv_email);
           tv_email.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           final TextView et_email=(TextView)dialoglayout.findViewById(R.id.et_email);
           et_email.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           TextView tv_message=(TextView)dialoglayout.findViewById(R.id.tv_message);
           tv_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           final EditText et_message=(EditText)dialoglayout.findViewById(R.id.et_message);
           et_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           Button btn_share=(Button)dialoglayout.findViewById(R.id.btn_share);
           btn_share.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

           Button btn_cancel=(Button)dialoglayout.findViewById(R.id.btn_cancel);
           btn_cancel.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));


           builder.setView(dialoglayout);
           sharedialog=builder.create();

           sharedialog.getWindow().getAttributes().windowAnimations=R.style.MyAnim_SearchWindow;
           sharedialog.show();


           btn_cancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   sharedialog.dismiss();
               }
           });

           btn_share.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(et_email.getText().toString().trim().length()==0){
                     UIutill.ShowSnackBar(getActivity(),getString(R.string.email_empty));
                   }
                   else{
                       String emails[] = et_email.getText().toString().trim()
                               .split(",");
                       StringBuilder builder = new StringBuilder();
                       for (int i = 0; i < emails.length; i++) {
                           if (!emails[i].trim().matches(
                                   Patterns.EMAIL_ADDRESS.pattern())
                                   ) {
                               UIutill.ShowSnackBar(getActivity(),getString(R.string.valied_Email));
                               return;
                           } else {
                               builder.append(emails[i].trim());
                               if (i != emails.length - 1) {
                                   builder.append(",");
                               }
                           }
                       }
                       if(methodClass.checkInternetConnection()){
                           try{
                               sharedialog.dismiss();
                               position=4;
                               System.out.println("emailids"+builder.toString());
                               Map<String,String> map=new HashMap<String, String>();
                               map.put("emailIds",builder.toString());
                               map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                               map.put("message",et_message.getText().toString().trim());
                               map.put("type",type);
                               map.put("fileName",filename);
                               map.put("fileId",fileid);
                               methodClass.MakeGetRequestWithParams(map,URLS.SHARE_FILE);
                           }
                           catch (Exception e){
                               e.printStackTrace();
                           }

                       }
                       else{
                           UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                       }
                   }

               }
           });
       }
   }


}
