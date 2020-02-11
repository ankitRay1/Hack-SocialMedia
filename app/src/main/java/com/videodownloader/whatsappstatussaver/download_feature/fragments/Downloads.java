package com.videodownloader.whatsappstatussaver.download_feature.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.videodownloader.whatsappstatussaver.MainActivity;
import com.videodownloader.whatsappstatussaver.R;
import com.videodownloader.whatsappstatussaver.VDFragment;
import com.videodownloader.whatsappstatussaver.download_feature.DownloadManager;
import com.videodownloader.whatsappstatussaver.download_feature.Tracking;
import com.videodownloader.whatsappstatussaver.utils.Utils;

public class Downloads extends VDFragment implements MainActivity.OnBackPressedListener, Tracking, DownloadsInProgress.OnNumDownloadsInProgressChangeListener, DownloadsCompleted.OnNumDownloadsCompletedChangeListener {
    private View view;

    private TextView downloadSpeed;
    private TextView remaining;
    private Handler mainHandler;
    private Tracking tracking;

    //private TabLayout tabs;
    private TextView inProgressTab;
    private TextView completedTab;
    private TextView pageSelected;
    private ViewPager pager;
    private DownloadsInProgress downloadsInProgress;
    private DownloadsCompleted downloadsCompleted;

    @Override
    public void onDestroy() {
        Fragment fragment;
        if ((fragment = getFragmentManager().findFragmentByTag("downloadsInProgress")) != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        if ((fragment = getFragmentManager().findFragmentByTag("downloadsCompleted")) != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        super.onDestroy();
    }

    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        if (view == null) {
            view = inflater.inflate(R.layout.downloads, container, false);

            downloadSpeed = view.findViewById(R.id.downloadSpeed);
            remaining = view.findViewById(R.id.remaining);

            getVDActivity().setOnBackPressedListener(this);

            mainHandler = new Handler(Looper.getMainLooper());
            tracking = new Tracking();

            pager = view.findViewById(R.id.downloadsPager);
            pager.setAdapter(new PagerAdapter());

            /*if (Build.VERSION.SDK_INT >= 22) {
                tabs = view.findViewById(R.id.downloadsTabs);
                tabs.addTab(tabs.newTab());
                tabs.addTab(tabs.newTab());
                tabs.addTab(tabs.newTab());

                pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
                tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        pager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            } else {*/
            LinearLayout tabs0 = view.findViewById(R.id.downloadsTabs);
            inProgressTab = tabs0.findViewById(R.id.inProgressTab);
            completedTab = tabs0.findViewById(R.id.completedTab);

            pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    switch (position) {
                        case 0:
                            unboxPreviousSelectedPageTab();
                            boxNewSelectedPageTab(completedTab);
                            break;
                        case 1:
                            unboxPreviousSelectedPageTab();
                            boxNewSelectedPageTab(inProgressTab);
                            break;
                    }
                }
            });

            completedTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unboxPreviousSelectedPageTab();
                    boxNewSelectedPageTab(completedTab);
                    pager.setCurrentItem(0);
                }
            });


            inProgressTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unboxPreviousSelectedPageTab();
                    boxNewSelectedPageTab(inProgressTab);
                    pager.setCurrentItem(1);
                }
            });

            pager.setOffscreenPageLimit(2);//default is 1 which would make Inactive tab not diplay

            downloadsInProgress = new DownloadsInProgress();
            downloadsCompleted = new DownloadsCompleted();

            downloadsInProgress.setOnNumDownloadsInProgressChangeListener(this);
            downloadsCompleted.setOnNumDownloadsCompletedChangeListener(this);

            getFragmentManager().beginTransaction().add(pager.getId(), downloadsInProgress,
                    "downloadsInProgress").commit();
            getFragmentManager().beginTransaction().add(pager.getId(), downloadsCompleted,
                    "downloadsCompleted").commit();

            downloadsInProgress.setTracking(this);

            downloadsInProgress.setOnAddDownloadedVideoToCompletedListener(downloadsCompleted);

        }

        return view;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setCurrentItem(0);
        boxNewSelectedPageTab(completedTab);
    }

    private void unboxPreviousSelectedPageTab() {
        if (pageSelected != null) {
            pageSelected.setBackground(null);
            pageSelected = null;
        }
    }

    private void boxNewSelectedPageTab(TextView selected) {
        pageSelected = selected;
        pageSelected.setBackground(getResources().getDrawable(R.drawable.tab_text_bg));
    }

    @Override
    public void onBackpressed() {
        getVDActivity().getBrowserManager().unhideCurrentWindow();
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onNumDownloadsInProgressChange() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpannableStringBuilder tabText = createStyledTabText(12, downloadsInProgress
                        .getNumDownloadsInProgress(), "In Progress " + downloadsInProgress
                        .getNumDownloadsInProgress());
                /*if (Build.VERSION.SDK_INT >= 22) {
                    TabLayout.Tab tab = tabs.getTabAt(0);
                    if (tab != null) {
                        tab.setText(tabText);
                    }
                } else {*/
                    inProgressTab.setText(tabText);
            }
        });
    }

    @Override
    public void onNumDownloadsCompletedChange() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpannableStringBuilder tabText = createStyledTabText(10, downloadsCompleted
                        .getNumDownloadsCompleted(), "Completed " + downloadsCompleted
                        .getNumDownloadsCompleted());
                /*if (Build.VERSION.SDK_INT >= 22) {
                    TabLayout.Tab tab = tabs.getTabAt(1);
                    if (tab != null) {
                        tab.setText(tabText);
                    }
                } else {*/
                    completedTab.setText(tabText);
            }
        });
    }


    private SpannableStringBuilder createStyledTabText(int start, int num, String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        ForegroundColorSpan fcs;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fcs = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        } else {
            fcs = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue, null));
        }
        sb.setSpan(fcs, start, start + String.valueOf(num).length(), Spanned
                .SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    class Tracking implements Runnable {

        @Override
        public void run() {
            long downloadSpeedValue = DownloadManager.getDownloadSpeed();
            String downloadSpeedText = "Speed:" + Formatter.formatShortFileSize(getActivity(),
                    downloadSpeedValue) + "/s";

            downloadSpeed.setText(downloadSpeedText);

            if (downloadSpeedValue > 0) {
                long remainingMills = DownloadManager.getRemaining();
                String remainingText = "Remaining:" + Utils.getHrsMinsSecs(remainingMills);
                remaining.setText(remainingText);
            } else {
                remaining.setText(R.string.remaining_undefine);
            }

            if (getFragmentManager() != null && getFragmentManager().findFragmentByTag
                    ("downloadsInProgress") != null) {
                downloadsInProgress.updateDownloadItem();
            }
            mainHandler.postDelayed(this, 1000);
        }
    }

    public void startTracking() {
        getActivity().runOnUiThread(tracking);
    }

    public void stopTracking() {
        mainHandler.removeCallbacks(tracking);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadSpeed.setText(R.string.speed_0);
                remaining.setText(R.string.remaining_undefine);
                if (getFragmentManager().findFragmentByTag("downloadsInProgress") != null) {
                    downloadsInProgress.updateDownloadItem();
                }
            }
        });
    }

    class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                case 0:
                    return downloadsCompleted;
                case 1:
                    return downloadsInProgress;
                default:
                    return downloadsCompleted;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }
    }
}
