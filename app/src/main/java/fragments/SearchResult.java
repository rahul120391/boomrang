package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import adapters.MyFilesAdapter;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.UIutill;
import commonutils.URLS;
import customviews.SwipeMenu;
import customviews.SwipeMenuListView;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/21/2015.
 */
public class SearchResult<T> extends Fragment implements AdapterView.OnItemClickListener, DataTransferInterface<T>,View.OnClickListener,SwipeMenuListView.OnMenuItemClickListener{

    View v=null;
    TextView tv_foldername,tv_back;
    ListView lv_myfiles;
    Stack<String> stack=new Stack<String>();
    Stack<String> foldernames=new Stack<>();
    MethodClass<T> methodclass;
    MyFilesAdapter adapter;
    String  fileid;
    int position;
    String searchtext;
    String foldername;
    ArrayList<MyFilesDataModel> mylist=new ArrayList<>();
    RelativeLayout layout_foldernames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_search,null);
            tv_foldername=(TextView)v.findViewById(R.id.tv_foldername);
            tv_back=(TextView)v.findViewById(R.id.tv_back);
            tv_foldername.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_back.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            tv_back.setOnClickListener(this);
            layout_foldernames=(RelativeLayout)v.findViewById(R.id.layout_foldernames);
            layout_foldernames.setVisibility(View.GONE);
            methodclass=new MethodClass<>(getActivity(),this);
            lv_myfiles=(ListView)v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnItemClickListener(this);

            if(savedInstanceState==null){
                Bundle b=getArguments();
                searchtext=b.getString("searctext");
            }
            else{
                System.out.println("inside saved instance");
                searchtext=savedInstanceState.getString("searchtext");
            }
            stack.clear();
            foldernames.clear();
            position=1;
            fileid=searchtext;
            foldername=getString(R.string.myfiles);
            if(methodclass.checkInternetConnection()){
                Map<String,String> map=new HashMap<String,String>();
                map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                map.put("searchText",searchtext);
                methodclass.MakeGetRequestWithParams(map, URLS.SEARCH_FILE_FOLDER);
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
    public void onItemClick(AdapterView<?> parent, View view, int positionn, long id) {
        if(((MyFilesDataModel)parent.getItemAtPosition(positionn)).getFiletype().equalsIgnoreCase("folder")){
            position=1;
            fileid=((MyFilesDataModel)parent.getItemAtPosition(positionn)).getFileid()+"";
            foldername=((MyFilesDataModel)parent.getItemAtPosition(positionn)).getFilename();
            Map<String,String> map=new HashMap<>();
            map.put("userId", getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
            map.put("folderId",fileid+"");
            searchtext=fileid+"";
            System.out.println("searchtext"+searchtext);
            methodclass.MakeGetRequestWithParams(map,URLS.GET_ROOT_FOLDER_FILES);
        }

    }

    @Override
    public void onSuccess(T s)
    {
         try{
             String value=new Gson().toJson(s);
             System.out.println("value"+value);
             JsonParser jsonParser = new JsonParser();
             JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
             boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
             if(IsSucess) {
                 if (jsonreturn.get("ResponseData").isJsonArray() && jsonreturn.get("ResponseData").getAsJsonArray().size() >= 0) {
                     JsonArray ResponseData = jsonreturn.get("ResponseData").getAsJsonArray();
                     System.out.println("response" + ResponseData);
                     mylist.clear();
                     for (int i = 0; i < ResponseData.size(); i++) {
                         JsonObject object = ResponseData.get(i).getAsJsonObject();
                         MyFilesDataModel model = new MyFilesDataModel();
                         model.setFileid(object.get("FileID").getAsInt());
                         if (object.get("Type") != null) {
                             model.setFiletype(object.get("Type").getAsString());
                         } else {
                             model.setFiletype("Unknown");
                         }

                         model.setFilepath(object.get("Path").getAsString());
                         model.setFilename(object.get("FileName").getAsString());
                         mylist.add(model);
                     }
                     layout_foldernames.setVisibility(View.VISIBLE);

                         tv_foldername.setText(foldername);
                         if(position==1){
                             stack.push(searchtext);
                             foldernames.push(foldername);
                         }
                         else if(position==2){
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
                          adapter=new MyFilesAdapter(getActivity(),mylist);
                          lv_myfiles.setAdapter(adapter);
                 }
                 else{
                     UIutill.ShowSnackBar(getActivity(),getString(R.string.no_result));
                 }
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("searchtext",searchtext);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                position=2;
                if(stack.size()>2){
                    System.out.println("insid eposition 2");
                    Map<String,String> map=new HashMap<>();
                    map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                    int index=stack.indexOf(stack.lastElement());
                    fileid=stack.get(index-1);
                    System.out.println("folderid"+fileid);
                    map.put("folderId",fileid);
                    methodclass.MakeGetRequestWithParams(map, URLS.GET_ROOT_FOLDER_FILES);
                }
                else{
                    Map<String,String> map=new HashMap<String,String>();
                    map.put("userId",getActivity().getSharedPreferences("Login",0).getString("UserID",""));
                    map.put("searchText",stack.get(0));
                    methodclass.MakeGetRequestWithParams(map, URLS.SEARCH_FILE_FOLDER);
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        return false;
    }
}
