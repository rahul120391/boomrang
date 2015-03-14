package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Boomerang.R;
import commonutils.UIutill;
import modelclasses.MyFilesDataModel;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFilesAdapter extends BaseAdapter {

    ArrayList<MyFilesDataModel> myfileslist;
    Context context;
    ImageView iv_file_folder;
    TextView tv_file_folder;
    LayoutInflater inflator;
    public MyFilesAdapter(Context context,ArrayList<MyFilesDataModel> myfileslist){
        this.context=context;
        this.myfileslist=myfileslist;
        inflator=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return myfileslist.size();
    }

    @Override
    public Object getItem(int position) {
        return myfileslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflator.inflate(R.layout.myfiles_folder_rowitem,null);
        }
        iv_file_folder=(ImageView)convertView.findViewById(R.id.iv_file_folder);
        tv_file_folder=(TextView)convertView.findViewById(R.id.tv_file_folder);
        tv_file_folder.setTypeface(UIutill.SetFont(context, "segoeuilght.ttf"));
        iv_file_folder.setImageResource(myfileslist.get(position).getImages());
        tv_file_folder.setText(myfileslist.get(position).getName());
        return convertView;
    }
}
