package net.umatoma.torrentmediaapp.repository;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict = false)
public class TorrentChannel {

    @ElementList(entry = "item", inline = true)
    public List<TorrentItem> items;

    public List<TorrentItem> getItems() {
        return items;
    }

}
