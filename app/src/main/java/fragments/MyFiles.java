package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Boomerang.R;
import activities.DashboardActivity;
import adapters.MyFilesAdapter;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.MyRetrofitInterface;
import commonutils.ProgressDialogClass;
import commonutils.UIutill;
import commonutils.URLS;
import customviews.SwipeMenu;
import customviews.SwipeMenuCreator;
import customviews.SwipeMenuItem;
import customviews.SwipeMenuLayout;
import customviews.SwipeMenuListView;
import de.greenrobot.event.EventBus;
import modelclasses.MyFilesDataModel;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>,AdapterView.OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener{
    View v=null;
    RelativeLayout layout_myfiles,layout_sync,layout_search,layout_upload,layout_createfolder;
    TextView tv_foldername;
    ImageView iv_back;
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
    LinearLayout mainlayout;
    int listviewpositionclick=0;
    String deviceId;
    ExecutorService executorService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            EventBus.getDefault().register(this);
            deviceId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            methodClass=new MethodClass<>(getActivity(),this);
            v=inflater.inflate(R.layout.fragment_myfiles,null);
            foldername=getString(R.string.myfiles);
            folderid=getActivity().getSharedPreferences("Login",0).getInt("DirectoryId", 0);
            layout_foldernames=(RelativeLayout)v.findViewById(R.id.layout_foldernames);
            foldernames.clear();
            stack.clear();
            layout_foldernames.setVisibility(View.GONE);
            mainlayout=(LinearLayout)v.findViewById(R.id.layout_main);
            mainlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(lv_myfiles.getCount()>0)
                    {
                        if (listviewpositionclick >= lv_myfiles.getFirstVisiblePosition()
                                && listviewpositionclick <= lv_myfiles.getLastVisiblePosition()) {
                            View view = lv_myfiles.getChildAt(listviewpositionclick - lv_myfiles.getFirstVisiblePosition());
                            if (view instanceof SwipeMenuLayout) {
                                ((SwipeMenuLayout) view).smoothCloseMenu();
                            }
                        }
                    }
                    return false;
                }
            });
            layout_myfiles=(RelativeLayout)v.findViewById(R.id.layout_myfiles);
            layout_sync=(RelativeLayout)v.findViewById(R.id.layout_sync);
            layout_upload=(RelativeLayout)v.findViewById(R.id.layout_upload);
            layout_createfolder=(RelativeLayout)v.findViewById(R.id.layout_createfolder);
            layout_search=(RelativeLayout)v.findViewById(R.id.layout_search);
            layout_search.setOnClickListener(this);
            layout_myfiles.setOnClickListener(this);
            layout_sync.setOnClickListener(this);
            layout_upload.setOnClickListener(this);
            layout_createfolder.setOnClickListener(this);
            lv_myfiles=(customviews.SwipeMenuListView)v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnItemClickListener(this);

            lv_myfiles.setOnMenuItemClickListener(this);
            iv_back=(ImageView)v.findViewById(R.id.iv_back);
            tv_foldername=(TextView)v.findViewById(R.id.tv_foldername);
            iv_back.setOnClickListener(this);
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));


            creator =new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    int viewtype = menu.getViewType();
                    SwipeMenuItem item1 = new SwipeMenuItem(
                            getActivity());
                    item1.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item1.setWidth(UIutill.dp2px(60, getActivity()));
                    item1.setIcon(R.drawable.iv_delete);
                    menu.addMenuItem(item1);


                    if (!myfileslist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
                        SwipeMenuItem item2 = new SwipeMenuItem(
                                getActivity());
                        item2.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                        item2.setWidth(UIutill.dp2px(60, getActivity()));
                        item2.setIcon(R.drawable.iv_download);
                        menu.addMenuItem(item2);
                    }
                    SwipeMenuItem item3 = new SwipeMenuItem(
                            getActivity());
                    item3.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item3.setWidth(UIutill.dp2px(60, getActivity()));
                    item3.setIcon(R.drawable.iv_share);
                    menu.addMenuItem(item3);
                    if (myfileslist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
                        SwipeMenuItem item4 = new SwipeMenuItem(
                                getActivity());
                        item4.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                        item4.setWidth(UIutill.dp2px(60, getActivity()));
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


    /********************************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_myfiles:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_sync.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_createfolder.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                break;
            case R.id.layout_search:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_sync.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_createfolder.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                ShowSearch_CreateFolderDialog("search");
                break;
            case R.id.layout_sync:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_sync.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_createfolder.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                if(methodClass.checkInternetConnection()){
                    if(stack!=null && stack.size()>0){
                        position=7;
                        UIutill.ShowSnackBar(getActivity(),"Sync started");
                        Map<String,String> map=new HashMap<>();
                        map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                        map.put("deviceId",deviceId);
                        map.put("folderId",stack.lastElement()+"");
                        methodClass.MakeGetRequestWithParams(map,URLS.SYNCFILES);
                    }
                }
                else{
                    UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                }
                break;
            case R.id.layout_upload:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_sync.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                layout_createfolder.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                if( stack!=null && stack.size()>0){
                    UploadFiles files=new UploadFiles();
                    Bundle b=new Bundle();
                    b.putInt("folderid",stack.lastElement());
                    files.setArguments(b);
                    ((DashboardActivity)getActivity()).FragmentTransactions(R.id.fragment_container,files,"uploadfiles");
                }
                break;
            case R.id.layout_createfolder:
                layout_myfiles.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_sync.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_search.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.myfiles_unselelcted));
                layout_createfolder.setBackgroundColor(getResources().getColor(R.color.myfiles_selected));
                if(stack.size()>0){
                    ShowSearch_CreateFolderDialog("createfolder");
                }
                break;
            case R.id.iv_back:
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

    /********************************************************************************************************/
    @Override
    public void onSuccess(T s) {
            try{
                System.out.println("inside on success");
                String value=new Gson().toJson(s);
                System.out.println("value"+value);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
                boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
                if(IsSucess) {
                    System.out.println("position value" + position);
                    if (position == 0 || position == 1 || position == 2 || position == 6) {
                        String message = jsonreturn.get("Message").getAsString().trim();
                        if (!message.equalsIgnoreCase("")) {
                            UIutill.ShowSnackBar(getActivity(), message);
                        }
                        if (jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size() >= 0) {
                            JsonArray ResponseData = jsonreturn.get("ResponseData").getAsJsonArray();
                            System.out.println("response" + ResponseData);
                            myfileslist.clear();
                            for (int i = 0; i < ResponseData.size(); i++) {
                                JsonObject object = ResponseData.get(i).getAsJsonObject();
                                MyFilesDataModel model = new MyFilesDataModel();
                                model.setFileid(object.get("FileID").getAsInt());
                                if (object.get("Type") != null) {
                                    model.setFiletype(object.get("Type").getAsString().trim());
                                } else {
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
                                iv_back.setVisibility(View.VISIBLE);
                            } else {
                                iv_back.setVisibility(View.GONE);
                            }
                            if (myfileslist.size() > 0) {
                                lv_myfiles.setMenuCreator(creator);
                            }
                            tv_foldername.setText(foldernames.lastElement());
                            adapter = new MyFilesAdapter(getActivity(), myfileslist);
                            lv_myfiles.setAdapter(adapter);

                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_result));
                        }
                    } else if (position == 3 || position == 4) {
                        UIutill.ShowSnackBar(getActivity(), jsonreturn.get("ResponseData").getAsString().trim());
                    } else if (position == 5) {
                        ProgressDialogClass.logout();
                        String fileurl = jsonreturn.get("ResponseData").getAsString();
                        String filename = jsonreturn.get("CallBack").getAsString();
                        DownloadFiles(filename, fileurl);
                    } else if (position == 7) {
                        JsonObject ResponseData = jsonreturn.getAsJsonObject("ResponseData");
                        JsonArray Table = ResponseData.getAsJsonArray("Table");
                        if (Table.isJsonArray() && Table.size() > 0) {
                            ArrayList<Integer> myfiledata=new ArrayList<>();
                            for(MyFilesDataModel dataModel:myfileslist){
                                myfiledata.add(dataModel.getFileid());
                            }
                            for (int x = 0; x < Table.size(); x++) {
                                JsonObject object = Table.get(x).getAsJsonObject();
                                System.out.println("objectt"+object.get("status").getAsInt());
                                if (object.get("status").getAsInt() == 0) {
                                    if(myfiledata.contains(object.get("FileId").getAsInt())){
                                        continue;
                                    }
                                    else{
                                        MyFilesDataModel model = new MyFilesDataModel();
                                        model.setFileid(object.get("FileId").getAsInt());
                                        if (object.get("Type") != null) {
                                            model.setFiletype(object.get("Type").getAsString().trim());
                                        } else {
                                            model.setFiletype("Unknown");
                                        }
                                        model.setFilename(object.get("FileName").getAsString().trim());
                                        myfileslist.add(0, model);
                                    }
                                } else if (object.get("status").getAsInt() == 1) {
                                    int fileid = object.get("FileId").getAsInt();
                                    Iterator<MyFilesDataModel> modell=myfileslist.iterator();
                                    while (modell.hasNext()){
                                        MyFilesDataModel mymodel=modell.next();
                                        if(mymodel.getFileid()==fileid){
                                            modell.remove();
                                        }
                                    }
                                }

                            }
                        }
                        adapter = new MyFilesAdapter(getActivity(), myfileslist);
                        lv_myfiles.setAdapter(adapter);
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
    /********************************************************************************************************/

    @Override
    public void onFailure(RetrofitError error) {
        if(error!=null){

            UIutill.ShowDialog(getActivity(),getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }

    /********************************************************************************************************/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positionn, long id) {

        System.out.println("item click"+positionn);
        listviewpositionclick=positionn;
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

    /********************************************************************************************************/

    @Override
    public boolean onMenuItemClick(int positionn, SwipeMenu menu, int index) {
        System.out.println("listview position"+positionn);
        System.out.println("indexx"+index);
        String type=null;
        String filetype=myfileslist.get(positionn).getFiletype();
        System.out.println("filetype"+filetype);
        if(filetype.equalsIgnoreCase("folder")){
            type="0";
        }
        else{
            type="1";
        }
        String fileid=myfileslist.get(positionn).getFileid()+"";
        switch (index){
            case 0:
                ShowConfirmDialog(fileid,type);
                break;
            case 1:
                if(type.equalsIgnoreCase("0")){
                    String filenamee=myfileslist.get(positionn).getFilename();
                    ShareDialogView(filenamee,fileid,filetype);
                }
                else {
                    if (type.equalsIgnoreCase("1")) {
                        int state = getActivity().getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                        if(state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED||
                                state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                                ||state== PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED){
                            try {
                                Toast.makeText(getActivity(),"Enable Download Manager",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
                                startActivity(intent);
                            } catch ( ActivityNotFoundException e ) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                        else{
                            String deviceId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                                    android.provider.Settings.Secure.ANDROID_ID);
                            Map<String, String> map = new HashMap<>();
                            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                            System.out.println("fileid" + fileid);
                            map.put("folderFileId", fileid);
                            map.put("type", "1");
                            map.put("deviceId",deviceId);
                            if(methodClass.checkInternetConnection()){
                                position=5;
                                System.out.println("position"+position);
                                methodClass.MakeGetRequestWithParams(map,URLS.DOWNLOAD);
                            }
                            else{
                                UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                            }
                        }

                    }
                }
                break;
            case 2:
               if(type.equalsIgnoreCase("0")){
                   String filename=myfileslist.get(positionn).getFilename();
                   RequestFolderView(fileid,filename);
               }
                else{
                   String filenamee=myfileslist.get(positionn).getFilename();
                   ShareDialogView(filenamee,fileid,filetype);
               }
                break;
            default:
                break;
        }
        return false;
    }

   /********************************************************************************************************/
    /**
     * this method is used to show search/create dialog
     * @param show
     * -value used to decide which dialog will be displayed.
     */
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
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
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
                                position=6;
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

    /********************************************************************************************************/

    /**
     * this method is used to confirm file deletion
     * @param fileid
     * -pass fileid of the file to delete
     * @param type
     * -pass the type of the file to delete(0 for folder delete and 1 for file delete)
     */
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

    /********************************************************************************************************/
    /**
     * this method is used to request a folder
     * @param folderid
     * -folder of the folder to request
     * @param foldername
     * -name of the folder
     */
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
            et_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_message=(TextView)dialoglayout.findViewById(R.id.tv_message);
            tv_message.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

            TextView tv_expiry=(TextView)dialoglayout.findViewById(R.id.tv_expiry);
            tv_expiry.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

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
                    else if(!et_email.getText().toString().trim()
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

    /********************************************************************************************************/
    /**
     * this method is used to show share dilaog
     * @param filename
     * -filename to share
     * @param fileid
     * -fileid of that particluar file
     * @param type
     * -type of that particular file
     */
   public void ShareDialogView(final String filename,final String fileid,final String type){
       if(sharedialog==null || !sharedialog.isShowing()){
           LayoutInflater inflater =LayoutInflater.from(getActivity());
           final View dialoglayout = inflater.inflate(R.layout.sharefolder_dialogview, null);
           final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
           TextView tv_share_file_folder=(TextView)dialoglayout.findViewById(R.id.tv_share_file_folder);
           tv_share_file_folder.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

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
           btn_cancel.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));


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
                   UIutill.HideKeyboard(getActivity());
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

    /********************************************************************************************************/

    /**
     * this method is used to download files using default android download manager
     * @param filename
     * -name of the file to download
     * @param url
     * -url from where the file to download
     */
   public void DownloadFiles(String filename,String url){
       int state = getActivity().getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
       if(state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED||
               state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
               ||state== PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED){
           try {
               Toast.makeText(getActivity(),"Enable Download Manager",Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
               startActivity(intent);
           } catch ( ActivityNotFoundException e ) {
               Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
           }
       }
       else{
           UIutill.ShowSnackBar(getActivity(),getString(R.string.download_start));
           String path = Environment.getExternalStorageDirectory().getAbsolutePath();
           File directory=new File(path+"/"+"MyData");
           System.out.println("url"+url);
           if(!directory.exists()){
               directory.mkdir();
           }
           String mypath = path+"/"+"MyData"+"/"+filename;
           System.out.println("path"+mypath);
           File file = new File(mypath);
           if(file.exists()){
               file.delete();
           }
           DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
           DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
           request.setTitle(filename);
           request.setDescription(getString(R.string.downloading));
           request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
           request.setDestinationInExternalPublicDir("/MyData",filename);
           manager.enqueue(request);
       }

   }

    /********************************************************************************************************/
    /**
     * this method is used to check the visibility of the screen
     * @param event
     * -event to be fired at certian time
     */
    public void onEvent(String event) {
        System.out.println("event is fired"+event);
        if(stack !=null && stack.size()>0){
            if(methodClass.checkInternetConnection()){
                Gson gson = new GsonBuilder()
                        .enableComplexMapKeySerialization()
                        .setDateFormat(DateFormat.LONG)
                        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .setPrettyPrinting()
                        .setVersion(1.0)
                        .create();
                final int folderid=stack.lastElement();

                Map<String,String> map=new HashMap<>();
                map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                map.put("deviceId",deviceId);
                map.put("folderId",folderid+"");
                executorService=Executors.newCachedThreadPool();
                final RestAdapter restadapter=new RestAdapter.Builder().
                        setEndpoint(URLS.COMMON_URL).
                        setExecutors(executorService, new MainThreadExecutor()).
                        setConverter(new GsonConverter(gson)).
                        build();
                MyRetrofitInterface<T> myretro=restadapter.create(MyRetrofitInterface.class);
                myretro.syncfiles(map,new Callback<T>() {
                    @Override
                    public void success(T t, Response response) {
                        try{
                            String value=new Gson().toJson(t);
                            System.out.println("value"+value);
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
                            boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
                            if(IsSucess){
                               if(folderid==stack.lastElement()){
                                   JsonObject ResponseData=jsonreturn.getAsJsonObject("ResponseData");
                                   JsonArray Table=ResponseData.getAsJsonArray("Table");
                                   if(Table.isJsonArray() && Table.size()>0){
                                       ArrayList<Integer> list=new ArrayList<Integer>();
                                       for(MyFilesDataModel mymodeldata:myfileslist){
                                           list.add(mymodeldata.getFileid());
                                       }
                                       for(int x=0;x<Table.size();x++){
                                           JsonObject object=Table.get(x).getAsJsonObject();
                                           if(object.get("status").getAsInt()==0){
                                               if(list.contains(object.get("FileId").getAsInt())){
                                                   continue;
                                               }
                                               else{
                                                   MyFilesDataModel model=new MyFilesDataModel();
                                                   model.setFileid(object.get("FileId").getAsInt());
                                                   if(object.get("Type")!=null){
                                                       model.setFiletype(object.get("Type").getAsString().trim());
                                                   }
                                                   else{
                                                       model.setFiletype("Unknown");
                                                   }
                                                   model.setFilename(object.get("FileName").getAsString().trim());
                                                   myfileslist.add(0,model);
                                               }
                                           }
                                           else if(object.get("status").getAsInt()==1){
                                               int fileid = object.get("FileId").getAsInt();
                                               Iterator<MyFilesDataModel> modell=myfileslist.iterator();
                                               while (modell.hasNext()){
                                                   MyFilesDataModel mymodel=modell.next();
                                                   if(mymodel.getFileid()==fileid){
                                                       modell.remove();
                                                   }
                                               }
                                           }

                                       }
                                   }
                                   JsonArray Table1=ResponseData.getAsJsonArray("Table1");
                                   if(Table1.isJsonArray() && Table1.size()>0){
                                       for(int i=0;i<Table1.size();i++){
                                           JsonObject obj=Table1.get(i).getAsJsonObject();
                                           String Description=obj.get("Description").getAsString();
                                           UIutill.generateNotification(getActivity(),Description);
                                       }
                                   }
                                   adapter=new MyFilesAdapter(getActivity(),myfileslist);
                                   lv_myfiles.setAdapter(adapter);
                               }

                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        System.out.println(CustomErrorHandling.ShowError(retrofitError, getActivity()));
                    }
                });
            }


        }
    }

    /********************************************************************************************************/
    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();

    }

   /********************************************************************************************************/

    @Override
    public void onStop() {
        if(executorService!=null){
            executorService.shutdownNow();
        }
        super.onStop();
    }
}
