package net.umatoma.torrentmediaapp.upnp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class UpnpDeviceXmlparser {
    public static UpnpDevice parseXml(String xmlString) throws Exception {
        StringReader stringReader = new StringReader(xmlString);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        XmlPullParser parser = factory.newPullParser();
        parser.setInput(stringReader);
        parser.nextTag();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("device")) {
                return readDevice(parser);
            } else {
                skip(parser);
            }
        }

        throw new Exception("Failed to parse the XML");
    }

    private static UpnpDevice readDevice(XmlPullParser parser) throws IOException, XmlPullParserException {
        String deviceType = null;
        String friendlyName = null;
        ArrayList<UpnpService> serviceList = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("deviceType")) {
                deviceType = readText(parser);
            } else if (name.equals("friendlyName")) {
                friendlyName = readText(parser);
            } else if (name.equals("serviceList")) {
                serviceList = readServiceList(parser);
            } else {
                skip(parser);
            }
        }

        return new UpnpDevice(deviceType, friendlyName, serviceList);
    }

    private static ArrayList<UpnpService> readServiceList(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<UpnpService> serviceList = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("service")) {
                serviceList.add(readService(parser));
            } else {
                skip(parser);
            }
        }

        return serviceList;
    }

    private static UpnpService readService(XmlPullParser parser) throws IOException, XmlPullParserException {
        String serviceType = null;
        String serviceId = null;
        String SCPDURL = null;
        String controlURL = null;
        String eventSubURL = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("serviceType")) {
                serviceType = readText(parser);
            } else if (name.equals("serviceId")) {
                serviceId = readText(parser);
            } else if (name.equals("SCPDURL")) {
                SCPDURL = readText(parser);
            } else if (name.equals("controlURL")) {
                controlURL = readText(parser);
            } else if (name.equals("eventSubURL")) {
                eventSubURL = readText(parser);
            } else {
                skip(parser);
            }
        }

        return new UpnpService(serviceType, serviceId, SCPDURL, controlURL, eventSubURL);
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
