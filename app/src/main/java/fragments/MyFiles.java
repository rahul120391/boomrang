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

import java.util.ArrayList;

import Boomerang.R;
import activities.DashboardActivity;
import adapters.MyFilesAdapter;
import commonutils.UIutill;
import modelclasses.MyFilesDataModel;

/**
 * Created by rahul on 3/11/2015.
 */
public class MyFiles extends Fragment implements View.OnClickListener{
    View v=null;
    RelativeLayout layout_myfiles,layout_search,layout_refresh,layout_upload;
    //TextView tv_myfiles,tv_search,tv_refresh,tv_upload;
    ListView lv_myfiles;
    ArrayList<MyFilesDataModel> myfileslist=new ArrayList<>();
    AlertDialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_myfiles,null);
            layout_myfiles=(RelativeLayout)v.findViewById(R.id.layout_myfiles);
            layout_search=(RelativeLayout)v.findViewById(R.id.layout_search);
            layout_refresh=(RelativeLayout)v.findViewById(R.id.layout_refresh);
            layout_upload=(RelativeLayout)v.findViewById(R.id.layout_upload);
            layout_myfiles.setOnClickListener(this);
            layout_search.setOnClickListener(this);
            layout_refresh.setOnClickListener(this);
            layout_upload.setOnClickListener(this);
            //tv_myfiles=(TextView)v.findViewById(R.id.tv_myfiles);
            lv_myfiles=(ListView)v.findViewById(R.id.lv_myfiles);
            View footer_view=getActivity().getLayoutInflater().inflate(R.layout.listview_header,null);
            lv_myfiles.addHeaderView(footer_view);
            for(int i=0;i<20;i++){
                MyFilesDataModel model=new MyFilesDataModel();
                if(i<10){
                    model.setImages(R.drawable.ic_folder);
                    model.setName("My Folder"+i);
                }
                else if(i>=10 && i<=15){
                    model.setImages(R.drawable.ic_image);
                    model.setName("Image"+i+".jpg");
                }
                else{
                    model.setImages(R.drawable.ic_video);
                    model.setName("video"+i+".3gp");
                }
               myfileslist.add(model);
            }
            MyFilesAdapter adapter=new MyFilesAdapter(getActivity(),myfileslist);
            lv_myfiles.setAdapter(adapter);
            //Set Font
        //    tv_myfiles.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
         //   tv_search.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
         //   tv_refresh.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
         //   tv_upload.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
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
            final AlertDialog dialog=builder.create();
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
}
