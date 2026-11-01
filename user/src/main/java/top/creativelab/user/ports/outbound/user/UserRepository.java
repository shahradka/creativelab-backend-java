package top.creativelab.user.ports.outbound.user;

import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    List<User> findAll();
    long count();
    PageResult<User> findPage(int page, int size, UserFilter filter);
    boolean existsByUsername(String username);
    Optional<User> findUserByUsername(String username);
    boolean existsById(UUID id);
    Optional<User> findById(UUID id);
    List<User> findByFirstname(String firstname);
    List<User> findByLastname(String lastname);
    List<User> findByStatus(UserStatus status);
    long countByStatus(UserStatus status);
    List<User> findByUsernameContaining(String pattern);
    List<User> findByEmailContaining(String pattern);
    List<User> findByFirstnameContaining(String pattern);
    List<User> findByLastnameContaining(String pattern);
}
