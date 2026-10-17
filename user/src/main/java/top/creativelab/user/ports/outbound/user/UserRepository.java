package top.creativelab.user.ports.outbound.user;

import top.creativelab.user.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    boolean existsByUsername(String username);
    Optional<User> findUserByUsername(String username);
}
