package top.creativelab.user.adapters.inbound.rest.user;

import io.swagger.v3.oas.annotations.media.Schema;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;

import java.util.UUID;

@Schema(name = "UserResponse", description = "Representation of a user returned by the API.")
public class UserResponse {

    @Schema(description = "Unique identifier of the user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;
    @Schema(description = "Username of the user", example = "jane.doe")
    private final String username;
    @Schema(description = "First name of the user", example = "Jane")
    private final String firstname;
    @Schema(description = "Last name of the user", example = "Doe")
    private final String lastname;
    @Schema(description = "Email address of the user", example = "jane.doe@example.com")
    private final Email email;
    @Schema(description = "Birth date of the user", example = "1990-01-31")
    private final BirthDate birthday;
    @Schema(description = "Current lifecycle status of the user", example = "ACTIVE")
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
