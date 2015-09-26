package commonutils;

import android.content.Context;

import com.boomerang.R;

import java.net.HttpURLConnection;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/13/2015.
 */
public class CustomErrorHandling {
    public static String ShowError(RetrofitError error, Context context) {
        String retrunvalue = context.getString(R.string.no_res);
        if (error.getResponse() != null) {
            switch (error.getResponse().getStatus()) {
                case HttpURLConnection.HTTP_ACCEPTED:  //202
                    retrunvalue = context.getString(R.string.http_accep);
                    break;
                case HttpURLConnection.HTTP_BAD_GATEWAY: //502
                    retrunvalue = context.getString(R.string.badgateway);
                    break;
                case HttpURLConnection.HTTP_BAD_METHOD: //405
                    retrunvalue = context.getString(R.string.http_bad_method);
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST: //400
                    retrunvalue = context.getString(R.string.http_bad_request);
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT: //408
                    retrunvalue = context.getString(R.string.client_timeout);
                    break;
                case HttpURLConnection.HTTP_CONFLICT:  //409
                    retrunvalue = context.getString(R.string.http_conflict);
                    break;
                case HttpURLConnection.HTTP_CREATED:  //201
                    retrunvalue = context.getString(R.string.http_created);
                    break;
                case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:  //413
                    retrunvalue = context.getString(R.string.http_created);
                    break;
                default:
                    retrunvalue = context.getString(R.string.unknown_error);
                    break;
            }
        }

        return retrunvalue;
    }
}
