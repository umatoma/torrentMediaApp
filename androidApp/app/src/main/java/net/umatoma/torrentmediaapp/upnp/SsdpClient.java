package net.umatoma.torrentmediaapp.upnp;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SsdpClient {

    private static final String SSDP_ADDRESS = "239.255.255.250";
    private static final int SSDP_PORT = 1900;

    private static final String SSDP_ALL = "ssdp:all";
    private static final String DEVICE_MEDIA_RENDERER = "urn:schemas-upnp-org:device:MediaRenderer:1";

    private final int timeout = 3000;
    private SsdpDiscoveryListener ssdpDiscoveryListener = null;
    private ArrayList<UpnpServer> upnpServerList = new ArrayList<>();
    private ArrayList<SsdpDiscoveryFilter> discoveryFilterList = new ArrayList<>();

    public interface SsdpDiscoveryListener {
        void onDeviceDiscovered(UpnpDevice upnpDevice);

        void onError(Exception ex);
    }

    public SsdpClient setSsdpDiscoveryListener(SsdpDiscoveryListener ssdpDiscoveryListener) {
        this.ssdpDiscoveryListener = ssdpDiscoveryListener;
        return this;
    }

    public SsdpClient addDeviceMediaRendererFilter() {
        this.discoveryFilterList.add(new SsdpDiscoveryFilter() {
            @Override
            public boolean isTargetServer(UpnpServer upnpServer) {
                return upnpServer.getServerType().equals(DEVICE_MEDIA_RENDERER);
            }
        });
        return this;
    }

    public SsdpClient addServerWebOSFilter() {
        this.discoveryFilterList.add(new SsdpDiscoveryFilter() {
            @Override
            public boolean isTargetServer(UpnpServer upnpServer) {
                return upnpServer.getServer().contains("WebOS");
            }
        });
        return this;
    }

    public SsdpClient addExcludeSameUsnFilter() {
        this.discoveryFilterList.add(new SsdpDiscoveryFilter() {
            @Override
            public boolean isTargetServer(UpnpServer upnpServer) {
                for (UpnpServer addedUpnpServer : SsdpClient.this.upnpServerList) {
                    if (addedUpnpServer.getUsn().equals(upnpServer.getUsn())) {
                        return false;
                    }
                }
                return true;
            }
        });
        return this;
    }

    public void startDiscovery(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("SsdpClient");

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;

                multicastLock.acquire();

                try {
                    socket = SsdpClient.this.sendMSearchPacket();

                    while (true) {
                        try {
                            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                            socket.receive(datagramPacket);

                            UpnpServer upnpServer = UpnpServerStringParser.parseString(
                                    new String(datagramPacket.getData()), datagramPacket.getAddress().getHostAddress());

                            if (SsdpClient.this.isTargetServer(upnpServer)) {
                                SsdpClient.this.upnpServerList.add(upnpServer);
                                SsdpClient.this.getSsdpDevice(upnpServer, new GetSsdpDeviceCallback() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        SsdpClient.this.ssdpDiscoveryListener.onError(e);
                                    }

                                    @Override
                                    public void onSuccess(UpnpDevice upnpDevice) {
                                        SsdpClient.this.ssdpDiscoveryListener.onDeviceDiscovered(upnpDevice);
                                    }
                                });
                            }
                        } catch (SocketTimeoutException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    SsdpClient.this.ssdpDiscoveryListener.onError(e);
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                    if (multicastLock.isHeld()) {
                        multicastLock.release();
                    }
                }
            }
        }).start();
    }

    private DatagramSocket sendMSearchPacket() throws IOException {
        byte[] message = new StringBuilder()
                .append("M-SEARCH * HTTP/1.1\r\n")
                .append("HOST: " + SSDP_ADDRESS + ":" + SSDP_PORT + "\r\n")
                .append("MAN: \"ssdp:discover\"\r\n")
                .append("MX: 5\r\n")
                .append("ST: " + SSDP_ALL + "\r\n")
                .append("\r\n")
                .toString()
                .getBytes();
        InetAddress address = InetAddress.getByName(SSDP_ADDRESS);
        DatagramPacket messagePacket = new DatagramPacket(message, message.length, address, SSDP_PORT);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(SsdpClient.this.timeout);
        socket.setReuseAddress(true);
        socket.send(messagePacket);

        return socket;
    }

    private void getSsdpDevice(UpnpServer upnpServer, final GetSsdpDeviceCallback callback) {
        Request request = new Request.Builder()
                .url(upnpServer.getLocation())
                .get()
                .build();

        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String xmlString = response.body().string();
                    UpnpDevice upnpDevice = UpnpDeviceXmlparser.parseXml(xmlString);
                    callback.onSuccess(upnpDevice);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private boolean isTargetServer(UpnpServer upnpServer) {
        for (SsdpDiscoveryFilter discoveryFilter : this.discoveryFilterList) {
            if (!discoveryFilter.isTargetServer(upnpServer)) {
                return false;
            }
        }
        return true;
    }

    private interface GetSsdpDeviceCallback {
        void onFailure(Exception e);

        void onSuccess(UpnpDevice upnpDevice);
    }

    private interface SsdpDiscoveryFilter {
        boolean isTargetServer(UpnpServer upnpServer);
    }
}
