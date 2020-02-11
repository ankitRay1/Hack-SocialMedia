package com.videodownloader.whatsappstatussaver.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.videodownloader.whatsappstatussaver.MainActivity;
import com.videodownloader.whatsappstatussaver.R;

public class FbAds {

    private Context mContext;
    private AdView mBannerAd;
    private InterstitialAd mInterstitialAd;

    public FbAds(Context context) {
        this.mContext = context;
        AudienceNetworkAds.initialize(mContext);
        setBannerAd();
        setInterstitialAd();
    }

    public void setBannerAd(){
        mBannerAd = new AdView(mContext, FbID.BANNER_ID, AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = ((MainActivity) mContext).findViewById(R.id.main_banner_container);
        adContainer.addView(mBannerAd);
        mBannerAd.loadAd();
    }

    public void setInterstitialAd(){
        mInterstitialAd = new InterstitialAd(mContext, FbID.INTER_ID);
        mInterstitialAd.loadAd();
    }

    public void loadInterstitialAd(){
        if(isInterLoaded()){
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    mInterstitialAd.loadAd();
                }
            });
        }
    }

    private boolean isInterLoaded(){
        if(mInterstitialAd == null || !mInterstitialAd.isAdLoaded()) {
            return false;
        }
        if(mInterstitialAd.isAdInvalidated()) {
            return false;
        }
        return true;
    }
}
