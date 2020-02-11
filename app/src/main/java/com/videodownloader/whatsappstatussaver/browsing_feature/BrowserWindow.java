package com.videodownloader.whatsappstatussaver.browsing_feature;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.videodownloader.whatsappstatussaver.MainActivity;
import com.videodownloader.whatsappstatussaver.R;
import com.videodownloader.whatsappstatussaver.VDApp;
import com.videodownloader.whatsappstatussaver.VDFragment;
import com.videodownloader.whatsappstatussaver.custom_video_view.CustomMediaController;
import com.videodownloader.whatsappstatussaver.custom_video_view.CustomVideoView;
import com.videodownloader.whatsappstatussaver.history_feature.HistorySQLite;
import com.videodownloader.whatsappstatussaver.history_feature.VisitedPage;
import com.videodownloader.whatsappstatussaver.utils.Utils;

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class BrowserWindow extends VDFragment implements View.OnClickListener, MainActivity.OnBackPressedListener {
    private String url;
    private View view;
    private TouchableWebView page;
    private SSLSocketFactory defaultSSLSF;

    private FrameLayout videoFoundTV;
    private CustomVideoView videoFoundView;
    private CustomMediaController mediaFoundController;
    private FloatingActionButton videosFoundHUD;
    private float prevX, prevY;
    private boolean moved = false;
    private GestureDetector gesture;

    private View foundVideosWindow;
    private VideoList videoList;
    private ImageView foundVideosClose;

    private ProgressBar loadingPageProgress;

    private int orientation;
    private boolean loadedFirsTime;

    private List<String> blockedWebsites;

    @Override
    public void onClick(View v) {
        if (v == videosFoundHUD) {
            foundVideosWindow.setVisibility(View.VISIBLE);
        } else if (v == foundVideosClose) {
            foundVideosWindow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        url = data.getString("url");
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory();
        blockedWebsites = Arrays.asList(getResources().getStringArray(R.array.blocked_sites));
        setRetainInstance(true);
    }

    private void createVideosFoundTV() {
        videoFoundTV = view.findViewById(R.id.videoFoundTV);
        videoFoundView = view.findViewById(R.id.videoFoundView);
        mediaFoundController =  view.findViewById(R.id.mediaFoundController);
        mediaFoundController.setFullscreenEnabled(false);
        videoFoundView.setMediaController(mediaFoundController);
        videoFoundTV.setVisibility(View.GONE);
    }

    private void createVideosFoundHUD() {
        videosFoundHUD = view.findViewById(R.id.videosFoundHUD);
        videosFoundHUD.setOnClickListener(this);
        gesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                videosFoundHUD.performClick();
                return true;
            }
        });
    }

    private void createFoundVideosWindow() {
        View oldFoundVideosWindow = foundVideosWindow;
        foundVideosWindow = view.findViewById(R.id.foundVideosWindow);
        if (videoList != null) {
            videoList.recreateVideoList((RecyclerView) foundVideosWindow.findViewById(R.id.videoList));
        } else {
            videoList = new VideoList(getActivity(), (RecyclerView) foundVideosWindow.findViewById(R.id.videoList)) {
                @Override
                void onItemDeleted() {
                    getVDActivity().loadInterstitialAd();
                    updateFoundVideosBar();
                }

                @Override
                void onVideoPlayed(String url) {
                    updateVideoPlayer(url);
                }
            };
        }

        if (oldFoundVideosWindow != null) {
            switch (oldFoundVideosWindow.getVisibility()) {
                case View.VISIBLE:
                    foundVideosWindow.setVisibility(View.VISIBLE);
                    break;
                case View.GONE:
                    foundVideosWindow.setVisibility(View.GONE);
                    break;
                case View.INVISIBLE:
                    foundVideosWindow.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            foundVideosWindow.setVisibility(View.GONE);
        }

        foundVideosClose = foundVideosWindow.findViewById(R.id.foundVideosClose);
        foundVideosClose.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (view == null || getResources().getConfiguration().orientation != orientation) {
            int visibility = View.VISIBLE;
            if (view != null) {
                visibility = view.getVisibility();
            }
            view = inflater.inflate(R.layout.browser, container, false);
            view.setVisibility(visibility);
            if (page == null) {
                page = view.findViewById(R.id.page);
            } else {
                View page1 = view.findViewById(R.id.page);
                ((ViewGroup) view).removeView(page1);
                ((ViewGroup) page.getParent()).removeView(page);
                ((ViewGroup) view).addView(page);
                ((ViewGroup) view).bringChildToFront(view.findViewById(R.id.videosFoundHUD));
                ((ViewGroup) view).bringChildToFront(view.findViewById(R.id.foundVideosWindow));
            }
            loadingPageProgress = view.findViewById(R.id.loadingPageProgress);
            loadingPageProgress.setVisibility(View.GONE);

            createVideosFoundHUD();
            createVideosFoundTV();
            createFoundVideosWindow();
            updateFoundVideosBar();
        }

        return view;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (!loadedFirsTime) {
            WebSettings webSettings = page.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            page.setWebViewClient(new WebViewClient() {//it seems not setting webclient, launches
                //default browser instead of opening the page in webview
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if(blockedWebsites.contains(Utils.getBaseDomain(request.getUrl().toString()))){
                        Log.d("vdd", "URL : " + request.getUrl().toString());
                        new AlertDialog.Builder(getContext())
                                .setMessage("Youtube is not supported according to google policy.")
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create()
                                .show();
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageStarted(final WebView webview, final String url, Bitmap favicon) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            EditText urlBox = getVDActivity().findViewById(R.id.et_search_bar);
                            urlBox.setText(url);
                            urlBox.setSelection(urlBox.getText().length());
                            BrowserWindow.this.url = url;
                        }
                    });
                    view.findViewById(R.id.loadingProgress).setVisibility(View.GONE);
                    loadingPageProgress.setVisibility(View.VISIBLE);
                    super.onPageStarted(webview, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    loadingPageProgress.setVisibility(View.GONE);
                }

                @Override
                public void onLoadResource(final WebView view, final String url) {
                    Log.d("fb :", "URL: " + url);
                    final String page = view.getUrl();
                    final String title = view.getTitle();

                    new VideoContentSearch(getActivity(), url, page, title) {
                        @Override
                        public void onStartInspectingURL() {
                            Utils.disableSSLCertificateChecking();
                        }

                        @Override
                        public void onFinishedInspectingURL(boolean finishedAll) {
                            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF);
                        }

                        @Override
                        public void onVideoFound(String size, String type, String link, String name, String page, boolean chunked, String website, boolean audio) {
                            videoList.addItem(size, type, link, name, page, chunked, website, audio);
                            updateFoundVideosBar();
                        }
                    }.start();
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    if (getActivity() != null) {
                        Log.d("VDDebug", "Url: " + url);
                        if (getActivity().getSharedPreferences("settings", 0).getBoolean(getString(R
                                .string.adBlockON), true)
                                && (url.contains("ad") || url.contains("banner") || url.contains("pop"))
                                && getVDActivity().getBrowserManager().checkUrlIfAds(url)) {
                            Log.d("VDDebug", "Ads detected: " + url);
                            return new WebResourceResponse(null, null, null);
                        }
                    }
                    return super.shouldInterceptRequest(view, url);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getVDActivity() !=
                            null) {
                        if (VDApp.getInstance().getSharedPreferences("settings", 0).getBoolean(getString
                                (R.string.adBlockON), true)
                                && (request.getUrl().toString().contains("ad") ||
                                request.getUrl().toString().contains("banner") ||
                                request.getUrl().toString().contains("pop"))
                                && getVDActivity().getBrowserManager().checkUrlIfAds(request.getUrl()
                                .toString())) {
                            Log.i("VDInfo", "Ads detected: " + request.getUrl().toString());
                            return new WebResourceResponse(null, null, null);
                        } else return null;
                    } else {
                        return shouldInterceptRequest(view, request.getUrl().toString());
                    }
                }
            });
            page.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    loadingPageProgress.setProgress(newProgress);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    videoList.deleteAllItems();
                    updateFoundVideosBar();
                    VisitedPage vp = new VisitedPage();
                    vp.title = title;
                    vp.link = view.getUrl();
                    new HistorySQLite(getActivity()).addPageToHistory(vp);
                }
            });

            page.loadUrl(url);
            loadedFirsTime = true;
        } else {
            EditText urlBox = getVDActivity().findViewById(R.id.et_search_bar);
            urlBox.setText(url);
            urlBox.setSelection(urlBox.getText().length());
        }
    }

    @Override
    public void onDestroy() {
        page.stopLoading();
        page.destroy();
        super.onDestroy();
    }

    private void updateFoundVideosBar() {
        if(getActivity() == null){
            Log.d("debug", "No activity found");
            return;
        }
        else {
            Log.d("debug", "Activity found");
            if (videoList.getSize() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //videosFoundHUD.setImageDrawable(getResources().getDrawable(R.drawable.icon_download));
                        videosFoundHUD.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                        videosFoundHUD.setEnabled(true);
                        Animation expandIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.expand_in);
                        videosFoundHUD.startAnimation(expandIn);
                    }
                });

            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //videosFoundHUD.setImageDrawable(getResources().getDrawable(R.drawable.icon_download_na));
                        videosFoundHUD.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
                        videosFoundHUD.setEnabled(false);
                        if (foundVideosWindow.getVisibility() == View.VISIBLE)
                            foundVideosWindow.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void updateVideoPlayer(String url){
        videoFoundTV.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(url);
        Log.d("debug", url);
        videoFoundView.setVideoURI(uri);
        videoFoundView.start();
    }

    @Override
    public void onBackpressed() {
        if (foundVideosWindow.getVisibility() == View.VISIBLE && !videoFoundView.isPlaying() && videoFoundTV.getVisibility() == View.GONE) {
            foundVideosWindow.setVisibility(View.GONE);
        } else if (videoFoundView.isPlaying() || videoFoundTV.getVisibility() == View.VISIBLE) {
            videoFoundView.closePlayer();
            videoFoundTV.setVisibility(View.GONE);
        } else if (page.canGoBack()) {
            page.goBack();
        } else {
            getVDActivity().getBrowserManager().closeWindow(BrowserWindow.this);
        }
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void onPause() {
        super.onPause();
        page.onPause();
        Log.d("debug", "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        page.onResume();
        Log.d("debug", "onResume: ");
    }


}
