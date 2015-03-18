package fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import Boomerang.R;
import activities.FileChooser;
import commonutils.RequestCodes;
import commonutils.UIutill;
import modelclasses.GalleryDataModel;

/**
 * Created by rahul on 3/18/2015.
 */
public class UploadFiles extends Fragment implements View.OnClickListener{

    View v=null;
    RelativeLayout layout_spinner;
    ListView lv_myfiles;
    Button btn_upload;
    MyUploadFilesAdapter adapter=null;
    ArrayList<GalleryDataModel> list=new ArrayList<GalleryDataModel>();
    private PopupWindow pwindo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            v=inflater.inflate(R.layout.fragment_fileupload,null);
            TextView tv_chooseoption=(TextView)v.findViewById(R.id.tv_chooseoption);
            tv_chooseoption.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            layout_spinner = (RelativeLayout) v.findViewById(R.id.layout_spinner);
            registerForContextMenu(layout_spinner);
            layout_spinner.setOnClickListener(this);
            lv_myfiles=(ListView)v.findViewById(R.id.lv_myfiles);
            btn_upload=(Button)v.findViewById(R.id.btn_upload);
            btn_upload.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));
            btn_upload.setOnClickListener(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lv_myfiles.getCount()==0){
            btn_upload.setVisibility(View.GONE);
        }else{
            btn_upload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_upload:
                try{
                     if(lv_myfiles.getCount()>5){
                         UIutill.ShowSnackBar(getActivity(),getString(R.string.upload_error_message));
                     }
                    else{
                        System.out.println("files"+lv_myfiles.toString());
                     }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.layout_spinner:
                try{
                    getActivity().openContextMenu(v);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Images");
        menu.add(0, v.getId(), 0, "Videos");
        menu.add(0, v.getId(), 0, "File browser");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Images")){
               intiatepopup(1,getString(R.string.camera));
        }
        else if(item.getTitle().equals("Videos")){
            intiatepopup(2,getString(R.string.video_record));
        }
        else{
            Intent filechooser = new Intent(getActivity(), FileChooser.class);
            startActivityForResult(filechooser,RequestCodes.REQUEST_FILE_BROWSER);
        }
        return true;
    }
    public void intiatepopup(final int pos, String name){
        if (pwindo == null || !pwindo.isShowing()) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View layout = inflator.inflate(R.layout.popup_layout,
                    (ViewGroup)getActivity().findViewById(R.id.main_layout));
            pwindo = new PopupWindow(layout, 450, ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tv_gallery = (TextView) layout
                    .findViewById(R.id.tv_gallery);
            TextView tv_camera = (TextView) layout.findViewById(R.id.tv_camera);
            tv_camera.setText(name);
            TextView tv_cancel = (TextView) layout.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });
            tv_gallery.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    Intent i = new Intent();
                    i.setAction(getString(R.string.choose_multipleaction));
                    if (pos == 1) {
                        i.putExtra("value", "images");
                        startActivityForResult(i, RequestCodes.REQUEST_IMAGE);
                    } else if (pos == 2) {
                        i.putExtra("value", "videos");
                        startActivityForResult(i,  RequestCodes.REQUEST_VIDEO);
                    }

                }
            });
            tv_camera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    if (pos == 1) {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(i, RequestCodes.REQUEST_CAMERA);
                    } else if (pos == 2) {
                        Intent record = new Intent(
                                MediaStore.ACTION_VIDEO_CAPTURE);
                        if (record.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(record, RequestCodes.REQUEST_VIDEO_RECORD);
                        } else {
                           UIutill.ShowSnackBar(getActivity(),getString(R.string.task_error));
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){

            if(requestCode==RequestCodes.REQUEST_IMAGE){

               ArrayList<GalleryDataModel> returnedlist= (ArrayList<GalleryDataModel>)data.getBundleExtra("bundle").getSerializable("list");
               for(GalleryDataModel model:returnedlist){
                   GalleryDataModel mymodel=new GalleryDataModel();
                   mymodel.setImage_path(model.getImage_path());
                   mymodel.setFiletitle(model.getFiletitle());
                   mymodel.setFilemimetype(model.getFilemimetype());
                   list.add(mymodel);
               }
                adapter=new MyUploadFilesAdapter(getActivity(),list,1);
            }
            else if(requestCode==RequestCodes.REQUEST_CAMERA){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = Bitmap.createScaledBitmap(photo, 100, 100, true);
                Uri tempUri = getImageUri(getActivity(), photo);
                String values[]=getRealPathFromURI(tempUri);
                GalleryDataModel mymodel=new GalleryDataModel();
                mymodel.setImage_path(values[0]);
                mymodel.setFiletitle(values[1]);
                mymodel.setFilemimetype(values[2]);
                list.add(mymodel);
                adapter=new MyUploadFilesAdapter(getActivity(),list,1);
            }
            else if(requestCode==RequestCodes.REQUEST_VIDEO){
                ArrayList<GalleryDataModel> returnedlist=  (ArrayList<GalleryDataModel>)data.getBundleExtra("bundle").getSerializable("list");
                for(GalleryDataModel model:returnedlist){
                    GalleryDataModel mymodel=new GalleryDataModel();
                    mymodel.setVideo_path(model.getVideo_path());
                    mymodel.setFiletitle(model.getFiletitle());
                    mymodel.setFilemimetype(model.getFilemimetype());
                    list.add(mymodel);
                }
                adapter=new MyUploadFilesAdapter(getActivity(),list,2);
            }
            else if(requestCode==RequestCodes.REQUEST_VIDEO_RECORD){
                Uri uri = data.getData();
                Log.e("URI", uri.toString());
                String path = uri.getPath();
                Log.v("Path", "path: " + path);
                Cursor cursor = getActivity().getContentResolver().query(uri,
                        new String[]{MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE, MediaStore.Video.VideoColumns.MIME_TYPE},
                        null, null, null);
                cursor.moveToFirst();
                String filepath = cursor.getString(0);
                String filetitle=cursor.getString(1);
                String mimetype=cursor.getString(2);
                GalleryDataModel mymodel=new GalleryDataModel();
                mymodel.setVideo_path(filepath);
                mymodel.setFiletitle(filetitle);
                mymodel.setFilemimetype(mimetype);
                list.add(mymodel);
                adapter=new MyUploadFilesAdapter(getActivity(),list,2);
            }
            else if(requestCode==RequestCodes.REQUEST_FILE_BROWSER){
                String curFileName = data.getStringExtra("GetFileName");
                System.out.println("name"+curFileName);
                String path=data.getStringExtra("GetPath");
                String image=data.getStringExtra("image");
                GalleryDataModel mymodel=new GalleryDataModel();
                mymodel.setFiletitle(curFileName);
                mymodel.setFilepath(path);
                mymodel.setFilethumnbail(image);
                list.add(mymodel);
                adapter=new MyUploadFilesAdapter(getActivity(),list,3);
            }
            lv_myfiles.setAdapter(adapter);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    public String[] getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        int idy=cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE);
        int idz=cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE);
        String values[]={cursor.getString(idx),cursor.getString(idy),cursor.getString(idz)};
        return values;
    }

    class  MyUploadFilesAdapter extends BaseAdapter{
        Context context;
        ArrayList<GalleryDataModel> mylist;
        LayoutInflater inf;
        ImageView iv_image, iv_delete;
        TextView tv_name;
        AQuery aq;
        int pos;
        public MyUploadFilesAdapter(Context context, ArrayList<GalleryDataModel> mylist,int pos) {
            this.pos=pos;
            this.context = context;
            this.mylist = mylist;
            inf = LayoutInflater.from(context);
            aq = new AQuery(context);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            try{
                if(convertView==null){
                    convertView=inf.inflate(R.layout.uploadfileslistview_row_item,null);
                }
                tv_name=(TextView)convertView.findViewById(R.id.tv_name);
                tv_name.setTypeface(UIutill.SetFont(getActivity(),"segoeuilght.ttf"));

                iv_delete=(ImageView)convertView.findViewById(R.id.iv_delete);
                iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
                if(pos==1){
                    aq.id(iv_image)
                            .image(mylist.get(position).getImage_path(), true, true, 200,
                                    R.drawable.ic_launcher, null, 0, 1.0f / 1.0f);
                    tv_name.setText(mylist.get(position).getFiletitle()+"."+mylist.get(position).getFilemimetype());
                }
                else if(pos==2){
                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(list.get(position).getVideo_path(),
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    aq.id(iv_image).image(thumbnail,1.0f/1.0f);
                    tv_name.setText(mylist.get(position).getFiletitle()+"."+mylist.get(position).getFilemimetype());
                }
                else if(pos==3){
                    tv_name.setText(mylist.get(position).getFiletitle());
                    String uri = "drawable/" + mylist.get(position).getFilethumnbail();
                    int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                    Drawable image = context.getResources().getDrawable(imageResource);
                    Bitmap thumbnail = ((BitmapDrawable)image).getBitmap();
                    aq.id(iv_image).image(thumbnail,1.0f/1.0f);
                }
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=lv_myfiles.getPositionForView(v);
                        mylist.remove(mylist.get(pos));
                        notifyDataSetChanged();
                    }
                });

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }


}
