package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import commonutils.ProgressDialogClass;
import commonutils.RequestCodes;
import commonutils.UIutill;
import commonutils.UnCaughtException;
import modelclasses.GalleryDataModel;

public class CustomGalleryActivity extends Activity implements View.OnClickListener {

    ListView lv_files;
    TextView tv_nofiles;
    ArrayList<GalleryDataModel> files_list = new ArrayList<>();
    Bundle statesave;
    MyAdapter adapter;
    RelativeLayout layout_top;
    TextView tv_select;
    int totalnumberofrows;
    ImageView iv_done;
    ArrayList<GalleryDataModel> datalist = null;
    //Button btn_loadmore;
    int fromm = 0;
    ArrayList<String> list = new ArrayList<>();
    String check;
    String imageprojection[] = {MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.TITLE, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media._ID};
    String videoprojection[] = {MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media._ID};
    ArrayList<GalleryDataModel> data = new ArrayList<GalleryDataModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statesave = savedInstanceState;
        setContentView(R.layout.activity_custom_gallery);
        fromm = 0;
        totalnumberofrows = 0;
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(CustomGalleryActivity.this));

        //intialize views
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        tv_select = (TextView) findViewById(R.id.tv_select);
        iv_done = (ImageView) findViewById(R.id.iv_done);
        lv_files = (ListView) findViewById(R.id.lv_files);
        tv_nofiles = (TextView) findViewById(R.id.tv_nofiles);
        //set typeface
        tv_select.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));
        tv_nofiles.setTypeface(UIutill.SetFont(this, "segoeuilght.ttf"));

        //set listeners
        iv_done.setOnClickListener(this);
        if (savedInstanceState == null) {
            if (getIntent().getStringExtra("value").equalsIgnoreCase("images")) {
                check = "images";
                Cursor mycursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageprojection, null, null, null);
                totalnumberofrows = mycursor.getCount();
                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "images");
            } else if (getIntent().getStringExtra("value").equalsIgnoreCase(
                    "videos")) {
                check = "videos";
                Cursor mycursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        videoprojection, null, null, null);
                totalnumberofrows = mycursor.getCount();
                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "videos");
            }
        } else {
            if (savedInstanceState.getString("value").equals("images")) {
                check = "images";
                Cursor mycursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageprojection, null, null, null);
                totalnumberofrows = mycursor.getCount();
                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "images");
            } else if (getIntent().getStringExtra("value").equalsIgnoreCase(
                    "videos")) {
                check = "videos";
                Cursor mycursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        videoprojection, null, null, null);
                totalnumberofrows = mycursor.getCount();
                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "videos");
            }
        }

        lv_files.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lv_files.getLastVisiblePosition() == lv_files.getAdapter().getCount() - 1
                            && lv_files.getChildAt(lv_files.getChildCount() - 1).getBottom() <= lv_files.getHeight()) {
                        if (lv_files.getCount() < totalnumberofrows) {
                            if (check.equalsIgnoreCase("images")) {
                                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "images");
                            } else if (check.equalsIgnoreCase("videos")) {
                                new GetMediaData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "videos");
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    /**************************************************************************************************************************/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("value", getIntent().getStringExtra("value"));
        super.onSaveInstanceState(outState);
    }

    /**************************************************************************************************************************/
    /**
     * this method is used to fetch the images inside phone/tablet
     *
     * @return -returns array of imagedata
     */
    public ArrayList<GalleryDataModel> GetImageData() {
        String sortOrder = String.format("%s limit 10 offset " + fromm, BaseColumns._ID);
        Cursor cr = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageprojection, null, null, sortOrder);

        if (cr != null) {
            while (cr.moveToNext()) {
                String path = cr.getString(0);
                String title = cr.getString(1);
                String mimetype = cr.getString(2);
                String id = cr.getString(3);
                if (path != null && !list.contains(path)) {
                    list.add(path);
                    GalleryDataModel model = new GalleryDataModel();
                    model.setFilemimetype(mimetype);
                    model.setFiletitle(title);
                    model.setStatus(false);
                    model.setFileid("image" + id);
                    model.setImage_path(path);
                    data.add(model);
                }

            }
        }
        return data;
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to get videos stored inside phone/tablet
     *
     * @return -returns the arraylist of videodata
     */
    public ArrayList<GalleryDataModel> GetVideoData() {
        String Thumbnail_projection[] = {MediaStore.Video.Thumbnails.DATA};
        String sortOrder = String.format("%s limit 10 offset " + fromm, BaseColumns._ID);
        Cursor cr = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoprojection, null, null, sortOrder);
        if (cr != null) {
            while (cr.moveToNext()) {

                String path = cr.getString(0);
                String title = cr.getString(1);
                String mimetype = cr.getString(2);
                String id = cr.getString(3);
                String imagepath = null;
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
                if (thumbnail != null) {
                    Uri uri = getImageUri(this, thumbnail);
                    imagepath = getRealPathFromURI(uri);
                }
                if (imagepath != null && !list.contains(imagepath)) {
                    list.add(imagepath);
                    GalleryDataModel model = new GalleryDataModel();
                    model.setVideo_path(path);
                    model.setStatus(false);
                    model.setImage_path(imagepath);
                    model.setFileid("video" + id);
                    model.setFiletitle(title);
                    model.setFilemimetype(mimetype);
                    data.add(model);
                }

            }
        }
        return data;
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to fetch the image uri
     *
     * @param inContext -pass the context of the fragment/activity
     * @param inImage   -pass the bitmap of the image
     * @return -returns the image uri
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, System.currentTimeMillis() + "", null);
        return Uri.parse(path);
    }

    /**************************************************************************************************************************/

    /**
     * this method is used to fetch the realpath of the image
     *
     * @param uri -uri of the image file
     * @return -returns the actual path of tyhe image
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
        switch (v.getId()) {
            case R.id.iv_done:
                try {
                    if (files_list.size() == 0) {
                        UIutill.ShowSnackBar(CustomGalleryActivity.this, getString(R.string.select_file));
                    } else if (files_list.size() > 5) {
                        UIutill.ShowSnackBar(CustomGalleryActivity.this, getString(R.string.file_max_limit));
                    } else {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list", files_list);
                        intent.putExtra("bundle", bundle);
                        if (statesave == null) {
                            if (getIntent().getStringExtra("value").equalsIgnoreCase("images")) {
                                setResult(RequestCodes.REQUEST_IMAGE, intent);
                            } else if (getIntent().getStringExtra("value").equalsIgnoreCase(
                                    "videos")) {
                                setResult(RequestCodes.REQUEST_VIDEO, intent);
                            }
                        } else if (statesave != null) {
                            if (statesave.getString("value").equalsIgnoreCase("images")) {
                                setResult(RequestCodes.REQUEST_IMAGE, intent);
                            } else if (statesave.getString("value").equalsIgnoreCase(
                                    "videos")) {
                                setResult(RequestCodes.REQUEST_VIDEO, intent);
                            }
                        }
                        finish();
                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**************************************************************************************************************************/

    class MyAdapter extends BaseAdapter {

        ArrayList<GalleryDataModel> mylist;
        Context cnt;
        AQuery aq;
        LayoutInflater inflater;
        ImageView iv_image;
        TextView tv_name;
        CheckBox ch_check;
        int poss;

        public MyAdapter(Context cnt, ArrayList<GalleryDataModel> mylist, int poss) {
            this.cnt = cnt;
            this.poss = poss;
            this.mylist = mylist;
            inflater = LayoutInflater.from(cnt);
            aq = new AQuery(cnt);
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
            try {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.gallerylist_row_item, null);
                }
                //intialize views
                iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                ch_check = (CheckBox) convertView.findViewById(R.id.ch_check);


                tv_name.setTypeface(UIutill.SetFont(cnt, "segoeuilght.ttf"));
                ch_check.setTag(position);
                aq.id(iv_image).image(mylist.get(position).getImage_path(), false, true, 100,
                        0, null, 0, 1.0f / 1.0f);

                String type[] = mylist.get(position).getFilemimetype().split("/");
                tv_name.setText(mylist.get(position).getFiletitle() + "." + type[1]);
                ch_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (Integer) v.getTag();
                        if (((CheckBox) v).isChecked()) {
                            mylist.get(pos).setStatus(true);
                            GalleryDataModel model = new GalleryDataModel();
                            if (poss == 2) {
                                model.setVideo_path(mylist.get(pos).getVideo_path());
                            }
                            model.setImage_path(mylist.get(pos).getImage_path());
                            model.setFileid(mylist.get(pos).getFileid());
                            model.setFilemimetype(mylist.get(pos).getFilemimetype());
                            model.setFiletitle(mylist.get(pos).getFiletitle());
                            files_list.add(model);
                        } else {
                            mylist.get(pos).setStatus(false);
                            files_list.remove(files_list.size() - 1);
                        }
                        notifyDataSetChanged();
                    }
                });
                ch_check.setChecked(mylist.get(position).isStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
    /**************************************************************************************************************************/

    /**
     * This async task is used to fetch data from the gallery
     */
    private class GetMediaData extends AsyncTask<String, String, String> {

        String from = null;

        @Override
        protected String doInBackground(String... params) {
            from = params[0];
            if (from.equalsIgnoreCase("images")) {
                datalist = GetImageData();
            } else if (from.equalsIgnoreCase("videos")) {
                datalist = GetVideoData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (datalist.size() == 0) {
                layout_top.setVisibility(View.GONE);
                if (from.equalsIgnoreCase("images")) {
                    tv_nofiles.setText(getString(R.string.no_image_avail));
                } else if (from.equalsIgnoreCase("videos")) {
                    tv_nofiles.setText(getString(R.string.no_video_avail));
                }
                tv_nofiles.setVisibility(View.VISIBLE);
                lv_files.setVisibility(View.GONE);
            } else {

                if (from.equalsIgnoreCase("images")) {
                    adapter = new MyAdapter(CustomGalleryActivity.this, datalist, 1);
                    lv_files.setAdapter(adapter);
                } else if (from.equalsIgnoreCase("videos")) {
                    adapter = new MyAdapter(CustomGalleryActivity.this, datalist, 2);
                    lv_files.setAdapter(adapter);
                }
                lv_files.setSelection(fromm);
                fromm = fromm + 10;
            }
            ProgressDialogClass.logout();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogClass.getDialog(CustomGalleryActivity.this);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
}
