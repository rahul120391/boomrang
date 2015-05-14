package commonutils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rahul on 5/14/2015.
 */
public class UnCaughtException implements Thread.UncaughtExceptionHandler {

    private Context context;

    public UnCaughtException(Context ctx) {
        context = ctx;
    }
    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private void addInformation(StringBuilder message) {
        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL)
                .append('\n');
        message.append("Android Version: ")
                .append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT)
                .append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
    }

    public void uncaughtException(Thread t, Throwable e) {
        try {
            StringBuilder report = new StringBuilder();
            Date curDate = new Date();
            report.append("Error Report collected on : ")
                    .append(curDate.toString()).append('\n').append('\n');
            report.append("Informations :").append('\n');
            addInformation(report);
            report.append('\n').append('\n');
            report.append("Stack:\n");
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            report.append(result.toString());
            printWriter.close();
            report.append('\n');
            report.append("**** End of current Report ***");
            Log.e(UnCaughtException.class.getName(),
                    "Error while sendErrorMail" + report);
            sendErrorMail(report);
        } catch (Throwable ignore) {
            Log.e(UnCaughtException.class.getName(),
                    "Error while sending error e-mail", ignore);
        }
    }

    public void sendErrorMail(final StringBuilder errorContent) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ((Activity)context).finish();
                Looper.loop();
            }
        }.start();
    }
}
