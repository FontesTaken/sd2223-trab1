package sd2223.trab1.server.java;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.PersonalFeed;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.FeedsClientFactory;
import sd2223.trab1.clients.UsersClientFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JavaFeeds implements Feeds {
	/** Constants */
	private final ConcurrentHashMap<String, PersonalFeed> allFeeds = new ConcurrentHashMap<>();

	/** Variables */
	private final String domain;
	private final long base;
	private long msgIDSeq = 1;

	/** Constructor */
	public JavaFeeds(String domain, long base) {
		this.domain = domain;
		this.base = base;
	}

	@Override
	public Result<Long> postMessage(String user, String pwd, Message message) {
		// Check if user data is valid
		if (user == null || pwd == null || !user.split("@")[1].equals(domain)) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		// Check if user exists in domain or if password is correct
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, pwd);
		if (!result.isOK()) {
			return Result.error(result.error());
		}

		// Insert message in feed
		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null)
			userFeed = new PersonalFeed();
		message.setId(msgIDSeq++ * 256 + base);
		userFeed.addMessage(message);
		allFeeds.put(user, userFeed);

		return Result.ok(message.getId());
	}

	@Override
	public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
		// Check if user data is valid
		if (user == null || pwd == null) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		// Check if user exists in domain or if password is correct
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, pwd);
		if (!result.isOK()) {
			return Result.error(result.error());
		}

		// Check if message exists
		PersonalFeed userFeed = allFeeds.get(user);
		Message message;
		if (userFeed == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);
		else {
			message = userFeed.getMessage(mid);
		}

		// Remove message from feed
		userFeed.removeMessage(message);

		return Result.ok(null);
	}

	@Override
	public Result<Message> getMessage(String user, long mid) {
		// Check user is remote
		String domain = user.split("@")[1];
		if (!domain.equals(this.domain)) {
			URI domainURI = Discovery.getInstance().knownUrisOf(domain, "feeds");
			var client = FeedsClientFactory.get(domainURI);
			return client.getMessage(user, mid);
		}

		// User is local
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, ""); // Basta saber se o user existe, não precisamos de dar uma pwd "correta"
		if (!result.isOK()) {
			if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
				return Result.error(result.error());
			}
		}
		PersonalFeed userFeed = allFeeds.get(user);

		if (userFeed == null) {
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}

		// Get message
		Message message = userFeed.getMessage(mid);
		if (message == null) { // Pode estar na lista de subs
			for (String u : userFeed.getSubscribers()) {
				String[] user_domain = u.split("@");

				if (user_domain[1].equals(this.domain)) {
					// Internal domain propagation
					message = allFeeds.get(u).getMessage(mid);

				} else {
					// Remote domain propagation
					URI domainURIRemote = Discovery.getInstance().knownUrisOf(user_domain[1], "feeds");
					var clientRemote = FeedsClientFactory.get(domainURIRemote);
					var resultRemote = clientRemote.getMessage(u, mid);

					if (!resultRemote.isOK()) {
						continue;
					}
					message = resultRemote.value();
				}

				// Quit if found..
				if (message != null) {
					break;
				}
			}
		} // Propagate to get message...

		// Check if message exists
		if (message == null) {
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}
		return Result.ok(message);
	}

	@Override
	public Result<List<Message>> getMessages(String user, long time) {
		// Check user is remote
		String domain = user.split("@")[1];
		if (!domain.equals(this.domain)) {
			URI domainURI = Discovery.getInstance().knownUrisOf(domain, "feeds");
			var client = FeedsClientFactory.get(domainURI);
			return client.getMessages(user, time);
		}

		// User is local. Check user exists
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, "");
		if (!result.isOK()) {
			if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
				return Result.error(result.error());
			}
		}

		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null) {
			return Result.ok(new LinkedList<>());
		} // If there are no messages, return immediately..

		// Concatenate messages
		List<Message> ownMessages = userFeed.getMessages(time);
		List<Message> propagatedMessages = new LinkedList<>();
		for (String u : userFeed.getSubscribers()) {
			String[] user_domain = u.split("@");

			if (user_domain[1].equals(this.domain)) {
				// Internal domain propagation
				var feed = allFeeds.get(u);
				if (feed != null) {
					propagatedMessages.addAll(feed.getMessages(time));
				}
			} else {
				// Remote domain propagation
				URI domainURIRemote = Discovery.getInstance().knownUrisOf(user_domain[1],"feeds");
				var clientRemote = FeedsClientFactory.get(domainURIRemote);
				var resultRemote = clientRemote.getMessagesFromRemote(u, user_domain[1], time);

				if (!result.isOK()) {
					continue;
				}

				propagatedMessages.addAll(resultRemote.value());
			}
		}
		ownMessages.addAll(propagatedMessages);

		return Result.ok(ownMessages);
	}

	@Override
	public Result<List<Message>> getMessagesFromRemote(String user, String originalDomain, long time) {
		// Check if user exists in domain or if password is correct
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, ""); // Basta saber se o user existe, não precisamos de dar uma pwd "correta"
		if (!result.isOK()) {
			if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
				return Result.error(result.error());
			}
		}

		// Check if not in a remote domain
		if (user.split("@")[1].equals(originalDomain)) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null) {
			return Result.ok(new LinkedList<>());
		} // If there are no messages, return immediately..

		// Gets own messages
		var messages = userFeed.getMessages(time);

		return Result.ok(new ArrayList<>(messages));
	}

	@Override
	public Result<Void> subUser(String user, String userSub, String pwd) {
		// Check if user data is valid
		if (user == null || pwd == null) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		// Check if user exists in domain or if password is correct
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, pwd);
		if (!result.isOK()) {
			return Result.error(result.error());
		}

		// Add subscription
		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null)
			userFeed = new PersonalFeed();
		userFeed.addSubscriber(userSub);
		allFeeds.put(user, userFeed);
		return Result.ok();
	}

	@Override
	public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
		// Check if user data is valid
		if (user == null || pwd == null) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		// Check if user exists in domain or if password is correct
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, pwd);
		if (!result.isOK()) {
			return Result.error(result.error());
		}

		// Unsubscribe
		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null)
			userFeed = new PersonalFeed();
		userFeed.removeSubscriber(userSub);
		allFeeds.put(user, userFeed);
		return Result.ok();
	}

	@Override
	public Result<List<String>> listSubs(String user) {
		// Check if user data is valid
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, "");
		if (!result.isOK()) {
			if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
				return Result.error(result.error());
			}
		}

		PersonalFeed userFeed = allFeeds.get(user);
		if (userFeed == null) {
			userFeed = new PersonalFeed();
	        allFeeds.put(user, userFeed);
		}

		return Result.ok(new LinkedList<>(userFeed.getSubscribers()));
	}

	@Override
	public Result<Void> deleteFeed(String user) {
		URI domainURI = Discovery.getInstance().knownUrisOf(domain,"users");
		var client = UsersClientFactory.get(domainURI);
		var result = client.getUser(user, "");
		if (!result.isOK()) {
			if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
				return Result.error(result.error());
			}
		}

		allFeeds.remove(user);
		return Result.ok(null);
	}
}