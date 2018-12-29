package net.umatoma.torrentmediaapp.upnp;

public class UpnpService {
    private String serviceType;
    private String serviceId;
    private String SCPDURL;
    private String controlURL;
    private String eventSubURL;

    public UpnpService(String serviceType, String serviceId, String SCPDURL, String controlURL, String eventSubURL) {
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.SCPDURL = SCPDURL;
        this.controlURL = controlURL;
        this.eventSubURL = eventSubURL;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getSCPDURL() {
        return SCPDURL;
    }

    public String getControlURL() {
        return controlURL;
    }

    public String getEventSubURL() {
        return eventSubURL;
    }
}
