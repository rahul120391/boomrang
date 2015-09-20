package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Boomerang.R;
import adapters.FileArrayAdapter;
import commonutils.UnCaughtException;
import modelclasses.Item;

public class FileChooser extends Activity {

    ListView lv_files;
	private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filechooser);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(FileChooser.this));
        lv_files=(ListView)findViewById(R.id.lv_files);
        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item o = adapter.getItem(position);
                if(o.getImage().equalsIgnoreCase("ic_folder")||o.getImage().equalsIgnoreCase("directory_up")){
                    currentDir = new File(o.getPath());
                    fill(currentDir);
                }
                else
                {
                    onFileClick(o);
                }
            }
        });

        currentDir = new File("/sdcard/");
        fill(currentDir); 
    }

    /**************************************************************************************************************************/

    private void fill(File f)
    {
       	File[]dirs = f.listFiles();
		this.setTitle("Current Dir: "+f.getName());
		List<Item>dir = new ArrayList<Item>();
		List<Item>fls = new ArrayList<Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"ic_folder"));
				}
				else
				{
                    if(ff.getAbsolutePath().endsWith("jpg") || ff.getAbsolutePath().endsWith("jpeg") ||
                            ff.getAbsolutePath().endsWith("png") ||
                            ff.getAbsolutePath().endsWith("gif") ||
                            ff.getAbsolutePath().endsWith("bmp") ||
                            ff.getAbsolutePath().endsWith("ico")||
                            ff.getAbsolutePath().endsWith("cdr")||
                            ff.getAbsolutePath().endsWith("tif") ||
                            ff.getAbsolutePath().endsWith("tiff")){

                    fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_image"));
				   }
                    else if(ff.getAbsolutePath().endsWith("txt")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_txt"));
                    }
                    else if(ff.getAbsolutePath().endsWith("xls")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_xls"));
                    }
                    else if(ff.getAbsolutePath().endsWith("xlsx")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_xlsx"));
                    }
                    else if(ff.getAbsolutePath().endsWith("doc") || ff.getAbsolutePath().endsWith("docx")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_docx"));
                    }
                    else if(ff.getAbsolutePath().endsWith("ppt")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_ppt"));
                    }
                    else if(ff.getAbsolutePath().endsWith("pptx")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_pptx"));
                    }
                    else if(ff.getAbsolutePath().endsWith("pdf")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_pdf"));
                    }
                    else if(ff.getAbsolutePath().endsWith("xml")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_xml"));
                    }
                    else if(ff.getAbsolutePath().endsWith("csv")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_csv"));
                    }
                    else if(ff.getAbsolutePath().endsWith("odt")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_odt"));
                    }
                    else if(ff.getAbsolutePath().endsWith("xla")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_xla"));
                    }
                    else if(ff.getAbsolutePath().endsWith("accdb")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_accdb"));
                    }
                    else if(ff.getAbsolutePath().endsWith("pub")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_pub"));
                    }
                    else if(ff.getAbsolutePath().endsWith("jnt")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_jnt"));
                    }
                    else if(ff.getAbsolutePath().endsWith("rtf")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_rtf"));
                    }
                    else if(ff.getAbsolutePath().endsWith("psd")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_psd"));
                    }
                    else if(ff.getAbsolutePath().endsWith("rar")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_rar"));
                    }
                    else if(ff.getAbsolutePath().endsWith("zip")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_zip"));
                    }
                    else if(ff.getAbsolutePath().endsWith("jar")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_jar"));
                    }
                    else if(ff.getAbsolutePath().endsWith("tar")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_tar"));
                    }
                    else if(ff.getAbsolutePath().endsWith("mp4") || ff.getAbsolutePath().endsWith("3gpp") || ff.getAbsolutePath().endsWith("avi") ||  ff.getAbsolutePath().endsWith("mkv")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_avi"));
                    }
                    else if(ff.getAbsolutePath().endsWith("mp3") || ff.getAbsolutePath().endsWith("mpeg") || ff.getAbsolutePath().endsWith("wmv") || ff.getAbsolutePath().endsWith("swf")  || ff.getAbsolutePath().endsWith("ogg") || ff.getAbsolutePath().endsWith("wav") || ff.getAbsolutePath().endsWith("wma"))
                    {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_mp3"));
                    }
                    else if(ff.getAbsolutePath().endsWith("aspx")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_aspx"));
                    }
                    else if(ff.getAbsolutePath().endsWith("vb")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_vb"));
                    }
                    else if(ff.getAbsolutePath().endsWith("cs")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_cs"));
                    }
                    else if(ff.getAbsolutePath().endsWith("js")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_js"));
                    }
                    else if(ff.getAbsolutePath().endsWith("css")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_css"));
                    }
                    else if(ff.getAbsolutePath().endsWith("htm")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_htm"));
                    }
                    else if(ff.getAbsolutePath().endsWith("html")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_html"));
                    }
                    else if(ff.getAbsolutePath().endsWith("bak")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_bak"));
                    }
                    else if(ff.getAbsolutePath().endsWith("ttf")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_ttf"));
                    }
                    else if(ff.getAbsolutePath().endsWith("apk")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_apk"));
                    }
                    else if(ff.getAbsolutePath().endsWith("ipa")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_ipa"));
                    }
                    else if(ff.getAbsolutePath().endsWith("bat")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_bat"));
                    }
                    else if(ff.getAbsolutePath().endsWith("exe")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_exe"));
                    }
                    else if(ff.getAbsolutePath().endsWith("dll")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_dll"));
                    }
                    else if(ff.getAbsolutePath().endsWith("iso")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_iso"));
                    }
                    else if(ff.getAbsolutePath().endsWith("nrg")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_nrg"));
                    }
                    else if(ff.getAbsolutePath().endsWith("msi")){
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_msi"));
                    }
                    else{
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "ic_default"));
                    }
             }

			 }
		 }catch(Exception e)
		 {    
			 e.printStackTrace();
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
         if(!f.getName().equalsIgnoreCase("sdcard"))
	     dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
         adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
         lv_files.setAdapter(adapter);
    }
    /**************************************************************************************************************************/

    private void onFileClick(Item o)
    {
    	//Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();

        o.getPath();

    	Intent intent = new Intent();
        intent.putExtra("GetPath",o.getPath());
        intent.putExtra("GetFileName",o.getName());
        intent.putExtra("image",o.getImage());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
    /**************************************************************************************************************************/
}
