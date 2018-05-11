package com.rli.inst.parser;

import com.google.common.base.MoreObjects;

import java.net.MalformedURLException;
import java.net.URL;

public class MediaSrc {
    public final String id;
    public final URL thumbnailSrc;
    public final URL displaySrc;
    public final URL videoSrc;
    public final boolean isVideo;

    public MediaSrc(String id, String thumbnailSrc, String displaySrc, String videoSrc, boolean isVideo) throws MalformedURLException {
        this.id = id;

        if (thumbnailSrc == null) {
            this.thumbnailSrc = null;
        } else {
            this.thumbnailSrc = new URL(thumbnailSrc);
        }

        if (displaySrc == null) {
            this.displaySrc = null;
        } else {
            this.displaySrc = new URL(displaySrc);
        }

        if (videoSrc == null || videoSrc.isEmpty()) {
            this.videoSrc = null;
        } else {
            this.videoSrc = new URL(videoSrc);
        }

        this.isVideo = isVideo;
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("thumbnailSrc", thumbnailSrc)
                .add("displaySrc", displaySrc)
                .add("videoSrc", videoSrc)
                .add("isVideo:", isVideo)
                .toString();
    }
}