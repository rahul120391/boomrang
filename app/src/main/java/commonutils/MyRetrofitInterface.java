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
    void login(@QueryMap Map<String,String> map,Callback<T> object);
    @POST(URLS.GETSPACESTATS)
    void getspacestats(@QueryMap Map<String,String> map,Callback<T> object);
    @Multipart
    @POST(URLS.UPLOAD_FILES)
    void fileupload(@Part("userid") String userid,@Part("folderid") String folderid,@PartMap Map<String,TypedFile> files,Callback<T> object);

    @GET(URLS.GET_ROOT_FOLDER_FILES)
    void getrootfolderfiles(@Query("userid") String userid,Callback<T> object);

    @GET(URLS.GET_ROOT_FOLDER_FILES)
    void getrootsubfolderfiles(@QueryMap Map<String,String> map,Callback<T> object);
}
