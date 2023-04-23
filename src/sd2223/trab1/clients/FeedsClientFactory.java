package sd2223.trab1.clients;


import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.clients.rest.RestFeedsClient;
import sd2223.trab1.clients.soap.SoapFeedsClient;
import sd2223.trab1.api.Discovery;

import java.net.URI;

public class FeedsClientFactory {

    /** Constants */
    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    public static Feeds get(String domain) {
        URI serverURI = Discovery.getInstance().knownUrisOf(domain, "feeds");
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RestFeedsClient(serverURI);
        else if (uriString.endsWith(SOAP))
            return new SoapFeedsClient(serverURI);
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}