package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import Boomerang.R;
import commonutils.RequestCodes;
import commonutils.UIutill;
import modelclasses.GalleryDataModel;

public class CustomGalleryActivity extends Activity implements View.OnClickListener{

    ListView lv_files;
    Button btn_done;
    TextView tv_nofiles;
    ArrayList<GalleryDataModel> files_list=new ArrayList<>();
    Bundle statesave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statesave=savedInstanceState;
        setContentView(R.layout.activity_custom_gallery);
        lv_files=(ListView)findViewById(R.id.lv_files);
        btn_done=(Button)findViewById(R.id.btn_done);
        btn_done.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));
        btn_done.setOnClickListener(this);
        tv_nofiles=(TextView)findViewById(R.id.tv_nofiles);
        tv_nofiles.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));
        ArrayList<GalleryDataModel> datalist;
        if(savedInstanceState==null){
            if(getIntent().getStringExtra("value").equalsIgnoreCase("images")){
                  datalist=GetImageData();
                if(datalist.size()==0){
                     btn_done.setVisibility(View.GONE);
                     tv_nofiles.setText(getString(R.string.no_image_avail));
                     tv_nofiles.setVisibility(View.VISIBLE);
                     lv_files.setVisibility(View.GONE);
                }
                else{
                    MyAdapter adapter=new MyAdapter(this,datalist,1);
                    lv_files.setAdapter(adapter);
                }
            }
            else if(getIntent().getStringExtra("value").equalsIgnoreCase(
                    "videos")){
                datalist=GetVideoData();
                if(datalist.size()==0){
                    btn_done.setVisibility(View.GONE);
                    tv_nofiles.setText(getString(R.string.no_video_avail));
                    tv_nofiles.setVisibility(View.VISIBLE);
                    lv_files.setVisibility(View.GONE);
                }
                else{
                    MyAdapter adapter=new MyAdapter(this,datalist,2);
                    lv_files.setAdapter(adapter);
                }
            }
        }
        else{
           if(savedInstanceState.getString("value").equals("images")){
               datalist=GetImageData();
               if(datalist.size()==0){
                   btn_done.setVisibility(View.GONE);
                   tv_nofiles.setText(getString(R.string.no_image_avail));
                   tv_nofiles.setVisibility(View.VISIBLE);
                   lv_files.setVisibility(View.GONE);
               }
               else{
                   MyAdapter adapter=new MyAdapter(this,datalist,1);
                   lv_files.setAdapter(adapter);
               }
           }
            else if(getIntent().getStringExtra("value").equalsIgnoreCase(
                   "videos")){
               datalist=GetVideoData();
               if(datalist.size()==0){
                   btn_done.setVisibility(View.GONE);
                   tv_nofiles.setText(getString(R.string.no_video_avail));
                   tv_nofiles.setVisibility(View.VISIBLE);
                   lv_files.setVisibility(View.GONE);
               }
               else{
                   MyAdapter adapter=new MyAdapter(this,datalist,2);
                   lv_files.setAdapter(adapter);
               }
           }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("value",getIntent().getStringExtra("value"));
        super.onSaveInstanceState(outState);
    }

    public ArrayList<GalleryDataModel> GetImageData(){
        ArrayList<GalleryDataModel> data = new ArrayList<GalleryDataModel>();
        String projection[] = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,MediaStore.Images.Media.MIME_TYPE};
        Cursor cr = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if(cr!=null){
           while(cr.moveToNext()){
               String path=cr.getString(0);
               String title=cr.getString(1);
               String mimetype=cr.getString(2);
               GalleryDataModel model=new GalleryDataModel();
               model.setFilemimetype(mimetype);
               model.setFiletitle(title);
               model.setImage_path(path);
               data.add(model);
           }
        }
        return data;
    }

    public ArrayList<GalleryDataModel> GetVideoData(){
        ArrayList<GalleryDataModel> data = new ArrayList<GalleryDataModel>();
        String projection[] = { MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,MediaStore.Video.Media.MIME_TYPE};
        Cursor cr = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cr != null) {
            while (cr.moveToNext()) {
                GalleryDataModel model=new GalleryDataModel();
                String path=cr.getString(0);
                String title=cr.getString(1);
                String mimetype=cr.getString(2);
                model.setVideo_path(path);
                model.setFiletitle(title);
                model.setFilemimetype(mimetype);
                data.add(model);
            }
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_done:
                try{
                    if(files_list.size()==0){
                         UIutill.ShowSnackBar(CustomGalleryActivity.this,getString(R.string.select_file));
                    }
                    else if(files_list.size()>5){
                         UIutill.ShowSnackBar(CustomGalleryActivity.this,getString(R.string.file_max_limit));
                    }
                    else{
                        Intent intent=new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list",files_list);
                        intent.putExtra("bundle",bundle);
                        if(statesave==null){
                            if (getIntent().getStringExtra("value").equalsIgnoreCase("images")){
                                setResult(RequestCodes.REQUEST_IMAGE,intent);
                            }
                            else if(getIntent().getStringExtra("value").equalsIgnoreCase(
                                    "videos")){
                                setResult(RequestCodes.REQUEST_VIDEO,intent);
                            }
                        }
                        else if(statesave!=null){
                            if (statesave.getString("value").equalsIgnoreCase("images")){
                                setResult(RequestCodes.REQUEST_IMAGE,intent);
                            }
                            else if(statesave.getString("value").equalsIgnoreCase(
                                    "videos")){
                                setResult(RequestCodes.REQUEST_VIDEO,intent);
                            }
                        }
                        finish();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    class MyAdapter extends BaseAdapter{

       ArrayList<GalleryDataModel> mylist;
       Context cnt;
       AQuery aq;
       LayoutInflater inflater;
       ImageView iv_image;
       TextView tv_name;
       CheckBox ch_check;
       boolean state[];
       int poss;
       public MyAdapter(Context cnt,ArrayList<GalleryDataModel> mylist,int poss){
           this.cnt=cnt;
           this.poss=poss;
           this.mylist=mylist;
           inflater=LayoutInflater.from(cnt);
           aq=new AQuery(cnt);
           state=new boolean[mylist.size()];
       }
       @Override
       public int getCount() {
           return mylist.size();
       }

       @Override
       public Object getItem(int position) {
           return mylist.get(position);
       }

       @Override
       public long getItemId(int position) {
           return position;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           try{
               if(convertView==null){
                 convertView=inflater.inflate(R.layout.gallerylist_row_item,null);
               }
               iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
               tv_name=(TextView)convertView.findViewById(R.id.tv_name);
               tv_name.setTypeface(UIutill.SetFont(cnt,"segoeuilght.ttf"));
               ch_check=(CheckBox)convertView.findViewById(R.id.ch_check);
               ch_check.setTag(position);
               if(poss==1){
                   aq.id(iv_image)
                           .image(mylist.get(position).getImage_path(), true, true, 200,
                                   R.drawable.ic_launcher, null, 0, 1.0f / 1.0f);
               }
               else if(poss==2){
                   Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mylist.get(position).getVideo_path(),
                           MediaStore.Images.Thumbnails.MINI_KIND);
                   aq.id(iv_image).image(thumbnail,1.0f/1.0f);
               }
               String type[]=mylist.get(position).getFilemimetype().split("/");
               tv_name.setText(mylist.get(position).getFiletitle()+"."+type[1]);
               ch_check.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       int pos = (Integer) v.getTag();
                       if (((CheckBox) v).isChecked()) {
                           state[pos] = true;
                           GalleryDataModel model=new GalleryDataModel();
                           String filetype[]=mylist.get(pos).getFilemimetype().split("/");
                           System.out.println("file type"+filetype[1]);
                           if(poss==1){
                               model.setImage_path(mylist.get(pos).getImage_path());
                           }
                           else if(poss==2){
                               model.setVideo_path(mylist.get(pos).getVideo_path());
                           }
                           model.setFilemimetype(filetype[1]);
                           model.setFiletitle(mylist.get(pos).getFiletitle());
                           files_list.add(model);
                       } else {
                           files_list.remove(files_list.get(pos));
                           state[pos] = false;
                       }
                   }
               });
           }
           catch (Exception e){
               e.printStackTrace();
           }
            ch_check.setChecked(state[position]);
           return convertView;
       }
   }
}
