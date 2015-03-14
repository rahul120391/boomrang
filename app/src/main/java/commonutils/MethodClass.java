package commonutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;
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
        gson = new GsonBuilder()
                .setFieldNamingPolicy(
                        FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
        adapter = new RestAdapter.Builder()
                .setEndpoint(URLS.COMMON_URL)
                .setClient(new RetrofitHttpClient())
                .setExecutors(Executors.newCachedThreadPool(),
                        new MainThreadExecutor())
                .setConverter(new GsonConverter(gson)).build();
        myretro = adapter.create(MyRetrofitInterface.class);
    }
/****************************************************************************************************************************/

    /**
     * method to make retrofit get request without parameters
     *
     * @param url -url for getting the data
     */
    public void MakeGetRequest(String url) {
        switch (url) {
   /*         case URLS.SPECIALITY:
                myretro.getstates(new CallbackClass<T>(inter, cnt));
                break;*/
            default:
                break;
        }

    }
/********************************************************************************************************************************/
    /**
     * method to make post request
     *
     * @param map -map containing params to pass in the post request
     * @param url -url to fetch data
     */
    public void MakePostRequest(Map<String, String> map, String url) {
        switch (url) {
            case URLS.LOGIN:
                System.out.println("map"+map);
                myretro.login(map,new CallbackClass<T>(inter, cnt));
                break;
            default:
                break;
        }

    }
    /*************************************************************************************************************************/

    /**
     * method to make getrequest with params
     *
     * @param map -map containing params to pass in the get request
     * @param url -url to fetch data
     */
    public void MakeGetRequestWithParams(Map<String, String> map, String url) {
        switch (url) {
            /*case URLS.SEARCH_REQUEST:
                myretro.sendtomany(map, new CallbackClass<T>(inter, cnt));
                break;*/
            default:
                break;
        }

    }


    /**
     * method to make multipart request for file uploading
     *
     * @param map  -map containing params to pass in the multipart request
     * @param file -file body of the file to upload
     * @param url  -url via which file to upload
     */
    public void MakeMultipartRequest(Map<String, String> map, TypedFile file, String url) {
        switch (url) {
       /*     case URLS.ADD_ANIMAL:
                myretro.addanimanl(map, file, new CallbackClass<T>(inter, cnt));
                break;*/
            default:
                break;
        }

    }


    /***
     * Check Internet connection
     * @return
     */
    public  boolean checkInternetConnection() {
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
