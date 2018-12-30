package net.umatoma.torrentmediaapp.upnp;

import android.net.Uri;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpnpAVTransportPlayer {

    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");

    private UpnpDevice upnpDevice;

    public UpnpAVTransportPlayer(UpnpDevice upnpDevice) {
        this.upnpDevice = upnpDevice;
    }

    public UpnpAVTransportPlayer setAVTransportURI(String currentUri, final CommandCallback commandCallback) {
        String xml = "" +
                "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                "<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<s:Body>" +
                "<u:SetAVTransportURI xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">" +
                "<InstanceID>0</InstanceID>" +
                "<CurrentURI>" + currentUri + "</CurrentURI>" +
                "<CurrentURIMetaData>" +
                "&lt;DIDL-Lite " +
                "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
                "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
                "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                "xmlns:sec=\"http://www.sec.co.kr/\"&gt;&lt;" +
                "item " +
                "id=\"f-0\" " +
                "parentID=\"0\" " +
                "restricted=\"0\"&gt;&lt;dc:title&gt;Video&lt;/dc:title&gt;&lt;dc:creator&gt;vGet&lt;/dc:creator&gt;&lt;upnp:class&gt;object.item.videoItem&lt;/upnp:class&gt;&lt;res " +
                "protocolInfo=\"http-get:*:video/mp4:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01700000000000000000000000000000\" " +
                "sec:URIType=\"public\"&gt;" + currentUri + "&lt;/res&gt;&lt;" +
                "/item&gt;&lt;" +
                "/DIDL-Lite&gt;" +
                "</CurrentURIMetaData>" +
                "</u:SetAVTransportURI>" +
                "</s:Body>" +
                "</s:Envelope>";
        return this.sendCommand("SetAVTransportURI", xml, commandCallback);
    }

    public UpnpAVTransportPlayer seek(final CommandCallback commandCallback) {
        String xml = "" +
                "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                "<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<s:Body>" +
                "<u:Seek xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">" +
                "<InstanceID>0</InstanceID>" +
                "<Unit>REL_TIME</Unit>" +
                "<Target>00:00:00</Target>" +
                "</u:Seek>" +
                "</s:Body>" +
                "</s:Envelope>";
        return this.sendCommand("Seek", xml, commandCallback);
    }

    public UpnpAVTransportPlayer play(final CommandCallback commandCallback) {
        String xml = "" +
                "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                "<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<s:Body>" +
                "<u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">" +
                "<InstanceID>0</InstanceID>" +
                "<Speed>1</Speed>" +
                "</u:Play>" +
                "</s:Body>" +
                "</s:Envelope>";
        return this.sendCommand("Play", xml, commandCallback);
    }

    private UpnpAVTransportPlayer sendCommand(final String commandName, String xmlBody, final CommandCallback commandCallback) {
        String controlUrl = Uri.parse(this.upnpDevice.getServerLocation())
                .buildUpon()
                .path(this.upnpDevice.getAVTransportService().getControlURL())
                .build()
                .toString();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_XML, xmlBody);
        Request request = new Request.Builder()
                .url(controlUrl)
                .post(requestBody)
                .addHeader("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#" + commandName + "\"")
                .build();

        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                commandCallback.onCommandFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    commandCallback.onCommandSuccess();
                } else {
                    commandCallback.onCommandFailure(new Exception("Command " + commandName + " failed"));
                }
            }
        });

        return this;
    }

    public interface CommandCallback {
        void onCommandFailure(Exception e);

        void onCommandSuccess();
    }
}
