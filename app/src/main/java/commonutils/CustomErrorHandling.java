package commonutils;

import android.content.Context;

import java.net.HttpURLConnection;

import Boomerang.R;
import retrofit.RetrofitError;

/**
 * Created by rahul on 3/13/2015.
 */
public class CustomErrorHandling {
    public static String ShowError(RetrofitError error,Context context){
        String retrunvalue="No response";
        if(error.getResponse()!=null){
            switch (error.getResponse().getStatus()){
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    retrunvalue=context.getString(R.string.badgateway);
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    retrunvalue=context.getString(R.string.client_timeout);
                    break;
                case HttpURLConnection.HTTP_SERVER_ERROR:
                    retrunvalue=context.getString(R.string.server_error);
                    break;
                default:
                    retrunvalue=context.getString(R.string.unknown_error);
                    break;

            }
        }

        return retrunvalue;
    }
}
