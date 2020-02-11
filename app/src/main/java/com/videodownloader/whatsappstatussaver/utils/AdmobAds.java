package com.videodownloader.whatsappstatussaver.utils;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.videodownloader.whatsappstatussaver.MainActivity;
import com.videodownloader.whatsappstatussaver.R;

public class AdmobAds {

    private Context mContext;
    private AdView mBannerAd;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequestInter = new AdRequest.Builder().build();

    public AdmobAds(Context context) {
        this.mContext = context;
        MobileAds.initialize(mContext, AdmobID.APP_ID);
        setBannerAd();
        setInterstitialAd();
    }

    public void setBannerAd(){
        mBannerAd = new AdView(mContext);
        mBannerAd.setAdSize(AdSize.SMART_BANNER);
        mBannerAd.setAdUnitId(AdmobID.BANNER_ID);
        LinearLayout adContainer = ((MainActivity) mContext).findViewById(R.id.main_banner_container);
        adContainer.addView(mBannerAd);
        mBannerAd.loadAd(adRequestInter);
    }

    public void setInterstitialAd(){
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(AdmobID.INTER_ID);
        mInterstitialAd.loadAd(adRequestInter);
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(adRequestInter);
            }
        });
    }

    public void loadInterstitialAd(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }
}
