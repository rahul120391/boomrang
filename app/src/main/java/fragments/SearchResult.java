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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import customviews.SwipeMenuLayout;
import customviews.SwipeMenuListView;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/21/2015.
 */
public class SearchResult<T> extends Fragment implements AdapterView.OnItemClickListener, DataTransferInterface<T>, View.OnClickListener, SwipeMenuListView.OnMenuItemClickListener {

    View v = null;
    TextView tv_foldername;
    ImageView iv_back;
    SwipeMenuListView lv_myfiles;
    SwipeMenuCreator creator;
    Stack<String> stack = new Stack<String>();
    Stack<String> foldernames = new Stack<>();
    MethodClass<T> methodclass;
    MyFilesAdapter adapter;
    String fileid;
    int position;    //0-temp delet file/folder, 1-get root file folders,  2-onbackpress in layoyt_foldername, 3-request file, 4-share file, 5-Download File
    String searchtext;
    String foldername;
    Dialog confirmdialog, requestfolder, sharedialog;
    ArrayList<MyFilesDataModel> mylist = new ArrayList<>();
    RelativeLayout layout_foldernames;
    RelativeLayout mainlayout,layout_bottom;
    TextView tv_total_files,tv_total,tv_total_folders;
    int parentid;
    int listviewindex;
    int listviewpositionclick = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            v = inflater.inflate(R.layout.fragment_search, null);
            layout_bottom=(RelativeLayout)v.findViewById(R.id.layout_bottom);
            layout_bottom.setVisibility(View.GONE);
            tv_total_folders=(TextView)v.findViewById(R.id.tv_total_folders);
            tv_total_files=(TextView)v.findViewById(R.id.tv_total_files);
            tv_total=(TextView)v.findViewById(R.id.tv_total);
            tv_total_folders.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_total_files.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_total.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            mainlayout = (RelativeLayout) v.findViewById(R.id.layout_main);
            mainlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(DashboardActivity.slidingpane.isOpen()){
                        DashboardActivity.slidingpane.closePane();
                    }
                    if (lv_myfiles.getCount() > 0) {
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
            tv_foldername = (TextView) v.findViewById(R.id.tv_foldername);
            iv_back = (ImageView) v.findViewById(R.id.iv_back);
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            iv_back.setOnClickListener(this);
            layout_foldernames = (RelativeLayout) v.findViewById(R.id.layout_foldernames);
            layout_foldernames.setVisibility(View.GONE);
            methodclass = new MethodClass<>(getActivity(), this);
            lv_myfiles = (SwipeMenuListView)v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnItemClickListener(this);
            lv_myfiles.setOnMenuItemClickListener(this);
            lv_myfiles.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                @Override
                public void onSwipeStart(int position) {
                    if (lv_myfiles.getCount() > 0) {
                        if (listviewpositionclick >= lv_myfiles.getFirstVisiblePosition()
                                && listviewpositionclick <= lv_myfiles.getLastVisiblePosition()) {
                            View view = lv_myfiles.getChildAt(listviewpositionclick - lv_myfiles.getFirstVisiblePosition());
                            if (view instanceof SwipeMenuLayout) {
                                ((SwipeMenuLayout) view).smoothCloseMenu();
                            }
                        }
                    }
                    listviewpositionclick=position;
                }
                @Override
                public void onSwipeEnd(int position) {
                }
            });
            lv_myfiles.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            if (savedInstanceState == null) {
                Bundle b = getArguments();
                parentid=b.getInt("parentid");
                searchtext = b.getString("searctext");
            }
            stack.clear();
            foldernames.clear();
            position = 1;
            fileid = searchtext;
            foldername = getString(R.string.search_result);
            if (methodclass.checkInternetConnection()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                map.put("searchText", searchtext);
                methodclass.MakeGetRequestWithParams(map, URLS.SEARCH_FILE_FOLDER);
            } else {
                UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
            }
            creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    int viewtype = menu.getViewType();
                    SwipeMenuItem item1 = new SwipeMenuItem(
                            getActivity());
                    item1.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                    item1.setWidth(UIutill.dp2px(60, getActivity()));
                    item1.setIcon(R.drawable.iv_delete);
                    menu.addMenuItem(item1);


                    if (!mylist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
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
                    if (mylist.get(viewtype).getFiletype().equalsIgnoreCase("folder")) {
                        SwipeMenuItem item4 = new SwipeMenuItem(
                                getActivity());
                        item4.setBackground(new ColorDrawable(getResources().getColor(R.color.login_box_bg)));
                        item4.setWidth(UIutill.dp2px(60, getActivity()));
                        item4.setIcon(R.drawable.iv_requestfile);
                        menu.addMenuItem(item4);
                    }
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positionn, long id) {
        listviewpositionclick = positionn;
        if (((MyFilesDataModel) parent.getItemAtPosition(positionn)).getFiletype().equalsIgnoreCase("folder")) {
            position = 1;
            fileid = ((MyFilesDataModel) parent.getItemAtPosition(positionn)).getFileid()+"";
            foldername = ((MyFilesDataModel) parent.getItemAtPosition(positionn)).getFilename();
            Map<String, String> map = new HashMap<>();
            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
            map.put("folderId", fileid + "");
            searchtext = fileid + "";
            parentid=((MyFilesDataModel) parent.getItemAtPosition(positionn)).getFileid();
            methodclass.MakeGetRequestWithParams(map, URLS.GET_ROOT_FOLDER_FILES);
        }

    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onSuccess(T s) {
        try {
            String value = new Gson().toJson(s);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonreturn = (JsonObject) jsonParser.parse(value);
            boolean IsSucess = jsonreturn.get("IsSucess").getAsBoolean();
            if (IsSucess) {
                if(position==0){
                    String message = jsonreturn.get("Message").getAsString().trim();
                    if (!message.equalsIgnoreCase("")) {
                        mylist.remove(listviewindex);
                        ArrayList<String> foldercount=new ArrayList<>();
                        for(MyFilesDataModel model:mylist){
                            if(model.getFiletype().equalsIgnoreCase("folder")){
                                foldercount.add(model.getFiletype());
                            }
                        }
                        Count(mylist,foldercount);
                        adapter=new MyFilesAdapter(getActivity(),mylist);
                        lv_myfiles.setAdapter(adapter);
                        UIutill.ShowSnackBar(getActivity(), message);
                        if(mylist.size()==0){
                            layout_bottom.setVisibility(View.GONE);
                        }
                        if(stack.size()==1 && mylist.size()==0){
                            layout_foldernames.setVisibility(View.GONE);
                        }
                    }
                    else{
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.delete_failed));
                    }
                }
                else if(position==1 || position==2){
                    if (jsonreturn.get("ResponseData")!=null && jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size() >= 0) {
                        JsonArray ResponseData = jsonreturn.get("ResponseData").getAsJsonArray();
                        mylist.clear();
                        ArrayList<String> foldercount=new ArrayList<>();
                        for (int i = 0; i < ResponseData.size(); i++) {
                            JsonObject object = ResponseData.get(i).getAsJsonObject();
                            MyFilesDataModel model = new MyFilesDataModel();
                            model.setFileid(object.get("FileID").getAsInt());
                            if (object.get("Type") != null) {
                                if(object.get("Type").getAsString().trim().equalsIgnoreCase("folder")){
                                    foldercount.add((object.get("Type").getAsString().trim()));
                                }
                                model.setFiletype(object.get("Type").getAsString().trim());
                            } else {
                                model.setFiletype("Unknown");
                            }

                            model.setFilepath(object.get("Path").getAsString().trim());
                            model.setFilename(object.get("FileName").getAsString().trim());
                            mylist.add(model);
                        }
                        Count(mylist,foldercount);
                        layout_foldernames.setVisibility(View.VISIBLE);
                        tv_foldername.setText(foldername);
                        if (position == 1) {
                            stack.push(searchtext);
                            foldernames.push(foldername);
                            if (mylist.size() == 0) {
                                UIutill.ShowSnackBar(getActivity(), getString(R.string.nofilefolder));
                            }
                        } else if (position == 2) {
                            stack.pop();
                            foldernames.pop();
                        }
                        if (stack.size() > 1) {
                            iv_back.setVisibility(View.VISIBLE);
                        } else {
                            iv_back.setVisibility(View.GONE);
                        }
                        if (mylist.size() > 0) {
                            lv_myfiles.setMenuCreator(creator);
                        }
                        tv_foldername.setText(foldernames.lastElement());
                        adapter = new MyFilesAdapter(getActivity(), mylist);
                        lv_myfiles.setAdapter(adapter);


                    } else {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.no_result));
                    }
                }
                else if(position==3 || position==4){
                    UIutill.ShowSnackBar(getActivity(), jsonreturn.get("ResponseData").getAsString().trim());
                }
                else if(position==5){
                    String fileurl = jsonreturn.get("ResponseData").getAsString();
                    String filename = jsonreturn.get("CallBack").getAsString();
                    DownloadFiles(filename, fileurl);
                }

            }
            else{
                UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("Message").getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onFailure(RetrofitError error) {
        if (error != null) {

            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("searchtext", searchtext);
        super.onSaveInstanceState(outState);
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                position = 2;
                if (stack.size() > 2) {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                    int index = stack.indexOf(stack.lastElement());
                    fileid = stack.get(index - 1);
                    map.put("folderId", fileid);
                    methodclass.MakeGetRequestWithParams(map, URLS.GET_ROOT_FOLDER_FILES);
                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                    map.put("searchText", stack.get(0));
                    methodclass.MakeGetRequestWithParams(map, URLS.SEARCH_FILE_FOLDER);
                }
                break;
        }
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public boolean onMenuItemClick(int positionn, SwipeMenu menu, int index) {
        listviewindex=positionn;
        String type = null;
        String filetype = mylist.get(positionn).getFiletype();
        if (filetype.equalsIgnoreCase("folder")) {
            type = "0";
        } else {
            type = "1";
        }
        String fileid = mylist.get(positionn).getFileid() + "";
        switch (index) {
            case 0:
                ShowConfirmDialog(fileid, type);
                break;
            case 1:
                if (type.equalsIgnoreCase("0")) {
                    String filenamee = mylist.get(positionn).getFilename();
                    ShareDialogView(filenamee, fileid, filetype);
                } else {
                    if (type.equalsIgnoreCase("1")) {
                        int state = getActivity().getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                            try {
                                Toast.makeText(getActivity(), "Enable Download Manager", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else {
                            String deviceId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                                    android.provider.Settings.Secure.ANDROID_ID);
                            Map<String, String> map = new HashMap<>();
                            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                            map.put("folderFileId", fileid);
                            map.put("type", "1");
                            map.put("deviceId", deviceId);
                            if (methodclass.checkInternetConnection()) {
                                position = 5;
                                methodclass.MakeGetRequestWithParams(map, URLS.DOWNLOAD);
                            } else {
                                UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                            }
                        }

                    }
                }
                break;
            case 2:
                if (type.equalsIgnoreCase("0")) {
                    String filename = mylist.get(positionn).getFilename();
                    RequestFolderView(fileid, filename);
                } else {
                    String filenamee = mylist.get(positionn).getFilename();
                    ShareDialogView(filenamee, fileid, filetype);
                }
                break;
            default:
                break;
        }
        return false;
    }

    /********************************************************************************************************/
    /**
     * this method is used to confirm file deletion
     *
     * @param fileid -pass fileid of the file to delete
     * @param type   -pass the type of the file to delete(0 for folder delete and 1 for file delete)
     */
    public void ShowConfirmDialog(final String fileid, final String type) {
        if (confirmdialog == null || !confirmdialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.confirmation_dialogview, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_title = (TextView) dialoglayout.findViewById(R.id.tv_title);
            tv_title.setText(getString(R.string.confirmation));
            TextView tv_message = (TextView) dialoglayout.findViewById(R.id.tv_message);
            if (type.equalsIgnoreCase("0")) {
                tv_message.setText(getString(R.string.delete_folder_message));
            } else if (type.equalsIgnoreCase("1")) {
                tv_message.setText(getString(R.string.delete_file_message));
            }
            Button btn_yes = (Button) dialoglayout.findViewById(R.id.btn_yes);
            Button btn_no = (Button) dialoglayout.findViewById(R.id.btn_no);

            tv_title.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            tv_message.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_no.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_yes.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            builder.setView(dialoglayout);
            confirmdialog = builder.create();
            confirmdialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
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
                    if (methodclass.checkInternetConnection()) {
                        position = 0;
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                        map.put("folderIdFileId", fileid);
                        map.put("deviceId",UIutill.getDeviceId(getActivity()));
                        map.put("type", type);
                        if(stack.size()==1){
                            map.put("currentFolderId",parentid+"");
                        }
                        else{
                            map.put("currentFolderId",stack.lastElement()+"");
                        }
                        methodclass.MakeGetRequestWithParams(map, URLS.TEMP_DELETE_FILE_FOLDER);
                    } else {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                    }
                }
            });
        }
    }
    /*********************************************************************************************************************/
    /**
     * this method is used to show share dilaog
     *
     * @param filename -filename to share
     * @param fileid   -fileid of that particluar file
     * @param type     -type of that particular file
     */
    public void ShareDialogView(final String filename, final String fileid, final String type) {
        if (sharedialog == null || !sharedialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.sharefolder_dialogview, null);
           // final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView tv_share_file_folder = (TextView) dialoglayout.findViewById(R.id.tv_share_file_folder);
            tv_share_file_folder.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_email = (TextView) dialoglayout.findViewById(R.id.tv_email);
            tv_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            final TextView et_email = (TextView) dialoglayout.findViewById(R.id.et_email);
            et_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_message = (TextView) dialoglayout.findViewById(R.id.tv_message);
            tv_message.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            final EditText et_message = (EditText) dialoglayout.findViewById(R.id.et_message);
            et_message.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            Button btn_share = (Button) dialoglayout.findViewById(R.id.btn_share);
            btn_share.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            Button btn_cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
            btn_cancel.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));


            //builder.setView(dialoglayout);
            //sharedialog = builder.create();

            //sharedialog.getWindow().getAttributes().windowAnimations = R.style.MyAnim_SearchWindow;
            sharedialog=new Dialog(getActivity(),R.style.DialogFragmentStyle);
            sharedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            sharedialog.setContentView(dialoglayout);
            sharedialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            sharedialog.getWindow().getAttributes().windowAnimations = R.style.MyAnim_SearchWindow;
            sharedialog.setCancelable(true);
            sharedialog.show();


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIutill.HideDialogKeyboard(getActivity(), v);
                    sharedialog.dismiss();
                }
            });

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIutill.HideDialogKeyboard(getActivity(), v);
                    if (et_email.getText().toString().trim().length() == 0) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.email_empty));
                    } else {
                        String emails[] = et_email.getText().toString().trim()
                                .split(",");
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < emails.length; i++)
                        {
                            if (!emails[i].trim().matches(
                                    Patterns.EMAIL_ADDRESS.pattern())
                                    ) {
                                UIutill.ShowSnackBar(getActivity(), getString(R.string.valied_Email));
                                return;
                            }
                        else {

                                builder.append(emails[i].trim());
                                if (i != emails.length - 1) {
                                    builder.append(",");
                                }
                            }
                        }
                        if (methodclass.checkInternetConnection()) {
                            try {
                                sharedialog.dismiss();
                                position = 4;
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("emailIds", builder.toString());
                                map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                                if(et_message.getText().toString().trim().length()==0){
                                    map.put("Message"," ");
                                }
                                else{
                                    map.put("Message", et_message.getText().toString().trim());
                                }
                                map.put("type", type);
                                map.put("fileName", filename);
                                map.put("fileId", fileid);
                                methodclass.MakeGetRequestWithParams(map, URLS.SHARE_FILE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                        }
                    }

                }
            });
        }
    }

    /**********************************************************************************************************************/

    /**
     * this method is used to request a folder
     *
     * @param folderid   -folder of the folder to request
     * @param foldername -name of the folder
     */
    public void RequestFolderView(final String folderid, final String foldername) {
        if (requestfolder == null || !requestfolder.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View dialoglayout = inflater.inflate(R.layout.requestfile_dialogview, null);
            //final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Button btn_cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
            btn_cancel.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            Button btn_request = (Button) dialoglayout.findViewById(R.id.btn_request);
            btn_request.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            TextView tv_requestfolder = (TextView) dialoglayout.findViewById(R.id.tv_requestfolder);
            tv_requestfolder.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_email = (TextView) dialoglayout.findViewById(R.id.tv_email);
            tv_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            final EditText et_email = (EditText) dialoglayout.findViewById(R.id.et_email);
            et_email.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_message = (TextView) dialoglayout.findViewById(R.id.tv_message);
            tv_message.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            TextView tv_expiry = (TextView) dialoglayout.findViewById(R.id.tv_expiry);
            tv_expiry.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            final EditText et_message = (EditText) dialoglayout.findViewById(R.id.et_message);
            et_message.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));

            final Spinner sp_select_expiry = (Spinner) dialoglayout.findViewById(R.id.sp_select_expiry);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.expiry_value_array)) {
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
                    v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    v.setTextColor(getResources().getColor(R.color.search_box_txtclr));
                    return v;
                }

                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
                    v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    v.setTextColor(getResources().getColor(R.color.search_box_txtclr));
                    return v;
                }
            };
            sp_select_expiry.setAdapter(adapter);
           // builder.setView(dialoglayout);
            //requestfolder = builder.create();
            //requestfolder.getWindow().getAttributes().windowAnimations = R.style.MyAnim_SearchWindow;
           // requestfolder.show();
            requestfolder=new Dialog(getActivity(),R.style.DialogFragmentStyle);
            requestfolder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            requestfolder.setContentView(dialoglayout);
            requestfolder.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            requestfolder.getWindow().getAttributes().windowAnimations = R.style.MyAnim_SearchWindow;
            requestfolder.setCancelable(true);
            requestfolder.show();

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIutill.HideDialogKeyboard(getActivity(), v);
                    requestfolder.dismiss();
                }
            });
            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (et_email.getText().toString().trim().length() == 0) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.email_empty));
                    } else if (!et_email.getText().toString().trim()
                            .matches(Patterns.EMAIL_ADDRESS.pattern())) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.valied_Email));
                    } else {
                        if (methodclass.checkInternetConnection()) {
                            requestfolder.dismiss();
                            position = 3;
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                            map.put("requestedFolderId", folderid);
                            map.put("requestedFolderName", foldername);
                            switch (sp_select_expiry.getSelectedItemPosition()) {
                                case 0:
                                    map.put("expiryInDays", "0");
                                    break;
                                case 1:
                                    map.put("expiryInDays", "1");
                                    break;
                                case 2:
                                    map.put("expiryInDays", "7");
                                    break;
                                default:
                                    break;
                            }
                            map.put("emailId", et_email.getText().toString().trim());
                            if(et_message.getText().toString().trim().length()==0){
                                map.put("Message"," ");
                            }
                            else{
                                map.put("Message", et_message.getText().toString().trim());
                            }
                            methodclass.MakeGetRequestWithParams(map, URLS.REQUEST_FILE);
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                        }

                    }
                }
            });
        }
    }
    /*********************************************************************************************************************************/
    /**
     * this method is used to download files using default android download manager
     *
     * @param filename -name of the file to download
     * @param url      -url from where the file to download
     */
    public void DownloadFiles(String filename, String url) {
        int state = getActivity().getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            try {
                Toast.makeText(getActivity(), "Enable Download Manager", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {
            UIutill.ShowSnackBar(getActivity(), getString(R.string.download_start));
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File directory = new File(path + "/" + "MyData");
            if (!directory.exists()) {
                directory.mkdir();
            }
            String mypath = path + "/" + "MyData" + "/" + filename;
            File file = new File(mypath);
            if (file.exists()) {
                file.delete();
            }
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(filename);
            request.setDescription(getString(R.string.downloading));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setDestinationInExternalPublicDir("/MyData", filename);
            manager.enqueue(request);
        }
    }
    /*********************************************************************************************************/
    /**
     * this method is used to count the files, folders and their total
     * @param myfileslist
     * -the list containing files and folders
     * @param foldercount
     * -the list containing folders
     */
    public void Count(ArrayList<MyFilesDataModel> myfileslist,ArrayList<String> foldercount){
        if(myfileslist.size()>0){
            layout_bottom.setVisibility(View.VISIBLE);
            tv_total.setText(myfileslist.size()+"");
            if(foldercount.size()==1){
                tv_total_folders.setText("Folder"+": "+foldercount.size());
            }
            else{
                tv_total_folders.setText("Folders"+": "+foldercount.size());
            }
            if(myfileslist.size()-foldercount.size()==1){
                tv_total_files.setText("File"+": "+(myfileslist.size()-foldercount.size()));
            }
            else{
                tv_total_files.setText("Files"+": "+(myfileslist.size()-foldercount.size()));
            }
        }
        else{
            layout_bottom.setVisibility(View.GONE);
        }
    }
}
