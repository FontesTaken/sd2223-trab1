package sd2223.trab1.api.java;

import java.util.List;

import sd2223.trab1.api.User;

public interface Users {

    Result<String> createUser(User user);

    Result<User> getUser(String userId, String password);

    Result<User> updateUser(String userId, String password, User user);

    Result<User> deleteUser(String userId, String password);

    Result<List<User>> searchUsers(String pattern);

}