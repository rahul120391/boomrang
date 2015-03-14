package commonutils;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by rahul on 3/4/2015.
 */
public interface MyRetrofitInterface<T> {

    @POST(URLS.LOGIN)
    void login(@QueryMap Map<String,String> map,Callback<T> object);
    @POST(URLS.GETSPACESTATS)
    void getspacestats(@QueryMap Map<String,String> map,Callback<T> object);
}
