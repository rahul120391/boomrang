package commonutils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

/**
 * Created by rahul on 3/4/2015.
 */
public class RetrofitHttpClient extends UrlConnectionClient {
    private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000; // 30s
    private static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private final OkUrlFactory factory;

    public RetrofitHttpClient() {
        factory = generateDefaultOkUrlFactory();
    }

    private static OkUrlFactory generateDefaultOkUrlFactory() {
        OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection openConnection(Request request) throws IOException {
        return factory.open(new URL(request.getUrl()));
    }
}
