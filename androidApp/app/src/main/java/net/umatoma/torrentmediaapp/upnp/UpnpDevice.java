package net.umatoma.torrentmediaapp.upnp;

import java.util.ArrayList;

public class UpnpDevice {
    private String deviceType;
    private String friendlyName;
    private ArrayList<UpnpService> serviceList;
    private UpnpServer upnpServer;

    public UpnpDevice(String deviceType, String friendlyName, ArrayList<UpnpService> serviceList, UpnpServer upnpServer) {
        this.deviceType = deviceType;
        this.friendlyName = friendlyName;
        this.serviceList = serviceList;
        this.upnpServer = upnpServer;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ArrayList<UpnpService> getServiceList() {
        return serviceList;
    }

    public String getServerLocation() {
        return upnpServer.getLocation();
    }

    public UpnpService getAVTransportService() {
        for (UpnpService upnpService : this.serviceList) {
            if (upnpService.isAVTransportType()) {
                return upnpService;
            }
        }
        return null;
    }
}
