package top.creativelab.user.adapters.inbound.rest.user;

import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;

import java.util.UUID;

public class UserResponse {

    private final UUID id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final Email email;
    private final BirthDate birthday;
    private final UserStatus userStatus;

    public UserResponse(
            UUID id,
            String username,
            String firstname,
            String lastname,
            Email email,
            BirthDate birthday,
            UserStatus userStatus) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birthday = birthday;
        this.userStatus = userStatus;
    }

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId().getValue(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getBirthday(),
                user.getUserStatus()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Email getEmail() {
        return email;
    }

    public BirthDate getBirthday() {
        return birthday;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }
}
