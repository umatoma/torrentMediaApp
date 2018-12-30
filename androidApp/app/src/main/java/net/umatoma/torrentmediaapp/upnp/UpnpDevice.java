package net.umatoma.torrentmediaapp.upnp;

import java.util.ArrayList;

public class UpnpDevice {
    private String deviceType;
    private String friendlyName;
    private String manufacturer;
    private ArrayList<UpnpService> serviceList;
    private UpnpServer upnpServer;

    public UpnpDevice(String deviceType, String friendlyName, String manufacturer, ArrayList<UpnpService> serviceList, UpnpServer upnpServer) {
        this.deviceType = deviceType;
        this.friendlyName = friendlyName;
        this.manufacturer = manufacturer;
        this.serviceList = serviceList;
        this.upnpServer = upnpServer;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getManufacturer() {
        return manufacturer;
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
