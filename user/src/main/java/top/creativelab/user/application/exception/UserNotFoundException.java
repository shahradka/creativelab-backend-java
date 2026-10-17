package top.creativelab.user.application.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("User not found with username: %s".formatted(username));
    }
}
