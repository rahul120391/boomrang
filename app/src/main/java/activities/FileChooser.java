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

public class FileChooser extends Activity {

    ListView lv_files;
	private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filechooser);
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
					
					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"ic_myfiles"));
				}
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
        lv_files.setAdapter(adapter);
    }

    private void onFileClick(Item o)
    {
    	//Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent();
        intent.putExtra("GetPath",o.getPath());
        intent.putExtra("GetFileName",o.getName());
        intent.putExtra("image",o.getImage());
        setResult(RESULT_OK, intent);
        finish();
    }
}
