package sd2223.trab1.server.rest;

import java.util.List;
import java.util.logging.Logger;

import sd2223.trab1.server.java.JavaUsers;
import jakarta.inject.Singleton;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.rest.UsersService;

@Singleton
public class RestUserResource extends RestResource implements UsersService {

	private static Logger Log = Logger.getLogger(RestUserResource.class.getName());

	private final Users impl = new JavaUsers();

	public RestUserResource() {
	}

	@Override
	public String createUser(User user) {

		Log.info("createUser : " + user);

		return super.fromJavaResult(impl.createUser(user));

	}

	@Override
	public User getUser(String name, String pwd) {

		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		return super.fromJavaResult(impl.getUser(name, pwd));

	}

	@Override
	public User updateUser(String name, String pwd, User user) {

		Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + user);

		return super.fromJavaResult(impl.updateUser(name, pwd, user));
	}

	@Override
	public User deleteUser(String name, String pwd) {

		Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

		return super.fromJavaResult(impl.deleteUser(name, pwd));

	}

	@Override
	public List<User> searchUsers(String pattern) {

		return super.fromJavaResult(impl.searchUsers(pattern));

	}

}
