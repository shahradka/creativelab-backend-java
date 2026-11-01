package top.creativelab.user.domain.user;
import lombok.Getter;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;

@Getter
public class User {
    private final UserId id;
    private String username;
    private String firstname;
    private String lastname;
    private BirthDate birthday;
    private Email email;
    private UserStatus userStatus;
    private Password password;

    public User(UserId id, String username, Password password, String firstname, String lastname) {
        this.id = id;
        this.username = username;
        this.userStatus = UserStatus.PENDING;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public static User create(UserId id, String username, Password password, String firstname, String lastname) {
        return new User(id, username, password, firstname, lastname);
    }

    public static User create(UserId id, String username, Password password) {
        return new User(id, username, password, "", "");
    }

    /**
     * Rehydrates a user from persisted state without applying creation defaults.
     * Used by the persistence adapter so status, email and birthday are preserved.
     */
    public static User rehydrate(UserId id, String username, Password password, String firstname, String lastname,
                                 Email email, BirthDate birthday, UserStatus userStatus) {
        User user = new User(id, username, password, firstname, lastname);
        user.email = email;
        user.birthday = birthday;
        user.userStatus = userStatus;
        return user;
    }

    public void updateFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void updateLastname(String lastname){
        this.lastname = lastname;
    }

    public void changePassword(Password password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.password = password;
    }

    public void updateEmail(Email email) {
        this.email = email;
    }

    public void updateBirthdate(BirthDate birthDate) {
        this.birthday = birthDate;
    }

    public void activate() {
        if (this.userStatus != UserStatus.PENDING) {
            throw new IllegalStateException();
        }
        this.userStatus = UserStatus.ACTIVE;
    }

    public void reActive(){
        if(this.userStatus != UserStatus.SUSPENDED){
            throw new IllegalArgumentException("User is not suspended");
        }
        this.userStatus = UserStatus.ACTIVE;
    }

    public void suspend(){
        if(this.userStatus == UserStatus.SUSPENDED){
            throw new IllegalArgumentException("User is Already suspended");
        }
        this.userStatus = UserStatus.SUSPENDED;
    }

    public void delete(){
        if(this.userStatus == UserStatus.DELETED){
            throw new IllegalArgumentException("This user is already deleted!");
        }
        this.userStatus = UserStatus.DELETED;
    }
}
