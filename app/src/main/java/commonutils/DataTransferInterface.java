package commonutils;

import retrofit.RetrofitError;

/**
 * Created by rahul on 3/4/2015.
 */
public interface DataTransferInterface<T> {
    void onSuccess(T s);
//
    void onFailure(RetrofitError error);
}
