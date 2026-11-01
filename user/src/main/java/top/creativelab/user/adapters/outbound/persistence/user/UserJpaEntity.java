package top.creativelab.user.adapters.outbound.persistence.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;
import top.creativelab.user.domain.user.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity representation of the {@link top.creativelab.user.domain.user.User} domain entity.
 *
 * This persistence entity maps directly to the {@code users} database table and serves as the
 * data carrier between the domain layer and the JPA-based persistence mechanism. It inherits the
 * primary key and auditing timestamps ({@code created_at} / {@code updated_at}) from
 * {@link JpaEntityBaseModel}.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends JpaEntityBaseModel {

    /**
     * The login name used for authentication purposes.
     * Must be non-null and unique across the system.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The current operational state of the user account.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    /**
     * The hashed password used for secure authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The user's first name.
     */
    @Column(name = "firstname")
    private String firstname;

    /**
     * The user's last name.
     */
    @Column(name = "lastname")
    private String lastname;

    /**
     * The user's date of birth.
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * The user's primary email address.
     */
    @Column(name = "email")
    private String email;

    /**
     * Constructs a new {@link UserJpaEntity} with the core identity attributes.
     *
     * @param id       the unique identifier for the user
     * @param username the login name
     * @param status   the initial user status
     * @param password the hashed password
     */
    public UserJpaEntity(UUID id, String username, UserStatus status, String password) {
        super(id);
        this.username = username;
        this.userStatus = status;
        this.password = password;
    }

    /**
     * Copies the mutable state from the domain aggregate onto this persistence entity.
     * The identifier and auditing timestamps are intentionally left untouched.
     */
    public void updateFrom(String username,
                           UserStatus userStatus,
                           String password,
                           String firstname,
                           String lastname,
                           String email,
                           LocalDate birthday) {
        this.username = username;
        this.userStatus = userStatus;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birthday = birthday;
    }
}
