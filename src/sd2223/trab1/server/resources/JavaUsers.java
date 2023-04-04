package sd2223.trab1.server.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static sd2223.trab1.api.Result.error;
import static sd2223.trab1.api.Result.ok;
import static sd2223.trab1.api.Result.ErrorCode.BAD_REQUEST;
import static sd2223.trab1.api.Result.ErrorCode.CONFLICT;
import static sd2223.trab1.api.Result.ErrorCode.FORBIDDEN;
import static sd2223.trab1.api.Result.ErrorCode.NOT_FOUND;
import sd2223.trab1.api.Result;
import sd2223.trab1.api.User;
import sd2223.trab1.api.Users;

public class JavaUsers implements Users {
	

	private final Map<String, User> users = new ConcurrentHashMap<>();

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());

	public JavaUsers() {
		
	}
	
	@Override
	public Result<String> createUser(User user) {

		// Check if user data is valid
		if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null
				|| user.getDomain() == null) {
			Log.info("User object invalid.");
			return error(BAD_REQUEST);
		}

		// Insert user, checking if name already exists
		if (users.putIfAbsent(user.getName(), user) != null) {
			Log.info("User already exists.");
			return error(CONFLICT);
		}
		return ok(user.getName()+"@"+user.getDomain());
	}

	@Override
	public Result<User> getUser(String name, String pwd) {

		// Check if input is valid
		if (name == null || pwd == null) {
			Log.info("Name or pwd null.");
			return error(BAD_REQUEST);
		}

		User user = users.get(name);
		// Check if user exists
		if (user == null) {
			Log.info("User does not exist.");
			return error(NOT_FOUND);
		}

		// Check if the pwd is correct
		if (!user.getPwd().equals(pwd)) {
			Log.info("pwd is incorrect.");
			return error(FORBIDDEN);
		}

		return ok(user);
	}

	@Override
	public Result<User> updateUser(String name, String pwd, User user) {

		// Check if user is valid
		if (name == null || pwd == null) {
			Log.info("name or pwd null.");
			return error(BAD_REQUEST);
		}

		var oldUser = users.get(name);

		// Check if user exists
		if (oldUser == null) {
			Log.info("User does not exist.");
			return error(NOT_FOUND);
		}

		// Check if the pwd is correct
		if (!oldUser.getPwd().equals(pwd)) {
			Log.info("pwd is incorrect.");
			return error(FORBIDDEN);
		}

		// If it exists then update the user information with the new one
		user.setName(oldUser.getName());
		if (user.getPwd() == null)
			user.setPwd(oldUser.getPwd());
		if (user.getDisplayName() == null)
			user.setDisplayName(oldUser.getDisplayName());
		user.setDomain(oldUser.getDomain());
		users.put(name, user);

		return ok(user);		
	}

	@Override
	public Result<User> deleteUser(String name, String pwd) {

		// Check if user is valid - 400
		if (name == null || pwd == null) {
			Log.info("name or pwd null.");
			return error(BAD_REQUEST);
		}

		var user = users.get(name);

		// Check if user exists - 404
		if (user == null) {
			Log.info("User does not exist.");
			return error(NOT_FOUND);
		}

		// Check if the pwd is correct - 403
		if (!user.getPwd().equals(pwd)) {
			Log.info("pwd is incorrect.");
			return error(FORBIDDEN);
		}

		// If it exists then we remove it from the map
		user = users.remove(name);

		return ok(user);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		// Check if input is valid - 400
		if (pattern == null) {
			Log.info("Please, input a valid pattern");
			return error(BAD_REQUEST);
		}

		List<User> listToReturn = new LinkedList<User>();

		for (User entry : users.values()) {
			if (entry.getDisplayName().toUpperCase().contains(pattern.toUpperCase())) {
				User newUser = entry.clone();
				newUser.setPwd("");
				listToReturn.add(newUser);
			}
		}
		return ok(listToReturn);
	}

}
