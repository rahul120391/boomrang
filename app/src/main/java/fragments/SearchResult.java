package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Stack;

import Boomerang.R;
import adapters.MyFilesAdapter;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.MySingletonclass;
import modelclasses.MyFilesDataModel;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/21/2015.
 */
public class SearchResult<T> extends Fragment implements AdapterView.OnItemClickListener, DataTransferInterface<T>{

    View v=null;
    TextView tv_foldername,tv_back;
    ListView lv_myfiles;
    Stack<Integer> stack=new Stack<>();
    MethodClass<T> methodclass;
    int  fileid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_search,null);
            methodclass=new MethodClass<>(getActivity(),this);
            lv_myfiles=(ListView)v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnItemClickListener(this);
            tv_foldername.setText(MySingletonclass.getobject().getSearchstring());
            ArrayList<MyFilesDataModel> list=MySingletonclass.getobject().getList();
            MyFilesAdapter adapter=new MyFilesAdapter(getActivity(),list);
            lv_myfiles.setAdapter(adapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fileid=((MyFilesDataModel)parent.getItemAtPosition(position)).getFileid();

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

         }
         catch (Exception e){
             e.printStackTrace();
         }
    }

    @Override
    public void onFailure(RetrofitError error) {

    }
}
