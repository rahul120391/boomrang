package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import Boomerang.R;
import commonutils.RequestCodes;
import commonutils.UIutill;
import modelclasses.GalleryDataModel;

public class CustomGalleryActivity extends Activity implements View.OnClickListener{

    ListView lv_files;
    TextView tv_nofiles;
    ArrayList<GalleryDataModel> files_list=new ArrayList<>();
    Bundle statesave;
    MyAdapter adapter;
    RelativeLayout layout_top;
    TextView tv_select;
    ImageView iv_done;
    ArrayList<GalleryDataModel> datalist=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statesave=savedInstanceState;
        setContentView(R.layout.activity_custom_gallery);
        layout_top=(RelativeLayout)findViewById(R.id.layout_top);

        tv_select=(TextView)findViewById(R.id.tv_select);
        tv_select.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));

        iv_done=(ImageView)findViewById(R.id.iv_done);
        iv_done.setOnClickListener(this);

        lv_files=(ListView)findViewById(R.id.lv_files);

        tv_nofiles=(TextView)findViewById(R.id.tv_nofiles);
        tv_nofiles.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));

        if(savedInstanceState==null){
            if(getIntent().getStringExtra("value").equalsIgnoreCase("images")){
                  datalist=GetImageData();
                if(datalist.size()==0){
                     layout_top.setVisibility(View.GONE);
                     tv_nofiles.setText(getString(R.string.no_image_avail));
                     tv_nofiles.setVisibility(View.VISIBLE);

                     lv_files.setVisibility(View.GONE);
                }
                else{
                    adapter=new MyAdapter(this,datalist,1);
                    lv_files.setAdapter(adapter);
                }
            }
            else if(getIntent().getStringExtra("value").equalsIgnoreCase(
                    "videos")){
                datalist=GetVideoData();
                if(datalist.size()==0){
                    layout_top.setVisibility(View.GONE);
                    tv_nofiles.setText(getString(R.string.no_video_avail));
                    tv_nofiles.setVisibility(View.VISIBLE);
                    lv_files.setVisibility(View.GONE);
                }
                else{
                     adapter=new MyAdapter(this,datalist,2);
                     lv_files.setAdapter(adapter);
                }
            }
        }
        else{
           if(savedInstanceState.getString("value").equals("images")){
               datalist=GetImageData();
               if(datalist.size()==0){
                   layout_top.setVisibility(View.GONE);
                   tv_nofiles.setText(getString(R.string.no_image_avail));
                   tv_nofiles.setVisibility(View.VISIBLE);
                   lv_files.setVisibility(View.GONE);
               }
               else{
                   adapter=new MyAdapter(this,datalist,1);
                   lv_files.setAdapter(adapter);
               }
           }
            else if(getIntent().getStringExtra("value").equalsIgnoreCase(
                   "videos")){
               datalist=GetVideoData();
               if(datalist.size()==0){
                   layout_top.setVisibility(View.GONE);
                   tv_nofiles.setText(getString(R.string.no_video_avail));
                   tv_nofiles.setVisibility(View.VISIBLE);
                   lv_files.setVisibility(View.GONE);
               }
               else{
                   adapter=new MyAdapter(this,datalist,2);
                   lv_files.setAdapter(adapter);
               }
           }
        }

    }

    /**************************************************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }

    /**************************************************************************************************************************/
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

    /**************************************************************************************************************************/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("value",getIntent().getStringExtra("value"));
        super.onSaveInstanceState(outState);
    }

    /**************************************************************************************************************************/
    /**
     * this method is used to fetch the images inside phone/tablet
     * @return
     * -returns array of imagedata
     */
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
               System.out.println("uris"+MediaStore.Images.Thumbnails.getContentUri(path));
               GalleryDataModel model=new GalleryDataModel();
               model.setFilemimetype(mimetype);
               model.setFiletitle(title);
               model.setStatus(false);
               model.setImage_path(path);
               data.add(model);
           }
        }
        return data;
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to get videos stored inside phone/tablet
     * @return
     * -returns the arraylist of videodata
     */
    public ArrayList<GalleryDataModel> GetVideoData(){
        ArrayList<GalleryDataModel> data = new ArrayList<GalleryDataModel>();

        String projection[] = { MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,MediaStore.Video.Media.MIME_TYPE,MediaStore.Video.Media._ID};
        String Thumbnail_projection[]={MediaStore.Video.Thumbnails.DATA};

        Cursor cr = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cr != null) {
            while (cr.moveToNext()) {
                GalleryDataModel model=new GalleryDataModel();
                String path=cr.getString(0);
                String title=cr.getString(1);
                String mimetype=cr.getString(2);
                String id=cr.getString(3);
                String imagepath=null;
                Cursor cursor=managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,Thumbnail_projection,MediaStore.Video.Thumbnails.VIDEO_ID+"="+id,null,null);
                if(cursor.moveToFirst()){
                    imagepath=cursor.getString(0);
                }
                else{
                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path,
                            MediaStore.Images.Thumbnails.MICRO_KIND);

                    Uri uri=getImageUri(this,thumbnail);
                    imagepath=getRealPathFromURI(uri);
                }

                model.setVideo_path(path);
                model.setStatus(false);
                model.setImage_path(imagepath);
                model.setFiletitle(title);
                model.setFilemimetype(mimetype);
                data.add(model);
            }
        }
        return data;
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to fetch the image uri
     * @param inContext
     * -pass the context of the fragment/activity
     * @param inImage
     * -pass the bitmap of the image
     * @return
     * -returns the image uri
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, System.currentTimeMillis()+"", null);
        return Uri.parse(path);
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to fetch the realpath of the image
     * @param uri
     * -uri of the image file
     * @return
     * -returns the actual path of tyhe image
     */
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**************************************************************************************************************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_done:
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

    /**************************************************************************************************************************/

    class MyAdapter extends BaseAdapter{

       ArrayList<GalleryDataModel> mylist;
       Context cnt;
       AQuery aq;
       LayoutInflater inflater;
       ImageView iv_image;
       TextView tv_name;
       CheckBox ch_check;
       int poss;

        public MyAdapter(Context cnt,ArrayList<GalleryDataModel> mylist,int poss){
           this.cnt=cnt;
           this.poss=poss;
           this.mylist=mylist;
           inflater=LayoutInflater.from(cnt);
           aq=new AQuery(cnt);
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
               aq.id(iv_image) .image(mylist.get(position).getImage_path(), false, true, 100,
                            0, null, 0, 1.0f / 1.0f);
               String type[]=mylist.get(position).getFilemimetype().split("/");
               tv_name.setText(mylist.get(position).getFiletitle()+"."+type[1]);
               ch_check.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       int pos = (Integer) v.getTag();
                       if (((CheckBox) v).isChecked()) {
                           mylist.get(pos).setStatus(true);
                           GalleryDataModel model=new GalleryDataModel();
                           if(poss==2){
                               model.setVideo_path(mylist.get(pos).getVideo_path());
                           }
                           model.setImage_path(mylist.get(pos).getImage_path());
                           model.setFilemimetype(mylist.get(pos).getFilemimetype());
                           model.setFiletitle(mylist.get(pos).getFiletitle());
                         files_list.add(model);
                       } else {
                           mylist.get(pos).setStatus(false);
                           files_list.remove(files_list.size()-1);
                       }
                       notifyDataSetChanged();
                   }
               });
               ch_check.setChecked(mylist.get(position).isStatus());
           }
           catch (Exception e) {
               e.printStackTrace();
           }
           return convertView;
       }
   }
    /**************************************************************************************************************************/
}
