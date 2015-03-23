package fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Boomerang.R;
import activities.FileChooser;
import commonutils.CustomErrorHandling;
import commonutils.DataTransferInterface;
import commonutils.MethodClass;
import commonutils.RequestCodes;
import commonutils.UIutill;
import commonutils.URLS;
import modelclasses.GalleryDataModel;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by rahul on 3/18/2015.
 */
public class UploadFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>{

    View v=null;
    RelativeLayout layout_spinner;
    ListView lv_myfiles;
    Button btn_upload;
    MyUploadFilesAdapter adapter=null;
    ArrayList<GalleryDataModel> list=new ArrayList<GalleryDataModel>();
    MethodClass<T> method;
    private PopupWindow pwindo;
    int folderid;
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
            method=new MethodClass<T>(getActivity(),this);
            if(savedInstanceState==null){
                Bundle bundle=getArguments();
                folderid=bundle.getInt("folderid",0);
            }
            else{
               folderid=savedInstanceState.getInt("folderid",0);
            }
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
                         if(method.checkInternetConnection()){
                            Map<String,TypedFile> values=new HashMap<String,TypedFile>();
                            for(int i=0;i<list.size();i++){
                                int a=i+1;
                                values.put("file"+a,new TypedFile(list.get(i).getFilemimetype(),new File(list.get(i).getFilepath())));
                             }
                           method.UploadFiles(getActivity().getSharedPreferences("Login",0).getString("UserID",""),folderid+"",values, URLS.UPLOAD_FILES);
                         }
                         else{
                             UIutill.ShowSnackBar(getActivity(),getString(R.string.no_network));
                         }

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
                        startActivityForResult(i, RequestCodes.REQUEST_VIDEO);
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
        //super.onActivityResult(requestCode, resultCode, data);
        int pos=0;
        ArrayList<String> valueslist=new ArrayList<>();
        if(data!=null){

            if(requestCode==RequestCodes.REQUEST_IMAGE){
               ArrayList<GalleryDataModel> returnedlist= (ArrayList<GalleryDataModel>)data.getBundleExtra("bundle").getSerializable("list");
                pos=1;
                for(GalleryDataModel values:list){
                    valueslist.add(values.getFiletitle());
                }
                for(GalleryDataModel model:returnedlist){
                    String name=model.getFiletitle()+"."+model.getFilemimetype().split("/")[1];
                    if(!valueslist.contains(name)){
                        GalleryDataModel mymodel=new GalleryDataModel();
                        mymodel.setFilepath(model.getImage_path());
                        String title=model.getFiletitle()+"."+model.getFilemimetype().split("/")[1];
                        mymodel.setFiletitle(title);
                        mymodel.setFilemimetype(model.getFilemimetype());
                        File imgFile = new File(model.getImage_path());
                        if(imgFile.exists()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            mymodel.setBitmap(myBitmap);
                        }
                        list.add(mymodel);
                    }
                }
            }
            else if(requestCode==RequestCodes.REQUEST_CAMERA){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
              //  photo = Bitmap.createScaledBitmap(photo, 100, 100, true);
                Uri tempUri = getImageUri(getActivity(), photo);
                String values[]=getRealPathFromURI(tempUri);
                GalleryDataModel mymodel=new GalleryDataModel();
                mymodel.setFilepath(values[0]);
                String typee[]=values[2].split("/");
                mymodel.setFiletitle(values[1]+"."+typee[1]);
                mymodel.setFilemimetype(values[2]);
                mymodel.setBitmap(photo);
                list.add(mymodel);
                pos=1;
            }
            else if(requestCode==RequestCodes.REQUEST_VIDEO){
                ArrayList<GalleryDataModel> returnedlist= (ArrayList<GalleryDataModel>)data.getBundleExtra("bundle").getSerializable("list");
                pos=2;
                for(GalleryDataModel values:list){
                    valueslist.add(values.getFiletitle());
                }
                for(GalleryDataModel model:returnedlist){
                    String name=model.getFiletitle()+"."+model.getFilemimetype().split("/")[1];
                    if(!valueslist.contains(name)){
                        GalleryDataModel mymodel=new GalleryDataModel();
                        mymodel.setFilepath(model.getVideo_path());
                        mymodel.setFiletitle(model.getFiletitle()+"."+model.getFilemimetype().split("/")[1]);
                        mymodel.setFilemimetype(model.getFilemimetype());
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(model.getVideo_path(),
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        mymodel.setBitmap(thumbnail);
                        list.add(mymodel);
                    }
                }
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
                String typee[]=mimetype.split("/");
                GalleryDataModel mymodel=new GalleryDataModel();
                mymodel.setFilepath(filepath);
                mymodel.setFiletitle(filetitle+"."+typee[1]);
                mymodel.setFilemimetype(mimetype);
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filepath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                mymodel.setBitmap(thumbnail);
                list.add(mymodel);
                pos=2;
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
                if(getMimeType(path)!=null){
                    mymodel.setFilemimetype(getMimeType(path));
                    String uri = "drawable/" + image;
                    int imageResource = getActivity().getResources().getIdentifier(uri, null, getActivity().getPackageName());
                    Drawable imagee = getActivity().getResources().getDrawable(imageResource);
                    Bitmap thumbnail = ((BitmapDrawable)imagee).getBitmap();
                    mymodel.setBitmap(thumbnail);
                    list.add(mymodel);
                    pos=3;
                }
                else{
                    UIutill.ShowSnackBar(getActivity(),getString(R.string.mime_type_error));
                }

            }
            lv_myfiles.setAdapter(null);
            adapter=new MyUploadFilesAdapter(getActivity(),list,pos);
            lv_myfiles.setAdapter(adapter);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, System.currentTimeMillis()+"", null);
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

    @Override
    public void onSuccess(T s) {
          try{
              String value=new Gson().toJson(s);
              JsonParser jsonParser = new JsonParser();
              JsonObject jsonreturn= (JsonObject)jsonParser.parse(value);
              boolean IsSucess=jsonreturn.get("IsSucess").getAsBoolean();
              if(IsSucess){
                  String response=jsonreturn.get("ResponseData").getAsString();
                  UIutill.ShowSnackBar(getActivity(),response);
              }
              else{
                  UIutill.ShowDialog(getActivity(),getString(R.string.error),jsonreturn.get("Message").getAsString());
              }
          }
          catch (Exception e){
              e.printStackTrace();
          }
    }

    @Override
    public void onFailure(RetrofitError error) {
        if(error!=null){
            System.out.println("error messsage"+error.getMessage());
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }

  public String getMimeType(String path){
      String type=null;
      if(path.lastIndexOf(".") != -1) {
          String ext = path.substring(path.lastIndexOf(".")+1);
          MimeTypeMap mime = MimeTypeMap.getSingleton();
          type = mime.getMimeTypeFromExtension(ext);
      } else {
          type = null;
      }
      return type;
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
                tv_name.setText(mylist.get(position).getFiletitle());
                aq.id(iv_image).image(mylist.get(position).getBitmap(),1.0f/1.0f);
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=lv_myfiles.getPositionForView(v);
                        mylist.remove(mylist.get(pos));
                        notifyDataSetChanged();
                        if(lv_myfiles.getCount()==0){
                            btn_upload.setVisibility(View.GONE);
                        }
                    }
                });

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
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
                tv_name.setText(mylist.get(position).getFiletitle());
                aq.id(iv_image).image(mylist.get(position).getBitmap(),1.0f/1.0f);
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=lv_myfiles.getPositionForView(v);
                        mylist.remove(mylist.get(pos));
                        notifyDataSetChanged();
                        if(lv_myfiles.getCount()==0){
                            btn_upload.setVisibility(View.GONE);
                        }
                    }
                });

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }    @Override
    public void onSaveInstanceState(Bundle outState) {
         outState.putInt("folderid",folderid);
         super.onSaveInstanceState(outState);
    }
}
