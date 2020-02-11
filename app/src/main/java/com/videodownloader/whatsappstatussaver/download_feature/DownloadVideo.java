package com.videodownloader.whatsappstatussaver.download_feature;

import java.io.Serializable;

public class DownloadVideo implements Serializable {
    public String size, type, link, name, page, website;
    public boolean chunked;
}
