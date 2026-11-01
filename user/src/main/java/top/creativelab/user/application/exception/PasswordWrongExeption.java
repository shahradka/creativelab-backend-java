package top.creativelab.user.application.exception;

public class PasswordWrongExeption extends RuntimeException{
    public PasswordWrongExeption(){
        super("password is wrong!");
    }

}
