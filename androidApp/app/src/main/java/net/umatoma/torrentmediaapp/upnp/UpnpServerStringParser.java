package net.umatoma.torrentmediaapp.upnp;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpnpServerStringParser {
    public static UpnpServer parseString(String serverString, String address) {
        HashMap<String, String> headersMap = new HashMap<>();
        String[] lines = serverString.split("\r\n");
        for (String line : lines) {
            Matcher matcher = Pattern.compile("(.*): (.*)").matcher(line);
            if (matcher.matches()) {
                headersMap.put(matcher.group(1).toUpperCase(), matcher.group(2));
            }
        }

        return new UpnpServer(
                address,
                headersMap.get("LOCATION"),
                headersMap.get("SERVER"),
                headersMap.get("ST"),
                headersMap.get("USN")
        );
    }
}
