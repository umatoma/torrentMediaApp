package net.umatoma.torrentmediaapp.upnp;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpnpAVTransportPlayer {

    private static final String TAG = "UpnpAVTransportPlayer";
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");

    private UpnpDevice upnpDevice;

    public UpnpAVTransportPlayer(UpnpDevice upnpDevice) {
        this.upnpDevice = upnpDevice;
    }

    public void setAVTransportURI(String currentUri) throws Exception {
        String commandName = "SetAVTransportURI";
        String currentURIMetaData = TextUtils.htmlEncode(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:sec=\"http://www.sec.co.kr/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\">\n" +
                        "   <item id=\"f-0\" parentID=\"0\" restricted=\"0\">\n" +
                        "      <dc:title>Video</dc:title>\n" +
                        "      <dc:creator>vGet</dc:creator>\n" +
                        "      <upnp:class>object.item.videoItem</upnp:class>\n" +
                        "      <res protocolInfo=\"http-get:*:video/mp4:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01700000000000000000000000000000\" sec:URIType=\"public\">" + currentUri + "</res>\n" +
                        "   </item>\n" +
                        "</DIDL-Lite>"
        );
        String body = "" +
                "<InstanceID>0</InstanceID>" +
                "<CurrentURI>" + currentUri + "</CurrentURI>" +
                "<CurrentURIMetaData>" + currentURIMetaData + "</CurrentURIMetaData>";
        String xmlBody = new CommandXmlBuilder()
                .setCommandName(commandName)
                .setBody(body)
                .build();

        this.sendCommand(commandName, xmlBody);
    }

    public void seek() throws Exception {
        String commandName = "Seek";
        String body = "<InstanceID>0</InstanceID><Unit>REL_TIME</Unit><Target>00:00:00</Target>";
        String xmlBody = new CommandXmlBuilder()
                .setCommandName(commandName)
                .setBody(body)
                .build();

        this.sendCommand(commandName, xmlBody);
    }

    public void stop() throws Exception {
        String commandName = "Stop";
        String body = "<InstanceID>0</InstanceID>";
        String xmlBody = new CommandXmlBuilder()
                .setCommandName(commandName)
                .setBody(body)
                .build();

        this.sendCommand(commandName, xmlBody);
    }

    public void play() throws Exception {
        String commandName = "Play";
        String body = "<InstanceID>0</InstanceID><Speed>1</Speed>";
        String xmlBody = new CommandXmlBuilder()
                .setCommandName(commandName)
                .setBody(body)
                .build();

        this.sendCommand(commandName, xmlBody);
    }

    private void sendCommand(final String commandName, String xmlBody) throws Exception {
        String controlUrl = Uri.parse(this.upnpDevice.getServerLocation())
                .buildUpon()
                .path(this.upnpDevice.getAVTransportService().getControlURL())
                .build()
                .toString();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_XML, xmlBody);
        Request request = new Request.Builder()
                .url(controlUrl)
                .post(requestBody)
                .addHeader("Soapaction", "\"urn:schemas-upnp-org:service:AVTransport:1#" + commandName + "\"")
                .build();

        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Response response = httpClient.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            Log.i(TAG, "Succeeded in requesting the " + commandName + " command");
            return;
        }

        String errorMessage = "Failed to request the " + commandName + " command: " + String.valueOf(response.code());
        Log.e(TAG, errorMessage);
        Log.e(TAG, responseBody);
        throw new Exception(errorMessage);
    }

    private class CommandXmlBuilder {
        private String commandName;
        private String body;

        public CommandXmlBuilder setCommandName(String commandName) {
            this.commandName = commandName;
            return this;
        }

        public CommandXmlBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public String build() {
            return "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                    "    <s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "    <s:Body>" +
                    "        <u:" + this.commandName + " xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">" +
                    "            " + this.body +
                    "        </u:" + this.commandName + ">" +
                    "    </s:Body>" +
                    "</s:Envelope>";
        }
    }

}
