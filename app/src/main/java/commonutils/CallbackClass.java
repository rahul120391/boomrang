package commonutils;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahul on 3/4/2015.
 */
public class CallbackClass<T> implements Callback<T> {
    DataTransferInterface<T> result;

    /**
     * constructor used to intialize datatransferinterface
     *
     * @param result -interface instance to pass to transfer data
     * @param cnt    -context of the activity/fragment
     */
    public CallbackClass(DataTransferInterface<T> result, Context cnt) {
        this.result = result;
    }

    @Override
    public void success(T apiresponse, Response response) {
        result.onSuccess(apiresponse);
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        result.onFailure(retrofitError);
    }
}
