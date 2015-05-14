package commonutils;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Created by rahul on 3/4/2015.
 */
public interface MyRetrofitInterface<T> {

    @POST(URLS.LOGIN)
    void login(@QueryMap Map<String, String> map, Callback<T> object);

    @POST(URLS.GETSPACESTATS)
    void getspacestats(@QueryMap Map<String, String> map, Callback<T> object);

    @Multipart
    @POST(URLS.UPLOAD_FILES)
    void fileupload(@Part("userid") String userid, @Part("folderid") String folderid,@Part("deviceId")String deviceid, @PartMap Map<String, TypedFile> files, Callback<T> object);

    @GET(URLS.GET_ROOT_FOLDER_FILES)
    void getrootfolderfiles(@Query("userid") String userid, Callback<T> object);

    @GET(URLS.GET_ROOT_FOLDER_FILES)
    void getrootsubfolderfiles(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.TEMP_DELETE_FILE_FOLDER)
    void tempdelfilefolder(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.PERMANENT_DELETE_FILE_FOLDER)
    void permamnentdelfilefolder(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.SEARCH_FILE_FOLDER)
    void searchfilefolder(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.CREATE_FOLDER)
    void createfolder(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.REQUEST_FILE)
    void requestfile(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.SHARE_FILE)
    void sharefile(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.SETTINGS)
    void savesettings(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.DOWNLOAD)
    void download(@QueryMap Map<String, String> mymap, Callback<T> object);

    @GET(URLS.UPDATEPROFILE)
    void updateprofile(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.SYNCFILES)
    void syncfiles(@QueryMap Map<String, String> map, Callback<T> object);

    @GET(URLS.FORGOTPASS)
    void forgotpass(@Query("Email") String Email, Callback<T> object);
}
