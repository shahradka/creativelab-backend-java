package top.creativelab.user.domain.user;
import lombok.Getter;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;

@Getter
public class User {
    private final UserId id;
    private final String username;
    private BirthDate birthday;
    private Email email;
    private UserStatus userStatus;
    private Password password;

    public User(UserId id, String username, Password password) {

        this.id = id;
        this.username = username;
        this.userStatus = UserStatus.PENDING;
        this.password = password;
    }

    public static User create(UserId id, String username, Password password){
        return new User(id, username, password);
    }

    public void changePassword(Password password){
        if(this.password == password) {
            throw new IllegalStateException("New password must be different");
        }
        this.password = password;
    }

    public void addEmail(Email email){
        if(this.email != null) {
            throw new IllegalStateException("Email already set!");
        }

        this.email = email;
    }

    public void addBirthdate(BirthDate birthDate){
        this.birthday = birthDate;
    }

    public void activate(){

        if(this.userStatus != UserStatus.PENDING){
            throw new IllegalStateException();
        }

        this.userStatus = UserStatus.ACTIVE;
    }
}
