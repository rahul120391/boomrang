package commonutils;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
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

    @GET(URLS.SPECIALITY)
    void getstates(Callback<T> object);

    @FormUrlEncoded
    @POST(URLS.LOGIN)
    void login(@FieldMap Map<String, String> map, Callback<T> object);

    @GET(URLS.OWNER_DASHBOARD)
    void getwithparam(@Query("id") String id, Callback<T> object);

    @GET(URLS.SEARCH_REQUEST)
    void sendtomany(@QueryMap Map<String, String> map1, Callback<T> object);

    @Multipart
    @POST(URLS.ADD_ANIMAL)
    void addanimanl(@PartMap Map<String, String> map2, @Part("filename") TypedFile photo, Callback<T> object);
}
