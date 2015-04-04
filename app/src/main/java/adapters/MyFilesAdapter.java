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

    public MyFilesAdapter(Context context, ArrayList<MyFilesDataModel> myfileslist) {
        this.context = context;
        this.myfileslist = myfileslist;
        inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myfileslist.size();
    }

    @Override
    public MyFilesDataModel getItem(int position) {
        return myfileslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        if(myfileslist.size()>0){
            return myfileslist.size();
        }
        else{
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.myfiles_folder_rowitem, null);
        }
        iv_file_folder = (ImageView) convertView.findViewById(R.id.iv_file_folder);
        tv_file_folder = (TextView) convertView.findViewById(R.id.tv_file_folder);
        tv_file_folder.setTypeface(UIutill.SetFont(context, "segoeuilght.ttf"));
        tv_file_folder.setText(myfileslist.get(position).getFilename());
        if (myfileslist.get(position).getFiletype().equalsIgnoreCase("folder")) {
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder));
        } else if (myfileslist.get(position).getFiletype().equalsIgnoreCase("image")) {
           iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_image));
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("media"))
        {
            if(myfileslist.get(position).getFilename().endsWith("mp4")|| myfileslist.get(position).getFilename().endsWith("avi") || myfileslist.get(position).getFilename().endsWith("3gpp")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avi));
            }
            else{
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp3));
            }

        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("document")){
            if(myfileslist.get(position).getFilename().endsWith("txt")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
            }
            else if(myfileslist.get(position).getFilename().endsWith("xla")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xla));
            }
            else if(myfileslist.get(position).getFilename().endsWith("accdb")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_accbd));
            }
            else if(myfileslist.get(position).getFilename().endsWith("pub")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pub));
            }
            else if(myfileslist.get(position).getFilename().endsWith("jnt")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_jnt));
            }
            else if(myfileslist.get(position).getFilename().endsWith("xml")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml));
            }
            else if(myfileslist.get(position).getFilename().endsWith("xls")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
            }
            else if(myfileslist.get(position).getFilename().endsWith("xlsx")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xlsx));
            }
            else if(myfileslist.get(position).getFilename().endsWith("doc") || myfileslist.get(position).getFilename().endsWith("docx")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_docx));
            }
            else if(myfileslist.get(position).getFilename().endsWith("ppt")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt));
            }
            else if(myfileslist.get(position).getFilename().endsWith("pptx")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pptx));
            }
            else if(myfileslist.get(position).getFilename().endsWith("pdf")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
            }
            else if(myfileslist.get(position).getFilename().endsWith("psd")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_psd));
            }
            else if(myfileslist.get(position).getFilename().endsWith("odt")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_odt));
            }
            else if(myfileslist.get(position).getFilename().endsWith("rtf")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rtf));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("android")){
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_apk));
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("Assembly")){
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dll));
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("batch")){
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bat));
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("code")){
            if(myfileslist.get(position).getFilename().endsWith("js")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_js));
            }
            else if(myfileslist.get(position).getFilename().endsWith("css")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_css));
            }
            else if(myfileslist.get(position).getFilename().endsWith("aspx")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aspx));
            }
            else if(myfileslist.get(position).getFilename().endsWith("vb")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vb));
            }
            else if(myfileslist.get(position).getFilename().endsWith("cs")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cs));
            }

        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("archive")){
            if(myfileslist.get(position).getFilename().endsWith("zip")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip));
            }
            else if(myfileslist.get(position).getFilename().endsWith("rar")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar));
            }
            else if(myfileslist.get(position).getFilename().endsWith("jar")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_jar));
            }
            else if(myfileslist.get(position).getFilename().endsWith("tar")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tar));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("html")){
            if(myfileslist.get(position).getFilename().endsWith("htm")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
            }
            else if(myfileslist.get(position).getFilename().endsWith("html")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("iphone")){
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ipa));
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("ISOImage")){
            if(myfileslist.get(position).getFilename().endsWith("iso")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_iso));
            }
            else if(myfileslist.get(position).getFilename().endsWith("nrg")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_nrg));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("file")){
            if(myfileslist.get(position).getFilename().endsWith("bak")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bak));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("font")){
            if(myfileslist.get(position).getFilename().endsWith("ttf")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ttf));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("executable")){
            if(myfileslist.get(position).getFilename().endsWith("exe")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_exe));
            }
        }
        else if(myfileslist.get(position).getFiletype().equalsIgnoreCase("Windows Installer")){
            if(myfileslist.get(position).getFilename().endsWith("msi")){
                iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_msi));
            }
        }
        else{
            iv_file_folder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default));
        }
        return convertView;
    }
}
