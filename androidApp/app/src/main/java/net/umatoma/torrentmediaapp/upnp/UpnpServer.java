package net.umatoma.torrentmediaapp.upnp;

public class UpnpServer {
    private final String ip;
    private final String location;
    private final String server;
    private final String serverType;
    private final String usn;

    public UpnpServer(String ip, String location, String server, String serverType, String usn) {
        this.ip = ip;
        this.location = location;
        this.server = server;
        this.serverType = serverType;
        this.usn = usn;
    }

    public String getLocation() {
        return location;
    }

    public String getServer() {
        return server;
    }

    public String getServerType() {
        return serverType;
    }

    public String getUsn() {
        return usn;
    }
}
