package net.umatoma.torrentmediaapp.upnp;

import java.util.ArrayList;

public class UpnpDevice {
    private String deviceType;
    private String friendlyName;
    private ArrayList<UpnpService> serviceList;

    public UpnpDevice(String deviceType, String friendlyName, ArrayList<UpnpService> serviceList) {
        this.deviceType = deviceType;
        this.friendlyName = friendlyName;
        this.serviceList = serviceList;
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
}
