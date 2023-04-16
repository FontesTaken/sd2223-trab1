package sd2223.trab1.server.rest;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.Discovery;

public class RestFeedsServer {
	private static Logger Log = Logger.getLogger(RestFeedsServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 4567;
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		try {

			String service = "feeds." + args[0];

			ResourceConfig config = new ResourceConfig();
			config.register(RestFeedsResource.class);

			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
			Discovery.getInstance().announce(service, serverURI);
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", service, serverURI));

			// More code can be executed here...
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
}