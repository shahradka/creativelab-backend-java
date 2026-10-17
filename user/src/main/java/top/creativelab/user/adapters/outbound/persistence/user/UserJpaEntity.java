package top.creativelab.user.adapters.outbound.persistence.user;

import jakarta.persistence.*;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.Password;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    @Column(nullable = false)
    private String password;

    

    protected UserJpaEntity() {
    }

    public UserJpaEntity(String id, String username, UserStatus status, String password) {
        this.id = id;
        this.username = username;
        this.userStatus = status;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public String getHashedPassword() {return password;}

}
