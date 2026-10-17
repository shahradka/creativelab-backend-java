package top.creativelab.user.adapters.outbound.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.ports.outbound.user.UserRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public void save(User user) {
        jpaRepository.save(mapToUserJpaEntity(user));
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return jpaRepository
        .findByUsername(username)
        .map(this::mapToDomain);
    }

    // =====================
    // Mapping
    // =====================

    private UserJpaEntity mapToUserJpaEntity(User user) {
        return new UserJpaEntity(
                user.getId().getValue(),
                user.getUsername(),
                user.getUserStatus(),
                user.getPassword().getHash()
        );
    }

    private User mapToDomain(UserJpaEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getUsername(),
                Password.fromHash(entity.getHashedPassword())
        );
    }
}
