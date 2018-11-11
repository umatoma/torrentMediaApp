package net.umatoma.torrentmediaapp.repository;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class TorrentRss {

    @Element(name = "channel")
    public TorrentChannel channel;

    public TorrentChannel getChannel() {
        return channel;
    }
}
