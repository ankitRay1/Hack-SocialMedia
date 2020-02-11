package com.videodownloader.whatsappstatussaver.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.videodownloader.whatsappstatussaver.R;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class Utils {

    private Utils() {
    }

    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
     * aid testing on a local box, not for use on production.
     */
    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity, IBinder token) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && token != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    token, 0);
        }
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getHrsMinsSecs(long milliseconds) {
        long totalHrs = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long totalHrsInMins = TimeUnit.HOURS.toMinutes(totalHrs);
        long totalMins = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long extraMins = totalMins - totalHrsInMins;
        long totalMinsInSecs = TimeUnit.MINUTES.toSeconds(totalMins);
        long totalSecs = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long extraSecs = totalSecs - totalMinsInSecs;
        if (totalHrs > 0) {
            return totalHrs + "h " + extraMins + "m " + extraSecs + "s";
        } else if (totalMins > 0) {
            return totalMins + "m " + extraSecs + "s";
        } else {
            return totalSecs + "s";
        }
    }

    public static DividerItemDecoration createDivider(final Context context) {
        DividerItemDecoration divider = new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                int verticalSpacing = (int) Math.ceil(TypedValue.applyDimension(TypedValue
                        .COMPLEX_UNIT_SP, 4, context.getResources()
                        .getDisplayMetrics()));
                outRect.top = verticalSpacing;
                outRect.bottom = verticalSpacing;
            }
        };
        divider.setDrawable(context.getResources().getDrawable(R.drawable.greydivider));
        return divider;
    }

    public static String getHost(String url){
        if(url == null || url.length() == 0)
            return "";

        int doubleslash = url.indexOf("//");
        if(doubleslash == -1)
            doubleslash = 0;
        else
            doubleslash += 2;

        int end = url.indexOf('/', doubleslash);
        end = end >= 0 ? end : url.length();

        int port = url.indexOf(':', doubleslash);
        end = (port > 0 && port < end) ? port : end;

        return url.substring(doubleslash, end);
    }

    public static String getBaseDomain(String url) {
        String host = getHost(url);
        int startIndex = 0;
        int nextIndex = host.indexOf('.');
        int lastIndex = host.lastIndexOf('.');
        while (nextIndex < lastIndex) {
            startIndex = nextIndex + 1;
            nextIndex = host.indexOf('.', startIndex);
        }
        if (startIndex > 0) {
            return host.substring(startIndex);
        } else {
            return host;
        }
    }
}
