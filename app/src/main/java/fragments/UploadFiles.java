package fragments;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import customviews.ActionSheet;
import customviews.SwipeDismissListViewTouchListener;
import modelclasses.GalleryDataModel;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by rahul on 3/18/2015.
 */
public class UploadFiles<T> extends Fragment implements View.OnClickListener, DataTransferInterface<T>, ActionSheet.ActionSheetListener {

    View v = null;
    RelativeLayout layout_spinner;
    ListView lv_myfiles;
    Button btn_upload;
    MyUploadFilesAdapter adapter = null;
    ArrayList<GalleryDataModel> list = new ArrayList<GalleryDataModel>();
    MethodClass<T> method;
    int folderid;
    Context cnt;
    int pos;
    ActionSheet.Builder actionsheet;
    TextView tv_note;
    float sum = 0.0f;
    int position_of_service;

    /**
     * this method is used to fetch file size
     *
     * @param f -file whose size to be find
     * @return
     */
    public static float getFileSize(File f) {
        float size = 0;
        size = f.length() / (1024 * 1024);
        return size;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cnt = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {

            v = inflater.inflate(R.layout.fragment_fileupload, null);
            tv_note = (TextView) v.findViewById(R.id.tv_note);
            tv_note.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            TextView tv_chooseoption = (TextView) v.findViewById(R.id.tv_chooseoption);
            tv_chooseoption.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            layout_spinner = (RelativeLayout) v.findViewById(R.id.layout_spinner);
            registerForContextMenu(layout_spinner);
            layout_spinner.setOnClickListener(this);
            lv_myfiles = (ListView) v.findViewById(R.id.lv_myfiles);
            lv_myfiles.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            btn_upload = (Button) v.findViewById(R.id.btn_upload);
            btn_upload.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
            btn_upload.setOnClickListener(this);
            method = new MethodClass<T>(getActivity(), this);
            if (savedInstanceState == null) {
                Bundle bundle = getArguments();
                folderid = bundle.getInt("folderid", 0);
            } else {
                folderid = savedInstanceState.getInt("folderid", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_myfiles,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    list.remove(position);
                                }
                                adapter.notifyDataSetChanged();
                                if (lv_myfiles.getCount() == 0) {
                                    tv_note.setVisibility(View.GONE);
                                    btn_upload.setVisibility(View.GONE);
                                }
                            }
                        });
        lv_myfiles.setOnTouchListener(touchListener);
        return v;
    }

    /**
     * ****************************************************************************************************
     */
    @Override
    public void onResume() {
        super.onResume();
        if (lv_myfiles.getCount() == 0) {
            tv_note.setVisibility(View.GONE);
            btn_upload.setVisibility(View.GONE);
        } else {
            tv_note.setVisibility(View.VISIBLE);
            btn_upload.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ****************************************************************************************************
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                try {
                    if (lv_myfiles.getCount() > 5) {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.upload_error_message));
                    } else {
                        if (method.checkInternetConnection()) {
                            Map<String, TypedFile> values = new HashMap<String, TypedFile>();
                            for (int i = 0; i < list.size(); i++) {
                                sum = sum + Float.valueOf(list.get(i).getFilesize());
                            }
                            position_of_service = 0;
                            method.MakeGetRequest(URLS.SPACE_AVAILABLE, getActivity().getSharedPreferences("Login", 0).getString("UserID", ""));
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_spinner:
                try {
                    pos = 0;
                    getActivity().setTheme(R.style.ActionSheetStyleIOS7);

                    actionsheet = ActionSheet.createBuilder(getActivity(), getFragmentManager());

                    actionsheet.setCancelButtonTitle(getActivity().getString(R.string.cancel));
                    actionsheet.setOtherButtonTitles("Images", "Videos", "File Browser");
                    actionsheet.setCancelableOnTouchOutside(true);
                    actionsheet.setListener(UploadFiles.this);

                    actionsheet.show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /********************************************************************************************************/

    /**
     * ****************************************************************************************************
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> valueslist = new ArrayList<>();
        if (data != null) {

            if (requestCode == RequestCodes.REQUEST_IMAGE) {
                ArrayList<GalleryDataModel> returnedlist = (ArrayList<GalleryDataModel>) data.getBundleExtra("bundle").getSerializable("list");

                for (GalleryDataModel values : list) {
                    valueslist.add(values.getFileid());
                }
                for (GalleryDataModel model : returnedlist) {
                    String fileid = model.getFileid();
                    if (!valueslist.contains(fileid)) {
                        GalleryDataModel mymodel = new GalleryDataModel();
                        mymodel.setFilepath(model.getImage_path());
                        mymodel.setFrom("image");
                        String title = model.getFiletitle() + "." + model.getFilemimetype().split("/")[1];
                        mymodel.setFiletitle(title);
                        mymodel.setFileid(model.getFileid());
                        mymodel.setFilemimetype(model.getFilemimetype());
                        mymodel.setImage_path(model.getImage_path());
                        mymodel.setFilesize(getFileSize(new File(model.getImage_path())) + "");
                        list.add(mymodel);
                    }
                }
            } else if (requestCode == RequestCodes.REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                //  photo = Bitmap.createScaledBitmap(photo, 100, 100, true);
                Uri tempUri = getImageUri(getActivity(), photo);
                String values[] = getRealPathFromURI(tempUri);
                GalleryDataModel mymodel = new GalleryDataModel();
                mymodel.setFrom("image");
                mymodel.setFilepath(values[0]);
                String typee[] = values[2].split("/");
                mymodel.setFiletitle(values[1] + "." + typee[1]);
                mymodel.setFilemimetype(values[2]);
                mymodel.setImage_path(values[0]);
                mymodel.setFilesize(getFileSize(new File(values[0])) + "");

                list.add(mymodel);
            } else if (requestCode == RequestCodes.REQUEST_VIDEO) {
                ArrayList<GalleryDataModel> returnedlist = (ArrayList<GalleryDataModel>) data.getBundleExtra("bundle").getSerializable("list");
                for (GalleryDataModel values : list) {
                    valueslist.add(values.getFileid());
                }
                for (GalleryDataModel model : returnedlist) {
                    String fileid = model.getFileid();
                    if (!valueslist.contains(fileid)) {
                        GalleryDataModel mymodel = new GalleryDataModel();
                        mymodel.setFilepath(model.getVideo_path());
                        mymodel.setFiletitle(model.getFiletitle() + "." + model.getFilemimetype().split("/")[1]);
                        mymodel.setFilemimetype(model.getFilemimetype());
                        mymodel.setFileid(model.getFileid());
                        mymodel.setFilesize(getFileSize(new File(model.getVideo_path())) + "");
                        mymodel.setFrom("image");
                        mymodel.setImage_path(model.getImage_path());
                        list.add(mymodel);
                    }
                }
            } else if (requestCode == RequestCodes.REQUEST_VIDEO_RECORD) {
                Uri uri = data.getData();
                Log.e("URI", uri.toString());
                String path = uri.getPath();
                Log.v("Path", "path: " + path);
                Cursor cursor = getActivity().getContentResolver().query(uri,
                        new String[]{MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE, MediaStore.Video.VideoColumns.MIME_TYPE},
                        null, null, null);
                cursor.moveToFirst();
                String filepath = cursor.getString(0);
                String filetitle = cursor.getString(1);
                String mimetype = cursor.getString(2);
                String typee[] = mimetype.split("/");
                GalleryDataModel mymodel = new GalleryDataModel();
                mymodel.setFilepath(filepath);
                mymodel.setFiletitle(filetitle);
                mymodel.setFilemimetype(mimetype);
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filepath,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
                Uri tempUri = getImageUri(getActivity(), thumbnail);
                String values[] = getRealPathFromURI(tempUri);
                mymodel.setImage_path(values[0]);
                mymodel.setFrom("image");
                mymodel.setFilesize(getFileSize(new File(filepath)) + "");
                list.add(mymodel);
            } else if (requestCode == RequestCodes.REQUEST_FILE_BROWSER) {
                String curFileName = data.getStringExtra("GetFileName");
                String path = data.getStringExtra("GetPath");
                String image = data.getStringExtra("image");
                GalleryDataModel mymodel = new GalleryDataModel();
                mymodel.setFilesize(getFileSize(new File(path)) + "");
                mymodel.setFiletitle(curFileName);
                mymodel.setFilepath(path);
                mymodel.setFilethumnbail(image);
                if (getMimeType(path) != null) {
                    mymodel.setFilemimetype(getMimeType(path));
                    String urii = "drawable/" + image;
                    int imageResource = getActivity().getResources().getIdentifier(urii, null, getActivity().getPackageName());
                    Drawable imagee = getActivity().getResources().getDrawable(imageResource);
                    Bitmap thumbnail = ((BitmapDrawable) imagee).getBitmap();
                    mymodel.setBitmap(thumbnail);
                    mymodel.setFrom("browser");
                    list.add(mymodel);
                } else {
                    UIutill.ShowSnackBar(getActivity(), getString(R.string.mime_type_error));
                }

            }
            adapter = new MyUploadFilesAdapter(getActivity(), list);
            lv_myfiles.setAdapter(adapter);

        }
    }

    /********************************************************************************************************/

    /**
     * this method is used to get the image uri
     *
     * @param inContext -pass the context of activity/fragment
     * @param inImage   -pass the bitmap of image to fetch the image uri
     * @return -uri of the image
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, System.currentTimeMillis() + "", null);
        return Uri.parse(path);
    }

    /**
     * this method is use to the fetch the real path
     *
     * @param uri -uri of the file to fetch data about that particular file
     * @return -return the string array of information about that file
     */
    public String[] getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        int idy = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE);
        int idz = cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE);
        String values[] = {cursor.getString(idx), cursor.getString(idy), cursor.getString(idz)};
        return values;
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onSuccess(T s) {
        try {
            String value = new Gson().toJson(s);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonreturn = (JsonObject) jsonParser.parse(value);
            boolean IsSucess = jsonreturn.get("IsSucess").getAsBoolean();
            if (IsSucess) {
                if (position_of_service == 1) {
                    String response = jsonreturn.get("ResponseData").getAsString();
                    UIutill.ShowSnackBar(getActivity(), response);
                    list.clear();
                    adapter = new MyUploadFilesAdapter(getActivity(), list);
                    lv_myfiles.setAdapter(adapter);
                    if (list.size() == 0) {
                        btn_upload.setVisibility(View.GONE);
                        tv_note.setVisibility(View.GONE);
                    }
                } else if (position_of_service == 0) {
                    JsonArray ResponseData = jsonreturn.get("ResponseData").getAsJsonArray();
                    String spaceavail = ResponseData.get(0).getAsJsonObject().get("spaceAvailable").getAsString();
                    float space = Float.valueOf(spaceavail);
                    if (space > sum) {
                        if (method.checkInternetConnection()) {
                            position_of_service = 1;
                            Map<String, TypedFile> values = new HashMap<String, TypedFile>();
                            for (int i = 0; i < list.size(); i++) {
                                int a = i + 1;
                                values.put("file" + a, new TypedFile(list.get(i).getFilemimetype(), new File(list.get(i).getFilepath())));
                            }
                            method.UploadFiles(getActivity().getSharedPreferences("Login", 0).getString("UserID", ""), folderid + "", UIutill.getDeviceId(getActivity()), values, URLS.UPLOAD_FILES);
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.no_network));
                        }
                    } else {
                        UIutill.ShowSnackBar(getActivity(), getString(R.string.space_error));
                    }
                }
            } else {
                UIutill.ShowDialog(getActivity(), getString(R.string.error), jsonreturn.get("Message").getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /********************************************************************************************************/

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onFailure(RetrofitError error) {
        if (error != null) {
            UIutill.ShowDialog(getActivity(), getString(R.string.error), CustomErrorHandling.ShowError(error, getActivity()));
        }
    }

    /**
     * this method is used to fetch the mimetype of the file
     *
     * @param path -pass path of the file to fethc the mimetype
     * @return -return the mimetype as string
     */
    public String getMimeType(String path) {
        String type = null;
        if (path.lastIndexOf(".") != -1) {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(ext);
        } else {
            type = null;
        }
        return type;
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("folderid", folderid);
        super.onSaveInstanceState(outState);
    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    /**
     * ****************************************************************************************************
     */

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                switch (pos) {
                    case 0:
                        pos = 1;
                        getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                        ActionSheet.createBuilder(cnt, getFragmentManager())
                                .setCancelButtonTitle(getActivity().getString(R.string.cancel))
                                .setOtherButtonTitles("Gallery", "Camera")
                                .setCancelableOnTouchOutside(true)
                                .setListener(UploadFiles.this).show();
                        break;
                    case 1:
                        //gallery of image
                        Intent imagegallery = new Intent();
                        imagegallery.setAction(getString(R.string.choose_multipleaction));
                        imagegallery.putExtra("value", "images");
                        startActivityForResult(imagegallery, RequestCodes.REQUEST_IMAGE);
                        getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                        break;
                    case 2:
                        //gallery of video
                        Intent videogallery = new Intent();
                        videogallery.setAction(getString(R.string.choose_multipleaction));
                        videogallery.putExtra("value", "videos");
                        startActivityForResult(videogallery, RequestCodes.REQUEST_VIDEO);
                        getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                        break;
                    default:
                        break;
                }
                break;
            case 1:
                switch (pos) {
                    case 0:
                        pos = 2;
                        getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                        ActionSheet.createBuilder(cnt, getFragmentManager())
                                .setCancelButtonTitle(getActivity().getString(R.string.cancel))
                                .setOtherButtonTitles("Gallery", "Record video")
                                .setCancelableOnTouchOutside(true)
                                .setListener(UploadFiles.this).show();
                        break;
                    case 1:
                        //camera of image
                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(camera, RequestCodes.REQUEST_CAMERA);

                        break;
                    case 2:
                        //record video
                        Intent record = new Intent(
                                MediaStore.ACTION_VIDEO_CAPTURE);
                        if (record.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(record, RequestCodes.REQUEST_VIDEO_RECORD);
                        } else {
                            UIutill.ShowSnackBar(getActivity(), getString(R.string.task_error));
                        }
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                Intent filechooser = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(filechooser, RequestCodes.REQUEST_FILE_BROWSER);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            default:
                break;
        }
    }
    /**************************************************************************************************************************/

    /**
     * this method is used to fetch the realpath of the image
     *
     * @param uri -uri of the image file
     * @return -returns the actual path of tyhe image
     */
    public String getSizeFromUri(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE);
        return cursor.getString(idx);
    }
    /**************************************************************************************************************************/

    /**
     * ****************************************************************************************************
     */

    class MyUploadFilesAdapter extends BaseAdapter {
        Context context;
        ArrayList<GalleryDataModel> mylist;
        LayoutInflater inf;
        ImageView iv_image, iv_delete;
        TextView tv_name;
        AQuery aq;

        public MyUploadFilesAdapter(Context context, ArrayList<GalleryDataModel> mylist) {
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
            try {
                if (convertView == null) {
                    convertView = inf.inflate(R.layout.uploadfileslistview_row_item, null);
                }
                tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                tv_name.setTypeface(UIutill.SetFont(getActivity(), "segoeuilght.ttf"));
                iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                tv_name.setText(mylist.get(position).getFiletitle());
                if (mylist.get(position).getFrom().equalsIgnoreCase("image")) {
                    aq.id(iv_image).image(mylist.get(position).getImage_path(), false, true, 100,
                            0, null, 0, 1.0f / 1.0f);
                } else if (mylist.get(position).getFrom().equalsIgnoreCase("browser")) {
                    aq.id(iv_image).image(mylist.get(position).getBitmap(), 1.0f / 1.0f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
