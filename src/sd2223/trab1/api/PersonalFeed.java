package sd2223.trab1.api;

import java.util.*;

public class PersonalFeed {

	private final List<Message> ownMessages;
	private final Set<String> mySubsList;

	public PersonalFeed() {
		this.ownMessages = new LinkedList<>();
		this.mySubsList = new HashSet<>();
	}

	public Set<String> addSubscriber(String user) {
		if (!mySubsList.contains(user)) {
			mySubsList.add(user);
		}

		return mySubsList;
	}

	public List<Message> addMessage(Message message) {
		if (!ownMessages.contains(message)) {
			ownMessages.add(message);
		}
		return ownMessages;
	}

	public Message getMessage(long msgID) {
	    Iterator<Message> iterator = ownMessages.iterator();
	    while (iterator.hasNext()) {
	        Message message = iterator.next();
	        if (message.getId() == msgID) {
	            return message;
	        }
	    }
	    return null;
	}

	public List<Message> getMessages(long time) {
		if (time == 0) return ownMessages;
		else {
			List<Message> result = new LinkedList<>();
		    for (Message message : ownMessages) {
		        if (message.getCreationTime() > time) {
		            result.add(message);
		        }
		    }
		    return result;
		}
	}

	public List<Message> removeMessage(Message message) {
		ownMessages.remove(message);
		return ownMessages;
	}

	public Set<String> removeSubscriber(String user) {
		mySubsList.remove(user);
		return mySubsList;
	}

	public Set<String> getSubscribers() {
		return mySubsList;
	}
	
	public List<Message> getMessage() {
		return ownMessages;
	}
}