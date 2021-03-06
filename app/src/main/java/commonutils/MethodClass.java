package commonutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

/**
 * Created by rahul on 3/4/2015.
 */
public class MethodClass<T> {
    RestAdapter adapter;
    Gson gson;
    MyRetrofitInterface<T> myretro;
    DataTransferInterface<T> inter;
    private Context cnt;

    /**
     * Paramterized constructer to intialize retrofit rest adapter
     *
     * @param cnt   -pass current activity instance to use this method on that activity
     * @param inter -pass interface instance to transfer the data to fragment/activity whose context has been passed
     */
    public MethodClass(Context cnt, DataTransferInterface<T> inter) {
        this.cnt = cnt;
        this.inter = inter;
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();
        adapter = new RestAdapter.Builder()
                .setEndpoint(URLS.COMMON_URL)
                .setClient(new RetrofitHttpClient())
                .setExecutors(Executors.newCachedThreadPool(),
                        new MainThreadExecutor())
                .setConverter(new GsonConverter(gson)).build();
        myretro = adapter.create(MyRetrofitInterface.class);
    }
/********************************************************************************************************************************/
    /**
     * @param is -input stream returned from response
     * @return
     * @throws IOException
     */
    public byte[] getBytesFromStream(InputStream is) throws IOException {
        int len;
        byte[] buf;
        int size = 1024;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();
        is.close();
        return buf;
    }

    /*******************************************************************************************************************************/
    /**
     * @param bytes -bytes returned from input stream
     * @param path  -path where the file will be saved
     */
    public void saveBytesToFile(byte[] bytes, String path) {
        FileOutputStream fileOuputStream = null;
        try {
            File file = new File(path);
            fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(bytes);
            fileOuputStream.flush();
            fileOuputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*************************************************************************************************************************/

    /**
     * method to make retrofit get request without parameters
     *
     * @param url -url for getting the data
     */
    public void MakeGetRequest(String url, String user_id_email) {
        switch (url) {
            case URLS.GET_ROOT_FOLDER_FILES:
                myretro.getrootfolderfiles(user_id_email, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.FORGOTPASS:
                myretro.forgotpass(user_id_email,new CallbackClass<T>(inter,cnt));
                break;
            case URLS.SPACE_AVAILABLE:
                myretro.spaceavailable(user_id_email,new CallbackClass<T>(inter,cnt));
            default:
                break;
        }

    }

    /**
     * method to make post request
     *
     * @param map -map containing params to pass in the post request
     * @param url -url to fetch data
     */
    public void MakePostRequest(Map<String, String> map, String url) {
        switch (url) {
            case URLS.LOGIN:
                myretro.login(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.GETSPACESTATS:
                myretro.getspacestats(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.CHANGEPASS:
                myretro.changepass(map,new CallbackClass<T>(inter,cnt));
                break;
            default:
                break;
        }

    }

    /**
     * method to make getrequest with params
     *
     * @param map -map containing params to pass in the get request
     * @param url -url to fetch data
     */
    public void MakeGetRequestWithParams(Map<String, String> map, String url) {
        switch (url) {
            case URLS.GET_ROOT_FOLDER_FILES:
                myretro.getrootsubfolderfiles(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.SEARCH_FILE_FOLDER:
                myretro.searchfilefolder(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.CREATE_FOLDER:
                myretro.createfolder(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.TEMP_DELETE_FILE_FOLDER:
                myretro.tempdelfilefolder(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.REQUEST_FILE:
                myretro.requestfile(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.SHARE_FILE:
                myretro.sharefile(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.SETTINGS:
                myretro.savesettings(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.DOWNLOAD:
                myretro.download(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.UPDATEPROFILE:
                myretro.updateprofile(map, new CallbackClass<T>(inter, cnt));
                break;
            case URLS.SYNCFILES:
                myretro.syncfiles(map, new CallbackClass<T>(inter, cnt));
                break;
            default:
                break;
        }

    }

    /**
     * @param userid   -useridof the logged in person
     * @param folderid -current folder id
     * @param files    -file map containing files to upload
     * @param url      -url to uplaod file
     */
    public void UploadFiles(String userid, String folderid,String deviceid, Map<String, TypedFile> files, String url) {
        switch (url) {
            case URLS.UPLOAD_FILES:
                myretro.fileupload(userid, folderid,deviceid, files, new CallbackClass<T>(inter, cnt));
                break;
            default:
                break;
        }
    }

    /**
     * Check Internet connection
     *
     * @return
     */
    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) cnt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }
}
