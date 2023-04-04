package sd2223.trab1.server.rest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class FeedsResource implements FeedsService {

	private final Map<String, Map<Long,String>> subs = new ConcurrentHashMap<>();
    private final Map<String, Map<Long,Message>> personalFeeds = new ConcurrentHashMap<>();
    private final RestUserResource ur = new RestUserResource();;

    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());

    public FeedsResource() {
    }
	
	@Override
	public long postMessage(String user, String pwd, Message msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeFromPersonalFeed(String user, long mid, String pwd) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message getMessage(String user, long mid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> getMessages(String user, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subUser(String user, String userSub, String pwd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribeUser(String user, String userSub, String pwd) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> listSubs(String user) {
		// TODO Auto-generated method stub
		return null;
	}

}
