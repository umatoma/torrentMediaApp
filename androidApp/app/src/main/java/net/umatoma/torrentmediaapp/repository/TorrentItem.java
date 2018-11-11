package net.umatoma.torrentmediaapp.repository;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class TorrentItem {

    @Element(required = false)
    public String title;

    @Element(required = false, data = true)
    public String description;

    @Element(required = false)
    public String pubDate;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

}
