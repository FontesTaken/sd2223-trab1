package sd2223.trab1.server.resources;

import java.util.List;
import java.util.logging.Logger;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.Result.ErrorCode;
import sd2223.trab1.api.User;
import sd2223.trab1.api.Users;
import sd2223.trab1.api.rest.UsersService;

@Singleton
public class UsersResource implements UsersService {
	
	private static Logger Log = Logger.getLogger(UsersResource.class.getName());

	private final Users impl = new JavaUsers();

	public UsersResource() {
	}

	@Override
    public String createUser(User user) {

        Log.info("createUser : " + user);

        var result = impl.createUser(user);

        if(result.isOK())
			return result.value();
		else if(result.error().equals(ErrorCode.BAD_REQUEST)){
			throw new WebApplicationException( Status.BAD_REQUEST );
		} else if(result.error().equals(ErrorCode.NOT_FOUND)){
			throw new WebApplicationException( Status.NOT_FOUND );
		} else if(result.error().equals(ErrorCode.CONFLICT)){
			throw new WebApplicationException( Status.CONFLICT );
		} else if(result.error().equals(ErrorCode.FORBIDDEN)){
			throw new WebApplicationException( Status.FORBIDDEN );
		} else {
			throw new WebApplicationException( Status.NOT_IMPLEMENTED );
		}
    }

    @Override
    public User getUser(String name, String pwd) {

        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        var result = impl.getUser(name, pwd);

        if(result.isOK())
			return result.value();
		else if(result.error().equals(ErrorCode.BAD_REQUEST)){
			throw new WebApplicationException( Status.BAD_REQUEST );
		} else if(result.error().equals(ErrorCode.NOT_FOUND)){
			throw new WebApplicationException( Status.NOT_FOUND );
		} else if(result.error().equals(ErrorCode.CONFLICT)){
			throw new WebApplicationException( Status.CONFLICT );
		} else if(result.error().equals(ErrorCode.FORBIDDEN)){
			throw new WebApplicationException( Status.FORBIDDEN );
		} else {
			throw new WebApplicationException( Status.NOT_IMPLEMENTED );
		}

    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        
        Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + user);

        var result = impl.updateUser(name, pwd, user);

        if(result.isOK())
			return result.value();
		else if(result.error().equals(ErrorCode.BAD_REQUEST)){
			throw new WebApplicationException( Status.BAD_REQUEST );
		} else if(result.error().equals(ErrorCode.NOT_FOUND)){
			throw new WebApplicationException( Status.NOT_FOUND );
		} else if(result.error().equals(ErrorCode.CONFLICT)){
			throw new WebApplicationException( Status.CONFLICT );
		} else if(result.error().equals(ErrorCode.FORBIDDEN)){
			throw new WebApplicationException( Status.FORBIDDEN );
		} else {
			throw new WebApplicationException( Status.NOT_IMPLEMENTED );
		}
    }

    @Override
    public User deleteUser(String name, String pwd) {

        Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

        var result = impl.deleteUser(name, pwd);

        if(result.isOK())
			return result.value();
		else if(result.error().equals(ErrorCode.BAD_REQUEST)){
			throw new WebApplicationException( Status.BAD_REQUEST );
		} else if(result.error().equals(ErrorCode.NOT_FOUND)){
			throw new WebApplicationException( Status.NOT_FOUND );
		} else if(result.error().equals(ErrorCode.CONFLICT)){
			throw new WebApplicationException( Status.CONFLICT );
		} else if(result.error().equals(ErrorCode.FORBIDDEN)){
			throw new WebApplicationException( Status.FORBIDDEN );
		} else {
			throw new WebApplicationException( Status.NOT_IMPLEMENTED );
		}    
    }

    @Override
    public List<User> searchUsers(String pattern) {

        var result = impl.searchUsers(pattern);

        if(result.isOK())
			return result.value();
		else if(result.error().equals(ErrorCode.BAD_REQUEST)){
			throw new WebApplicationException( Status.BAD_REQUEST );
		} else if(result.error().equals(ErrorCode.NOT_FOUND)){
			throw new WebApplicationException( Status.NOT_FOUND );
		} else if(result.error().equals(ErrorCode.CONFLICT)){
			throw new WebApplicationException( Status.CONFLICT );
		} else if(result.error().equals(ErrorCode.FORBIDDEN)){
			throw new WebApplicationException( Status.FORBIDDEN );
		} else {
			throw new WebApplicationException( Status.NOT_IMPLEMENTED );
		}     }


}
