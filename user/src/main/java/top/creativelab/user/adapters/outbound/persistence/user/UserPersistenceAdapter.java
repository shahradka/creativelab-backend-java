package top.creativelab.user.adapters.outbound.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.ports.outbound.user.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter extends BasePersistenceAdapter<User, UserJpaEntity, UUID> implements UserRepository {


    public UserPersistenceAdapter(UserJpaRepository userJpaRepository){
        super(userJpaRepository);
    }
    // =====================
    // Mapping
    // =====================

    public UserJpaEntity mapToJpaEntity(User user) {
        return new UserJpaEntity(
                user.getId().getValue(),
                user.getUsername(),
                user.getUserStatus(),
                user.getPassword().getHash()
        );
    }


    public User mapToDomain(UserJpaEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getUsername(),
                Password.fromHash(entity.getPassword()),
                entity.getFirstname(),
                entity.getLastname()
        );
    }

    @Override
    public boolean existsByUsername(String username) {
        return ((UserJpaRepository) repository).existsByUsername(username);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.empty();
    }
}
