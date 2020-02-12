# Hack-SocialMedia
##(Download Demo App)[https://drive.google.com/file/d/1ncn7qBKjtHx2NY1cmsPSZ-GILW5OURx-/view?usp=sharing]

Hack-SocialMedia app auto detects videos and you can download them with just one click. It has in-built download manager which allows you to pause and resume downloads, download in the background, download several files at the same time and rename downloaded file name. Hack-SocialMedia has an in built private browser that helps you to easily search videos from different websites. This app can also be used as whatsapp video status saver along with video downloader.


Hey! Here is  the App Documentation

Resources

⦁  activity_main.xml 

Main Activity xml which has toolbar, recycler view for video sites,banner ad and bottom navigation

⦁  Toolbar -- home_toolbar.xml 

⦁  Bottom navigation -- items added from bottom_nav_menu.xml in menu folder

⦁  Video_sites_item.xml -- How video sites will look (video icon & name)

⦁  activity_splash.xml

Splash activity xml with logo and app name

⦁  browser.xml

Whenever any video site will be clicked, this activity loads. This activity has webview, video found view (icon), video found window with recycler view for video list and video view for playing video.

⦁  Animation for video found button is in anim folder

⦁  Videos_found_item.xml is for how founded video will look

⦁  download.xml

Download xml has download toolbar, download speed and download tabs for completion and inprogress fragment

⦁  Downloads_completed.xml -- Recycler view for download completed list, delete button and folder location button

⦁  Downloads_completed_item.xml -- How completed item will look icon and name

⦁  Downloads_menu.xml -- In menu folder, Popup to delete, rename and share

⦁  Downloads_in_progress.xml -- How the items which are being downloaded will look, recycler view for list and pause/play button 

⦁  Downloads_in_progress_menu.xml -- In menu folder, Popup to delete and rename 

⦁  history.xml

             History xml has history search toolbar at top where you can search for any particular website you want. This activity has recycler view to show history items.

⦁  History_item -- How history list item will look 

⦁  History_menu -- In menu folder, Popup to delete, copy and open url

⦁  whatsapp.xml

             Whatsapp xml is for showing whatsapp video status in recycler view. You can see three buttons on each item that is share, share on whatsapp and download.

⦁  Settings.xml

Toolbar, switch for all settings, privacy policy and rating 

⦁  Values folder:

⦁  Colors -- For changing color scheme of app

⦁  Dimens -- for custom dimension 

⦁  Settings_key -- string name for saving settings

⦁  Strings -- all the text that is in app is from here, to change anything change here

⦁  XML folder:

⦁  Paths.xml -- It has path name and that is same as app_name in string.xml (If you change app name string then change here also)

⦁  Change admode (fb or admob from here)

JAVA Code

⦁  AdmobID: Contains app id, banner and inter id

⦁  Splash activity: For loading splash screen 

⦁  Main activity: 

⦁  setUPToolbarView: Method for initializing toolbar with search edittext box

⦁  setUPVideoSites: Method for recycler view setup for video sites (VideoSitesList class have all the site items declared)

⦁  setUPAdView: Method for initializing banner and interstitial ad

⦁  Settings activity:

⦁  All the settings button is here only

⦁  For browser: All the codes are in browsing_feature folder

⦁  For download: All the code are in download_feature folder

⦁  For history:  All the code are in history_feature folder

⦁  For whatsapp : All the code are in whatsapp_feature folder

What to do for blocking websites?

In strings.xml add the sites you want to block in blocked_sites array. Don't forget to add .com at the end.
